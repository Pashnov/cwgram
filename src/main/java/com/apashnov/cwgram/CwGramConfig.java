package com.apashnov.cwgram;

import com.apashnov.cwgram.cw.Container;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

//java -jar -Dbase.path="C:\my_projects\cwgram\build\libs" cwgram-0.0.1-SNAPSHOT.jar

@Configuration
@PropertySource("file:${base.path}/global.properties")
public class CwGramConfig {

    @Autowired
    Environment env;

    @Value("${cw.deffer}")
    boolean isDef;

    @Value("${cw.group.red.alert.id}")
    long groupId;

    @Value("${base.path}")
    String basePath;

    @Value("${cw.gram.api.key}")
    int apiKey;

    @Value("${cw.gram.api.hash}")
    String apiHash;

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


        return containers;
    }

}
