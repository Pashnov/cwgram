package com.apashnov.cwgram.cw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CaptchaSolver {

    private Map<String, List<String>> captcha = new HashMap<>();

    public String solve(String msg){
        for (Map.Entry<String, List<String>> entry : captcha.entrySet()) {
            for (String keyWord : entry.getValue()) {
                if(msg.contains(keyWord)){
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    @Autowired
    private void extractKeyWords(Environment env){
        captcha.put("\uD83C\uDF46\uD83E\uDD55", Stream.of(env.getProperty("eggplantAndCarrot").split(",")).collect(Collectors.toList()));
        captcha.put("\uD83C\uDF5E\uD83E\uDDC0", Stream.of(env.getProperty("breadAndCheese").split(",")).collect(Collectors.toList()));
        captcha.put("\uD83C\uDF49\uD83C\uDF52", Stream.of(env.getProperty("watermelonAndCherry").split(",")).collect(Collectors.toList()));
        captcha.put("\uD83C\uDF55", Stream.of(env.getProperty("pizza").split(",")).collect(Collectors.toList()));
        captcha.put("\uD83E\uDDC0", Stream.of(env.getProperty("cheese").split(",")).collect(Collectors.toList()));
        captcha.put("\uD83C\uDF2D", Stream.of(env.getProperty("hotdog").split(",")).collect(Collectors.toList()));
        captcha.put("\uD83D\uDC16", Stream.of(env.getProperty("pig").split(",")).collect(Collectors.toList()));
        captcha.put("\uD83D\uDC0E", Stream.of(env.getProperty("horse").split(",")).collect(Collectors.toList()));
        captcha.put("\uD83D\uDC3F", Stream.of(env.getProperty("squirrel").split(",")).collect(Collectors.toList()));
        captcha.put("\uD83D\uDC10", Stream.of(env.getProperty("goat").split(",")).collect(Collectors.toList()));
        captcha.put("\uD83D\uDC15", Stream.of(env.getProperty("dog").split(",")).collect(Collectors.toList()));
        captcha.put("\uD83D\uDC08", Stream.of(env.getProperty("cat").split(",")).collect(Collectors.toList()));
    }

}
