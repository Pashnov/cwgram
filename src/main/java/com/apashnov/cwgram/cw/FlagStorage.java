package com.apashnov.cwgram.cw;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

@Component
public class FlagStorage {

    private Lock lockAttack = new ReentrantLock();
    private Condition conditionAttack = lockAttack.newCondition();
    private Lock lockDefend = new ReentrantLock();
    private Condition conditionDefend = lockDefend.newCondition();

    private String attack;
    private String defend;

    public String getAttack() {
        lockAttack.lock();
        try {
            conditionAttack.await(3, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String result = this.attack;
        lockAttack.unlock();
        return result;
    }

    public void setAttack(String attack) {
        this.attack = attack;
        lockAttack.lock();
        conditionAttack.signalAll();
        lockAttack.unlock();
    }

    public String getDefend() {
        lockDefend.lock();
        try {
            conditionDefend.await(3, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String result = this.defend;
        lockDefend.unlock();
        return result;
    }

    public void setDefend(String defend) {
        this.defend = defend;
        lockDefend.lock();
        conditionDefend.signalAll();
        lockDefend.unlock();
    }

}