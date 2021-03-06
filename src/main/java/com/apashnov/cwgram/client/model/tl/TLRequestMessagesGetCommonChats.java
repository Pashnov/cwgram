//package com.apashnov.cwgram.client.model.tl;
//
//import org.telegram.api.input.user.TLInputUser;
//import org.telegram.api.messages.chats.TLAbsMessagesChats;
//import org.telegram.tl.StreamingUtils;
//import org.telegram.tl.TLContext;
//import org.telegram.tl.TLMethod;
//import org.telegram.tl.TLObject;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
///**
// * Created by apashnov on 16.05.2017.
// */
//public class TLRequestMessagesGetCommonChats extends TLMethod<TLAbsMessagesChats> {
//    /**
//     * The constant CLASS_ID.
//     */
//    public static final int CLASS_ID = 0xd0a48c4;
//
//    private TLInputUser userId;
//    private int maxId;
//    private int limit;
//
//    /**
//     * Instantiates a new TL request messages get chats.
//     */
//    public TLRequestMessagesGetCommonChats() {
//        super();
//    }
//
//    public int getClassId() {
//        return CLASS_ID;
//    }
//
//    public TLAbsMessagesChats deserializeResponse(InputStream stream, TLContext context)
//            throws IOException {
//        final TLObject res = StreamingUtils.readTLObject(stream, context);
//        if (res == null) {
//            throw new IOException("Unable to parse response");
//        }
//        if ((res instanceof TLAbsMessagesChats)) {
//            return (TLAbsMessagesChats) res;
//        }
//        throw new IOException("Incorrect response type. Expected " + TLAbsMessagesChats.class.getName() + ", got: " + res.getClass().getCanonicalName());
//    }
//
//    public TLInputUser getUserId() {
//        return userId;
//    }
//
//    public void setUserId(TLInputUser userId) {
//        this.userId = userId;
//    }
//
//    public int getMaxId() {
//        return maxId;
//    }
//
//    public void setMaxId(int maxId) {
//        this.maxId = maxId;
//    }
//
//    public int getLimit() {
//        return limit;
//    }
//
//    public void setLimit(int limit) {
//        this.limit = limit;
//    }
//
//    public void serializeBody(OutputStream stream) throws IOException {
//        userId.serialize(stream);
//        StreamingUtils.writeInt(maxId, stream);
//        StreamingUtils.writeInt(limit, stream);
//    }
//
//    public void deserializeBody(InputStream stream, TLContext context) throws IOException {
//        userId = ((TLInputUser) StreamingUtils.readTLObject(stream, context));
//        this.maxId = StreamingUtils.readInt(stream);
//        this.limit = StreamingUtils.readInt(stream);
//    }
//
//    public String toString() {
//        return "messages.getCommonChats#d0a48c4";
//    }
//}