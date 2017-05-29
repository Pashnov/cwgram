package com.apashnov.cwgram.cw;

import com.apashnov.cwgram.client.UpdatesStorage.SpecificStorage;
import com.apashnov.cwgram.client.model.User;
import org.jetbrains.annotations.NotNull;
import org.telegram.api.engine.RpcException;
import org.telegram.api.keyboard.TLKeyboardButtonRow;
import org.telegram.api.keyboard.button.TLAbsKeyboardButton;
import org.telegram.api.keyboard.replymarkup.TLReplayKeyboardMarkup;
import org.telegram.api.message.TLMessage;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.structure.IUser;

import java.util.List;
import java.util.stream.Collectors;

import static com.apashnov.cwgram.cw.CustomLogger.log;
import static com.apashnov.cwgram.cw.CwConstants.BTN_RED_FLAG;
import static com.apashnov.cwgram.cw.CwConstants.BTN_TEXT_ATACK;
import static com.apashnov.cwgram.cw.CwConstants.BTN_TEXT_DEFENSE;

public class CwActionHelper {


    public static IUser convert(TLUser tlUser) {
        return new User(tlUser.getId(), tlUser.getAccessHash());
    }

    public static void goToMainMenuThanRedDefThanGoingAttack(IKernelComm kernelComm, TLUser chatWarsBot, SpecificStorage specificStorage, String uniqueName) throws Exception {
        sendMessage(kernelComm,convert(chatWarsBot), "/report");
        log(uniqueName,"1_sent '/report'");
        List<TLMessage> messagesCW = waitResponse(specificStorage, chatWarsBot, uniqueName );
        log(uniqueName,"2_msgCW -> " + toReadable(messagesCW));

        sendMessage(kernelComm,convert(chatWarsBot), BTN_TEXT_DEFENSE);
        log(uniqueName,"2_sent '"+BTN_TEXT_DEFENSE+"'");
        messagesCW = waitResponse(specificStorage, chatWarsBot, uniqueName);
        log(uniqueName,"3_msgCW -> " + toReadable(messagesCW));

        TLReplayKeyboardMarkup replyMarkup = (TLReplayKeyboardMarkup) (messagesCW.get(0)).getReplyMarkup();
        log(uniqueName,"3_replyMarkup" + toReadable(replyMarkup));
        boolean hasBtnWithText = hasBtnWithText(replyMarkup, BTN_RED_FLAG);

        if(hasBtnWithText){
            sendMessage(kernelComm,convert(chatWarsBot), BTN_RED_FLAG);
            log(uniqueName,"2_sent '"+BTN_RED_FLAG+"'");
        }
        messagesCW = waitResponse(specificStorage, chatWarsBot, uniqueName);
        log(uniqueName,"4_msgCW -> " + toReadable(messagesCW));

        sendMessage(kernelComm,convert(chatWarsBot), BTN_TEXT_ATACK);
        log(uniqueName,"5_sent '"+BTN_TEXT_ATACK+"'");
    }

    public static String toReadable(List<TLMessage> messagesCW) {
        return messagesCW.stream().map(m -> m.getMessage()).collect(Collectors.joining(";"));
    }

    private static String toReadable(TLReplayKeyboardMarkup replyMarkup) {
        String result = "";
        if(replyMarkup != null) {
            for (TLKeyboardButtonRow row : replyMarkup.getRows()) {
                result += "_row:";
                for (TLAbsKeyboardButton btn : row.buttons) {
                    result += (",btn-> " +btn.getText());
                }
            }
        } else {
            result += "null";
        }
        return result;
    }

    public static List<TLMessage> waitResponse(SpecificStorage specificStorage, TLUser chatWarsBot, String uniqueName) throws InterruptedException, java.util.concurrent.ExecutionException, RpcException {
        List<TLMessage> chatWars;
        do {
            Thread.sleep(3000);
            chatWars = specificStorage.getChatWars();
            log(uniqueName,"waitResponse#, msgs -> " + toReadable(chatWars));
            log(uniqueName,"waitResponse#, condition -> " + (chatWars.isEmpty() || chatWars.get(0).getFromId() != chatWarsBot.getId()));
        } while (chatWars.isEmpty() || chatWars.get(0).getFromId() != chatWarsBot.getId());
        return chatWars;
    }

    public static boolean hasBtnWithText(TLReplayKeyboardMarkup replyMarkup, String text) {
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

    public static void sendFlagThanGoingAttack(String flag, IKernelComm kernelComm, TLUser chatWarsBot, SpecificStorage specificStorage, String uniqueName) throws Exception {
        log(uniqueName,"sendFlagThanGoingAttack, flag -> " + flag);
        sendMessage(kernelComm,convert(chatWarsBot), flag);
        log(uniqueName, "sent flag-> " + flag);
        waitResponse(specificStorage, chatWarsBot, uniqueName);
        sendMessage(kernelComm,convert(chatWarsBot), BTN_TEXT_ATACK);
        log(uniqueName, "sent btn-> " + BTN_TEXT_ATACK);
    }

    public static void sendMessage(IKernelComm kernelComm, @NotNull IUser user, @NotNull String message) throws RpcException {
        kernelComm.sendMessage(user, message);
        kernelComm.performMarkAsRead(user, 0);
    }
}
