package com.fancy_software.crawling;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

/**
 * Created by Yaro
 * * Date: 15.11.13
 * Time: 15:09
 */
public class Utils {

    public static Map<String, String> getAuthInfo(String path) {
        XStream xstream = new XStream(new DomDriver());
        try {
            return (Map<String, String>) xstream.fromXML(new FileReader(path));

        } catch (FileNotFoundException | StreamException e) {
            e.printStackTrace();
        }
        return null;
    }

    //remove first element, which is command
    public static String[] getArgs(String fullCommand) {
        String[] commandAndArgs = fullCommand.split(" ");
        if (commandAndArgs.length > 1) {
            String[] args = new String[commandAndArgs.length - 1];
            for (int i = 0; i < args.length; i++)
                args[i] = commandAndArgs[i + 1];
            return args;
        } else
            return new String[0];
    }
}
