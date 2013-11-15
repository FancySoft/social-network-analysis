package com.fancy_software.crawling;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Yaro
 * Date: 30.10.13
 * Time: 15:39
 */
public class VkCrawler implements ICrawler {

    private static final String AUTH_PATH = "NewCrawler/config/settings.xml";
    private Map<String, String> passwordMap;
    private long maxAccount = 200000000;

    public void init() {
        passwordMap = Utils.getAuthInfo(AUTH_PATH);
        System.out.println(passwordMap);
    }

    @Override
    public void start() {
        int amount = passwordMap.keySet().size();
        ExecutorService executor = Executors.newFixedThreadPool(amount);
        long start = 0;
        long perCaller = maxAccount / amount;
        long finish = perCaller;
        for (Map.Entry<String, String> entry : passwordMap.entrySet()) {
            VkApiCaller apiCaller = new VkApiCaller(start, finish);
            executor.execute(new CallRunner(apiCaller, entry.getKey(), entry.getValue(), ExtractType.ACCOUNTS));
            start += perCaller;
            finish += perCaller;
        }
        executor.shutdown();
    }

    @Override
    public void finish() {

    }

}