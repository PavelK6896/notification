package ru.geekbrains.parser.cian.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UserAgentService {

    @Value("classpath:data/user-agents.txt")
    Resource resourceFile;


    public String getRandomUserAgent() {

        List<String> userAgents = this.getUserAgents();
        if (userAgents.size() == 0) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(userAgents.size());

        return userAgents.get(index);
    }

    private List<String> getUserAgents() {

        BufferedReader reader;
        List<String> userAgents = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(this.resourceFile.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                if (!line.isEmpty()) {
                    userAgents.add(line);
                }

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userAgents;
    }
}
