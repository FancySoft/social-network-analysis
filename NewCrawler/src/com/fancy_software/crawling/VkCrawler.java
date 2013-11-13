package com.fancy_software.crawling;

import com.fancy_software.accounts_matching.io_local_base.Settings;

/**
 * Created by Yaro
 * Date: 30.10.13
 * Time: 15:39
 */
public class VkCrawler implements ICrawler {

    private static final String KEY_VK_LOGIN = "vk_login";
    private static final String KEY_VK_PASSWORD = "vk_password";
    private String login;
    private String password;

    private VkApiCaller vkApiCaller;

    public VkCrawler() {
        vkApiCaller = new VkApiCaller();
    }

    public void init() {
        Settings settings = Settings.getInstance();
        login = settings.get(KEY_VK_LOGIN);
        password = settings.get(KEY_VK_PASSWORD);
    }

    @Override
    public void start() {
        vkApiCaller.auth(login, password);
//        vkApiCaller.start(ExtractType.ACCOUNTS);
        vkApiCaller.start(ExtractType.FRIENDS);
//        vkApiCaller.start(ExtractType.GROUPS);
    }


    @Override
    public void finish() {

    }
}