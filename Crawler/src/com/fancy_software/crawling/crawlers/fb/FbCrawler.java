package com.fancy_software.crawling.crawlers.fb;

import com.fancy_software.accounts_matching.io_local_base.Settings;
import com.fancy_software.accounts_matching.model.SocialNetworkId;
import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.crawling.parsers.IParser;
import com.fancy_software.crawling.parsers.fb.FbParser;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executors;

public class FbCrawler extends AbstractCrawler {

    {
        socialNetworkId = SocialNetworkId.FB;
    }

    public FbCrawler(ExtractType extractType) {
        this.extractType = extractType;
    }


    @Override
    public void start() {
        Settings settings = Settings.getInstance();
        List<String> loginList = settings.getArray(Settings.FB_LOGINS);
        List<String> passwordList = settings.getArray(Settings.FB_PASSWORDS);
        int amount = loginList.size();
        executor = Executors.newFixedThreadPool(amount);

        ListIterator<String> loginIterator = loginList.listIterator();
        ListIterator<String> passwordIterator = passwordList.listIterator();

        while (loginIterator.hasNext() && passwordIterator.hasNext()) {
            IParser fbParser = new FbParser();
            executor.execute(new ParserRunner(fbParser, loginIterator.next(), passwordIterator.next()));
        }
        executor.shutdown();
    }
}
