package com.apashnov.cwgram.cw;

import com.apashnov.cwgram.client.ChatUpdatesBuilderImpl;
import com.apashnov.cwgram.client.ClientConfig;
import com.apashnov.cwgram.client.DatabaseManagerInMemory;
import com.apashnov.cwgram.client.handler.ChatsHandler;
import com.apashnov.cwgram.client.handler.MessageHandler;
import com.apashnov.cwgram.client.handler.TLMessageHandler;
import com.apashnov.cwgram.client.handler.UsersHandler;
import org.jetbrains.annotations.NotNull;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.structure.LoginStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class Container implements Comparable<Container> {

    private int id;
    private String phoneNumber;
    private String name;
    private Path path;
    private Warrior warrior;

    public Container(Path path) {
        String[] split = path.getFileName().toString().split("_");
        id = Integer.valueOf(split[0]);
        phoneNumber = name = split[1];
        if (split.length == 3) {
            name = split[2];
        }
        this.path = path;
    }

    public void activate(int apiKey, String apiHash) {
        DatabaseManagerInMemory db = DatabaseManagerInMemory.getInstance();
        MessageHandler msgHandler = new MessageHandler();
        ChatUpdatesBuilderImpl chatUpdatesBuilder = new ChatUpdatesBuilderImpl(db, msgHandler, new UsersHandler(), new ChatsHandler(), new TLMessageHandler(msgHandler, db));

        ClientConfig config = new ClientConfig(path, phoneNumber);

        final TelegramBot kernel = new TelegramBot(config, chatUpdatesBuilder, apiKey, apiHash);

        LoginStatus status;
        try {
            status = kernel.init();

            if (status == LoginStatus.CODESENT) {
                System.out.println("type code for -> " + phoneNumber);
                System.out.println("and click enter");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                    String code = br.readLine().trim();
                    while (!isMatch(code)) {
                        System.out.println("type code again for -> " + phoneNumber);
                        code = br.readLine().trim();
                    }
                    boolean success = kernel.getKernelAuth().setAuthCode(code);
                    if (success) {
                        status = LoginStatus.ALREADYLOGGED;
                    }
                }
            }
            if (status == LoginStatus.ALREADYLOGGED) {
                System.out.println(phoneNumber + "(" + name + ") logged in successfully");
            } else {
                throw new RuntimeException("Failed to log in: " + status + ", for  -> " + phoneNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMatch(String code) {
        if (code != null && code.length() == 5) {
            for (int i = 0; i < code.length(); i++) {
                if (!Character.isDigit(code.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(@NotNull Container o) {
        return Integer.compare(id, o.getId());
    }
}
