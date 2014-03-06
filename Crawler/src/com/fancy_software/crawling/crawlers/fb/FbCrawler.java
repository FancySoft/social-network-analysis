package com.fancy_software.crawling.crawlers.fb;

import com.fancy_software.accounts_matching.model.SocialNetworkId;
import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.crawling.parsers.IParser;
import com.fancy_software.crawling.parsers.fb.FbParser;
import com.fancy_software.crawling.utils.ExtractType;
import com.fancy_software.utils.Settings;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executors;

public class FbCrawler extends AbstractCrawler {

    {
        socialNetworkId = SocialNetworkId.FB;
    }

    public FbCrawler() {
        this.extractType = ExtractType.SAMPLE;
    }

    @Override
    public void start() {
        Settings settings = Settings.getInstance();
        List<String> loginList = settings.getArray(Settings.FB_LOGINS);
        List<String> passwordList = settings.getArray(Settings.FB_PASSWORDS);
        String initialId = settings.get(Settings.FB_START_SAMPLE_ID);
        int amount = loginList.size();
        executor = Executors.newFixedThreadPool(amount);

        ListIterator<String> loginIterator = loginList.listIterator();
        ListIterator<String> passwordIterator = passwordList.listIterator();

        while (loginIterator.hasNext() && passwordIterator.hasNext()) {
            IParser fbParser = new FbParser(this, initialId);
            executor.execute(new ParserRunner(fbParser, loginIterator.next(), passwordIterator.next()));
        }
        executor.shutdown();
    }
}