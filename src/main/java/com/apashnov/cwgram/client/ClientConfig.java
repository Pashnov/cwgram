package com.apashnov.cwgram.client;

import org.telegram.bot.structure.BotConfig;

import java.nio.file.Path;

public class ClientConfig extends BotConfig {

    private String phoneNumber;

    public ClientConfig(Path path, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        setAuthfile(path.resolve(phoneNumber + ".auth").toString());
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
