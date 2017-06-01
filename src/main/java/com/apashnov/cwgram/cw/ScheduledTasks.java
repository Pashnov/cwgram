package com.apashnov.cwgram.cw;

import com.apashnov.cwgram.cw.handler.ArenaHandler;
import com.apashnov.cwgram.cw.handler.GetterFlagHandler;
import com.apashnov.cwgram.cw.handler.QuestHandler;
import com.apashnov.cwgram.cw.handler.ReaderFlagHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static com.apashnov.cwgram.cw.CustomLogger.log;

@Component
public class ScheduledTasks {

    @Autowired
    private Notifier notifier;

    @Scheduled(cron = "0 56 3,7,11,15,19,23 * * *")
//    @Scheduled(cron = "0 41 12 * * *")
//    @Scheduled(initialDelay = 2000, fixedDelay = 1000)
//    @Scheduled(fixedRate = 15000)
    public void signalGetterFlagHandler() {
        log("common", "signalGetterFlagHandler waked");
        Lock lock = notifier.getLock(GetterFlagHandler.class);
        Condition condition = notifier.getCondition(GetterFlagHandler.class);
        lock.lock();
        log("common", "signalGetterFlagHandler locked");
        condition.signalAll();
        log("common", "signalGetterFlagHandler signalAll");
        lock.unlock();
        log("common", "signalGetterFlagHandler unlocked");
    }

    //    @Scheduled(cron = "0 58 3,7,11,15,19,23 * * *")
    @Scheduled(cron = "0 57 3,7,11,15,19,23 * * *")
    public void signalReaderFlagHandler() {
        log("common", "signalReaderFlagHandler waked");
        Lock lock = notifier.getLock(ReaderFlagHandler.class);
        Condition condition = notifier.getCondition(ReaderFlagHandler.class);
        lock.lock();
        log("common", "signalReaderFlagHandler locked");
        condition.signalAll();
        log("common", "signalReaderFlagHandler signalAll");
        lock.unlock();
        log("common", "signalReaderFlagHandler unlocked");
    }

    //    @Scheduled(cron = "0 58 3,7,11,15,19,23 * * *")
    @Scheduled(cron = "0 10 9-23 * * *")
    public void signalArenaHandler() {
        log("common", "signalArenaHandler waked");
        Lock lock = notifier.getLock(ArenaHandler.class);
        Condition condition = notifier.getCondition(ReaderFlagHandler.class);
        lock.lock();
        log("common", "signalArenaHandler locked");
        condition.signalAll();
        log("common", "signalArenaHandler signalAll");
        lock.unlock();
        log("common", "signalArenaHandler unlocked");
    }

//    @Scheduled(initialDelay = 2000)
//    @Scheduled(fixedRate = 15000)
    @Scheduled(initialDelay = 5 * 60 *1000, fixedRate = 4 * 60 * 60 * 1000)
    public void signalQuestHandler() {
        log("common", "signalQuestHandler waked");
        Lock lock = notifier.getLock(QuestHandler.class);
        Condition condition = notifier.getCondition(QuestHandler.class);
        lock.lock();
        log("common", "signalQuestHandler locked");
        condition.signalAll();
        log("common", "signalQuestHandler signalAll");
        lock.unlock();
        log("common", "signalQuestHandler unlocked");
    }


}
