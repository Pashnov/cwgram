package com.apashnov.cwgram.cw;

import com.apashnov.cwgram.client.*;
import com.apashnov.cwgram.client.handler.ChatsHandler;
import com.apashnov.cwgram.client.handler.MessageHandler;
import com.apashnov.cwgram.client.handler.TLMessageHandler;
import com.apashnov.cwgram.client.handler.UsersHandler;
import com.apashnov.cwgram.cw.handler.CwHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.structure.LoginStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Properties;

public class Container {

    public static final String LOCAL_FILE_NAME = "local.properties";
    public static final String GLOBAL_FILE_NAME = "global.properties";

    private int id;
    private String phoneNumber;
    private String name;
    private Path path;
    private Warrior warrior;
    private KernelCommNew kernelComm;

    public Container(Path path) {
        String[] split = path.getFileName().toString().split("_");
        id = Integer.valueOf(split[0]);
        phoneNumber = name = split[1];
        if (split.length == 3) {
            name = split[2];
        }
        this.path = path;
        loadWarrior();
    }

    private void loadWarrior() {
        Properties properties = new Properties();

        try (FileReader fileReader = new FileReader(path.getParent().resolve(GLOBAL_FILE_NAME).toString())) {
            properties.load(fileReader);
        } catch (IOException e) {
            throw new RuntimeException(getUniqueName() + "not successfully load " + GLOBAL_FILE_NAME);
        }
        try (FileReader reader = new FileReader(path.resolve(LOCAL_FILE_NAME).toString())){
            properties.load(reader);
        } catch (IOException e) {
            System.out.println(getUniqueName() + "not successfully load " + LOCAL_FILE_NAME);
        }
        warrior = new Warrior(properties);
    }

    public String getUniqueName() {
        return phoneNumber + "(" + name + ") -> ";
    }

    public void activate(int apiKey, String apiHash) {
        DatabaseManagerInMemory db = DatabaseManagerInMemory.getInstance();
        MessageHandler msgHandler = new MessageHandler();
        ChatUpdatesBuilderImpl chatUpdatesBuilder = new ChatUpdatesBuilderImpl(db, msgHandler, new UsersHandler(), new ChatsHandler(), new TLMessageHandler(msgHandler, db));

        ClientConfig config = new ClientConfig(path, phoneNumber);

        final TelegramBotNew kernel = new TelegramBotNew(config, chatUpdatesBuilder, apiKey, apiHash);

        LoginStatus status;
        try {
            status = kernel.init();

            if (status == LoginStatus.CODESENT) {
                System.out.println("type code for -> " + phoneNumber);
                System.out.println("and click enter");
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
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
            if (status == LoginStatus.ALREADYLOGGED) {
                System.out.println(phoneNumber + "(" + name + ") logged in successfully");
                kernelComm = kernel.getKernelComm();
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

    public void addHandler(CwHandler handler){
        handler.handle(warrior, kernelComm, getUniqueName());
    }

}
