package com.apashnov.cwgram.cw.handler;

import com.apashnov.cwgram.client.KernelCommNew;
import com.apashnov.cwgram.client.model.tl.TLRequestMessagesGetDialogsNew;
import com.apashnov.cwgram.client.model.tl.TLRequestMessagesGetHistoryNew;
import com.apashnov.cwgram.cw.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.input.peer.TLInputPeerChannel;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.messages.TLAbsMessages;
import org.telegram.api.messages.dialogs.TLDialogs;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;

import java.time.LocalTime;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static com.apashnov.cwgram.cw.CwActionHelper.goToMainMenuThanRedDefThanGoingAttack;
import static com.apashnov.cwgram.cw.CwActionHelper.sendFlagThanGoingAttack;

@Component
public class GetterFlagHandler implements CwHandler {

    @Autowired
    private FlagStorage flagStorage;

    @Value("${cw.group.red.alert.id}")
    private int redAlertId;
    private long redAlertAccessHash;

    private TLUser chatWarsBot;

    private Lock notifier;
    private Condition condition;
    private String uniqueName;

    @Override
    public void handle(Warrior warrior, KernelCommNew kernelComm, String uniqueName) {
        this.uniqueName = uniqueName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(uniqueName + "started");
                TLRequestMessagesGetDialogsNew dialogsNew = new TLRequestMessagesGetDialogsNew(0, -1, 100);
                TLDialogs tlDialogs = null;
                try {
                    tlDialogs = kernelComm.getApi().doRpcCall(dialogsNew);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TLAbsChat redAlertLegion = tlDialogs.getChats().stream().filter(c -> c.getId() == redAlertId).findFirst().get();
                redAlertAccessHash = ((TLChannel) redAlertLegion).getAccessHash();
                chatWarsBot = (tlDialogs.getUsers()).stream().filter((TLAbsUser c) -> ((TLUser) c).getUserName().equals("ChatWarsBot"))
                        .findFirst().map(c -> ((TLUser) c)).get();

                while (true) {
                    try {
                        waitUntilWaked(notifier, condition);
                        System.out.println(uniqueName + " waked to get flag");

                        goToMainMenuThanRedDefThanGoingAttack(kernelComm, chatWarsBot);
                        String currentFlag = CwConstants.BTN_RED_FLAG;
                        while (notRegimeNoise()) {
//                        while (true) {
                            System.out.println(uniqueName+"currentFlag -> " + currentFlag);
                            System.out.println(uniqueName+"going to solve flag");
                            findCommandsAndSolve(kernelComm);
                            System.out.println(uniqueName+"solved flag");
                            String flag;
                            if (WarriorKind.AGGRESSOR == warrior.getKind()) {
                                System.out.println(uniqueName+" going to get atk flag");
                                flag = atcFlag;
                                System.out.println(uniqueName+" got atk flag -> " + flag);
                            } else {
                                System.out.println(uniqueName+" going to get def flag");
                                flag = defFlag;
                                System.out.println(uniqueName+" got def flag -> " + flag);
                            }
                            if (flag == null || flag == currentFlag) {
                                try {
                                    Thread.sleep(3075);
                                    continue;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                currentFlag = flag;
                                System.out.println(uniqueName+ "going to send flag -> " + currentFlag);
                                sendFlagThanGoingAttack(currentFlag, kernelComm, chatWarsBot);
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

    private void findCommandsAndSolve(KernelCommNew kernelComm) throws Exception {
        TLInputPeerChannel peer = new TLInputPeerChannel();
        peer.setChannelId(redAlertId);
        peer.setAccessHash(redAlertAccessHash);
        TLRequestMessagesGetHistoryNew history = new TLRequestMessagesGetHistoryNew(peer, 0, Integer.MIN_VALUE, 6);
        TLAbsMessages tlAbsMessages = kernelComm.doRpcCallSync(history);
        if (tlAbsMessages != null) {
            boolean atc = true;
            boolean def = true;
            boolean full = true;
            for (TLAbsMessage tlAbsMessage : tlAbsMessages.getMessages()) {
                if (tlAbsMessage instanceof TLMessage) {
                    TLMessage message = (TLMessage) tlAbsMessage;
                    String text = message.getMessage().trim();
                    System.out.println("findCommandsAndSolve#text -> " + text);
                    if (!text.isEmpty()) {
                        switch (text.charAt(0)) {
                            case 'a':
                            case 'A':
                            case 'а':
                            case 'А':
                                if(atc) {
                                    atc = false;
                                    solveAtc(text);
                                }
                                break;
                            case 'д':
                            case 'Д':
                            case 'd':
                            case 'D':
                                if(def) {
                                    def = false;
                                    solveDef(text);
                                }
                                break;
                            case 'ф':
                            case 'Ф':
                                if(full) {
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
        }
    }

    private void solveFull(String text) {
        System.out.println("solveFull#text -> " + text);
        String flag = solve(text);
        atcFlag = flag;
        defFlag = flag;
        flagStorage.setAttack(flag);
        flagStorage.setDefend(flag);
    }

    private void solveDef(String text) {
        System.out.println("solveDef#text -> " + text);
        String flag = solve(text);
        defFlag = flag;
        flagStorage.setDefend(flag);
    }

    private void solveAtc(String text) {
        System.out.println("solveAtc#text -> " + text);
        String flag = solve(text);
        atcFlag = flag;
        flagStorage.setAttack(flag);
    }

    private String solve(String text) {
        String t = text.toLowerCase();
        System.out.println("solve#text -> " + text);
        String result = CwConstants.BTN_RED_FLAG;
        if(t.contains("+б")) {
            result = CwConstants.BTN_WHITE_FLAG;
        } else if(t.contains("+су")) {
            result = CwConstants.BTN_GLOOMY_FLAG;
        } else if(t.contains("+с")) {
            result = CwConstants.BTN_BLUE_FLAG;
        } else if(t.contains("+ч")) {
            result = CwConstants.BTN_BLACK_FLAG;
        } else if(t.contains("+к")) {
            result = CwConstants.BTN_RED_FLAG;
        } else if(t.contains("+ж")) {
            result = CwConstants.BTN_YELLOW_FLAG;
        } else if(t.contains("+м")) {
            result = CwConstants.BTN_MINT_FLAG;
        } else if(t.contains("+г")) {
            result = CwConstants.BTN_MOUNT_FLAG;
        } else if(t.contains("+л")) {
            result = CwConstants.BTN_FOREST_FLAG;
        }
        System.out.println("solve#result -> " + result);
        return result;
    }


    public static boolean notRegimeNoise() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        switch (hour) {
            case 0:
            case 4:
            case 8:
            case 12:
            case 16:
            case 20:
                return false;
        }
        return true;
    }

    private void goIntoDefense(Warrior warrior, IKernelComm kernelComm) {
        //todo::
//        kernelComm.sendMessage(user, message);
//        kernelComm.performMarkAsRead(user, 0);
    }
    @Autowired
    public void setNotifier(Notifier notifier) {
        this.notifier = notifier.getLock(this.getClass());
        this.condition = notifier.getCondition(this.getClass());
    }
}