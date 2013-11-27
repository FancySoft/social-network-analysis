package com.fancy_software.accounts_matching.crawling.crawlers;

import com.fancy_software.accounts_matching.crawling.parsers.IParser;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class AbstractCrawler implements ICrawler {

    protected IParser parser;
    protected Queue<Long> usersToParse;

    public AbstractCrawler(){
        usersToParse=new LinkedBlockingQueue<Long>();
    }

    @Override
    public void init(Long id) {
        usersToParse.add(id);
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {
        //todo write queue content in file
    }

    public void addUserToParse(Long id) {
        // TODO:check if already parsed
        if (!usersToParse.contains(id)) usersToParse.add(id);
    }
}
