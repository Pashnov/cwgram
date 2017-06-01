package com.apashnov.cwgram.cw;

import com.apashnov.cwgram.client.ChatUpdatesBuilderImpl;
import com.apashnov.cwgram.client.ClientConfig;
import com.apashnov.cwgram.client.DatabaseManagerInMemory;
import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.client.handler.ChatsHandler;
import com.apashnov.cwgram.client.handler.MessageHandler;
import com.apashnov.cwgram.client.handler.TLMessageHandler;
import com.apashnov.cwgram.client.handler.UsersHandler;
import com.apashnov.cwgram.cw.handler.CwHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.structure.LoginStatus;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import static com.apashnov.cwgram.cw.CustomLogger.log;

public class Container {

    public static final String LOCAL_FILE_NAME = "local.properties";
    public static final String GLOBAL_FILE_NAME = "global.properties";

    private int id;
    private String idStr;
    private String phoneNumber;
    private String name;
    private Path path;
    private Warrior warrior;
    private IKernelComm kernelComm;
    private Properties properties;

    public Container(Path path) {
        String[] split = path.getFileName().toString().split("_");
        id = Integer.valueOf(split[0]);
        idStr = split[0];
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
        try (FileReader reader = new FileReader(path.resolve(LOCAL_FILE_NAME).toString())) {
            properties.load(reader);
        } catch (IOException e) {
//            System.out.println(getUniqueName() + "not successfully load " + LOCAL_FILE_NAME);
            log(getUniqueName(), "not successfully load " + LOCAL_FILE_NAME);
        }
        this.properties = properties;
        warrior = new Warrior(properties);
    }

    public String getUniqueName() {
        return idStr +"_"+phoneNumber + "(" + name + ") - ";
    }

    public void activate(int apiKey, String apiHash, UpdatesStorage updatesStorage) {
        DatabaseManagerInMemory db = DatabaseManagerInMemory.getInstance();
        MessageHandler msgHandler = new MessageHandler();
        ChatUpdatesBuilderImpl chatUpdatesBuilder = new ChatUpdatesBuilderImpl(db, msgHandler, new UsersHandler(db), new ChatsHandler(db),
                new TLMessageHandler(msgHandler, db, updatesStorage.get(phoneNumber), getUniqueName()), updatesStorage.get(phoneNumber), getUniqueName());

        ClientConfig config = new ClientConfig(path, phoneNumber);

        final TelegramBot kernel = new TelegramBot(config, chatUpdatesBuilder, apiKey, apiHash);

        LoginStatus status;
        try {
            status = kernel.init();

            if (status == LoginStatus.CODESENT) {
                ViewInputCode.getText().setText("type code for -> " + phoneNumber + " below");
                ViewInputCode.getCode().setText("");
                ViewInputCode.show();

                Thread.sleep(5000);
                String code = ViewInputCode.getCode().getText();
                while (!isMatch(code)) {
                    Thread.sleep(2000);
                    code = ViewInputCode.getCode().getText();
                }

                ViewInputCode.hide();
//                out.close();
                boolean success = kernel.getKernelAuth().setAuthCode(code);
                if (success) {
                    status = LoginStatus.ALREADYLOGGED;
                }

            }
            if (status == LoginStatus.ALREADYLOGGED) {
                log(getUniqueName(), " logged in successfully");
                kernelComm = kernel.getKernelComm();
                //kernel.startBot();  // aka turn on getDifAllTime;
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

    public void addHandler(CwHandler handler) {
        handler.handle(warrior, kernelComm, getUniqueName(), phoneNumber);
    }

}
