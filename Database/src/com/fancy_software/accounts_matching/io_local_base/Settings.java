package com.fancy_software.accounts_matching.io_local_base;

import com.fancy_software.logger.Log;

import java.io.*;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ************************************************************************
 * Created by akirienko on 04.11.13
 * Copyright (c) 2013 Artem Kirienko. All rights reserved.
 * ************************************************************************
 */

public class Settings {

    private static final String TAG = Settings.class.getSimpleName();
    private static final String PATH = "config/settings.txt";
    private static SoftReference<Settings> instance;
    private Map<String, String> settings;

    public static final String VK_ACCOUNT_FOLDER="vk_account_folder";
    public static final String FB_ACCOUNT_FOLDER="fb_account_folder";
    public static final String VK_LOGINS="vk_logins";
    public static final String VK_PASSWORDS="vk_passwords";
    public static final String FB_LOGINS="fb_logins";
    public static final String FB_PASSWORDS="fb_passwords";



    private Settings() {
        settings = new HashMap<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new DataInputStream(new FileInputStream(PATH))));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                String[] cur = line.split(" = ");
                if (cur.length != 2) {
                    Log.e(TAG, "Settings file corrupted! Please verify if it has the following format:\n" +
                            "key = value\n");
                    break;
                }
                settings.put(cur[0], cur[1]);
            }
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
        if (instance == null) instance = new SoftReference<>(new Settings());
        if (instance.get() == null) instance = new SoftReference<>(new Settings());
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
     * NOT THREAD-SAFE
     * @param key   key
     * @param value value
     */
    public void put(String key, String value) {
        settings.put(key, value);
        writeToFile();
    }

    /**
     * Get stored array of values
     * @param key array key
     * @return    array
     */
    @SuppressWarnings("unused")
    public List<String> getArray(String key) {
        System.out.println(key+"_size");
        if (!settings.containsKey(key + "_size")) return null;
        int size = Integer.parseInt(settings.get(key + "_size"));
        List<String> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(settings.get(key + "_" + i));
        }
        return result;
    }

    /**
     * Store array of values
     * NOT THREAD-SAFE
     * @param key   key
     * @param value values
     */
    @SuppressWarnings("unused")
    public void putArray(String key, List<String> value) {
        settings.put(key + "_size", Integer.toString(value.size()));
        int i = 0;
        for (String s : value) {
            settings.put(key + "_" + i++, s);
        }
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