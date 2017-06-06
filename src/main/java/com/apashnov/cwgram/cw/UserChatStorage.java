package com.apashnov.cwgram.cw;

import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.user.TLUser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserChatStorage {

    private static Map<String, TLChannel> uniqueNameRedAlertGroup = new ConcurrentHashMap<>();
    private static Map<String, TLUser> uniqueNameChatWarsBot = new ConcurrentHashMap<>();
    private static Map<String, TLUser> uniqueNameCaptchaBot = new ConcurrentHashMap<>();

    public static void setRedAlertGroup(String uniqueName, TLChannel redAlertGroup){
        uniqueNameRedAlertGroup.put(uniqueName, redAlertGroup);
    }

    public static void setChatWarsBot(String uniqueName, TLUser chatWarsBot){
        uniqueNameChatWarsBot.put(uniqueName, chatWarsBot);
    }

    public static void setCaptchaBot(String uniqueName, TLUser captchaBot){
        uniqueNameCaptchaBot.put(uniqueName, captchaBot);
    }

    public static TLChannel getRedAlertGroup(String uniqueName){
        return uniqueNameRedAlertGroup.get(uniqueName);
    }

    public static TLUser getChatWarsBot(String uniqueName){
        return uniqueNameChatWarsBot.get(uniqueName);
    }

    public static TLUser getCaptchaBot(String uniqueName){
        return uniqueNameCaptchaBot.get(uniqueName);
    }

}
