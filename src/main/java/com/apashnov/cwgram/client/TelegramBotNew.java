package com.apashnov.cwgram.client;

import org.telegram.api.engine.LoggerInterface;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.bot.kernel.differenceparameters.DifferenceParametersService;
import org.telegram.bot.kernel.engine.MemoryApiState;
import org.telegram.bot.services.BotLogger;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;
import org.telegram.mtproto.log.LogInterface;
import org.telegram.mtproto.log.Logger;

import java.lang.reflect.InvocationTargetException;

public class TelegramBotNew {
    private static final String LOGTAG = "KERNELMAIN";
    private final BotConfig config;
    private final ChatUpdatesBuilderImpl chatUpdatesBuilder;
    private final int apiKey;
    private final String apiHash;
    private AbsApiState apiState;
    private KernelAuthNew kernelAuth;
    private MainHandlerWithoutHandlerDiff mainHandler;
    private KernelCommNew kernelComm;

    public TelegramBotNew(BotConfig config, ChatUpdatesBuilderImpl chatUpdatesBuilder, int apiKey, String apiHash) {
        if (config == null) {
            throw new NullPointerException("At least a BotConfig must be added");
        }
        if (chatUpdatesBuilder == null) {
            throw new NullPointerException("At least a ChatUpdatesBuilder must be added");
        }
        BotLogger.info(LOGTAG, "--------------KERNEL CREATED--------------");
        setLogging();
        this.apiKey = apiKey;
        this.apiHash = apiHash;
        this.config = config;
        this.chatUpdatesBuilder = chatUpdatesBuilder;
        chatUpdatesBuilder.setDifferenceParametersService(new DifferenceParametersService(chatUpdatesBuilder.getDatabaseManager()));
    }

    private static void setLogging() {
        Logger.registerInterface(new LogInterface() {
            @Override
            public void w(String tag, String message) {
                BotLogger.warn("MTPROTO", message);
            }

            @Override
            public void d(String tag, String message) {
                BotLogger.debug("MTPROTO", message);
            }

            @Override
            public void e(String tag, String message) {
                BotLogger.error("MTPROTO", message);
            }

            @Override
            public void e(String tag, Throwable t) {
                BotLogger.error("MTPROTO", t);
            }
        });
        org.telegram.api.engine.Logger.registerInterface(new LoggerInterface() {
            @Override
            public void w(String tag, String message) {
                BotLogger.warn("TELEGRAMAPI", message);
            }

            @Override
            public void d(String tag, String message) {
                BotLogger.debug("TELEGRAMAPI", message);
            }

            @Override
            public void e(String tag, Throwable t) {
                BotLogger.error("TELEGRAMAPI", t);
            }
        });
    }

    public LoginStatus init() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BotLogger.debug(LOGTAG, "Creating API");
        apiState = new MemoryApiState(config.getAuthfile());
        BotLogger.debug(LOGTAG, "API created");
        createKernelComm(); // Only set up threads and assign api state
        createKernelAuth(); // Only assign api state to kernel auth
        initKernelComm(); // Create TelegramApi and run the updates handler threads
        final LoginStatus loginResult = startKernelAuth(); // Perform login if necessary
        createKernelHandler(); // Create rest of handlers
        BotLogger.info(LOGTAG, "----------------BOT READY-----------------");
        return loginResult;
    }

    public void startBot() {
        initKernelHandler();
    }

    public void stopBot() {
        this.mainHandler.stop();
    }

    private void initKernelHandler() {
        final long start = System.currentTimeMillis();
        this.mainHandler.start();
        BotLogger.info(LOGTAG, String.format("%s init in %d ms", this.kernelAuth.getClass().getName(), (start - System.currentTimeMillis()) * -1));
    }

    private void initKernelComm() {
        final long start = System.currentTimeMillis();
        this.kernelComm.init();
        BotLogger.info(LOGTAG, String.format("%s init in %d ms", this.kernelComm.getClass().getName(), (start - System.currentTimeMillis()) * -1));
    }

    private void createKernelAuth() {
        final long start = System.currentTimeMillis();
        this.kernelAuth = new KernelAuthNew(this.apiState, config, this.kernelComm, apiKey, apiHash);
        BotLogger.info(LOGTAG, String.format("%s init in %d ms", this.kernelAuth.getClass().getName(), (start - System.currentTimeMillis()) * -1));
    }

    private LoginStatus startKernelAuth() {
        final long start = System.currentTimeMillis();
        final LoginStatus status = this.kernelAuth.start();
        BotLogger.info(LOGTAG, String.format("%s started in %d ms", this.kernelAuth.getClass().getName(), (start - System.currentTimeMillis()) * -1));
        return status;
    }

    private void createKernelHandler() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final long start = System.currentTimeMillis();
        chatUpdatesBuilder.setKernelComm(kernelComm);
        this.mainHandler = new MainHandlerWithoutHandlerDiff(kernelComm, chatUpdatesBuilder.build());
        BotLogger.info(LOGTAG, String.format("%s init in %d ms", this.mainHandler.getClass().getName(), (start - System.currentTimeMillis()) * -1));
    }

    private void createKernelComm() {
        final long start = System.currentTimeMillis();
        this.kernelComm = new KernelCommNew(apiKey, apiState);
        BotLogger.info(LOGTAG, String.format("%s init in %d ms", getKernelComm().getClass().getName(), (start - System.currentTimeMillis()) * -1));
    }

    public BotConfig getConfig() {
        return this.config;
    }

    public KernelCommNew getKernelComm() {
        return this.kernelComm;
    }

    public KernelAuthNew getKernelAuth() {
        return this.kernelAuth;
    }

    public MainHandlerWithoutHandlerDiff getMainHandler() {
        return this.mainHandler;
    }

    public boolean isAuthenticated() {
        return this.apiState.isAuthenticated();
    }
}
