package com.fancy_software.crawling.crawlers.vk;

import com.fancy_software.accounts_matching.io_local_base.Settings;
import com.fancy_software.accounts_matching.model.SocialNetworkId;
import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.crawling.parsers.IParser;
import com.fancy_software.crawling.parsers.vk.VkApiParser;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executors;

/**
 * Created by Yaro
 * Date: 30.10.13
 * Time: 15:39
 */
public class VkCrawler extends AbstractCrawler {

    private long startId  = 0;
    private long finishId = 200000000;

    {
        socialNetworkId = SocialNetworkId.VK;
    }

    public VkCrawler(ExtractType extractType) {
        this.extractType = extractType;
    }

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
    public void start() {
        Settings settings = Settings.getInstance();
        List<String> loginList = settings.getArray(Settings.VK_LOGINS);
        List<String> passwordList = settings.getArray(Settings.VK_PASSWORDS);
        int amount = loginList.size();
        executor = Executors.newFixedThreadPool(amount);
        long start = startId;
        long perCaller = finishId / amount;
        long finish = perCaller;

        ListIterator<String> loginIterator = loginList.listIterator();
        ListIterator<String> passwordIterator = passwordList.listIterator();

        while (loginIterator.hasNext() && passwordIterator.hasNext()) {
            IParser apiCaller = new VkApiParser(this, start, finish);
            executor.execute(new ParserRunner(apiCaller, loginIterator.next(), passwordIterator.next()));
            start += perCaller;
            finish += perCaller;
        }
        executor.shutdown();
    }

}