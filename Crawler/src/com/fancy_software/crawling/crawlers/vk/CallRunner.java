package com.fancy_software.crawling.crawlers.vk;

import com.fancy_software.crawling.crawlers.vk.VkCrawler.ExtractType;
/**
 * Created by Yaro
 * Date: 13.11.13
 * Time: 18:10
 */
public class CallRunner implements Runnable {

    VkApiCaller           apiCaller;
    String                login;
    String                password;
    ExtractType extractType;

    public CallRunner(VkApiCaller apiCaller, String login, String password, ExtractType extractType) {
        this.apiCaller = apiCaller;
        this.login = login;
        this.password = password;
        this.extractType = extractType;
    }

    @Override
    public void run() {
        apiCaller.auth(login, password);
        apiCaller.start(extractType);
    }
}
