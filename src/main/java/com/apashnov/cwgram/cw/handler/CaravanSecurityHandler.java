package com.apashnov.cwgram.cw.handler;

import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.client.UpdatesStorage.SpecificStorage;
import com.apashnov.cwgram.client.model.tl.TLRequestMessagesGetDialogsNew;
import com.apashnov.cwgram.cw.CaptchaSolver;
import com.apashnov.cwgram.cw.Notifier;
import com.apashnov.cwgram.cw.Warrior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.api.engine.RpcException;
import org.telegram.api.keyboard.replymarkup.TLReplayKeyboardMarkup;
import org.telegram.api.message.TLMessage;
import org.telegram.api.messages.dialogs.TLDialogs;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static com.apashnov.cwgram.Constants.BTN_QUEST;
import static com.apashnov.cwgram.cw.CustomLogger.log;
import static com.apashnov.cwgram.cw.CwActionHelper.*;
import static com.apashnov.cwgram.cw.handler.QuestHandler.findChatWarsUser;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CaravanSecurityHandler implements CwHandler {

    @Autowired
    private UpdatesStorage updatesStorage;

    @Autowired
    private CaptchaSolver captchaSolver;

    private TLUser chatWarsBot;

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

                chatWarsBot = findChatWarsUser(kernelComm, uniqueName);

                while (true) {
                    try {
                        Thread.sleep(1 * 60 * 1000);
                        log(uniqueName," check caravan ");
                        List<TLMessage> chatWars = specificStorage.getChatWars();
                        log(uniqueName, toReadable(chatWars));
                        for (TLMessage msgChatWars : chatWars) {
                            if(msgChatWars.getFromId() == chatWarsBot.getId()){
                                if(msgChatWars.getMessage().contains("/go")){
                                    sendMessage(kernelComm, convert(chatWarsBot), "/go");
                                    log(uniqueName, "sent '/go'");
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