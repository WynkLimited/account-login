package com.wynk.newcode.user.core.constant;

public enum UserSongType {
    RENTAL("RENTAL", "All Downloaded"), PURCHASE("PURCHASE", "All Purchased"), FAVOURITE("FAVOURITE", "All Liked");

    public final String name;
    public final String title;

    UserSongType(String name, String title) {
        this.name = name;
        this.title = title;
    }

}
