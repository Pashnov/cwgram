package com.apashnov.cwgram.client;

import com.apashnov.cwgram.client.handler.*;
import org.telegram.bot.ChatUpdatesBuilder;
import org.telegram.bot.handlers.UpdatesHandlerBase;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by apashnov on 15.05.2017.
 */
public class ChatUpdatesBuilderImpl {

    private KernelCommNew kernelComm;
    private IDifferenceParametersService differenceParametersService;
    private DatabaseManager databaseManager;
    private MessageHandler messageHandler;
    private UsersHandler usersHandler;
    private ChatsHandler chatsHandler;
    private TLMessageHandler tlMessageHandler;

    public ChatUpdatesBuilderImpl(DatabaseManager databaseManager, MessageHandler messageHandler, UsersHandler usersHandler, ChatsHandler chatsHandler, TLMessageHandler tlMessageHandler) {
        this.databaseManager = databaseManager;
        this.messageHandler = messageHandler;
        this.usersHandler = usersHandler;
        this.chatsHandler = chatsHandler;
        this.tlMessageHandler = tlMessageHandler;
    }


    public UpdatesHandlerBase build() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (kernelComm == null) {
            throw new NullPointerException("Can't build the handler without a KernelComm");
        }
        if (differenceParametersService == null) {
            throw new NullPointerException("Can't build the handler without a differenceParamtersService");
        }

        messageHandler.setKernelComm(this.kernelComm);
//        final Constructor<CustomUpdatesHandler> constructor = updatesHandlerBase.getConstructor(IKernelComm.class, IDifferenceParametersService.class, DatabaseManager.class);
//        final CustomUpdatesHandler updatesHandler =
//                constructor.newInstance(kernelComm, differenceParametersService, getDatabaseManager());
//        CustomUpdatesHandler updatesHandler = new CustomUpdatesHandler(kernelComm, differenceParametersService, getDatabaseManager());
//        updatesHandler.setConfig(botConfig);
//        updatesHandler.setHandlers(messageHandler, usersHandler, chatsHandler, tlMessageHandler);
        return null;
    }

    public void setKernelComm(KernelCommNew kernelComm) {
        this.kernelComm = kernelComm;
    }

    public void setDifferenceParametersService(IDifferenceParametersService differenceParametersService) {
        this.differenceParametersService = differenceParametersService;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
