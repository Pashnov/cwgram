package com.apashnov.cwgram.cw;

/**
 * Created by we on 14.05.2017.
 */
public abstract class Warrior {

    public final String sessionId;

    public Warrior(String sessionId) {
        this.sessionId = sessionId;
    }

    public abstract WarriorKind getKind();
}
