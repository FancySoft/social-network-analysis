package com.fancy_software.accounts_matching.crawling;

import com.fancy_software.accounts_matching.crawling.crawlers.ICrawler;
import com.fancy_software.accounts_matching.crawling.crawlers.VkCrawler;
import com.fancy_software.accounts_matching.deprecated.Parsers;

import java.io.*;

public class Main {

    private static final String LIST = "accounts/parsing_list.txt";

    public static void main(String args[]) {

        String path;
        if (args.length == 0) path = LIST;
        else path = args[0];

        ICrawler crawler=new VkCrawler();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new DataInputStream(new FileInputStream(path))));
            String line;
            while ((line = reader.readLine()) != null) {
                crawler.init(line);
            }
            crawler.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
