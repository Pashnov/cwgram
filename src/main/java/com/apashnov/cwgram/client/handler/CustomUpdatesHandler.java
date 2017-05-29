package com.apashnov.cwgram.client.handler;

import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.client.model.User;
import org.jetbrains.annotations.NotNull;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.update.TLUpdateNewMessage;
import org.telegram.api.updates.TLUpdateShortMessage;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.handlers.DefaultUpdatesHandler;
import org.telegram.bot.handlers.interfaces.IChatsHandler;
import org.telegram.bot.handlers.interfaces.IUsersHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.IUser;

import java.util.List;

import static com.apashnov.cwgram.Constants.CHAT_WARS_ID;
import static com.apashnov.cwgram.Constants.RED_ALERT_ID;
import static com.apashnov.cwgram.cw.CustomLogger.log;
import static com.apashnov.cwgram.cw.CwActionHelper.convert;

/**
 * Created by apashnov on 15.05.2017.
 */
public class CustomUpdatesHandler extends DefaultUpdatesHandler {
    private static final String LOGTAG = "CHATUPDATESHANDLER";

    private final DatabaseManager databaseManager;
    private BotConfig botConfig;
    private MessageHandler messageHandler;
    private IUsersHandler usersHandler;
    private IChatsHandler chatsHandler;
    private TLMessageHandler tlMessageHandler;
    private UpdatesStorage.SpecificStorage specificStorage;
    private String uniqueName;
    private IKernelComm kernelComm;

    public CustomUpdatesHandler(IKernelComm kernelComm, IDifferenceParametersService differenceParametersService, DatabaseManager databaseManager, UpdatesStorage.SpecificStorage specificStorage, String uniqueName) {
        super(kernelComm, differenceParametersService, databaseManager);
        this.databaseManager = databaseManager;
        this.uniqueName = uniqueName;
        this.specificStorage = specificStorage;
        this.kernelComm = kernelComm;
    }

    public void setConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public void setHandlers(MessageHandler messageHandler, IUsersHandler usersHandler, IChatsHandler chatsHandler, TLMessageHandler tlMessageHandler) {
        this.messageHandler = messageHandler;
        this.chatsHandler = chatsHandler;
        this.usersHandler = usersHandler;
        this.tlMessageHandler = tlMessageHandler;
    }

    @Override
    public void onTLUpdateShortMessageCustom(TLUpdateShortMessage update) {
        final IUser user = databaseManager.getUserById(update.getUserId());
        if (user != null) {
            BotLogger.info(LOGTAG, "Received message from: " + update.getUserId());
            messageHandler.handleMessage(user, update);
        }
    }

    @Override
    public void onTLUpdateNewMessageCustom(TLUpdateNewMessage update) {
        onTLAbsMessageCustom(update.getMessage());
    }

    @Override
    protected void onTLAbsMessageCustom(TLAbsMessage message) {
        if (message instanceof TLMessage) {
            BotLogger.debug(LOGTAG, "Received TLMessage");
            onTLMessage((TLMessage) message);
        } else {
            log(uniqueName, message.toString());
            BotLogger.debug(LOGTAG, "!!!!!!!!!!!!!!!!!!!!!!! -> " + message.toString());
            BotLogger.debug(LOGTAG, "!!!!!!!!!!!!!!!!!!!!!!! -> " + message.toString());
            BotLogger.debug(LOGTAG, "!!!!!!!!!!!!!!!!!!!!!!! -> " + message.toString());
            BotLogger.debug(LOGTAG, "!!!!!!!!!!!!!!!!!!!!!!! -> " + message.toString());
            BotLogger.debug(LOGTAG, "Unsupported TLAbsMessage -> " + message.toString());
            BotLogger.debug(LOGTAG, "Unsupported TLAbsMessage_class -> " + message.getClass());
            BotLogger.debug(LOGTAG, "!!!!!!!!!!!!!!!!!!!!!!! -> " + message.toString());
            BotLogger.debug(LOGTAG, "!!!!!!!!!!!!!!!!!!!!!!! -> " + message.toString());
            BotLogger.debug(LOGTAG, "!!!!!!!!!!!!!!!!!!!!!!! -> " + message.toString());
            BotLogger.debug(LOGTAG, "!!!!!!!!!!!!!!!!!!!!!!! -> " + message.toString());
        }
    }

    @Override
    protected void onUsersCustom(List<TLAbsUser> users) {
        usersHandler.onUsers(users);
    }

    @Override
    protected void onChatsCustom(List<TLAbsChat> chats) {
        chatsHandler.onChats(chats);
    }

    /**
     * Handles TLMessage
     * @param message Message to handle
     */
    private void onTLMessage(@NotNull TLMessage message) {
        log(uniqueName,"onTLMessage#, from msg -> "+message.getMessage(),", fromId -> "+message.getFromId());
        if (message.hasFromId()) {
//            kernelComm.performMarkAsRead(new User(kernelComm.getCurrentUserId(), kernelComm.), 0);
            switch (message.getFromId()){
                case CHAT_WARS_ID:
                    log(uniqueName,"onTLMessage#, CHAT_WARS_ID, from msg -> " + message.getMessage());
                    specificStorage.putChatWars(message);
                    break;
            }
            switch (message.getToId().getId()){
                case RED_ALERT_ID:
                    log(uniqueName,"onTLMessage#, RED_ALERT_ID, to msg -> " + message.getMessage());
                    specificStorage.putRedAlert(message);
                    break;
                case CHAT_WARS_ID:
                    specificStorage.putChatWars(message);
                    log(uniqueName,"onTLMessage#, CHAT_WARS_ID, to msg -> " + message.getMessage());
                    break;
            }
//            final IUser user = databaseManager.getUserById(message.getFromId());
//            if (user != null) {
//                this.tlMessageHandler.onTLMessage(message);
//            }
        }
    }

}