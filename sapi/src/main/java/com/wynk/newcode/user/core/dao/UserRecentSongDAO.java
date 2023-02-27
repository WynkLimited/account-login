package com.wynk.newcode.user.core.dao;

import com.wynk.newcode.user.core.entity.RecentSong;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import world.ignite.common.logging.LoggingMarkers;

@Repository
public class UserRecentSongDAO {

  private static final Logger logger =
      LoggerFactory.getLogger(UserRecentSongDAO.class.getCanonicalName());
  @Autowired private CassandraDao<RecentSong> cassandraDao;

  public List<RecentSong> getRecentlyPlayedSongs(String uid) {
    if (StringUtils.isBlank(uid)) {
      throw new RuntimeException("Blank uid");
    }
    try {
      String query =
          String.format("select * from %1$s where %2$s = '%3$s' ", "user_recent_songs", "uid", uid);
      logger.debug(query);
      logger.info(query);
      return cassandraDao.select(query, RecentSong.class);
    } catch (Throwable th) {
      logger.error(
          LoggingMarkers.CASSANDRA_ERROR,
          "Error while getting songs for uid: {}, ERROR: {}",
          new Object[] {uid, th.getMessage(), th});
      throw new RuntimeException(th);
    }
  }
}
