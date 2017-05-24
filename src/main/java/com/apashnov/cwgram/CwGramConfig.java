package com.apashnov.cwgram;

import com.apashnov.cwgram.cw.*;
import com.apashnov.cwgram.cw.handler.CwHandler;
import com.apashnov.cwgram.cw.handler.GetterFlagHandler;
import com.apashnov.cwgram.cw.handler.ReaderFlagHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

//java -jar -Dbase.path="C:\my_projects\cwgram\build\libs" cwgram-0.0.1-SNAPSHOT.jar

@Configuration
@EnableScheduling
@PropertySource("file:${base.path}/global.properties")
public class CwGramConfig {

    @Autowired
    Environment env;

    @Value("${cw.defender}")
    boolean isDef;

    @Value("${cw.group.red.alert.id}")
    long groupId;

    @Value("${base.path}")
    String basePath;

    @Value("${cw.gram.api.key}")
    int apiKey;

    @Value("${cw.gram.api.hash}")
    String apiHash;

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public List<Container> containers() throws IOException {
        System.out.println(isDef);
        System.out.println(groupId);
        List<Container> containers = Files.list(Paths.get(basePath))
                .filter(path -> Files.isDirectory(path))
                .map(path -> new Container(path))
                .peek(container -> container.activate(apiKey, apiHash))
                .collect(Collectors.toList());

        containers.sort(Comparator.comparing(Container::getId));

        boolean isFirst = true;
        for (Container container : containers) {
            if(isFirst){
                container.addHandler(applicationContext.getBean(GetterFlagHandler.class));
                isFirst = false;
            } else {
                container.addHandler(applicationContext.getBean(ReaderFlagHandler.class));
            }
        }

        return containers;
    }

}
