package com.apashnov.cwgram.client.handler;

import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.handlers.interfaces.IUsersHandler;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by apashnov on 15.05.2017.
 */
public class UsersHandler implements IUsersHandler {

    private final ConcurrentHashMap<Integer, TLAbsUser> temporalUsers = new ConcurrentHashMap<>();

    @Override
    public void onUsers(List<TLAbsUser> users) {
        if ((this.temporalUsers.size() + users.size()) > 4000) {
            this.temporalUsers.clear();
        }
        users.stream().forEach(x -> this.temporalUsers.put(x.getId(), x));
//        users.forEach();
        System.out.println("debug UsersHandler");
    }
}
