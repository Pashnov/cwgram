package com.apashnov.cwgram;

import com.apashnov.cwgram.client.ChatUpdatesBuilderImpl;
import com.apashnov.cwgram.client.ClientConfig;
import com.apashnov.cwgram.client.DatabaseManagerInMemory;
import com.apashnov.cwgram.client.handler.ChatsHandler;
import com.apashnov.cwgram.client.handler.MessageHandler;
import com.apashnov.cwgram.client.handler.TLMessageHandler;
import com.apashnov.cwgram.client.handler.UsersHandler;
import com.apashnov.cwgram.client.model.tl.TLRequestMessagesGetCommonChats;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.api.account.TLAccountAuthorizations;
import org.telegram.api.account.TLAccountDaysTTL;
import org.telegram.api.channel.TLChannelParticipant;
import org.telegram.api.contacts.TLAbsContacts;
import org.telegram.api.engine.RpcException;
import org.telegram.api.engine.TimeoutException;
import org.telegram.api.functions.account.TLRequestAccountGetAccountTTL;
import org.telegram.api.functions.account.TLRequestAccountGetAuthorizations;
import org.telegram.api.functions.channels.TLRequestChannelsGetParticipant;
import org.telegram.api.functions.contacts.TLRequestContactsGetContacts;
import org.telegram.api.functions.contacts.TLRequestContactsImportContacts;
import org.telegram.api.functions.messages.TLRequestMessagesGetChats;
import org.telegram.api.functions.messages.TLRequestMessagesGetDialogs;
import org.telegram.api.functions.users.TLRequestUsersGetUsers;
import org.telegram.api.input.user.TLInputUser;
import org.telegram.api.messages.TLMessagesChats;
import org.telegram.api.messages.dialogs.TLAbsDialogs;
import org.telegram.api.user.TLAbsUser;
import org.telegram.bot.TelegramFunctionCallback;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.tl.TLIntVector;
import org.telegram.tl.TLVector;

import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import static com.apashnov.cwgram.Constants.*;

@SpringBootApplication
public class TelegramClientCwApplication {



    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext app = SpringApplication.run(TelegramClientCwApplication.class, args);

//      app.getBean();
        DatabaseManagerInMemory db = DatabaseManagerInMemory.getInstance();
        MessageHandler msgHandler = new MessageHandler();
        ChatUpdatesBuilderImpl chatUpdatesBuilder = new ChatUpdatesBuilderImpl(db, msgHandler, new UsersHandler(), new ChatsHandler(), new TLMessageHandler(msgHandler, db));

        ClientConfig config = new ClientConfig(Constants.PHONENUMBER);

        final TelegramBot kernel = new TelegramBot(config, chatUpdatesBuilder, APIKEY, APIHASH);
        LoginStatus status = kernel.init();
        if (status == LoginStatus.CODESENT) {
            Scanner in = new Scanner(System.in);
            boolean success = kernel.getKernelAuth().setAuthCode(in.nextLine().trim());
            if (success) {
                status = LoginStatus.ALREADYLOGGED;
            }
        }
        if (status == LoginStatus.ALREADYLOGGED) {
            kernel.startBot();
//            kernel.getKernelComm().getApi().doRpcCallWeak();
//            GzipRequest

//            TLRequestContactsGetContacts contacts = new TLRequestContactsGetContacts();
//            contacts.setHash(APIHASH);
//            new TLRequestContactsImportContacts()
//            TLAbsContacts tlAbsContacts = kernel.getKernelComm().doRpcCallSync(contacts);

//            TLRequestMessagesGetChats method = new TLRequestMessagesGetChats();
//            method.setId(new TLIntVector());
//            TLMessagesChats tlAbsContacts = kernel.getKernelComm().doRpcCallSync(method);
//            TLMessagesChats tlAbsContacts = kernel.getKernelComm().doRpcCallSync(method);

            //TLRequestChannelsGetParticipant method =  new TLRequestChannelsGetParticipant();
//TLRequestAccountGetAccountTTL m = new TLRequestAccountGetAccountTTL();
//            TLRequestContactsGetContacts m = new TLRequestContactsGetContacts();
//            m.setHash("");
//            TLAbsContacts x4 = kernel.getKernelComm().doRpcCallSync(m);
//            System.out.println(x4);
//
//            TLAccountDaysTTL x = kernel.getKernelComm().doRpcCallSync(new TLRequestAccountGetAccountTTL());
//            System.out.println(x);

//            TLAccountAuthorizations x2 = kernel.getKernelComm().doRpcCallSync(new TLRequestAccountGetAuthorizations());
//            System.out.println(x2);

//            TLRequestUsersGetUsers method = new TLRequestUsersGetUsers();
//            method.setId(new TLVector<>());
//            TLVector<TLAbsUser> x3 = kernel.getKernelComm().doRpcCallSync(method);
//            System.out.println(x3);

//            TLRequestMessagesGetCommonChats method = new TLRequestMessagesGetCommonChats();
//            TLInputUser userId = new TLInputUser();
//            userId.setAccessHash();
//            userId.setUserId(kernel.getKernelAuth().getApiState().getUserId());
//            method.setUserId(userId);
//            TLVector<TLAbsUser> x3 = kernel.getKernelComm().doRpcCallSync(method);
//            System.out.println(x3);
//
//            TLRequestDif
//            TLVector<TLAbsUser> x3 = kernel.getKernelComm().doRpcCallSync(method);
//            System.out.println(x3);

//            Thread.sleep(10000);
//            kernel.getKernelComm().doRpcCallAsync(new TLRequestContactsGetContacts(), new TelegramFunctionCallback<TLAbsContacts>() {
//                @Override
//                public void onSuccess(TLAbsContacts result) {
//                    System.out.println(result);
//                }
//
//                @Override
//                public void onRpcError(RpcException e) {
//                    System.out.println(e);
//                }
//
//                @Override
//                public void onTimeout(TimeoutException e) {
//                    System.out.println(e);
//                }
//
//                @Override
//                public void onUnknownError(Throwable e) {
//                    System.out.println(e);
//                }
//            });
            System.out.println("debug point");

        } else {
            throw new Exception("Failed to log in: " + status);
        }

        app.close();

    }
}
