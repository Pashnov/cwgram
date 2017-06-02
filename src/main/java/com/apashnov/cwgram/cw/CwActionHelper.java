package com.apashnov.cwgram.cw;

import com.apashnov.cwgram.client.UpdatesStorage.SpecificStorage;
import com.apashnov.cwgram.client.model.User;
import com.apashnov.cwgram.client.model.tl.TLRequestMessagesGetDialogsNew;
import org.jetbrains.annotations.NotNull;
import org.telegram.api.engine.RpcException;
import org.telegram.api.keyboard.TLKeyboardButtonRow;
import org.telegram.api.keyboard.button.TLAbsKeyboardButton;
import org.telegram.api.keyboard.replymarkup.TLReplayKeyboardMarkup;
import org.telegram.api.message.TLMessage;
import org.telegram.api.messages.dialogs.TLDialogs;
import org.telegram.api.peer.TLPeerChat;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.structure.IUser;

import java.util.List;
import java.util.stream.Collectors;

import static com.apashnov.cwgram.Constants.CHAT_WARS_ID;
import static com.apashnov.cwgram.Constants.CW_CAPTCHA_BOT_ID;
import static com.apashnov.cwgram.cw.CustomLogger.log;
import static com.apashnov.cwgram.cw.CwConstants.*;

public class CwActionHelper {


    public static IUser convert(TLUser tlUser) {
        return new User(tlUser.getId(), tlUser.getAccessHash());
    }

    public static void goToMainMenuThanRedDefThanGoingAttack(IKernelComm kernelComm, TLUser chatWarsBot, SpecificStorage specificStorage, String uniqueName) throws Exception {
        log(uniqueName, " in goToMainMenuThanRedDefThanGoingAttack");
        sendMessageChatWars(uniqueName, kernelComm, convert(chatWarsBot), "/report", specificStorage);
        log(uniqueName, "1_sent '/report'");
        List<TLMessage> messagesCW = waitResponse(specificStorage, chatWarsBot, uniqueName);
        log(uniqueName, "2_msgCW -> " + toReadable(messagesCW));

        sendMessageChatWars(uniqueName, kernelComm, convert(chatWarsBot), BTN_TEXT_DEFENSE, specificStorage);
        log(uniqueName, "2_sent '" + BTN_TEXT_DEFENSE + "'");
        messagesCW = waitResponse(specificStorage, chatWarsBot, uniqueName);
        log(uniqueName, "3_msgCW -> " + toReadable(messagesCW));

        TLReplayKeyboardMarkup replyMarkup = (TLReplayKeyboardMarkup) (messagesCW.get(0)).getReplyMarkup();
        log(uniqueName, "3_replyMarkup" + toReadable(replyMarkup));
        boolean hasBtnWithText = hasBtnWithText(replyMarkup, BTN_RED_FLAG);

        if (hasBtnWithText) {
            sendMessageChatWars(uniqueName, kernelComm, convert(chatWarsBot), BTN_RED_FLAG, specificStorage);
            log(uniqueName, "2_sent '" + BTN_RED_FLAG + "'");
        }
        messagesCW = waitResponse(specificStorage, chatWarsBot, uniqueName);
        log(uniqueName, "4_msgCW -> " + toReadable(messagesCW));

        sendMessageChatWars(uniqueName, kernelComm, convert(chatWarsBot), BIN_TEXT_ATTACK, specificStorage);
        log(uniqueName, "5_sent '" + BIN_TEXT_ATTACK + "'");
        log(uniqueName, " out goToMainMenuThanRedDefThanGoingAttack");

    }

    public static String toReadable(List<TLMessage> messagesCW) {
        return messagesCW.stream().map(m -> m.getMessage()).collect(Collectors.joining(";")).replace("\n", "");
    }

