package com.wynk.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.wynk.constants.GeoDBConstants;
import com.wynk.db.S3StorageService;
import com.wynk.constants.GeoDBConstants;
import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class GeoDBService {

    private static final Logger logger = LoggerFactory.getLogger(GeoDBService.class.getCanonicalName());
    private static DatabaseReader geoIPCountryMappingReader;
    private static DatabaseReader geoIPCityMappingReader;
    private static String geoDBCountryLocalMd5Hash = "";
    private static String geoDBCityLocalMd5Hash = "";

    public void init() {
        logger.info(" Starting Initializing GeoDB Service");
        initializeDBService(GeoDBConstants.PRODUCT_GEOIP2_COUNTRY);
        initializeDBService(GeoDBConstants.PRODUCT_GEOLITE_CITY);
        logger.info("Finished Initializing GeoDB Service");
    }

    private void initializeDBService(String product) {
        try {
            geoDBLocalReader(product);
        } catch (Exception e) {
            logger.warn(new StringBuilder("Error initializing " + product + ". Error : ").append(e.getMessage()).toString(), e);
            try {
                geoDBLocalWriter(product);
            } catch (IOException e1) {
                logger.error(new StringBuilder("Couldn't initialize " + product + " from S3 as well. Error :  ").append(e1.getMessage()).toString(), e1);
            }
        }
    }

    public void refreshDatabase(String product) {
        logger.info("refresh " + product + " Database cron started ");
        try {
            if (isGeoDbFileModified(product)) {
                try {
                    // re-initialise
                    geoDBLocalWriter(product);
                } catch (IOException e) {
                    logger.error(new StringBuilder("Couldn't read " + product + " from s3 . Error :  ").append(e.getMessage()).toString(), e);
                    // in case of failure read from local
                    initializeDBService(product);
                }
            }
        } catch (IOException e1) {
            logger.error(new StringBuilder("Couldn't copy " + product + " from S3. Error :  ").append(e1.getMessage()).toString(),e1);
        }
        logger.info("refresh " + product + " cron completed ");
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 12,initialDelay = 1000 * 60 * 6) //initial delay of 6 min and then every 12 hours
    public void refreshCountryDatabase() {
        refreshDatabase(GeoDBConstants.PRODUCT_GEOIP2_COUNTRY);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 11,initialDelay = 1000 * 60 * 5) //initial delay of 5 min and then every 11 hours
    public void refreshCityDatabase() {
        refreshDatabase(GeoDBConstants.PRODUCT_GEOLITE_CITY);
    }

    private boolean isGeoDbFileModified(String product) throws IOException {
        String localHashData = product.equals(GeoDBConstants.PRODUCT_GEOIP2_COUNTRY) ? geoDBCountryLocalMd5Hash : geoDBCityLocalMd5Hash;
        String s3HashData = product.equals(GeoDBConstants.PRODUCT_GEOIP2_COUNTRY) ? GeoDBConstants.GEODB_COUNTRY_S3_HASH_FILE : GeoDBConstants.GEODB_CITY_S3_HASH_FILE;
        BufferedReader s3FileReader = S3StorageService.getReaderForS3File(GeoDBConstants.GEODB_BUCKET, s3HashData);
        String s3Md5Hash = s3FileReader.readLine();
        return !s3Md5Hash.equals(localHashData);
    }

    private void geoDBLocalReader(String product) throws IOException {
        switch (product)
        {
            case "GeoIP2-Country":
                geoIPCountryMappingReader = new DatabaseReader.Builder(new File(GeoDBConstants.GEODB_COUNTRY_LOCAL_FILE)).fileMode(com.maxmind.db.Reader.FileMode.MEMORY).build();
                geoDBCountryLocalMd5Hash = FileUtils.readFileToString(new File(GeoDBConstants.GEODB_COUNTRY_HASH_LOCAL_FILE), StandardCharsets.UTF_8);
                break;
            case "GeoLite2-City":
                geoIPCityMappingReader = new DatabaseReader.Builder(new File(GeoDBConstants.GEODB_CITY_LOCAL_FILE)).fileMode(com.maxmind.db.Reader.FileMode.MEMORY).build();
                geoDBCityLocalMd5Hash = FileUtils.readFileToString(new File(GeoDBConstants.GEODB_CITY_HASH_LOCAL_PATH), StandardCharsets.UTF_8);
                break;
            default:
                logger.info("no match for maxmind product : "+product);
        }
    }

    private void geoDBLocalWriter(String product) throws IOException {
        String s3DBFile = product.equals(GeoDBConstants.PRODUCT_GEOIP2_COUNTRY) ? GeoDBConstants.GEODB_COUNTRY_S3_FILE : GeoDBConstants.GEODB_CITY_S3_FILE;
        String s3HashFile = product.equals(GeoDBConstants.PRODUCT_GEOIP2_COUNTRY) ? GeoDBConstants.GEODB_COUNTRY_S3_HASH_FILE : GeoDBConstants.GEODB_CITY_S3_HASH_FILE;
        String localDBFile = product.equals(GeoDBConstants.PRODUCT_GEOIP2_COUNTRY) ? GeoDBConstants.GEODB_COUNTRY_LOCAL_FILE : GeoDBConstants.GEODB_CITY_LOCAL_FILE;
        String localHashFile = product.equals(GeoDBConstants.PRODUCT_GEOIP2_COUNTRY) ? GeoDBConstants.GEODB_COUNTRY_HASH_LOCAL_FILE : GeoDBConstants.GEODB_CITY_HASH_LOCAL_PATH;
        InputStream inputStreamGeoDB = readDataFromS3(s3DBFile);
        InputStream inputStreamGeoDBHash = readDataFromS3(s3HashFile);
        logger.info("File is modified hence " + product + " copying from S3 URL.");
        String geoDBLocalMd5Hash = IOUtils.toString(inputStreamGeoDBHash, StandardCharsets.UTF_8);
        // storing mmdb file
        FileUtils.copyInputStreamToFile(inputStreamGeoDB, new File(localDBFile));
        // storing hash file
        FileUtils.writeStringToFile(new File(localHashFile), geoDBLocalMd5Hash, StandardCharsets.UTF_8);
        switch (product)
        {
            case "GeoIP2-Country":
                geoIPCountryMappingReader = new DatabaseReader.Builder(readDataFromS3(s3DBFile)).fileMode(com.maxmind.db.Reader.FileMode.MEMORY).build();
                geoDBCountryLocalMd5Hash = geoDBLocalMd5Hash;
                break;
            case "GeoLite2-City":
                geoIPCityMappingReader = new DatabaseReader.Builder(readDataFromS3(s3DBFile)).fileMode(com.maxmind.db.Reader.FileMode.MEMORY).build();
                geoDBCityLocalMd5Hash = geoDBLocalMd5Hash;
                break;
            default:
                logger.info("no match for maxmind product : "+product);
        }
        inputStreamGeoDBHash.close();
    }

    public InputStream readDataFromS3(String key){
        return S3StorageService.getDbS3ObjectInputStream(GeoDBConstants.GEODB_BUCKET,key);
    }

    public String getCountry(String ipAddress) {
        return getCountryFromMaxMind(ipAddress);
    }

    private String getCountryFromMaxMind(String ipAddress) {
        if (geoIPCountryMappingReader == null)
            return null;

        String countryCode = null;

        long stime = System.currentTimeMillis();
        try {
            CountryResponse countryResponse = geoIPCountryMappingReader.
                country(InetAddress.getByName(ipAddress));
            Country country = countryResponse.getCountry();
            if (country == null) {
                logger.warn(new StringBuilder("No country for IP : ").append(ipAddress).toString());
                return null;
            }
            logger.info(new StringBuilder("IP-COUNTRY - ").append(ipAddress).append(" : ").append(country.getIsoCode()).append(",").append(country.getName()).append(",").append(country.getConfidence()).toString());
            if (country.getIsoCode() == null) {
                logger.warn(new StringBuilder("No country details for IP : ").append(ipAddress).toString());
                return null;
            }

            countryCode = country.getIsoCode().toUpperCase();
            return countryCode;
        } catch (AddressNotFoundException anfe) {
            logger.warn(new StringBuilder("Error getting country details for IP : ").append(ipAddress).append(". Error : ").append(anfe.getMessage()).toString());
        } catch (UnknownHostException UnknownHostexception) {
            logger.warn(new StringBuilder("Error getting country details due to unknown host for IP : ").append(ipAddress).toString());
        } catch (Exception e) {
            logger.error(new StringBuilder("Error getting country details for IP : ").append(ipAddress).append(". Error : ").append(e.getMessage()).toString(), e);

        } finally {
            logger.info(new StringBuilder("Country lookup time for IP (").append(ipAddress).append(") : ").append((
                System.currentTimeMillis() - stime)).toString());
        }

        return null;

    }

    public String getCity(String ipAddress) {
        return getCityFromMaxMind(ipAddress);
    }

    private String getCityFromMaxMind(String ipAddress) {
        if (geoIPCityMappingReader == null)
            return null;
        long stime = System.currentTimeMillis();
        try {
            CityResponse cityResponse = geoIPCityMappingReader.
                city(InetAddress.getByName(ipAddress));
            City city = cityResponse.getCity();
            if (city == null) {
                logger.warn(new StringBuilder("No city for IP : ").append(ipAddress).toString());
                return null;
            }
            logger.info(new StringBuilder("IP-CITY - ").append(ipAddress).append(" : ").append(city.getName()).append(",").append(city.getConfidence()).toString());
            return city.getName();

        } catch (AddressNotFoundException anfe) {
            logger.warn(new StringBuilder("Error getting city details for IP : ").append(ipAddress).append(". Error : ").append(anfe.getMessage()).toString());
        } catch (Exception e) {
            logger.error(new StringBuilder("Error getting city details for IP : ").append(ipAddress).append(". Error : ").append(e.getMessage()).toString(), e);
        } finally {
            logger.info(new StringBuilder("City lookup time for IP (").append(ipAddress).append(") : ").append((
                System.currentTimeMillis() - stime)).toString());
        }
        return null;
    }
}