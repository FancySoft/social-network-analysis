package com.fancy_software.accounts_matching.crawling.crawlers;

import com.fancy_software.accounts_matching.crawling.ParserFactory;
import com.fancy_software.accounts_matching.crawling.parsers.SocialNetworkId;
import com.fancy_software.accounts_matching.model.AccountVector;

import java.io.*;

public class VkCrawler extends AbstractCrawler {

    private static String authConfigName = "config/authentication_config.txt";
    private static String VK_LOGIN;
    private static String VK_PASSWORD;

    public VkCrawler() {
        super();
    }



    @Override
    public void init(Long id) {
        super.init(id);
        parser = ParserFactory.getApiWorkerInstance(SocialNetworkId.VK);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new DataInputStream(new FileInputStream(authConfigName))));
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (count == 0) {
                    count++;
                    VK_LOGIN = line;
                } else
                    VK_PASSWORD = line;
            }
            parser.auth(VK_LOGIN, VK_PASSWORD);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        System.out.println(VK_LOGIN);
        System.out.println(VK_PASSWORD);
        AccountVector cur;
        while(usersToParse.size()>0){
            cur = parser.parse(Long.toString(usersToParse.remove()));
            for (Long l : cur.getFriends()) addUserToParse(l);
        }

    }

}
