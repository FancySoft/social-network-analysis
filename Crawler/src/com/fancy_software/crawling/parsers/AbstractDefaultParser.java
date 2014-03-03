package com.fancy_software.crawling.parsers;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.crawling.utils.ExtractType;

import java.util.List;

public abstract class AbstractDefaultParser implements IParser {

    protected AbstractCrawler crawler;

    protected ExtractType extractType;

    @Override
    public void auth(String login, String password) {

    }

    @Override
    public void start() {

    }

    public ExtractType getExtractType() {
        return extractType;
    }

    protected void notifyCrawler(AccountVector vector) {
        crawler.addUsersToWrite(vector);
    }

    protected void notifyCrawler(List<AccountVector> list) {
        crawler.addUsersToWrite(list);
    }


}
