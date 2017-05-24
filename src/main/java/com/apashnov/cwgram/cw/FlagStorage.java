package com.apashnov.cwgram.cw;

import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class FlagStorage {

    private ReadWriteLock lockAttack = new ReentrantReadWriteLock();
    private ReadWriteLock lockDefend = new ReentrantReadWriteLock();

    private String attack;
    private String defend;

    public String getAttack() {
        lockAttack.readLock().lock();
        String result = this.attack;
        lockAttack.readLock().unlock();
        return result;
    }

    public void setAttack(String attack) {
        lockAttack.writeLock().lock();
        this.attack = attack;
        lockAttack.writeLock().unlock();
    }

    public String getDefend() {
        lockDefend.readLock().lock();
        String result = this.defend;
        lockDefend.readLock().unlock();
        return result;
    }

    public void setDefend(String defend) {
        lockDefend.writeLock().lock();
        this.defend = defend;
        lockDefend.writeLock().unlock();
    }

}
