package com.wynk.newcode.user.core.dao;


import com.wynk.newcode.user.core.entity.ShortUrlReverse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import world.ignite.common.exception.WynkRuntimeException;
import world.ignite.common.logging.LoggingMarkers;

@Repository
public class ShortUrlReverseDao {
    private static final Logger logger = LoggerFactory.getLogger(ShortUrlReverseDao.class);

    @Autowired
    private CassandraDao<ShortUrlReverse> cassandraDao;

    public ShortUrlReverse getShortUrl(String shortUrl) {
        try {
            String query =
                    String.format("select * from shorturl_rev where short_url = '%s'", shortUrl);
            logger.debug(query);
            return cassandraDao.selectOne(query, ShortUrlReverse.class);
        } catch (Throwable th) {
            logger.error(
                    LoggingMarkers.CASSANDRA_ERROR,
                    "Error while getting info from short URL : {}, ERROR: {}",
                    new Object[]{shortUrl, th.getMessage(), th});
            throw new RuntimeException(th);
        }
    }

    public void saveShortUrlReverse(ShortUrlReverse shortUrlReverse) {
        try {
            cassandraDao.insert(shortUrlReverse);
        } catch (Throwable th) {
            logger.error(
                    LoggingMarkers.CASSANDRA_ERROR,
                    "Error while saving short url for : {}, ERROR: {}",
                    new Object[]{shortUrlReverse, th.getMessage(), th});
            throw new RuntimeException(th);
        }
    }
}
