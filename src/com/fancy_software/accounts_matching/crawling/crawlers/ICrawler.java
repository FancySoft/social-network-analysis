package com.fancy_software.accounts_matching.crawling.crawlers;

public interface ICrawler {

    public void init(String id);

    public void auth(String login, String password);

    public void start();

    public void finish();
}
