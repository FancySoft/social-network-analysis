package com.fancy_software.crawling;

import com.fancy_software.accounts_matching.io_local_base.Settings;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Yaro
 * Date: 30.10.13
 * Time: 15:39
 */
public class VkCrawler implements ICrawler {

    private static final String AUTH_PATH = "NewCrawler/config/settings.xml";
    private long startId  = 0;
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

    }

    @Override
    public void start() {
        Settings settings = Settings.getInstance();
        List<String> logins = settings.getArray(Settings.VK_LOGINS);
        List<String> passwords =settings.getArray(Settings.VK_PASSWORDS);
        int amount = logins.size();
        ExecutorService executor = Executors.newFixedThreadPool(amount);
        long start = startId;
        long perCaller = finishId / amount;
        long finish = perCaller;

        ListIterator<String> loginIter = logins.listIterator();
        ListIterator<String> passwordIter = passwords.listIterator();

        while (loginIter.hasNext() && passwordIter.hasNext()){
            VkApiCaller apiCaller = new VkApiCaller(start, finish);
            executor.execute(new CallRunner(apiCaller, loginIter.next(), passwordIter.next(), ExtractType.ACCOUNTS));
            start += perCaller;
            finish += perCaller;
        }
        executor.shutdown();
    }

    @Override
    public void finish() {

    }

}