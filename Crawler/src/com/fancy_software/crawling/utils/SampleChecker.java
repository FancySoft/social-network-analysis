package com.fancy_software.crawling.utils;

import com.fancy_software.crawling.parsers.vk.VkApiParser;
import com.fancy_software.utils.Settings;
import com.fancy_software.utils.io.LocalAccountReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ************************************************************************
 * Created by akirienko on 25.04.14
 * Copyright (c) 2014 Artem Kirienko
 * ************************************************************************
 */
public class SampleChecker {
    private static final VkApiParser parser;

    static {
        parser = new VkApiParser(null);

        Settings settings = Settings.getInstance();
        List<String> logins = settings.getArray(Settings.VK_LOGINS);
        List<String> passwords = settings.getArray(Settings.VK_PASSWORDS);

        String login = logins.get(0);
        String password = passwords.get(0);

        parser.auth(login, password);
    }

    public static void main(String[] args) {
        Settings settings = Settings.getInstance();
        String startIdPath = settings.get(Settings.VK_START_ID_PATH);
        Set<String> ids = LocalAccountReader.getStartIds(startIdPath);
        String path = settings.get(Settings.VK_ACCOUNT_FOLDER);
        Set<String> notDownloaded = new HashSet<>();
        for (String id : ids) {
            String longId = parser.convertId(id);
            try {
                LocalAccountReader.readAccountFromLocalBase(path + File.separator + longId + ".xml");
            } catch (FileNotFoundException e) {
                System.out.println(longId);
                notDownloaded.add(longId);
            }
        }

        System.out.println("--------------------");
        for (String cur : notDownloaded)
            System.out.println(cur);
    }
}
