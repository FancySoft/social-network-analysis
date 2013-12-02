package com.fancy_software.crawling;

import com.fancy_software.accounts_matching.io_local_base.Utils;

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
    private long startId = 0;
    private long finishId = 200000000;

    public long getFinishId() {
        return finishId;
    }

    public void setFinishId(long finishId) {
        this.finishId = finishId;
    }

    public long getStartId() {
        return startId;
    }

    public void setStartId(long startId) {
        this.startId = startId;
    }

    @Override
    public void init() {
        passwordMap = Utils.getAuthInfo(AUTH_PATH);
        System.out.println(passwordMap);
    }

    @Override
    public void start() {
        int amount = passwordMap.keySet().size();
        ExecutorService executor = Executors.newFixedThreadPool(amount);
        long start = startId;
        long perCaller = finishId / amount;
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