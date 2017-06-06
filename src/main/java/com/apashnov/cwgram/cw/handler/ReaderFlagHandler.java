package com.apashnov.cwgram.cw.handler;

import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.cw.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static com.apashnov.cwgram.cw.CustomLogger.log;
import static com.apashnov.cwgram.cw.CwActionHelper.*;
import static com.apashnov.cwgram.cw.handler.GetterFlagHandler.notRegimeNoise;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReaderFlagHandler implements CwHandler {

    @Autowired
    private FlagStorage flagStorage;
    @Autowired
    private UpdatesStorage updatesStorage;

    private Lock notifier;
    private Condition condition;
    private String uniqueName;
    private String phoneNumber;
    private UpdatesStorage.SpecificStorage specificStorage;

    @Override
    public void handle(Warrior warrior, IKernelComm kernelComm, String uniqueName, String phoneNumber) {
        this.uniqueName = uniqueName;
        this.phoneNumber = phoneNumber;
        this.specificStorage = updatesStorage.get(phoneNumber);
        new Thread(new Runnable() {
            @Override
            public void run() {
                log(uniqueName, "ReaderFlagHandler# starting");
                findNecessaryChatsGroup(kernelComm, uniqueName);
                TLUser chatWarsBot = UserChatStorage.getChatWarsBot(uniqueName);
                log(uniqueName, "ReaderFlagHandler# started");

                while (true) {
                    try {
                        waitUntilWaked(notifier, condition);
                        log(uniqueName, "run# waked to read flag");

                        goToMainMenuThanRedDefThanGoingAttack(kernelComm, chatWarsBot, specificStorage, uniqueName);
                        String currentFlag = CwConstants.BTN_RED_FLAG;
                        while (notRegimeNoise()) {
                            log(uniqueName, "run#currentFlag -> " + currentFlag);
                            String flag;
                            if (WarriorKind.AGGRESSOR == warrior.getKind()) {
                                log(uniqueName, "run# going to get atk flag");
                                flag = flagStorage.getAttack();
                                log(uniqueName, " got atk flag -> " + flag);
                            } else {
                                log(uniqueName, "run# going to get def flag");
                                flag = flagStorage.getDefend();
                                log(uniqueName, "run# got def flag -> " + flag);
                            }
                            if (flag == null || flag == currentFlag) {
//                                try {
//                                    Thread.sleep(3003);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
                            } else {
                                currentFlag = flag;
                                log(uniqueName, "run#going to send flag -> " + currentFlag);
                                sendFlagThanGoingAttack(currentFlag, kernelComm, chatWarsBot, specificStorage, uniqueName);
//                                try {
//                                    Thread.sleep(3003);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
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