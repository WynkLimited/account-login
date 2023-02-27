package com.wynk.newcode.user.core.dao;

import com.wynk.newcode.user.core.constant.UserSongType;
import com.wynk.newcode.user.core.entity.UserSong;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import world.ignite.common.logging.LoggingMarkers;

import java.util.List;

@Repository
public class UserSongsDao {


    private static final Logger logger =
            LoggerFactory.getLogger(UserSongsDao.class.getCanonicalName());
    @Autowired
    private CassandraDao<UserSong> cassandraDao;

    public void saveSongs(String uid, List<UserSong> userSongs) {
        try {
            if (userSongs == null || userSongs.isEmpty()) return;
            cassandraDao.insert(userSongs);
        } catch (Throwable th) {
            logger.error(
                    LoggingMarkers.CASSANDRA_ERROR,
                    "Error while saving user songs for uid: {}, ERROR: {}",
                    new Object[]{uid, th.getMessage(), th});
            throw new RuntimeException(th);
        }
    }

    public void saveSong(String uid, UserSong userSong) {
        try {
            if (userSong == null) return;
            cassandraDao.insert(userSong);
        } catch (Throwable th) {
            logger.error(
                    LoggingMarkers.CASSANDRA_ERROR,
                    "Error while saving user songs for uid: {}, ERROR: {}",
                    new Object[]{uid, th.getMessage(), th});
            throw new RuntimeException(th);
        }
    }

    public void removeSong(String uid, UserSongType type, String songId) {
        try {
            String query =
                    String.format(
                            "delete from user_songs where uid = '%s' and type = '%s' and song_id = '%s'",
                            uid, type.name, songId);
            logger.debug(query);
            cassandraDao.execute(query);
        } catch (Throwable th) {
            logger.error(
                    LoggingMarkers.CASSANDRA_ERROR,
                    "Error while removing song for uid: {}, ERROR: {}",
                    new Object[]{uid, th.getMessage(), th});
            throw new RuntimeException(th);
        }
    }

    public List<UserSong> getSongs(String uid, UserSongType type) {
        if (StringUtils.isBlank(uid)) {
            throw new RuntimeException("Blank uid");
        }
        try {
            String query =
                    String.format(
                            "select * from user_songs where uid = '%s' and type = '%s'", uid, type.name);
            logger.debug(query);
            return cassandraDao.select(query, UserSong.class);
        } catch (Throwable th) {
            logger.error(
                    LoggingMarkers.CASSANDRA_ERROR,
                    "Error while getting songs for uid: {}, ERROR: {}",
                    new Object[]{uid, th.getMessage(), th});
            throw new RuntimeException(th);
        }
    }

    public UserSong getSong(String uid, UserSongType type, String songId) {
        if (StringUtils.isBlank(uid)) {
            throw new RuntimeException("Blank uid");
        }
        try {
            String query =
                    String.format(
                            "select * from user_songs where uid = '%s' and type = '%s' and song_id = '%s'", uid, type.name, songId);
            logger.debug(query);
            return cassandraDao.selectOne(query, UserSong.class);
        } catch (Throwable th) {
            logger.error(
                    LoggingMarkers.CASSANDRA_ERROR,
                    "Error while getting songs for uid: {}, ERROR: {}",
                    new Object[]{uid, th.getMessage(), th});
            throw new RuntimeException(th);
        }
    }

}
