package com.fancy_software.crawling;

import com.fancy_software.accounts_matching.io_local_base.Settings;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Yaro
 * Date: 30.10.13
 * Time: 15:39
 */
public class VkCrawler implements ICrawler {

    private Map<String, String> passwordMap;
    private long maxAccount = 200000000;

    public void init() {
        Settings settings = Settings.getInstance();
        passwordMap = settings.getSettings();
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