package com.fancy_software.accounts_matching.crawler;

import java.io.*;

public class Main {

    private static final String LIST = "accounts/parsing_list.txt";

    public static void main(String args[]) {

        String path;
        if (args.length == 0) path = LIST;
        else path = args[0];

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new DataInputStream(new FileInputStream(path))));
            String line;
            while ((line = reader.readLine()) != null) {
                Parsers.parseVK(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
