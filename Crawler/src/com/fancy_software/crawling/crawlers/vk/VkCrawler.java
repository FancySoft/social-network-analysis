package com.fancy_software.crawling.crawlers.vk;

import com.fancy_software.accounts_matching.model.SocialNetworkId;
import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.crawling.parsers.IParser;
import com.fancy_software.crawling.parsers.vk.VkApiParser;
import com.fancy_software.crawling.utils.ExtractType;
import com.fancy_software.utils.Settings;
import com.fancy_software.utils.io.LocalAccountReader;

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

    public VkCrawler(ExtractType extractType, boolean useIdList) {
        if (extractType != ExtractType.SAMPLE)
            throw new IllegalArgumentException();
        this.extractType = extractType;
        this.useIdList = useIdList;
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
    public void init() {
        Settings settings = Settings.getInstance();
        initialId = settings.get(Settings.VK_START_SAMPLE_ID);
        loginList = settings.getArray(Settings.VK_LOGINS);
        passwordList = settings.getArray(Settings.VK_PASSWORDS);
        String startIdPath = settings.get(Settings.VK_START_ID_PATH);
        startIds = LocalAccountReader.getStartIds(startIdPath);
        super.init();
    }

    @Override
    public void start() {
        int amount = loginList.size();
        executor = Executors.newFixedThreadPool(amount);
        long start = startId;
        long perCaller = finishId / amount;
        long finish = perCaller;

        ListIterator<String> loginIterator = loginList.listIterator();
        ListIterator<String> passwordIterator = passwordList.listIterator();

        while (loginIterator.hasNext() && passwordIterator.hasNext()) {
            IParser vkApiParser = createVkParser(start, finish);
            executor.execute(new ParserRunner(vkApiParser, loginIterator.next(), passwordIterator.next()));
            start += perCaller;
            finish += perCaller;
        }
        executor.shutdown();
    }

    public IParser createVkParser(long startId, long finishId) {
        switch (extractType) {
            case ALL_ACCOUNTS:
            case GROUPS:
            case FRIENDS:
            case WALL:
                return new VkApiParser(this, startId, finishId);
            case SAMPLE:
                if (useIdList)
                    return new VkApiParser(this, startIds);
                else
                    return new VkApiParser(this, initialId);
            default:
                return null;
        }
    }
}