package com.fancy_software.crawling.facebook.commandline;

import com.fancy_software.logger.Log;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * ************************************************************************
 * Created by akirienko on 28.11.13
 * Copyright (c) 2013 Artem Kirienko. All rights reserved.
 * ************************************************************************
 */
public class Command {
    private static final String TAG = Command.class.getSimpleName();
    private Collection<Long> mLongArgs;
    private Type             mType;

    private Command(Type type) {
        mType = type;
        mLongArgs = Collections.emptyList();
    }

    private Command(Type type, Collection<Long> longArgs) {
        mType = type;
        mLongArgs = longArgs;
    }

    public static Command parseCommand(String command) {
        switch (command) {
            case "parse":
                return new Command(Type.PARSE_ALL);
            case "start":
                return new Command(Type.PARSE_ALL);
            case "exit":
                return new Command(Type.STOP);
            case "stop":
                return new Command(Type.STOP);
            case "quit":
                return new Command(Type.STOP);
            case "help":
                return new Command(Type.HELP);
            case "?":
                return new Command(Type.HELP);
            default:
                if (command.contains("parse")) {
                    String[] splited = command.split(" ");
                    try {
                        if (splited.length == 2) {
                            return new Command(Type.PARSE_RANGE, Arrays.asList((long) 0, Long.parseLong(splited[1])));
                        } else if (splited.length == 3) {
                            return new Command(Type.PARSE_RANGE,
                                               Arrays.asList(Long.parseLong(splited[1]), Long.parseLong(splited[2])));
                        }
                    } catch (NumberFormatException e) {
                        Log.d(TAG, e.toString());
                    }
                }
        }
        return new Command(Type.UNKNOWN);
    }

    public Type getType() {
        return mType;
    }

    public Collection<Long> getLongArgs() {
        return mLongArgs;
    }

    public static enum Type {
        PARSE_ALL,
        PARSE_RANGE,
        STOP,
        HELP,
        UNKNOWN
    }
}
