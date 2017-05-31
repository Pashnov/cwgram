package com.apashnov.cwgram.cw;

import java.util.Properties;

import static com.apashnov.cwgram.Constants.KEY_IS_DEFENDER;

public class Warrior {

    private WarriorKind kind = WarriorKind.DEFENDER;
    private Properties properties;

    public Warrior(Properties properties){
        this.properties = properties;
        if(properties.getProperty(KEY_IS_DEFENDER).equals("false")){
            kind = WarriorKind.AGGRESSOR;
        }
    }

    public WarriorKind getKind(){
        return kind;
    }

    public Properties getProperties() {
        return properties;
    }
}
