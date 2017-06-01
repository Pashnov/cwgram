package com.apashnov.cwgram.client.handler;

import com.apashnov.cwgram.client.DatabaseManagerInMemory;
import com.apashnov.cwgram.client.model.ChatImpl;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.TLChat;
import org.telegram.api.chat.TLChatForbidden;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.chat.channel.TLChannelForbidden;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.Chat;

import java.util.List;

/**
 * Created by apashnov on 15.05.2017.
 */
public class ChatsHandler implements IChatsHandler {

    private static final String LOGTAG = "CHATSHANDLER";
    private final DatabaseManagerInMemory databaseManager;

    public ChatsHandler(DatabaseManagerInMemory databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void onChats(List<TLAbsChat> chats) {
        chats.forEach(this::onAbsChat);
    }

    private void onAbsChat(TLAbsChat chat) {
        if (chat instanceof TLChannel) {
            if(!((TLChannel) chat).getTitle().equals("Chat Wars Marketplace")
                    && !((TLChannel) chat).getTitle().equals("RedAlert Legion")){
//                System.out.println(chat); // can be get channel aka group - title =  RedAlert Legion
            }
//            onChannel((TLChannel) chat);
        } else if (chat instanceof TLChannelForbidden) {
//            onChannelForbidden((TLChannelForbidden) chat);
        } else if (chat instanceof TLChat) {
//            System.out.println(chat);
            onChat((TLChat) chat);
        } else if (chat instanceof TLChatForbidden) {
            onChatForbidden((TLChatForbidden) chat);
        } else {
            BotLogger.warn(LOGTAG, "Unsupported chat type " + chat);
        }
    }

    private void onChatForbidden(TLChatForbidden chat) {
        onChat(chat.getId());
    }


    private void onChat(TLChat chat) {
        onChat(chat.getId());
    }

    private void onChat(int chatId) {
        boolean updating = true;
        ChatImpl current = (ChatImpl) databaseManager.getChatById(chatId);
        if (current == null) {
            updating = false;
            current = new ChatImpl(chatId);
        }
        current.setChannel(false);

        if (updating) {
            databaseManager.updateChat(current);
        } else {
            databaseManager.addChat(current);
        }
    }


    private void onChannelForbidden(TLChannelForbidden channel) {
        boolean updating = true;
        ChatImpl current = (ChatImpl) databaseManager.getChatById(channel.getId());
        if (current == null) {
            updating = false;
            current = new ChatImpl(channel.getId());
        }
        current.setChannel(true);
        current.setAccessHash(channel.getAccessHash());

        if (updating) {
            databaseManager.updateChat(current);
        } else {
            databaseManager.addChat(current);
        }
    }

    private void onChannel(TLChannel channel) {
        boolean updating = true;
        ChatImpl current = (ChatImpl) databaseManager.getChatById(channel.getId());
        if (current == null) {
            updating = false;
            current = new ChatImpl(channel.getId());
        }
        current.setChannel(true);
        if (channel.hasAccessHash()) {
            current.setAccessHash(channel.getAccessHash());
        }

        if (updating) {
            databaseManager.updateChat(current);
        } else {
            databaseManager.addChat(current);
        }
    }

}
