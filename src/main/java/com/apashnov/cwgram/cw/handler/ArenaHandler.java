package com.apashnov.cwgram.cw.handler;

import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.client.UpdatesStorage.SpecificStorage;
import com.apashnov.cwgram.cw.FlagStorage;
import com.apashnov.cwgram.cw.Notifier;
import com.apashnov.cwgram.cw.Warrior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.api.message.TLMessage;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static com.apashnov.cwgram.cw.CustomLogger.log;
import static com.apashnov.cwgram.cw.CwActionHelper.convert;
import static com.apashnov.cwgram.cw.CwActionHelper.findChatWarsUser;
import static com.apashnov.cwgram.cw.CwActionHelper.sendMessageChatWars;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ArenaHandler implements CwHandler {

    @Autowired
    private FlagStorage flagStorage;
    @Autowired
    private UpdatesStorage updatesStorage;

    private TLUser chatWarsBot;

    private Lock notifier;
    private Condition condition;
    private String uniqueName;
    private String phoneNumber;
    private SpecificStorage specificStorage;

    private boolean isReachedArenaLimit;
    private int dayWhenSetLimit;

    @Override
    public void handle(Warrior warrior, IKernelComm kernelComm, String uniqueName, String phoneNumber) {
        this.uniqueName = uniqueName;
        this.phoneNumber = phoneNumber;
        this.specificStorage = updatesStorage.get(phoneNumber);
        new Thread(new Runnable() {
            @Override
            public void run() {
                log(uniqueName, "run#started ArenaHandler");
                chatWarsBot = findChatWarsUser(kernelComm, uniqueName);

                while (true) {
                    try {
                        waitUntilWaked(notifier, condition);
                        log(uniqueName, "ArenaHandler# waked to read flag");

                        while (!isReachedArenaLimit()) {
                            sendMessageChatWars(uniqueName, kernelComm, convert(chatWarsBot), "/report", specificStorage);
                            List<TLMessage> chatWars = specificStorage.getChatWars();
                            while (chatWars.isEmpty() || chatWars.get(0).getMessage().contains("Твои результаты в бою")){
                                chatWars = specificStorage.getChatWars();
                            }



                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private boolean isReachedArenaLimit() {
        LocalDate date = LocalDate.now();
        if (date.getDayOfMonth() == dayWhenSetLimit) {
            return isReachedArenaLimit;
        } else {
            dayWhenSetLimit = date.getDayOfMonth();
            isReachedArenaLimit = false;
            return isReachedArenaLimit;
        }
    }

    private void setReachedArenaLimit() {
        LocalDate date = LocalDate.now();
        dayWhenSetLimit = date.getDayOfMonth();
        isReachedArenaLimit = true;
    }


    @Autowired
    public void setNotifier(Notifier notifier) {
        this.notifier = notifier.getLock(this.getClass());
        this.condition = notifier.getCondition(this.getClass());
    }

}