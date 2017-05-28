package com.apashnov.cwgram.cw.handler;

import com.apashnov.cwgram.cw.Notifier;
import com.apashnov.cwgram.cw.Warrior;
import org.telegram.bot.kernel.IKernelComm;

import java.util.Properties;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public interface CwHandler {

    void handle(Warrior warrior, IKernelComm kernelComm, String uniqueName, String phoneNumber);

    default void waitUntilWaked(Lock lock, Condition condition){
        lock.lock();
        try {
            condition.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
