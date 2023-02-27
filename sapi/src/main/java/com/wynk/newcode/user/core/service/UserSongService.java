package com.wynk.newcode.user.core.service;

import com.wynk.newcode.user.core.constant.UserSongType;
import com.wynk.newcode.user.core.dao.UserSongsDao;
import com.wynk.newcode.user.core.entity.UserSong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSongService {


    @Autowired
    private UserSongsDao userSongsDao;

    public List<UserSong> getUserRental(String uid) {
        return userSongsDao.getSongs(uid, UserSongType.RENTAL);
    }

    public UserSong getUserPurchase(String uid, String songId) {
        return userSongsDao.getSong(uid, UserSongType.PURCHASE, songId);
    }

    public void saveUserSong(String uid, UserSong userSong) {
        userSongsDao.saveSong(uid, userSong);
    }

    public void saveUserSongs(String uid, List<UserSong> userSongs) {
        userSongsDao.saveSongs(uid, userSongs);
    }

    public void removeUserRental(String uid, String songId) {
        userSongsDao.removeSong(uid, UserSongType.RENTAL, songId);
    }
}
