package com.apashnov.cwgram.client.handler;

import org.telegram.api.chat.TLAbsChat;
import org.telegram.bot.handlers.interfaces.IChatsHandler;

import java.util.List;

/**
 * Created by apashnov on 15.05.2017.
 */
public class ChatsHandler implements IChatsHandler {

    @Override
    public void onChats(List<TLAbsChat> chats) {
        System.out.println(chats);
        System.out.println("debug ");
    }
}
