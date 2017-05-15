package com.apashnov.cwgram.client.handler;

import org.telegram.api.message.TLMessage;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.IUser;

/**
 * Created by apashnov on 15.05.2017.
 */
public class TLMessageHandler {
    private static final String LOGTAG = "TLMESSAGEHANDLER";
    private final MessageHandler messageHandler;
    private final DatabaseManager databaseManager;

    public TLMessageHandler(MessageHandler messageHandler, DatabaseManager databaseManager) {
        this.messageHandler = messageHandler;
        this.databaseManager = databaseManager;
    }

    public void onTLMessage(TLMessage message) {
        final TLAbsPeer absPeer = message.getToId();
        if (absPeer instanceof TLPeerUser) {
            onTLMessageForUser(message);
        } else {
            BotLogger.severe(LOGTAG, "Unsupported Peer: " + absPeer.toString());
        }
    }

    private void onTLMessageForUser(TLMessage message) {
        if (!message.isSent()) {
            final IUser user = databaseManager.getUserById(message.getFromId());
            if (user != null) {
                this.messageHandler.handleMessage(user, message);
            }
        }
    }
}