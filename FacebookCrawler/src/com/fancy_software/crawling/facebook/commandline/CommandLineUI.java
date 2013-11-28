package com.fancy_software.crawling.facebook.commandline;

import com.fancy_software.logger.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.System.out;

/**
 * ************************************************************************
 * Created by akirienko on 28.11.13
 * Copyright (c) 2013 Artem Kirienko. All rights reserved.
 * ************************************************************************
 */
public class CommandLineUI {

    private static final String TAG = CommandLineUI.class.getSimpleName();

    public void start() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        Command command;
        final String nl = System.getProperty("line.separator");
        while (true) {
            try {
                line = reader.readLine();
                command = Command.parseCommand(line);
                switch (command.getType()) {
                    case STOP:
                        return;
                    case PARSE_ALL:
                        break;
                    case PARSE_RANGE:
                        break;
                    case HELP:
                        out.println("Supported commands:" + nl +
                                    "parse [from_id] [to_id]" + nl +
                                    "stop");
                        break;
                    case UNKNOWN:
                        out.println("Unrecognized command. Type \"help\" or \"?\" for help.");
                        break;
                }
                Thread.sleep(50);
            } catch (InterruptedException | IOException e) {
                Log.e(TAG, e);
            }
        }
    }
}
