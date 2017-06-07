package com.apashnov.cwgram.cw.handler;

import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.client.UpdatesStorage.SpecificStorage;
import com.apashnov.cwgram.cw.Notifier;
import com.apashnov.cwgram.cw.UserChatStorage;
import com.apashnov.cwgram.cw.Warrior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.api.message.TLMessage;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static com.apashnov.cwgram.cw.CustomLogger.log;
import static com.apashnov.cwgram.cw.CwActionHelper.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CommandHandler implements CwHandler {

    @Autowired
    private UpdatesStorage updatesStorage;

    private Lock notifier;
    private Condition condition;
    private String uniqueName;
    private String phoneNumber;
    private SpecificStorage specificStorage;

    private Map<String, String> ids = new LinkedHashMap<String, String>(){
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest){return this.size() > 5;}
    };

    @Override
    public void handle(Warrior warrior, IKernelComm kernelComm, String uniqueName, String phoneNumber) {
        this.uniqueName = uniqueName;
        this.phoneNumber = phoneNumber;
        this.specificStorage = updatesStorage.get(phoneNumber);
        new Thread(new Runnable() {
            @Override
            public void run() {
                log(uniqueName, "CommandHandler# starting");
                TLUser chatWarsBot;
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                    chatWarsBot = UserChatStorage.getChatWarsBot(uniqueName);
                } while (chatWarsBot == null);
                log(uniqueName, "CommandHandler# started");

                while (true) {
                    try {
                        TLMessage tlMessage = specificStorage.getCommandGroup().get(0);
                        String message = tlMessage.getMessage();
                        // /start 01 /build_hq 312       25
                        // /keywr id /command  pause(s)  times
                        String[] params = message.split(" ");
                        String start = params[0].trim();
                        String id = params[1].trim();
                        String command = params[2].trim();
                        int timeout = Integer.valueOf(params[3].trim());
                        int reapedTimes = Integer.valueOf(params[4].trim());

                        if(start.equals("/start") && !ids.containsKey(id)){
                            for (int i = 0; i < reapedTimes; i++) {
                                sendMessageChatWars(uniqueName, kernelComm, convert(chatWarsBot), command, specificStorage);
                                Thread.sleep(timeout * 1000);
                            }
                            ids.put(id, id);
                        } else {
                            Thread.sleep(60*1000);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Autowired
    public void setNotifier(Notifier notifier) {
        this.notifier = notifier.getLock(this.getClass());
        this.condition = notifier.getCondition(this.getClass());
    }

}