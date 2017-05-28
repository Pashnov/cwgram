package com.apashnov.cwgram.client;

import org.springframework.stereotype.Component;
import org.telegram.api.message.TLMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class UpdatesStorage {

    private Map<String, SpecificStorage> userNumberStorage = new ConcurrentHashMap<>();

    public SpecificStorage get(String phoneNumber) {
        SpecificStorage storage = userNumberStorage.get(phoneNumber);
        if (storage == null) {
            synchronized (userNumberStorage) {
                storage = userNumberStorage.get(phoneNumber);
                if (storage == null) {
                    storage = new SpecificStorage();
                    userNumberStorage.put(phoneNumber, storage);
                }
            }
        }
        return storage;
    }

    public static class SpecificStorage {

        private static final int size = 10;

        private ConcurrentLinkedDeque<TLMessage> redAlertGroup = new ConcurrentLinkedDeque<>();
        private ConcurrentLinkedDeque<TLMessage> chatWarsChat = new ConcurrentLinkedDeque<>();

        public void putRedAlert(TLMessage message) {
            put(message, redAlertGroup);
        }

        public List<TLMessage> getRedAlert() {
            return new ArrayList<>(redAlertGroup);
        }

        public void putChatWars(TLMessage message) {
            put(message, chatWarsChat);
        }

        public List<TLMessage> getChatWars() {
            return new ArrayList<>(chatWarsChat);
        }

        private void put(TLMessage m, ConcurrentLinkedDeque<TLMessage> deque) {
            while (deque.size() >= size) {
//                redAlertGroup.pollFirst(); //first == head
                deque.pollLast();
            }
            deque.push(m);
        }

    }

}
