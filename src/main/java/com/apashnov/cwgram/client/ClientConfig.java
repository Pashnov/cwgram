package com.apashnov.cwgram.client;

import org.telegram.bot.structure.BotConfig;

public class ClientConfig extends BotConfig {

    private String phoneNumber;

    public ClientConfig(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        setAuthfile(phoneNumber + ".auth");
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String getBotToken() {
        return null;
    }

    @Override
    public boolean isBot() {
        return false;
    }
}
