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
import org.telegram.api.engine.RpcException;
import org.telegram.api.message.TLMessage;
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
import static com.apashnov.cwgram.cw.CustomLogger.log;
import static com.apashnov.cwgram.cw.CwActionHelper.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class QuestHandler implements CwHandler {

    @Autowired
    private UpdatesStorage updatesStorage;

    private TLUser chatWarsBot;
    private TLUser cwCaptchaBot;

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
                log(uniqueName, "QuestHandler# starting");
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    chatWarsBot = UserChatStorage.getChatWarsBot(uniqueName);
                    cwCaptchaBot = UserChatStorage.getCaptchaBot(uniqueName);
                } while (chatWarsBot == null || cwCaptchaBot == null);

                log(uniqueName, "QuestHandler# started");

                while (true) {
                    try {
                        waitUntilWaked(notifier, condition);
                        log(uniqueName, "run# waked to go in quests");
                        //todo: add change equip
                        List<String> quests = getQuest();
                        for (String quest : quests) {
                            clickQuest(kernelComm);
                            clickSpecificQuest(kernelComm, quest);

                            Thread.sleep(6 * 60 * 1000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }


        }).start();
    }

    private void clickSpecificQuest(IKernelComm kernelComm, String quest) throws RpcException, InterruptedException, ExecutionException {
        sendMessageChatWars(uniqueName, kernelComm, convert(chatWarsBot), quest, specificStorage);
        log(uniqueName, "clickSpecificQuest#clicked -> " + quest);
        List<TLMessage> tlMessages = waitResponse(specificStorage, chatWarsBot, uniqueName);
        String msg = tlMessages.get(0).getMessage();
        log(uniqueName, "clickSpecificQuest#msg = " + msg);
        if (msg.contains("На выходе из замка охрана")) {

            sendMessageCaptchaBot(uniqueName, kernelComm, convert(cwCaptchaBot), msg, specificStorage);

            String buttonText;
            Thread.sleep(4500);
            tlMessages = waitResponseCaptcha(specificStorage, cwCaptchaBot, uniqueName);
            buttonText = tlMessages.get(0).getMessage();

//            String buttonText = captchaSolver.solve(msg);
//            if (buttonText != null) {
            sendMessageChatWars(uniqueName, kernelComm, convert(chatWarsBot), buttonText, specificStorage);
//            } else {
//                TLReplayKeyboardMarkup replyMarkup;
//                do {
//                    Thread.sleep(5 * 60 * 1000);
//                    tlMessages = waitResponse(specificStorage, chatWarsBot, uniqueName );
//                    replyMarkup = (TLReplayKeyboardMarkup) tlMessages.get(0).getReplyMarkup();
//                } while (hasBtnWithText(replyMarkup, "\uD83E\uDDC0") || hasBtnWithText(replyMarkup, "\uD83D\uDC3F"));
//                to-do use captchawator
//            }
        }
        log(uniqueName, "clickSpecificQuest# out");
    }

    private void clickQuest(IKernelComm kernelComm) throws RpcException, InterruptedException, ExecutionException {
        sendMessageChatWars(uniqueName, kernelComm, convert(chatWarsBot), BTN_QUEST, specificStorage);
        log(uniqueName, "clickQuest# clicked quest");
        waitResponse(specificStorage, chatWarsBot, uniqueName);
    }

    private List<String> getQuest() {
        LocalTime time = LocalTime.now();
        int hour = time.getHour();
        if (hour >= 0 && hour <= 7 && nightQuestsAllowed) {
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