package com.apashnov.cwgram;


import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final int APIKEY = 0;// your api key
    public static final String APIHASH = ""; // your api hash
    public static final String PHONENUMBER = ""; // Your phone number

    public static final int CHAT_WARS_ID = 265204902;
    public static final int RED_ALERT_ID = 1112569524;

    //keys
    public static final String KEY_IS_DEFENDER = "cw.defender";
    public static final String KEY_QUESTS = "cw.quests";
    public static final String KEY_NIGHT_QUESTS = "cw.night.quests";
    public static final String KEY_NIGHT_QUESTS_ALLOWED = "cw.night.quests.allowed";

    //keywords
    public static final String KEY_FOREST = "forest";
    public static final String KEY_CAVE = "cave";
    public static final String KEY_CARAVAN = "caravan";

    //value of keywords
    public static final String BTN_QUEST = "\uD83D\uDDFA Квесты";
    public static final String BTN_FOREST = "\uD83C\uDF32Лес";
    public static final String BTN_CAVE = "\uD83D\uDD78Пещера";
    public static final String BTN_CARAVAN = "\uD83D\uDC2BГРАБИТЬ КОРОВАНЫ";

    public static final String BTN_TEXT_BACK = "\uD83D\uDC2BГРАБИТЬ КОРОВАНЫ";

    public static final Map<String, String> QUESTS;

    static {
        QUESTS = new HashMap();
        QUESTS.put(KEY_FOREST, BTN_FOREST);
        QUESTS.put(KEY_CAVE, BTN_CAVE);
        QUESTS.put(KEY_CARAVAN, BTN_CARAVAN);
    }

}
