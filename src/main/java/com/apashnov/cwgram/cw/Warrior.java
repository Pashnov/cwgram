package com.apashnov.cwgram.cw;

import java.util.Properties;

public class Warrior {

    public static final String KEY_CW_DEFENDER = "cw.defender";

    private WarriorKind kind = WarriorKind.DEFENDER;

    public Warrior(Properties properties){
        if(properties.getProperty(KEY_CW_DEFENDER).equals("false")){
            kind = WarriorKind.AGGRESSOR;
        }
    }

    public WarriorKind getKind(){
        return kind;
    }

}
