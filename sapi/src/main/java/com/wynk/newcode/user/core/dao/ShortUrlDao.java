package com.wynk.newcode.user.core.dao;


import com.wynk.newcode.user.core.entity.ShortUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import world.ignite.common.exception.WynkRuntimeException;
import world.ignite.common.logging.LoggingMarkers;


@Repository
public class ShortUrlDao {
    private static final Logger logger = LoggerFactory.getLogger(ShortUrlDao.class);

    @Autowired
    private CassandraDao<ShortUrl> cassandraDao;

    public ShortUrl getShortUrl(String contentId, String type) {
        try {
            String query =
                    String.format(
                            "select * from shorturl where content_id = '%s' and type = '%s'",
                            contentId, type);
            logger.debug(query);
            return cassandraDao.selectOne(query, ShortUrl.class);
        } catch (Throwable th) {
            logger.error(
                    LoggingMarkers.CASSANDRA_ERROR,
                    "Error while getting short URL for contentId: {}, ERROR: {}",
                    new Object[]{contentId, th.getMessage(), th});
            throw new RuntimeException(th);
        }
    }

    public void saveShortUrl(ShortUrl shortUrl) {
        try {
            cassandraDao.insert(shortUrl);
        } catch (Throwable th) {
            logger.error(
                    LoggingMarkers.CASSANDRA_ERROR,
                    "Error while saving short url for : {}, ERROR: {}",
                    new Object[]{shortUrl, th.getMessage(), th});
            throw new RuntimeException(th);
        }
    }
}
