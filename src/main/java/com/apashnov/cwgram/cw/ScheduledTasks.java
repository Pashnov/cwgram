package com.apashnov.cwgram.cw;

import com.apashnov.cwgram.cw.handler.GetterFlagHandler;
import com.apashnov.cwgram.cw.handler.ReaderFlagHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Component
public class ScheduledTasks {

    @Autowired
    private Notifier notifier;

    @Scheduled(cron = "0 58 3,7,11,15,19,23 * * *")
//    @Scheduled(initialDelay = 2000, fixedDelay = 1000)
//    @Scheduled(fixedRate = 15000)
    public void signalGetterFlagHandler() {
        Lock lock = notifier.getLock(GetterFlagHandler.class);
        Condition condition = notifier.getCondition(GetterFlagHandler.class);
        lock.lock();
        condition.signalAll();
        lock.unlock();
    }

    @Scheduled(cron = "0 58 3,7,11,15,19,23 * * *")
    public void signalReaderFlagHandler() {
        Lock lock = notifier.getLock(ReaderFlagHandler.class);
        Condition condition = notifier.getCondition(ReaderFlagHandler.class);
        lock.lock();
        condition.signalAll();
        lock.unlock();
    }
}
