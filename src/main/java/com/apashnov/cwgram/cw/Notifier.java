package com.apashnov.cwgram.cw;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class Notifier {

    private Map<Class, Lock> locks = new ConcurrentHashMap<>();
    private Map<Class, Condition> conditions = new ConcurrentHashMap<>();

    public void checkExists(Class clazz){
        if(!locks.containsKey(clazz)){
            Lock lock = new ReentrantLock();
            locks.put(clazz, lock);
            conditions.put(clazz, lock.newCondition());
        }
    }

    public Lock getLock(Class clazz){
        checkExists(clazz);
        return locks.get(clazz);
    }

    public Condition getCondition(Class clazz){
        checkExists(clazz);
        return conditions.get(clazz);
    }
}
