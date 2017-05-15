package com.apashnov.cwgram;

import com.apashnov.cwgram.client.ChatUpdatesBuilderImpl;
import com.apashnov.cwgram.client.ClientConfig;
import com.apashnov.cwgram.client.DatabaseManagerInMemory;
import com.apashnov.cwgram.client.handler.ChatsHandler;
import com.apashnov.cwgram.client.handler.MessageHandler;
import com.apashnov.cwgram.client.handler.TLMessageHandler;
import com.apashnov.cwgram.client.handler.UsersHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.structure.LoginStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

@SpringBootApplication
public class TelegramClientCwApplication {

    private static final int APIKEY = ;// your api key
    private static final String APIHASH = ""; // your api hash
    private static final String PHONENUMBER = ""; // Your phone number

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext app = SpringApplication.run(TelegramClientCwApplication.class, args);

//      app.getBean();
        DatabaseManagerInMemory db = new DatabaseManagerInMemory();
        MessageHandler msgHandler = new MessageHandler();
        ChatUpdatesBuilderImpl chatUpdatesBuilder = new ChatUpdatesBuilderImpl(db, msgHandler, new UsersHandler(), new ChatsHandler(), new TLMessageHandler(msgHandler, db));

        ClientConfig config = new ClientConfig(PHONENUMBER);

        final TelegramBot kernel = new TelegramBot(config, chatUpdatesBuilder, APIKEY, APIHASH);
        LoginStatus status = kernel.init();
        if (status == LoginStatus.CODESENT) {
            Scanner in = new Scanner(System.in);
            boolean success = kernel.getKernelAuth().setAuthCode(in.nextLine().trim());
            if (success) {
                status = LoginStatus.ALREADYLOGGED;
            }
        }
        if (status == LoginStatus.ALREADYLOGGED) {
            kernel.startBot();
        } else {
            throw new Exception("Failed to log in: " + status);
        }

        app.close();

    }
}
