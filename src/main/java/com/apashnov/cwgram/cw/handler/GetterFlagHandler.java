package com.apashnov.cwgram.cw.handler;

import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.client.UpdatesStorage.SpecificStorage;
import com.apashnov.cwgram.client.model.tl.TLRequestMessagesGetDialogsNew;
import com.apashnov.cwgram.cw.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.message.TLMessage;
import org.telegram.api.messages.dialogs.TLDialogs;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static com.apashnov.cwgram.Constants.RED_ALERT_ID;
import static com.apashnov.cwgram.cw.CustomLogger.log;
import static com.apashnov.cwgram.cw.CwActionHelper.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetterFlagHandler implements CwHandler {

    @Autowired
    private FlagStorage flagStorage;
    @Autowired
    private UpdatesStorage updatesStorage;

    private long redAlertAccessHash;

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
                log(uniqueName, "GetterFlagHandler# started ");
                TLRequestMessagesGetDialogsNew dialogsNew = new TLRequestMessagesGetDialogsNew(0, -1, 99);
                TLDialogs tlDialogs = null;
                do {
                    try {
                        tlDialogs = kernelComm.getApi().doRpcCall(dialogsNew);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (tlDialogs == null);
                TLAbsChat redAlertLegion = tlDialogs.getChats().stream().filter(c -> c.getId() == RED_ALERT_ID).findFirst().get();
                redAlertAccessHash = ((TLChannel) redAlertLegion).getAccessHash();
                chatWarsBot = (tlDialogs.getUsers()).stream().filter((TLAbsUser c) -> ((TLUser) c).getUserName().equals("ChatWarsBot"))
                        .findFirst().map(c -> ((TLUser) c)).get();

                while (true) {
                    try {
                        waitUntilWaked(notifier, condition);
                        log(uniqueName, "GetterFlagHandler# waked to get flag");

                        goToMainMenuThanRedDefThanGoingAttack(kernelComm, chatWarsBot, specificStorage, uniqueName);
                        String currentFlag = CwConstants.BTN_RED_FLAG;
                        while (notRegimeNoise()) {
//                        while (true) {
                            log(uniqueName, "GetterFlagHandler#currentFlag -> " + currentFlag);
                            log(uniqueName, "GetterFlagHandler#going to solve flag");
                            findCommandsAndSolve(specificStorage);
                            log(uniqueName, "GetterFlagHandler#solved flag");
                            String flag;
                            if (WarriorKind.AGGRESSOR == warrior.getKind()) {
                                log(uniqueName, "GetterFlagHandler# going to get atk flag");
                                flag = atcFlag;
                                log(uniqueName, "GetterFlagHandler# got atk flag -> " + flag);
                            } else {
                                log(uniqueName, "GetterFlagHandler# going to get def flag");
                                flag = defFlag;
                                log(uniqueName, "GetterFlagHandler# got def flag -> " + flag);
                            }
                            if (flag == null || flag == currentFlag) {
                                try {
                                    Thread.sleep(1500);
                                    continue;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                currentFlag = flag;
                                log(uniqueName, "GetterFlagHandler#going to send flag -> " + currentFlag);
                                sendFlagThanGoingAttack(currentFlag, kernelComm, chatWarsBot, specificStorage, uniqueName);
                                try {
                                    Thread.sleep(2075);
                                    continue;
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

    private String atcFlag;
    private String defFlag;

    private void findCommandsAndSolve(SpecificStorage specificStorage) throws Exception {
        List<TLMessage> messagesRedAlert = specificStorage.getRedAlert();
        boolean atc = true;
        boolean def = true;
        boolean full = true;
        log(uniqueName, "findCommandsAndSolve#RedAlertMsgs =" + toReadable(messagesRedAlert));
        for (TLMessage message : messagesRedAlert) {
            String text = message.getMessage().trim();
            log(uniqueName, "findCommandsAndSolve#text -> " + text);
            if (!text.isEmpty()) {
                switch (text.charAt(0)) {
                    case 'a':
                    case 'A':
                    case 'а':
                    case 'А':
                        if (atc) {
                            atc = false;
                            solveAtc(text);
                        }
                        break;
                    case 'п':
                    case 'П':
                    case 'д':
                    case 'Д':
                    case 'd':
                    case 'D':
                        if (def) {
                            def = false;
                            solveDef(text);
                        }
                        break;
                    case 'ф':
                    case 'Ф':
                        if (full) {
                            full = false;
                            solveFull(text);
                        }
                        break;
//                            default:
//                                solveFull(text);
//                                break;
                }
            }
        }
    }

    private void solveFull(String text) {
        log(uniqueName, "solveFull#text -> " + text);
        String flag = solve(text);
        atcFlag = flag;
        defFlag = flag;
        flagStorage.setAttack(flag);
        flagStorage.setDefend(flag);
    }

    private void solveDef(String text) {
        log(uniqueName, "solveDef#text -> " + text);
        String flag = solve(text);
        defFlag = flag;
        flagStorage.setDefend(flag);
    }

    private void solveAtc(String text) {
        log(uniqueName, "solveAtc#text -> " + text);
        String flag = solve(text);
        atcFlag = flag;
        flagStorage.setAttack(flag);
    }

    private String solve(String text) {
        String t = text.toLowerCase();
        log(uniqueName, "solve#text -> " + text);
        String result = CwConstants.BTN_RED_FLAG;
        if (t.contains("+б")) {
            result = CwConstants.BTN_WHITE_FLAG;
        } else if (t.contains("+су")) {
            result = CwConstants.BTN_GLOOMY_FLAG;
        } else if (t.contains("+с")) {
            result = CwConstants.BTN_BLUE_FLAG;
        } else if (t.contains("+ч")) {
            result = CwConstants.BTN_BLACK_FLAG;
        } else if (t.contains("+к")) {
            result = CwConstants.BTN_RED_FLAG;
        } else if (t.contains("+ж")) {
            result = CwConstants.BTN_YELLOW_FLAG;
        } else if (t.contains("+м")) {
            result = CwConstants.BTN_MINT_FLAG;
        } else if (t.contains("+г")) {
            result = CwConstants.BTN_MOUNT_FLAG;
        } else if (t.contains("+л")) {
            result = CwConstants.BTN_FOREST_FLAG;
        }
        log(uniqueName, "solve#result -> " + result);
        return result;
    }

    public static boolean notRegimeNoise() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        switch (hour) {
            case 0:
            case 4:
            case 8:
            case 12:
            case 16:
            case 20:
                if (minute >= 1) {
                    return false;
                }
                return true;
        }
        return true;
    }

    @Autowired
    public void setNotifier(Notifier notifier) {
        this.notifier = notifier.getLock(this.getClass());
        this.condition = notifier.getCondition(this.getClass());
    }

}