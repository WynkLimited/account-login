package com.wynk.user.dto;

import com.wynk.common.Gender;
import com.wynk.common.UserType;
import com.wynk.dto.NdsUserInfo;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

@Table(value = "userOperatorInfo")
public class UserProfile {

    @PrimaryKey
    private String uid;

    private String userType;

    private String preferredLanguage;

    private String circle;

    private long    updateTimestamp;

    private String dataRating;

    private String location;

    private String gender;

    private boolean corporateUser;

    private boolean dataUser = true;

    private Boolean threeGCapable;

    private Boolean gprsCapable;

    private String imei;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public String getDataRating() {
        return dataRating;
    }

    public void setDataRating(String dataRating) {
        this.dataRating = dataRating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isCorporateUser() {
        return corporateUser;
    }

    public void setCorporateUser(boolean corporateUser) {
        this.corporateUser = corporateUser;
    }

    public boolean isDataUser() {
        return dataUser;
    }

    public void setDataUser(boolean dataUser) {
        this.dataUser = dataUser;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getThreeGCapable() {
        return threeGCapable;
    }

    public void setThreeGCapable(Boolean threeGCapable) {
        this.threeGCapable = threeGCapable;
    }

    public Boolean getGprsCapable() {
        return gprsCapable;
    }

    public void setGprsCapable(Boolean gprsCapable) {
        this.gprsCapable = gprsCapable;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public static UserProfile newUserProfileFromNdsUserInfo(NdsUserInfo ndsUser, String uuid) {
        UserProfile profile = new UserProfile();
        profile.setUid(uuid);
        profile.setCircle(ndsUser.getCircle());
        profile.setPreferredLanguage(ndsUser.getPreferredLanguage());
        profile.setUpdateTimestamp(System.currentTimeMillis());
        if (null != ndsUser.getUserType()) {
            profile.setUserType(ndsUser.getUserType().name());
        }
        profile.setDataRating(ndsUser.getDataRating());
        profile.setDataUser(ndsUser.isDataUser());
        profile.setCorporateUser(ndsUser.isCorporateUser());
        profile.setLocation(ndsUser.getLocation());
        if (null != ndsUser.getGender()) {
            profile.setGender(ndsUser.getGender().name());
        }
        profile.setGprsCapable(ndsUser.getGprsCapable());
        profile.setThreeGCapable(ndsUser.getThreeGCapable());
        profile.setImei(ndsUser.getImei());
        return profile;
    }

    public static NdsUserInfo toNdsUserInfo(UserProfile profile) {
        NdsUserInfo ndsUser = new NdsUserInfo();
        ndsUser.setCircle(profile.getCircle());
        ndsUser.setPreferredLanguage(profile.getPreferredLanguage());
        if(null != profile.getUserType()) {
            ndsUser.setUserType(UserType.valueOf(profile.getUserType()));
        }
        ndsUser.setLocation(profile.getLocation());
        ndsUser.setDataRating(profile.getDataRating());
        ndsUser.setDataUser(profile.isDataUser());
        ndsUser.setCorporateUser(profile.isCorporateUser());
        if(null != profile.getGender()) {
            ndsUser.setGender(Gender.valueOf(profile.getGender()));
        }
        ndsUser.setGprsCapable(profile.getGprsCapable());
        ndsUser.setThreeGCapable(profile.getThreeGCapable());
        ndsUser.setImei(profile.getImei());
        return ndsUser;
    }

    public boolean update(boolean forceUpdate, int hoursToKeep) {
        if (forceUpdate || ((System.currentTimeMillis() - getUpdateTimestamp()) / (1000 * 60 * 60) >= hoursToKeep)) {
            return true;
        }
        return false;
    }
}
