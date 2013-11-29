package com.fancy_software.accounts_matching.serverapp.localio;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * ************************************************************************
 * Created by akirienko on 04.11.13
 * Copyright (c) 2013 Desman, Inc. All rights reserved.
 * ************************************************************************
 */

/**
 * NOT THREAD-SAFE
 */
public class Settings {
    private static final String PATH = "config/settings.txt";
    public static final Settings INSTANCE = new Settings();
    private Map<String, String> settings;

    private static final Logger log = Logger.getLogger(Settings.class.getName());

    private Settings() {
        settings = new HashMap<String, String>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new DataInputStream(new FileInputStream(PATH))));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cur = line.split(" = ");
                if (cur.length != 2) {
                    log.error("Settings file corrupted! Please verify if it has the following format:" +
                            "key = value");
                    break;
                }
                settings.put(cur[0], cur[1]);
            }
        } catch (FileNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Get stored value for key
     *
     * @param key key
     * @return value
     */
    public String get(String key) {
        return settings.get(key);
    }

    /**
     * Store value for key
     *
     * @param key   key
     * @param value value
     */
    public void put(String key, String value) {
        settings.put(key, value);
        writeToFile();
    }

    private void writeToFile() {
        try {
            PrintWriter writer = new PrintWriter(PATH);
            for (String key : settings.keySet()) {
                writer.println(String.format("%s = %s", key, settings.get(key)));
            }
            writer.flush();
        } catch (FileNotFoundException e) {
            log.error(e);
        }
    }

    @Override
    public void finalize() throws Throwable {
        writeToFile();
        super.finalize();
    }
}
