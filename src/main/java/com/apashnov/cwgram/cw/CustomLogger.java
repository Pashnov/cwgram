package com.apashnov.cwgram.cw;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomLogger {

    public static final String LOG_PATH = "/log";

    private static String basePath;

    private static Map<String, BufferedWriter> userResource = new ConcurrentHashMap<>();

    public static void log(String uniqueName, Object... args) {
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
                for (Object arg : args) {
                    out.append(String.valueOf(arg) + ", ");
                }
                out.append(";");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("logger failed, uniqueName ->" + uniqueName, e);
            }
        }
    }

    public static void setBasePath(String basePath) {
        CustomLogger.basePath = basePath;
    }
}