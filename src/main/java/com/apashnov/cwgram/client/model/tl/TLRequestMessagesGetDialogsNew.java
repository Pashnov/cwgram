package com.apashnov.cwgram.client.model.tl;

import org.telegram.api.messages.dialogs.TLAbsDialogs;
import org.telegram.api.messages.dialogs.TLDialogs;
import org.telegram.tl.StreamingUtils;
import org.telegram.tl.TLContext;
import org.telegram.tl.TLMethod;
import org.telegram.tl.TLObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by apashnov on 17.05.2017.
 */
public class TLRequestMessagesGetDialogsNew extends TLMethod<TLDialogs> {
    /**
     * The constant CLASS_ID.
     */
    public static final int CLASS_ID = 0xeccf1df6;

    private int offset;
    private int maxId;
    private int limit;

    /**
     * Instantiates a new TL request messages get dialogs.
     */
    public TLRequestMessagesGetDialogsNew() {
        super();
    }

    public TLRequestMessagesGetDialogsNew(int offset, int maxId, int limit) {
        this.offset = offset;
        this.maxId = maxId;
        this.limit = limit;
    }

    public int getClassId() {
        return CLASS_ID;
    }

    public TLDialogs deserializeResponse(InputStream stream, TLContext context)
            throws IOException {
        final TLObject res = StreamingUtils.readTLObject(stream, context);
        if (res == null) {
            throw new IOException("Unable to parse response");
        }
        if ((res instanceof TLAbsDialogs)) {
            return (TLDialogs) res;
        }
        if ((res instanceof TLDialogs)) {
            return (TLDialogs) res;
        }
        throw new IOException("Incorrect response type. Expected " + TLAbsDialogs.class.getName() + ", got: " + res.getClass().getCanonicalName());
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getMaxId() {
        return maxId;
    }

    public void setMaxId(int maxId) {
        this.maxId = maxId;
    }

    /**
     * Gets limit.
     *
     * @return the limit
     */
    public int getLimit() {
        return this.limit;
    }

    /**
     * Sets limit.
     *
     * @param value the value
     */
    public void setLimit(int value) {
        this.limit = value;
    }

    public void serializeBody(OutputStream stream)
            throws IOException {
        StreamingUtils.writeInt(this.offset, stream);
        StreamingUtils.writeInt(this.maxId, stream);
        StreamingUtils.writeInt(this.limit, stream);
    }

    public void deserializeBody(InputStream stream, TLContext context)
            throws IOException {
        this.offset = StreamingUtils.readInt(stream);
        this.maxId = StreamingUtils.readInt(stream);
        this.limit = StreamingUtils.readInt(stream);
    }

    public String toString() {
        return "messages.getDialogs#eccf1df6";
    }
}