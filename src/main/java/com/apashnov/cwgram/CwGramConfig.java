package com.apashnov.cwgram;

import com.apashnov.cwgram.client.UpdatesStorage;
import com.apashnov.cwgram.cw.*;
import com.apashnov.cwgram.cw.handler.*;
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
import java.util.stream.Collectors;

//java -jar -Dbase.path="C:\my_projects\cwgram\build\libs" cwgram-0.0.1-SNAPSHOT.jar
//java -jar -Dbase.path="/home/apashnov/git/github/cwgram/test" cwgram-0.0.1-SNAPSHOT.jar

@Configuration
@EnableScheduling
@PropertySource("file:${base.path}/global.properties")
public class CwGramConfig {

    @Autowired
    Environment env;

    @Value("${base.path}")
    private String basePath;

    @Value("${cw.gram.api.key}")
    int apiKey;

    @Value("${cw.gram.api.hash}")
    String apiHash;

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public List<Container> containers(UpdatesStorage updatesStorage) throws IOException {
        CustomLogger.setBasePath(basePath);
        boolean questAllow = true;
        boolean caravanSecurity = true;

        List<Container> containers = Files.list(Paths.get(basePath))
                .filter(path -> Files.isDirectory(path))
                .map(path -> new Container(path))
                .peek(container -> container.activate(apiKey, apiHash, updatesStorage))
                .collect(Collectors.toList());

        containers.sort(Comparator.comparing(Container::getId));

        boolean isFirst = true;
        for (Container container : containers) {
            container.addHandler(applicationContext.getBean(CustomDifferencesListener.class));

            if(isFirst){
                container.addHandler(applicationContext.getBean(GetterFlagHandler.class));
                isFirst = false;
            } else {
                container.addHandler(applicationContext.getBean(ReaderFlagHandler.class));
            }

            if(questAllow){
                container.addHandler(applicationContext.getBean(QuestHandler.class));
            }

            if(caravanSecurity){
                container.addHandler(applicationContext.getBean(CaravanSecurityHandler.class));
            }
            container.addHandler(applicationContext.getBean(ArenaHandler.class));


        }

        return containers;
    }



}
