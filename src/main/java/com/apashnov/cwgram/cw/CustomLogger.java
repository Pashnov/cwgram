package com.apashnov.cwgram.cw;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class CustomLogger {

    public static final String LOG_PATH = "/log";

    private static String basePath;

    private static Map<String, BufferedWriter> userResource = new ConcurrentHashMap<>();
    private static LinkedBlockingDeque<LogObject> stack = new LinkedBlockingDeque<>();

    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        LogObject logObject = stack.take();
                        String uniqueName = logObject.uniqueName;
                        synchronized (uniqueName) {
                            try {
                                BufferedWriter out = userResource.get(uniqueName);
                                if (out == null) {
                                    out = new BufferedWriter(new FileWriter(basePath + File.separator + ".." + File.separator + LOG_PATH + File.separator + uniqueName + ".log"));
                                    userResource.put(uniqueName, out);
                                }
                                out.newLine();
                                out.append(LocalDateTime.now() + "-");
                                out.append(uniqueName + ", ");
                                for (Object arg : logObject.args) {
                                    out.append(String.valueOf(arg) + ", ");
                                }
                                out.append(";");
                                out.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw new RuntimeException("logger failed, uniqueName ->" + uniqueName, e);
                            }
                        }
                    } catch (Exception e) {
                        //noop
                    }
                }
            }
        }).start();
    }

    public static void log(String uniqueName, Object... args) {
        LogObject logObject = new LogObject(uniqueName, args);
        stack.addLast(logObject);

    }

    public static void setBasePath(String basePath) {
        CustomLogger.basePath = basePath;
    }

    private static class LogObject {
        String uniqueName;
        Object[] args;

        public LogObject(String uniqueName, Object[] args) {
            this.uniqueName = uniqueName;
            this.args = args;
        }
    }
}