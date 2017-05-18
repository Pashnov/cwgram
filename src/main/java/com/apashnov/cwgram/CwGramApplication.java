package com.apashnov.cwgram;

import com.apashnov.cwgram.client.ChatUpdatesBuilderImpl;
import com.apashnov.cwgram.client.ClientConfig;
import com.apashnov.cwgram.client.DatabaseManagerInMemory;
import com.apashnov.cwgram.client.handler.ChatsHandler;
import com.apashnov.cwgram.client.handler.MessageHandler;
import com.apashnov.cwgram.client.handler.TLMessageHandler;
import com.apashnov.cwgram.client.handler.UsersHandler;
import com.apashnov.cwgram.client.model.tl.TLRequestMessagesGetDialogsNew;
import com.apashnov.cwgram.client.model.tl.TLRequestMessagesGetHistoryNew;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.input.peer.TLInputPeerUser;
import org.telegram.api.messages.TLAbsMessages;
import org.telegram.api.messages.dialogs.TLDialogs;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.structure.LoginStatus;

import java.util.Scanner;

import static com.apashnov.cwgram.Constants.*;

@SpringBootApplication
public class CwGramApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext app = SpringApplication.run(CwGramApplication.class, args);

//      app.getBean();
//        DatabaseManagerInMemory db = DatabaseManagerInMemory.getInstance();
//        MessageHandler msgHandler = new MessageHandler();
//        ChatUpdatesBuilderImpl chatUpdatesBuilder = new ChatUpdatesBuilderImpl(db, msgHandler, new UsersHandler(), new ChatsHandler(), new TLMessageHandler(msgHandler, db));
//
//        ClientConfig config = new ClientConfig(Constants.PHONENUMBER);
//
//        final TelegramBot kernel = new TelegramBot(config, chatUpdatesBuilder, APIKEY, APIHASH);
//
//        LoginStatus status = kernel.init();
//        if (status == LoginStatus.CODESENT) {
//            Scanner in = new Scanner(System.in);
//            boolean success = kernel.getKernelAuth().setAuthCode(in.nextLine().trim());
//            if (success) {
//                status = LoginStatus.ALREADYLOGGED;
//            }
//        }
//        if (status == LoginStatus.ALREADYLOGGED) {
////            kernel.startBot();
////            kernel.getKernelComm().getApi().doRpcCallWeak();
////            GzipRequest
//
////            TLRequestContactsGetContacts contacts = new TLRequestContactsGetContacts();
////            contacts.setHash(APIHASH);
////            new TLRequestContactsImportContacts()
////            TLAbsContacts tlAbsContacts = kernel.getKernelComm().doRpcCallSync(contacts);
//
////            TLRequestMessagesGetChats method = new TLRequestMessagesGetChats();
////            method.setId(new TLIntVector());
////            TLMessagesChats tlAbsContacts = kernel.getKernelComm().doRpcCallSync(method);
////            TLMessagesChats tlAbsContacts = kernel.getKernelComm().doRpcCallSync(method);
//
//            //TLRequestChannelsGetParticipant method =  new TLRequestChannelsGetParticipant();
////TLRequestAccountGetAccountTTL m = new TLRequestAccountGetAccountTTL();
////            TLRequestContactsGetContacts m = new TLRequestContactsGetContacts();
////            m.setHash("");
////            TLAbsContacts x4 = kernel.getKernelComm().doRpcCallSync(m);
////            System.out.println(x4);
////
////            TLAccountDaysTTL x = kernel.getKernelComm().doRpcCallSync(new TLRequestAccountGetAccountTTL());
////            System.out.println(x);
//
////            TLAccountAuthorizations x2 = kernel.getKernelComm().doRpcCallSync(new TLRequestAccountGetAuthorizations());
////            System.out.println(x2);
//
////            TLRequestUsersGetUsers method = new TLRequestUsersGetUsers();
////            method.setId(new TLVector<>());
////            TLVector<TLAbsUser> x3 = kernel.getKernelComm().doRpcCallSync(method);
////            System.out.println(x3);
//
////            TLRequestMessagesGetCommonChats method = new TLRequestMessagesGetCommonChats();
////            TLInputUser userId = new TLInputUser();
////            userId.setAccessHash();
////            userId.setUserId(kernel.getKernelAuth().getApiState().getUserId());
////            method.setUserId(userId);
////            TLVector<TLAbsUser> x3 = kernel.getKernelComm().doRpcCallSync(method);
////            System.out.println(x3);
////
////            TLRequestDif
////            TLVector<TLAbsUser> x3 = kernel.getKernelComm().doRpcCallSync(method);
////            System.out.println(x3);
//
////            Thread.sleep(10000);
////            kernel.getKernelComm().doRpcCallAsync(new TLRequestContactsGetContacts(), new TelegramFunctionCallback<TLAbsContacts>() {
////                @Override
////                public void onSuccess(TLAbsContacts result) {
////                    System.out.println(result);
////                }
////
////                @Override
////                public void onRpcError(RpcException e) {
////                    System.out.println(e);
////                }
////
////                @Override
////                public void onTimeout(TimeoutException e) {
////                    System.out.println(e);
////                }
////
////                @Override
////                public void onUnknownError(Throwable e) {
////                    System.out.println(e);
////                }
////            });
//            TelegramApi telegramApi = kernel.getKernelComm().getApi();
////            TLRequestMessagesGetDialogs dialogs = new TLRequestMessagesGetDialogs();
////            dialogs.setOffsetPeer(new TLInputPeerEmpty());
////            TLAbsDialogs tlAbsDialogs = telegramApi.doRpcCall(dialogs);
//
//            TLRequestMessagesGetDialogsNew dialogsNew = new TLRequestMessagesGetDialogsNew(0, -1, 100);
//            TLDialogs tlDialogs = telegramApi.doRpcCall(dialogsNew);
////            ((List<TLUser>)(List<?>) tlDialogs.getUsers()).stream().filter((TLUser c) -> c.getUserName().equals("ChatWarsBot"));
//
//            (tlDialogs.getUsers()).stream().filter((TLAbsUser c) -> ((TLUser) c).getUserName().equals("ChatWarsBot"))
//                    .findFirst().map(c -> ((TLUser) c)).ifPresent(CwGramApplication::setChatWarsBot);
//
//            new Thread(new Runnable() {
//                int index = 0;
//
//                @Override
//                public void run() {
//
//                    try {
//                        while (index < 1000) {
//                            Thread.sleep(1000);
////                            TLRequestMessagesGetMessages msgs= new TLRequestMessagesGetMessages();
////                            TLIntVector ids = new TLIntVector();
//////                            ids.add(id);
////                            msgs.setId(ids);
////                            TLAbsMessages tlAbsMessages = telegramApi.doRpcCall(msgs);
////                            System.out.println("index = " + index++ + ". size = " + tlAbsMessages.getMessages().size());
//
//                            TLInputPeerUser peer = new TLInputPeerUser();
//                            peer.setUserId(chatWarsBot.getId());
//                            peer.setAccessHash(chatWarsBot.getAccessHash());
//                            TLRequestMessagesGetHistoryNew history = new TLRequestMessagesGetHistoryNew(peer, 0, -1, 10);
//                            TLAbsMessages tlAbsMessages = telegramApi.doRpcCall(history);
//
//
//                            System.out.println(tlAbsMessages);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();
//
//            System.out.println("debug point");
//
//
//
//
//        } else {
//            throw new Exception("Failed to log in: " + status);
//        }
//

    }

    static TLUser chatWarsBot = null;

    public static void setChatWarsBot(TLUser chatWarsBot) {
        CwGramApplication.chatWarsBot = chatWarsBot;
    }
}
