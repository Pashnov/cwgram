package com.apashnov.cwgram.client;

import org.jetbrains.annotations.NotNull;
import org.telegram.api.engine.RpcException;
import org.telegram.api.functions.updates.TLRequestUpdatesGetState;
import org.telegram.api.updates.*;
import org.telegram.bot.handlers.UpdatesHandlerBase;
import org.telegram.bot.handlers.interfaces.IUpdatesHandler;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.MainHandler;
import org.telegram.bot.kernel.UpdateWrapper;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.services.NotificationsService;

import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainHandlerWithoutHandlerDiff implements NotificationsService.NotificationObserver {
    private static final String LOGTAG = "KERNELHANDLER";
    private final KernelCommNew kernelComm;

    private boolean running;
    private final AtomicBoolean gettingDifferences = new AtomicBoolean(false);
    private final AtomicBoolean needGetUpdateState = new AtomicBoolean(true);
    private final ConcurrentLinkedDeque<TLAbsUpdates> updatesQueue = new ConcurrentLinkedDeque<>();
    private final IUpdatesHandler updatesHandler;
//    private final MainHandler.UpdateHandlerThread updateHandlerThread;

    MainHandlerWithoutHandlerDiff(KernelCommNew kernelComm, UpdatesHandlerBase updatesHandler) {
        NotificationsService.getInstance().addObserver(this, NotificationsService.needGetUpdates);
        this.kernelComm = kernelComm;
        this.updatesHandler = updatesHandler;
        this.running = false;
//        new MainHandler.UpdatesHandlerThread().start();
//        updateHandlerThread = new MainHandler.UpdateHandlerThread();
//        updateHandlerThread.start();
        kernelComm.setMainHandler(this);
    }

    public void start() {
        this.updatesQueue.clear();
        this.running = true;
    }

    void stop() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    /**
     * Add a TLAbsUpdates to the updates queue.
     * Load Updates states from database if needed
     * @param updates Updates to add
     */
    void onUpdate(@NotNull final TLAbsUpdates updates) {
        if (this.running) {
            this.updatesQueue.addLast(updates);
            synchronized (this.updatesQueue) {
                this.updatesQueue.notifyAll();
            }
        }
    }


    /**
     * Load updats state from server
     */
    private void getUpdatesState() {
        try {
            final TLUpdatesState state = kernelComm.doRpcCallSync(new TLRequestUpdatesGetState());
            if (state != null) {
                BotLogger.error(LOGTAG, "Received updates state");
                updatesHandler.updateStateModification(state);
                needGetUpdateState.set(false);
            } else {
                BotLogger.error(LOGTAG, "Error getting updates state");
            }
        } catch (ExecutionException | RpcException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    /**
     * Force getting updates state on next update received
     */
    public void needGetUpdates() {
        this.needGetUpdateState.set(true);
        synchronized (this.updatesQueue) {
            this.updatesQueue.notifyAll();
        }
    }

    public IUpdatesHandler getUpdatesHandler() {
        return updatesHandler;
    }

    @Override
    public void onNotificationReceived(int notificationId, Object... args) {
        if (notificationId == NotificationsService.needGetUpdates) {
            needGetUpdates();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        NotificationsService.getInstance().removeObserver(this, NotificationsService.needGetUpdates);
        super.finalize();
    }


}
