package com.apashnov.cwgram.cw.handler;

import com.apashnov.cwgram.client.KernelCommNew;
import com.apashnov.cwgram.cw.Notifier;
import com.apashnov.cwgram.cw.Warrior;
import org.telegram.bot.kernel.IKernelComm;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public interface CwHandler {

    void handle(Warrior warrior, KernelCommNew kernelComm, String uniqueName);

    void setNotifier(Notifier notifier);

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
