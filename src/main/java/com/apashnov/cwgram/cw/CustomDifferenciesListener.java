package com.apashnov.cwgram.cw;

import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.client.UpdatesStorage.SpecificStorage;
import com.apashnov.cwgram.cw.handler.CwHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.api.functions.updates.TLRequestUpdatesGetChannelDifference;
import org.telegram.api.functions.updates.TLRequestUpdatesGetDifference;
import org.telegram.api.functions.updates.TLRequestUpdatesGetState;
import org.telegram.api.input.chat.TLAbsInputChannel;
import org.telegram.api.input.chat.TLInputChannel;
import org.telegram.api.message.TLMessage;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.updates.TLUpdatesState;
import org.telegram.api.updates.difference.TLAbsDifference;
import org.telegram.api.updates.difference.TLDifference;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.apashnov.cwgram.Constants.*;
import static com.apashnov.cwgram.cw.CustomLogger.log;
import static com.apashnov.cwgram.cw.handler.QuestHandler.findChatWarsUser;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomDifferenciesListener implements CwHandler{

    private SpecificStorage storage;

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
                log(uniqueName,"run#CustomDifferenciesListener");

                while (true) {
                    try {
                        log(uniqueName, "getDiff");
                        TLRequestUpdatesGetState state = new TLRequestUpdatesGetState();
                        TLUpdatesState tlUpdatesState = kernelComm.doRpcCallSync(state);
                        System.out.println("debug point 1, tlUpdatesState = " + tlUpdatesState);
                        log(uniqueName, "debug point, tlUpdatesState = " + tlUpdatesState);
                        int date = tlUpdatesState.getDate();
                        int pts = tlUpdatesState.getPts();
                        int qts = tlUpdatesState.getQts();
                        int seq = tlUpdatesState.getSeq();
                        int unreadCount = tlUpdatesState.getUnreadCount();
                        log(uniqueName, "date=", date, "pts=", pts, "qts=", qts, "seq=", seq);
                        log(uniqueName, "old =", CustomDifferenciesListener.this.date, "pts=", CustomDifferenciesListener.this.pts, "qts=", CustomDifferenciesListener.this.qts, "seq=", CustomDifferenciesListener.this.seq);
                        System.out.println("date old, new " + CustomDifferenciesListener.this.date + ", " +date);
                        System.out.println("pts old, new " + CustomDifferenciesListener.this.pts + ", " +pts);
                        System.out.println("qts old, new " + CustomDifferenciesListener.this.qts + ", " +qts);
                        System.out.println("seq old, new " + CustomDifferenciesListener.this.seq + ", " +seq);

                        if(CustomDifferenciesListener.this.date == 0){
                            CustomDifferenciesListener.this.date = date;
                            CustomDifferenciesListener.this.pts = pts;
//                            CustomDifferenciesListener.this.qts = qts;
                            CustomDifferenciesListener.this.seq = seq;
                        }
                        CustomDifferenciesListener.this.date = date;

                        if(CustomDifferenciesListener.this.pts != pts || CustomDifferenciesListener.this.seq != seq) {

//                            CustomDifferenciesListener.this.date = date;
//                            CustomDifferenciesListener.this.pts = pts;
//                            CustomDifferenciesListener.this.qts = qts; // not important
//                            CustomDifferenciesListener.this.seq = seq;

                            TLRequestUpdatesGetDifference diff = new TLRequestUpdatesGetDifference();
                            diff.setDate(CustomDifferenciesListener.this.date);
                            diff.setPts(CustomDifferenciesListener.this.pts);
                            diff.setQts(CustomDifferenciesListener.this.qts);
//                        diff.setQts(0);
//                            diff.setPtsTotalLimit(100);
                            TLAbsDifference tlAbsDifference = kernelComm.doRpcCallSync(diff);
                            log(uniqueName, "debug point 2", tlAbsDifference);
                            if(tlAbsDifference instanceof TLDifference){
                                TLDifference tlDifference = (TLDifference) tlAbsDifference;
                                TLUpdatesState state1 = tlDifference.getState();
//                                log(uniqueName, state1);
                                CustomDifferenciesListener.this.date = state1.getDate();
                                CustomDifferenciesListener.this.pts = state1.getPts();
                                CustomDifferenciesListener.this.qts = state1.getQts(); // not important
                                CustomDifferenciesListener.this.seq = state1.getSeq();
                                // 1-2-1
                                tlDifference.getNewMessages().stream()
                                        .filter(msg -> msg instanceof TLMessage)
                                        .map(msg -> (TLMessage)msg )
                                        .forEachOrdered(CustomDifferenciesListener.this::onMsg);
                                // 1-2-*
                                tlAbsDifference.getOtherUpdates().stream()
                                        .filter(upd -> upd instanceof TLUpdateChannelNewMessage)
                                        .map(upd -> ((TLUpdateChannelNewMessage)upd).getMessage())
                                        .filter(tlAbsMessage -> tlAbsMessage instanceof TLMessage)
                                        .map((tlAbsMessage -> (TLMessage)tlAbsMessage))
                                        .forEachOrdered(CustomDifferenciesListener.this::onMsg);
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
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }


        }).start();
    }

    private void onMsg(TLMessage message) {
        log(uniqueName,"onMsg#, from msg -> "+message.getMessage().replace("\n", ""),", fromId -> "+message.getFromId());

        if (message.hasFromId()) { // while state in static is stored
//            kernelComm.performMarkAsRead(new User(kernelComm.getCurrentUserId(), kernelComm.), 0);
            switch (message.getFromId()){
                case CHAT_WARS_ID:
                    log(uniqueName,"onMsg#, CHAT_WARS_ID, from msg -> " + message.getMessage().replace("\n", ""));
                    specificStorage.putChatWars(message);
                    break;
            }
            switch (message.getToId().getId()){
                case RED_ALERT_ID:
                    log(uniqueName,"onMsg#, RED_ALERT_ID, to msg -> " + message.getMessage().replace("\n", ""));
                    specificStorage.putRedAlert(message);
                    break;
                case CHAT_WARS_ID:
                    specificStorage.putChatWars(message);
                    log(uniqueName,"onMsg#, CHAT_WARS_ID, to msg -> " + message.getMessage().replace("\n", ""));
                    break;
            }
//            final IUser user = databaseManager.getUserById(message.getFromId());
//            if (user != null) {
//                this.tlMessageHandler.onTLMessage(message);
//            }
        }
    }
}
