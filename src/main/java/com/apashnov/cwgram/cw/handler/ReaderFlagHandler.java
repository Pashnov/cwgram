package com.apashnov.cwgram.cw.handler;

import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.client.model.tl.TLRequestMessagesGetDialogsNew;
import com.apashnov.cwgram.cw.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.api.messages.dialogs.TLDialogs;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static com.apashnov.cwgram.cw.CwActionHelper.goToMainMenuThanRedDefThanGoingAttack;
import static com.apashnov.cwgram.cw.CwActionHelper.sendFlagThanGoingAttack;
import static com.apashnov.cwgram.cw.handler.GetterFlagHandler.notRegimeNoise;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReaderFlagHandler implements CwHandler {

    @Autowired
    private FlagStorage flagStorage;
    @Autowired
    private UpdatesStorage updatesStorage;

    private TLUser chatWarsBot;

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
                System.out.println(uniqueName + "started ReaderFlagHandler");
                TLRequestMessagesGetDialogsNew dialogsNew = new TLRequestMessagesGetDialogsNew(0, -1, 99);
                TLDialogs tlDialogs = null;
                try {
                    tlDialogs = kernelComm.getApi().doRpcCall(dialogsNew);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                chatWarsBot = (tlDialogs.getUsers()).stream().filter((TLAbsUser c) -> ((TLUser) c).getUserName().equals("ChatWarsBot"))
                        .findFirst().map(c -> ((TLUser) c)).get();
                while (true) {
                    try {
                        waitUntilWaked(notifier, condition);
                        System.out.println(uniqueName + " waked to read flag");

                        goToMainMenuThanRedDefThanGoingAttack(kernelComm, chatWarsBot, specificStorage, uniqueName);
                        String currentFlag = CwConstants.BTN_RED_FLAG;
                        while (notRegimeNoise()) {
                            System.out.println(uniqueName+"currentFlag -> " + currentFlag);
                            String flag;
                            if (WarriorKind.AGGRESSOR == warrior.getKind()) {
                                System.out.println(uniqueName+" going to get atk flag");
                                flag = flagStorage.getAttack();
                                System.out.println(uniqueName+" got atk flag -> " + flag);
                            } else {
                                System.out.println(uniqueName+" going to get def flag");
                                flag = flagStorage.getDefend();
                                System.out.println(uniqueName+" got def flag -> " + flag);
                            }
                            if (flag == null || flag == currentFlag) {
//                                try {
//                                    Thread.sleep(3003);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
                            } else {
                                currentFlag = flag;
                                System.out.println(uniqueName+ "going to send flag -> " + currentFlag);
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

    private void sendFlag(String currentFlag, IKernelComm kernelComm) {
        //todo::
    }

    private void goIntoDefense(Warrior warrior, IKernelComm kernelComm) {
        //todo::
    }

    @Autowired
    public void setNotifier(Notifier notifier) {
        this.notifier = notifier.getLock(this.getClass());
        this.condition = notifier.getCondition(this.getClass());
    }

}