    private static String toReadable(TLReplayKeyboardMarkup replyMarkup) {
        String result = "";
        if (replyMarkup != null) {
            for (TLKeyboardButtonRow row : replyMarkup.getRows()) {
                result += "_row:";
                for (TLAbsKeyboardButton btn : row.buttons) {
                    result += (",btn-> " + btn.getText());
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
            log(uniqueName, "waitResponse#, ChatWarsMsgs -> " + toReadable(chatWars));
            log(uniqueName, "waitResponse#, condition -> " + (chatWars.isEmpty() || chatWars.get(0).getFromId() != chatWarsBot.getId()));
        } while (chatWars.isEmpty() || chatWars.get(0).getFromId() != chatWarsBot.getId());
        return chatWars;
    }

    public static List<TLMessage> waitResponseCaptcha(SpecificStorage specificStorage, TLUser cwCaptchaBot, String uniqueName) throws InterruptedException, java.util.concurrent.ExecutionException, RpcException {
        List<TLMessage> cwCaptcha;
        do {
            Thread.sleep(3000);
            cwCaptcha = specificStorage.getCwCaptchaBotChat();
            log(uniqueName, "waitResponseCaptcha#, CaptchaBotMsgs -> " + toReadable(cwCaptcha));
            log(uniqueName, "waitResponseCaptcha#, condition -> " + (cwCaptcha.isEmpty() || cwCaptcha.get(0).getFromId() != cwCaptchaBot.getId()));
        } while (cwCaptcha.isEmpty() || cwCaptcha.get(0).getFromId() != cwCaptchaBot.getId());
        return cwCaptcha;
    }

    public static boolean hasBtnWithText(TLReplayKeyboardMarkup replyMarkup, String text) {
        if (replyMarkup != null) {
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
        log(uniqueName, "sendFlagThanGoingAttack, flag -> " + flag);
        sendMessageChatWars(uniqueName, kernelComm, convert(chatWarsBot), flag, specificStorage);
        log(uniqueName, "sendFlagThanGoingAttack#sent flag-> " + flag);
        waitResponse(specificStorage, chatWarsBot, uniqueName);
        sendMessageChatWars(uniqueName, kernelComm, convert(chatWarsBot), BIN_TEXT_ATTACK, specificStorage);
        log(uniqueName, "sendFlagThanGoingAttack#sent btn-> " + BIN_TEXT_ATTACK);
    }

    public static void sendMessageChatWars(String uniqueName, IKernelComm kernelComm, @NotNull IUser chatWars, @NotNull String message, SpecificStorage specificStorage) throws RpcException {
        log(uniqueName, "sendMessageChatWars#send -> " + message);
        kernelComm.sendMessage(chatWars, message);
        putMessageToStorageChatWars(kernelComm, message, specificStorage);
        kernelComm.performMarkAsRead(chatWars, 0);
    }

    public static void sendMessageCaptchaBot(String uniqueName, IKernelComm kernelComm, @NotNull IUser captchaBot, @NotNull String message, SpecificStorage specificStorage) throws RpcException {
        log(uniqueName, "sendMessageCaptchaBot#send -> " + message);
        kernelComm.sendMessage(captchaBot, message);
        putMessageToStorageCaptchaBot(kernelComm, message, specificStorage);
        kernelComm.performMarkAsRead(captchaBot, 0);
    }

    private static void putMessageToStorageChatWars(IKernelComm kernelComm, @NotNull String message, SpecificStorage specificStorage) {
        TLMessage msg = new TLMessage();
        msg.setMessage(message);
        msg.setFromId(kernelComm.getCurrentUserId());
        TLPeerChat toId = new TLPeerChat();
        toId.setId(CHAT_WARS_ID);
        msg.setToId(toId);
        specificStorage.putChatWars(msg);
    }

    private static void putMessageToStorageCaptchaBot(IKernelComm kernelComm, @NotNull String message, SpecificStorage specificStorage) {
        TLMessage msg = new TLMessage();
        msg.setMessage(message);
        msg.setFromId(kernelComm.getCurrentUserId());
        TLPeerChat toId = new TLPeerChat();
        toId.setId(CW_CAPTCHA_BOT_ID);
        msg.setToId(toId);
        specificStorage.putCwCaptchaBotChat(msg);
    }

    public static TLUser findChatWarsUser(IKernelComm kernelComm, String uniqueName) {
        log(uniqueName, "findChatWarsUser#started QuestHandler");
        TLRequestMessagesGetDialogsNew dialogsNew = new TLRequestMessagesGetDialogsNew(0, -1, 100);
        TLDialogs tlDialogs = null;
        do {
            try {
                tlDialogs = kernelComm.getApi().doRpcCall(dialogsNew);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (tlDialogs == null);
        return (tlDialogs.getUsers()).stream().filter((TLAbsUser c) -> ((TLUser) c).getUserName().equals("ChatWarsBot"))
                .findFirst().map(c -> ((TLUser) c)).get();
    }
}
