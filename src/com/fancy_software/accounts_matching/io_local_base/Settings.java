package com.fancy_software.accounts_matching.io_local_base;

import com.fancy_software.logger.Log;

import java.io.*;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * ************************************************************************
 * Created by akirienko on 04.11.13
 * Copyright (c) 2013 Artem Kirienko. All rights reserved.
 * ************************************************************************
 */

/**
 * NOT THREAD-SAFE
 */
public class Settings {

    private static final String TAG = Settings.class.getSimpleName();
    private static final String PATH = "config/settings.txt";
    private static SoftReference<Settings> instance;
    private Map<String, String> settings;

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
                    Log.e(TAG, "Settings file corrupted! Please verify if it has the following format:\n" +
                            "key = value\n");
                    break;
                }
                settings.put(cur[0], cur[1]);
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e);
        } catch (IOException e) {
            Log.e(TAG, e);
        }
    }

    /**
     * Get an instance of Settings to use
     *
     * @return instance
     */
    public static Settings getInstance() {
        if (instance == null) instance = new SoftReference<Settings>(new Settings());
        if (instance.get() == null) instance = new SoftReference<Settings>(new Settings());
        return instance.get();
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
            Log.e(TAG, e);
        }
    }
}
