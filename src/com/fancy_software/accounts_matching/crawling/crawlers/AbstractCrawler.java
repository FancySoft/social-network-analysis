package com.fancy_software.accounts_matching.crawling.crawlers;

import com.fancy_software.accounts_matching.crawling.parsers.IParser;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class AbstractCrawler implements ICrawler {

    protected String id;
    protected IParser parser;
    protected Queue<Long> usersToParse;
    protected String login;
    protected String password;

    public AbstractCrawler(){
        usersToParse=new LinkedBlockingQueue<Long>();
    }

    @Override
    public void init(String id) {
        this.id=id;
    }

    @Override
    public void auth(String login, String password) {
        this.login=login;
        this.password=password;
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {
        //todo write queue content in file
    }

    public void addUserToParse(Long id){
        usersToParse.add(id);
    }
}
