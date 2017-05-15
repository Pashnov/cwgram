package com.apashnov.cwgram.client.model;

import org.telegram.bot.structure.IUser;

/**
 * Created by apashnov on 15.05.2017.
 */
public class User implements IUser {
    private final int userId; ///< ID of the user (provided by Telegram server)
    private Long userHash; ///< Hash of the user (provide by Telegram server)

    public User(int uid) {
        this.userId = uid;
    }

    public User(User copy) {
        this.userId = copy.getUserId();
        this.userHash = copy.getUserHash();
    }

    @Override
    public int getUserId() {
        return this.userId;
    }

    @Override
    public Long getUserHash() {
        return userHash;
    }

    public void setUserHash(Long userHash) {
        this.userHash = userHash;
    }

    @Override
    public String toString() {
        return "" + this.userId;
    }
}
