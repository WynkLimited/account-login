package com.wynk.music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.wynk.common.OperatorCircle;
import com.wynk.constants.MusicConstants;
import com.wynk.db.S3StorageService;

@Service
public class CircleInfoMccMncCache {
	@Autowired
	private  S3StorageService s3ServiceManager ;
	private  final Logger logger               = LoggerFactory.getLogger(CircleInfoMccMncCache.class.getCanonicalName());
	private final HashMap<String, OperatorCircle> mccMncCircleMapping = new HashMap<String, OperatorCircle>();
	private  Set<String> mccMncCodes =null;

	private  String getMCCMNCJSONStringFromS3() {

        String jsonString = "";
        BufferedReader br = null;
        try {
            InputStream inputStream = s3ServiceManager.fetchData(MusicConstants.ALMUSICAPP, "MccMnc/mcc-mnc.json");
            br = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;
            while((line = br.readLine()) != null) {
                jsonString += line;
            }
            br.close();
        }
        catch (Exception e) {
            logger.error("Error reading module ordering/renaming JSON " + e.getMessage(), e);
        }
        finally {
            try {
                if(br != null)
                    br.close();
            } catch (IOException e) {
                logger.error("Error closing S3 input stream " + e.getMessage(), e);
            }
        }

        return jsonString;
    }
	
	@PostConstruct
	private  void loadCache() {
		String json = getMCCMNCJSONStringFromS3();
		if(!StringUtils.isEmpty(json)){
			JSONParser parser = new JSONParser();
			try {
				JSONArray jsonArray = (JSONArray) parser.parse(json);
				Iterator<JSONObject> iterator = jsonArray.iterator();
				while(iterator.hasNext()){
					JSONObject jsonObj = iterator.next();
					logger.info(jsonObj.toJSONString());
					String code =  jsonObj.get("code").toString();
					OperatorCircle oc = new OperatorCircle();
					oc.fromJSONObject(jsonObj);
					mccMncCircleMapping.put(code, oc);
				}
				if(!CollectionUtils.isEmpty(mccMncCircleMapping))
					mccMncCodes = mccMncCircleMapping.keySet();
			} catch (ParseException e) {
				logger.error("Exception while parsing json string : "+json,e);
			}
		}else{
			logger.info("Failed to load mcc mnc cache");
		}
			
		
	}
	
	public  OperatorCircle getOperatorCircleInfo(String mccMnc){
		return mccMncCircleMapping.get(mccMnc);
	}
	
	public OperatorCircle getOperatorCircleInfoFromImsi(String imsi){
		Iterator<String> iterator = mccMncCodes.iterator();
		while(iterator.hasNext()){
			String mccMnc = iterator.next();
			if(imsi.startsWith(mccMnc))
				return mccMncCircleMapping.get(mccMnc);
		}
		return null;
	}
}
