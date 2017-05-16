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

    private static final String LOGTAG = "ChatsHandler#";

    @Override
    public void onChats(List<TLAbsChat> chats) {
        chats.forEach(this::onAbsChat);
    }

    private void onAbsChat(TLAbsChat chat) {
        if (chat instanceof TLChannel) {
//            onChannel((TLChannel) chat);
        } else if (chat instanceof TLChannelForbidden) {
//            onChannelForbidden((TLChannelForbidden) chat);
        } else if (chat instanceof TLChat) {
            onChat((TLChat) chat);
        } else if (chat instanceof TLChatForbidden) {
//            onChatForbidden((TLChatForbidden) chat);
        } else {
            BotLogger.warn(LOGTAG, "Unsupported chat type " + chat);
        }
    }

    private void onChat(TLChat chat) {
        onChat(chat.getId());
    }

    private void onChat(int chatId) {
        boolean updating = true;
        ChatImpl current = (ChatImpl) DatabaseManagerInMemory.getInstance().getChatById(chatId);
        if (current == null) {
            updating = false;
            current = new ChatImpl(chatId);
        }
        current.setChannel(false);

        if (updating) {
            DatabaseManagerInMemory.getInstance().updateChat(current);
        } else {
            DatabaseManagerInMemory.getInstance().addChat(current);
        }
    }
}
