package com.apashnov.cwgram.cw;

/**
 * Created by we on 14.05.2017.
 */
public class Aggressor extends Warrior {


    public Aggressor(String sessionId) {
        super(sessionId);
    }

    @Override public WarriorKind getKind() {
        return WarriorKind.AGGRESSOR;
    }
}
