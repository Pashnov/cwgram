package com.apashnov.cwgram.cw.handler;

import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.client.UpdatesStorage.SpecificStorage;
import com.apashnov.cwgram.client.model.tl.TLRequestMessagesGetDialogsNew;
import com.apashnov.cwgram.cw.*;
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
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.apashnov.cwgram.Constants.*;
import static com.apashnov.cwgram.cw.CwActionHelper.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class QuestHandler implements CwHandler {

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
    private Properties prop;
    private List<String> quests;
    private List<String> nightQuests;
    private boolean nightQuestsAllowed;


    @Override
    public void handle(Warrior warrior, IKernelComm kernelComm, String uniqueName, String phoneNumber) {
        this.uniqueName = uniqueName;
        this.phoneNumber = phoneNumber;
        this.specificStorage = updatesStorage.get(phoneNumber);
        this.prop = warrior.getProperties();

        quests = Stream.of(prop.getProperty(KEY_QUESTS).split(",")).map(key -> QUESTS.get(key)).collect(Collectors.toList());
        nightQuests = Stream.of(prop.getProperty(KEY_NIGHT_QUESTS).split(",")).map(key -> QUESTS.get(key)).collect(Collectors.toList());
        nightQuestsAllowed = Boolean.valueOf(prop.getProperty(KEY_NIGHT_QUESTS_ALLOWED));

        new Thread(new Runnable() {
            @Override
            public void run() {

                chatWarsBot = findChatWarsUser(kernelComm, uniqueName);

                while (true) {
                    try {
                        waitUntilWaked(notifier, condition);
                        System.out.println(uniqueName + " waked to go in quests");
                        //todo: add change equip
                        List<String> quests = getQuest();
                        for (String quest : quests) {
                            clickQuest(kernelComm);
                            clickSpecificQuest(kernelComm, quest);

                            Thread.sleep(6 * 60 * 1000);
//                            if (flag == null || flag == currentFlag) {
//                                try {
//                                    Thread.sleep(3075);
//                                    continue;
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            } else {
//                                currentFlag = flag;
//                                System.out.println(uniqueName + "going to send flag -> " + currentFlag);
//                                sendFlagThanGoingAttack(currentFlag, kernelComm, chatWarsBot, specificStorage);
//                                try {
//                                    Thread.sleep(2075);
//                                    continue;
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }


        }).start();
    }

    public static TLUser findChatWarsUser(IKernelComm kernelComm, String uniqueName) {
        System.out.println(uniqueName + "started QuestHandler");
        TLRequestMessagesGetDialogsNew dialogsNew = new TLRequestMessagesGetDialogsNew(0, -1, 100);
        TLDialogs tlDialogs = null;
        try {
            tlDialogs = kernelComm.getApi().doRpcCall(dialogsNew);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (tlDialogs.getUsers()).stream().filter((TLAbsUser c) -> ((TLUser) c).getUserName().equals("ChatWarsBot"))
                .findFirst().map(c -> ((TLUser) c)).get();
    }

    private void clickSpecificQuest(IKernelComm kernelComm, String quest) throws RpcException, InterruptedException, ExecutionException {
        kernelComm.sendMessage(convert(chatWarsBot), quest);
        System.out.println(uniqueName+"clicked -> " + quest);
        List<TLMessage> tlMessages = waitResponse(specificStorage, chatWarsBot, uniqueName);
        String msg = tlMessages.get(0).getMessage();
        if(!msg.contains("Ты отправился искать")) {
            String buttonText = captchaSolver.solve(msg);
            if (buttonText != null) {
                kernelComm.sendMessage(convert(chatWarsBot), buttonText);
            } else {
                TLReplayKeyboardMarkup replyMarkup;
                do {
                    Thread.sleep(5 * 60 * 1000);
                    tlMessages = waitResponse(specificStorage, chatWarsBot, uniqueName );
                    replyMarkup = (TLReplayKeyboardMarkup) tlMessages.get(0).getReplyMarkup();
                } while (hasBtnWithText(replyMarkup, "\uD83E\uDDC0") || hasBtnWithText(replyMarkup, "\uD83D\uDC3F"));
                //todo use captchawator
            }
        }
    }

    private void clickQuest(IKernelComm kernelComm) throws RpcException, InterruptedException, ExecutionException {
        kernelComm.sendMessage(convert(chatWarsBot), BTN_QUEST);
        //todo read all above msg
        System.out.println(uniqueName+"clicked quest");
        waitResponse(specificStorage, chatWarsBot, uniqueName);
    }

    private List<String> getQuest() {
        LocalTime time = LocalTime.now();
        int hour = time.getHour();
        if(hour >= 0 && hour <= 7 && nightQuestsAllowed){
            return nightQuests;
        }
        return quests;
    }

    @Autowired
    public void setNotifier(Notifier notifier) {
        this.notifier = notifier.getLock(this.getClass());
        this.condition = notifier.getCondition(this.getClass());
    }
}