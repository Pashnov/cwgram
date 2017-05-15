package com.apashnov.cwgram.client.model;

import org.telegram.bot.structure.Chat;

/**
 * Created by apashnov on 15.05.2017.
 */
public class ChatImpl implements Chat {
    private int id;
    private Long accessHash;
    private boolean isChannel;

    public ChatImpl(int id) {
        this.id = id;
    }

    public ChatImpl() {
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Long getAccessHash() {
        return accessHash;
    }

    @Override
    public boolean isChannel() {
        return isChannel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccessHash(Long accessHash) {
        this.accessHash = accessHash;
    }

    public void setChannel(boolean channel) {
        isChannel = channel;
    }
}
