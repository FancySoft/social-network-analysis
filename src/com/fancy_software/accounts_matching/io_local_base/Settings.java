package com.fancy_software.accounts_matching.io_local_base;

import com.fancy_software.logger.Log;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
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
    private static final String PATH = "config/settings.xml";
    private static SoftReference<Settings> instance;
    private Map<String, String> settings;

    private Settings() {
        XStream xstream = new XStream(new DomDriver());
        try {
            settings = (HashMap<String, String>) xstream.fromXML(new FileReader(PATH));
            System.out.println(settings);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamException e) {
            e.printStackTrace();
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

    public Map<String, String> getSettings() {
        return settings;
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
