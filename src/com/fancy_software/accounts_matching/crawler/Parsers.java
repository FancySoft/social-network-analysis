package com.fancy_software.accounts_matching.crawler;

import com.fancy_software.accounts_matching.crawler.apiworkers.IApiWorker;
import com.fancy_software.accounts_matching.crawler.apiworkers.SocialNetworkId;
import com.fancy_software.accounts_matching.model.AccountVector;

import java.io.*;


public class Parsers {

    private static String authConfigName = "config/authentication_config.txt";
    private static String VK_LOGIN;
    private static String VK_PASSWORD;
    private static boolean needVKAuth = true;


    public static void init() {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Парсить страничку вконтакте
     *
     * @param id пользователя
     * @return вектор пользователя
     * @throws IOException
     */
    public static AccountVector parseVK(String id) throws IOException {
        init();
        System.out.println(VK_LOGIN);
        System.out.println(VK_PASSWORD);
        IApiWorker VK_WORKER = ApiWorkerFactory.getApiWorkerInstance("accounts/vk/", SocialNetworkId.VK);
        if (needVKAuth) {
            VK_WORKER.Auth(VK_LOGIN, VK_PASSWORD);
            needVKAuth = false;
        }
        return VK_WORKER.Parse(id);
    }

    /**
     * Парсить пользователя в твиттере
     *
     * @param id
     * @return
     */
    public static AccountVector parseTwitter(String id) {
        return null;
    }

    /**
     * Парсить страничку в фейсбуке
     *
     * @param url адрес странички
     * @return вектор пользователя
     */
    public static AccountVector parseFacebook(String url) {
        return null;
    }
}
