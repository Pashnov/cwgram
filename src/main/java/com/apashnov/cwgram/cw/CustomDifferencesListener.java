package com.apashnov.cwgram.cw;

import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.client.UpdatesStorage.SpecificStorage;
import com.apashnov.cwgram.cw.handler.CwHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.api.functions.updates.TLRequestUpdatesGetDifference;
import org.telegram.api.functions.updates.TLRequestUpdatesGetState;
import org.telegram.api.message.TLMessage;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.updates.TLUpdatesState;
import org.telegram.api.updates.difference.TLAbsDifference;
import org.telegram.api.updates.difference.TLDifference;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static com.apashnov.cwgram.Constants.*;
import static com.apashnov.cwgram.cw.CustomLogger.log;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomDifferencesListener implements CwHandler {

    @Autowired
    UpdatesStorage updatesStorage;

    private TLUser chatWarsBot;

    private Lock notifier;
    private Condition condition;
    private String uniqueName;
    private String phoneNumber;
    private SpecificStorage specificStorage;
    int date;
    int pts;
    int qts;
    int seq;

    @Override
    public void handle(Warrior warrior, IKernelComm kernelComm, String uniqueName, String phoneNumber) {
        this.uniqueName = uniqueName;
        this.phoneNumber = phoneNumber;
        this.specificStorage = updatesStorage.get(phoneNumber);

        new Thread(new Runnable() {
            @Override
            public void run() {
                log(uniqueName, "run#CustomDifferenciesListener");

                while (true) {
                    try {
//                        log(uniqueName, "getDiff");
                        TLRequestUpdatesGetState state = new TLRequestUpdatesGetState();
                        TLUpdatesState tlUpdatesState = kernelComm.doRpcCallSync(state);
//                        System.out.println("debug point 1, tlUpdatesState = " + tlUpdatesState);
//                        log(uniqueName, "debug point, tlUpdatesState = " + tlUpdatesState);
                        int date = tlUpdatesState.getDate();
                        int pts = tlUpdatesState.getPts();
                        int qts = tlUpdatesState.getQts();
                        int seq = tlUpdatesState.getSeq();
//                        log(uniqueName, "date=", date, "pts=", pts, "qts=", qts, "seq=", seq);
//                        log(uniqueName, "old =", CustomDifferencesListener.this.date, "pts=", CustomDifferencesListener.this.pts, "qts=", CustomDifferencesListener.this.qts, "seq=", CustomDifferencesListener.this.seq);
//                        System.out.println("date old, new " + CustomDifferencesListener.this.date + ", " +date);
//                        System.out.println("pts old, new " + CustomDifferencesListener.this.pts + ", " +pts);
//                        System.out.println("qts old, new " + CustomDifferencesListener.this.qts + ", " +qts);
//                        System.out.println("seq old, new " + CustomDifferencesListener.this.seq + ", " +seq);

                        if (CustomDifferencesListener.this.date == 0) {
                            CustomDifferencesListener.this.date = date;
                            CustomDifferencesListener.this.pts = pts;
                            CustomDifferencesListener.this.seq = seq;
                        }
                        CustomDifferencesListener.this.date = date;

                        if (CustomDifferencesListener.this.pts != pts || CustomDifferencesListener.this.seq != seq) {
                            log(uniqueName, "CustomDifferencesListener#run going to get Diff ");
                            TLRequestUpdatesGetDifference diff = new TLRequestUpdatesGetDifference();
                            diff.setDate(CustomDifferencesListener.this.date);
                            diff.setPts(CustomDifferencesListener.this.pts);
                            diff.setQts(CustomDifferencesListener.this.qts);
//                        diff.setQts(0);
//                            diff.setPtsTotalLimit(100);
                            TLAbsDifference tlAbsDifference = kernelComm.doRpcCallSync(diff);
//                            log(uniqueName, "debug point 2", tlAbsDifference);
                            if (tlAbsDifference instanceof TLDifference) {
                                TLDifference tlDifference = (TLDifference) tlAbsDifference;
                                TLUpdatesState state1 = tlDifference.getState();
//                                log(uniqueName, state1);
                                CustomDifferencesListener.this.date = state1.getDate();
                                CustomDifferencesListener.this.pts = state1.getPts();
                                CustomDifferencesListener.this.qts = state1.getQts(); // not important
                                CustomDifferencesListener.this.seq = state1.getSeq();
                                // 1-2-1
                                tlDifference.getNewMessages().stream()
                                        .filter(msg -> msg instanceof TLMessage)
                                        .map(msg -> (TLMessage) msg)
                                        .forEachOrdered(CustomDifferencesListener.this::onMsg);
                                // 1-2-*
                                tlAbsDifference.getOtherUpdates().stream()
                                        .filter(upd -> upd instanceof TLUpdateChannelNewMessage)
                                        .map(upd -> ((TLUpdateChannelNewMessage) upd).getMessage())
                                        .filter(tlAbsMessage -> tlAbsMessage instanceof TLMessage)
                                        .map((tlAbsMessage -> (TLMessage) tlAbsMessage))
                                        .forEachOrdered(CustomDifferencesListener.this::onMsg);
                            } else {
                                log(uniqueName, "CustomDifferencesListener#run not TLDifference = " + tlAbsDifference.getClass());
                            }

//                            TLRequestUpdatesGetChannelDifference channelDiff = new TLRequestUpdatesGetChannelDifference();
//                            TLInputChannel inputChannel = new TLInputChannel();
//                            inputChannel.setChannelId();
//                            inputChannel.setAccessHash(); //todo: check for redAlert and ChatWars

//                            channelDiff.setChannel(inputChannel);
//                            channelDiff.setPts(pts);
//                            channelDiff.setQts(qts);
//                            channelDiff.setLimit(100);

                        }
                        Thread.sleep(777);
                    } catch (Exception e) {
                        log(uniqueName, "CustomDifferencesListener#run e = " + e);
                        e.printStackTrace();
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

            }


        }).start();
    }

    private void onMsg(TLMessage message) {
//        log(uniqueName,"onMsg#, from msg -> "+message.getMessage().replace("\n", ""),", fromId -> "+message.getFromId());

        if (message.hasFromId()) {
            switch (message.getFromId()) {
                case CHAT_WARS_ID:
                    log(uniqueName, "onMsg#, CHAT_WARS_ID, from msg -> " + message.getMessage().replace("\n", ""));
                    specificStorage.putChatWars(message);
                    break;
                case CW_CAPTCHA_BOT_ID:
                    log(uniqueName, "onMsg#, CW_CAPTCHA_BOT_ID, from msg -> " + message.getMessage().replace("\n", ""));
                    specificStorage.putCwCaptchaBotChat(message);
                    break;
            }
            switch (message.getToId().getId()) {
                case RED_ALERT_ID:
                    log(uniqueName, "onMsg#, RED_ALERT_ID, to msg -> " + message.getMessage().replace("\n", ""));
                    specificStorage.putRedAlert(message);
                    break;
                    // now this messages are added on sending step
//                case CHAT_WARS_ID:
//                    specificStorage.putChatWars(message);
//                    log(uniqueName, "onMsg#, CHAT_WARS_ID, to msg -> " + message.getMessage().replace("\n", ""));
//                    break;
//                case CW_CAPTCHA_BOT_ID:
//                    specificStorage.putCwCaptchaBotChat(message);
//                    log(uniqueName, "onMsg#, CW_CAPTCHA_BOT_ID, to msg -> " + message.getMessage().replace("\n", ""));
//                    break;
            }
//            final IUser user = databaseManager.getUserById(message.getFromId());
//            if (user != null) {
//                this.tlMessageHandler.onTLMessage(message);
//            }
        }
    }
}
