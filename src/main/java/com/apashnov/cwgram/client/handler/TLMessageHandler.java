package com.apashnov.cwgram.client.handler;

import com.apashnov.cwgram.client.UpdatesStorage.SpecificStorage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.IUser;

/**
 * Created by apashnov on 15.05.2017.
 */


//switch (chatId){
//        case 1112569524:
//        specificStorage.putRedAlert(message);
//        break;
//        case 265204902:
//        specificStorage.putChatWars(message);
//        break;
//        }
public class TLMessageHandler {
    private static final String LOGTAG = "TLMESSAGEHANDLER";
    private final MessageHandler messageHandler;
    private final DatabaseManager databaseManager;
    private SpecificStorage specificStorage;
    private String uniqueName;

    public TLMessageHandler(MessageHandler messageHandler, DatabaseManager databaseManager, SpecificStorage specificStorage, String uniqueName) {
        this.messageHandler = messageHandler;
        this.databaseManager = databaseManager;
        this.specificStorage = specificStorage;
        this.uniqueName = uniqueName;
    }

    public void onTLMessage(TLMessage message) {
        final TLAbsPeer absPeer = message.getToId();
        if (absPeer instanceof TLPeerUser) {
            onTLMessageForUser(message);
        } else {
            BotLogger.severe(LOGTAG, "Unsupported Peer: " + absPeer.toString());
        }
//        int chatId = message.getChatId();

    }

    private void onTLMessageForUser(TLMessage message) {
        System.out.println(message);
        if (!message.isSent()) {
            final IUser user = databaseManager.getUserById(message.getFromId());
            if (user != null) {
                this.messageHandler.handleMessage(user, message);
            }
        }
    }
}