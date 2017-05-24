package com.apashnov.cwgram.cw;

import com.apashnov.cwgram.client.KernelCommNew;
import com.apashnov.cwgram.client.model.User;
import com.apashnov.cwgram.client.model.tl.TLRequestMessagesGetHistoryNew;
import org.telegram.api.engine.RpcException;
import org.telegram.api.input.peer.TLInputPeerUser;
import org.telegram.api.keyboard.TLKeyboardButtonRow;
import org.telegram.api.keyboard.button.TLAbsKeyboardButton;
import org.telegram.api.keyboard.replymarkup.TLReplayKeyboardMarkup;
import org.telegram.api.message.TLMessage;
import org.telegram.api.messages.TLAbsMessages;
import org.telegram.api.user.TLUser;
import org.telegram.bot.structure.IUser;

import static com.apashnov.cwgram.cw.CwConstants.BTN_RED_FLAG;
import static com.apashnov.cwgram.cw.CwConstants.BTN_TEXT_ATACK;
import static com.apashnov.cwgram.cw.CwConstants.BTN_TEXT_DEFENSE;


public class CwActionHelper {


    private static IUser convert(TLUser tlUser) {
        return new User(tlUser.getId(), tlUser.getAccessHash());
    }

    public static void goToMainMenuThanRedDefThanGoingAttack(KernelCommNew kernelComm, TLUser chatWarsBot) throws Exception {
        kernelComm.sendMessage(convert(chatWarsBot), "/report");
        TLAbsMessages tlAbsMessages = doRpcCallHistoryAndWaitResponse(kernelComm, chatWarsBot, chatWarsBot.getId(), chatWarsBot.getAccessHash());

        TLMessage tlMessage = (TLMessage) tlAbsMessages.getMessages().get(0);
//        while (!hasBtnWithText(replyMarkup, BTN_TEXT_DEFENSE)){
        kernelComm.sendMessage(convert(chatWarsBot), BTN_TEXT_DEFENSE);
//            TLRequestMessagesSendMessage request = new TLRequestMessagesSendMessage();
//            request.setPeer(TLFactory.createTLInputPeer(convert(chatWarsBot), null));
//            request.setMessage(BTN_TEXT_DEFENSE);
//            request.setRandomId(System.currentTimeMillis());
//            TLAbsUpdates tlAbsUpdates = kernelComm.doRpcCallSync(request);
//            System.out.println(tlAbsUpdates);
//        }
        tlAbsMessages = doRpcCallHistoryAndWaitResponse(kernelComm, chatWarsBot, chatWarsBot.getId(), chatWarsBot.getAccessHash());
        if(hasBtnWithText((TLReplayKeyboardMarkup) ((TLMessage) tlAbsMessages.getMessages().get(0)).getReplyMarkup(), BTN_RED_FLAG)){
            kernelComm.sendMessage(convert(chatWarsBot), BTN_RED_FLAG);
        }
        tlAbsMessages = doRpcCallHistoryAndWaitResponse(kernelComm, chatWarsBot, chatWarsBot.getId(), chatWarsBot.getAccessHash());
        kernelComm.sendMessage(convert(chatWarsBot), BTN_TEXT_ATACK);

    }

    private static ThreadLocal<Integer> safeState = new ThreadLocal<>();
    private static int get2till6() {
        Integer state = safeState.get();
        if(state == null){
            state = 2;
        }
        if(state == 2){
            safeState.set(3);
            return 2;
        }
        if(state == 3){
            safeState.set(4);
            return 3;
        }
        if(state == 4){
            safeState.set(5);
            return 4;
        }
        if(state == 5){
            safeState.set(6);
            return 5;
        }
        if(state == 6){
            safeState.set(2);
            return 6;
        }
        return 6;
    }

    private static TLAbsMessages doRpcCallHistoryAndWaitResponse(KernelCommNew kernelComm, TLUser chatWarsBot, int id, long accessHash) throws InterruptedException, java.util.concurrent.ExecutionException, RpcException {
        TLAbsMessages tlAbsMessages;
        do {
            Thread.sleep(3000);
            TLInputPeerUser peer = new TLInputPeerUser();
            peer.setUserId(id);
            peer.setAccessHash(accessHash);
            tlAbsMessages = kernelComm.doRpcCallSync(new TLRequestMessagesGetHistoryNew(peer, 0, -1, get2till6()));
        } while (((TLMessage) tlAbsMessages.getMessages().get(0)).getId() == chatWarsBot.getId());
        return tlAbsMessages;
    }

    private static boolean hasBtnWithText(TLReplayKeyboardMarkup replyMarkup, String text) {
        if(replyMarkup != null) {
            for (TLKeyboardButtonRow row : replyMarkup.getRows()) {
                for (TLAbsKeyboardButton btn : row.buttons) {
                    if (btn.getText().equals(text)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void sendFlagThanGoingAttack(String flag, KernelCommNew kernelComm, TLUser chatWarsBot) throws Exception {
        kernelComm.sendMessage(convert(chatWarsBot), flag);
        doRpcCallHistoryAndWaitResponse(kernelComm, chatWarsBot, chatWarsBot.getId(), chatWarsBot.getAccessHash());
        kernelComm.sendMessage(convert(chatWarsBot), BTN_TEXT_ATACK);
    }
}
