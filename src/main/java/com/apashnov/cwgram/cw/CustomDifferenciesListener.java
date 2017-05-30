package com.apashnov.cwgram.cw;

import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.client.UpdatesStorage.SpecificStorage;
import com.apashnov.cwgram.cw.handler.CwHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.api.functions.updates.TLRequestUpdatesGetChannelDifference;
import org.telegram.api.functions.updates.TLRequestUpdatesGetDifference;
import org.telegram.api.functions.updates.TLRequestUpdatesGetState;
import org.telegram.api.input.chat.TLAbsInputChannel;
import org.telegram.api.input.chat.TLInputChannel;
import org.telegram.api.updates.TLUpdatesState;
import org.telegram.api.updates.difference.TLAbsDifference;
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
    private Properties prop;

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
                        log(uniqueName, "date=", date, "pts=", pts, "qts=", qts, "seq=", seq, "unreadCount=", unreadCount);

                        TLRequestUpdatesGetDifference diff = new TLRequestUpdatesGetDifference();
                        diff.setDate(date);
                        diff.setPts(pts);
                        diff.setQts(qts);
                        diff.setPtsTotalLimit(100);
                        TLAbsDifference tlAbsDifference = kernelComm.doRpcCallSync(diff);
                        log(uniqueName, "debug point 2", tlAbsDifference);

                        TLRequestUpdatesGetChannelDifference channelDiff = new TLRequestUpdatesGetChannelDifference();
                        TLInputChannel inputChannel = new TLInputChannel();
                        inputChannel.setChannelId();
                        inputChannel.setAccessHash(); //todo: check for redAlert and ChatWars

                        channelDiff.setChannel(inputChannel);
                        channelDiff.setPts(pts);
//                        channelDiff.setQts(qts);
                        channelDiff.setLimit(100);


                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }


        }).start();
    }
}
