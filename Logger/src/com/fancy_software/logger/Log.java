package com.fancy_software.logger;

import static java.lang.System.out;

/**
 * ************************************************************************
 * Created by akirienko on 04.11.13
 * Copyright (c) 2013 Desman, Inc. All rights reserved.
 * ************************************************************************
 */
public class Log {

    private static final boolean DEBUG = true;
    private static final boolean VERBOSE = true;

    /**
     * Debug level
     * @param tag     log tag
     * @param message log message
     */
    public static void d(String tag, String message) {
        if (DEBUG) out.println(String.format("%s : %s", tag, message));
    }

    /**
     * Error level
     * @param tag       log tag
     * @param message   log message
     */
    public static void e(String tag, String message) {
        if (VERBOSE) out.println(String.format("%s : %s", tag, message));
    }

    /**
     * Error level
     * @param tag   log tag
     * @param e     exception thrown
     */
    public static void e(String tag, Throwable e) {
        if (VERBOSE) out.println(String.format("%s : %s", tag, e.toString()));
    }
}
