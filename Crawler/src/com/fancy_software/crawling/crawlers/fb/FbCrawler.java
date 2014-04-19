package com.fancy_software.crawling.crawlers.fb;

import com.fancy_software.accounts_matching.model.SocialNetworkId;
import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.crawling.parsers.AbstractSampleParser;
import com.fancy_software.crawling.parsers.IParser;
import com.fancy_software.crawling.parsers.fb.FbParser;
import com.fancy_software.crawling.utils.ExtractType;
import com.fancy_software.utils.Settings;
import com.fancy_software.utils.io.LocalAccountReader;

import java.util.ListIterator;
import java.util.concurrent.Executors;

public class FbCrawler extends AbstractCrawler {

    private AbstractSampleParser.SampleParseType parseType;

    public AbstractSampleParser.SampleParseType getParseType() {
        return parseType;
    }

    {
        socialNetworkId = SocialNetworkId.FB;
        extractType = ExtractType.SAMPLE;
    }

    public FbCrawler(boolean useIdList, AbstractSampleParser.SampleParseType parseType) {
        this.useIdList = useIdList;
        this.parseType = parseType;
    }

    @Override
    public void init() {
        Settings settings = Settings.getInstance();
        initialId = settings.get(Settings.FB_START_SAMPLE_ID);
        loginList = settings.getArray(Settings.FB_LOGINS);
        passwordList = settings.getArray(Settings.FB_PASSWORDS);
        String startIdPath = settings.get(Settings.FB_START_ID_PATH);
        startIds = LocalAccountReader.getStartIds(startIdPath);
        super.init();

    }

    @Override
    public void start() {
        int amount = loginList.size();
        executor = Executors.newFixedThreadPool(amount);

        ListIterator<String> loginIterator = loginList.listIterator();
        ListIterator<String> passwordIterator = passwordList.listIterator();

        while (loginIterator.hasNext() && passwordIterator.hasNext()) {
            IParser fbParser;
            if (useIdList)
                fbParser = new FbParser(this, startIds, parseType);
            else
                fbParser = new FbParser(this, initialId, parseType);
            executor.execute(new ParserRunner(fbParser, loginIterator.next(), passwordIterator.next()));
        }
        executor.shutdown();
    }
}
