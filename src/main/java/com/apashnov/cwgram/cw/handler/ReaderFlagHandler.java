package com.apashnov.cwgram.cw.handler;

import com.apashnov.cwgram.client.KernelCommNew;
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

    private TLUser chatWarsBot;

    private Lock notifier;
    private Condition condition;

    @Override
    public void handle(Warrior warrior, KernelCommNew kernelComm, String uniqueName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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

                        goToMainMenuThanRedDefThanGoingAttack(kernelComm, chatWarsBot);
                        String currentFlag = CwConstants.BTN_RED_FLAG;
                        while (notRegimeNoise()) {

                            String flag;
                            if (WarriorKind.AGGRESSOR == warrior.getKind()) {
                                flag = flagStorage.getAttack();
                            } else {
                                flag = flagStorage.getDefend();
                            }
                            if (flag == null || flag == currentFlag) {
                                try {
                                    Thread.sleep(3003);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                currentFlag = flag;
                                sendFlagThanGoingAttack(currentFlag, kernelComm, chatWarsBot);
                                try {
                                    Thread.sleep(3003);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
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
