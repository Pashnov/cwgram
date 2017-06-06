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

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static com.apashnov.cwgram.cw.CustomLogger.log;
import static com.apashnov.cwgram.cw.CwActionHelper.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CaravanSecurityHandler implements CwHandler {

    @Autowired
    private UpdatesStorage updatesStorage;

    private Lock notifier;
    private Condition condition;
    private String uniqueName;
    private String phoneNumber;
    private SpecificStorage specificStorage;

    @Override
    public void handle(Warrior warrior, IKernelComm kernelComm, String uniqueName, String phoneNumber) {
        this.uniqueName = uniqueName;
        this.phoneNumber = phoneNumber;
        this.specificStorage = updatesStorage.get(phoneNumber);

        new Thread(new Runnable() {
            @Override
            public void run() {
                log(uniqueName, "CaravanSecurityHandler# starting");

                TLUser chatWarsBot;
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                    chatWarsBot = UserChatStorage.getChatWarsBot(uniqueName);
                } while (chatWarsBot == null);

                log(uniqueName, "CaravanSecurityHandler# started");


                while (true) {
                    try {
                        Thread.sleep(1 * 60 * 1000);
                        log(uniqueName, "CaravanSecurityHandler# check caravan ");
                        List<TLMessage> chatWars = specificStorage.getChatWars();
                        if(!chatWars.isEmpty()){
                            if (chatWars.get(0).getFromId() == chatWarsBot.getId()) {
                                if (chatWars.get(0).getMessage().contains("/go")) {
                                    sendMessageChatWars(uniqueName, kernelComm, convert(chatWarsBot), "/go", specificStorage);
                                    log(uniqueName, "CaravanSecurityHandler#sent '/go'");
                                }
                            }
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