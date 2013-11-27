package com.fancy_software.accounts_matching.crawling.crawlers;

public interface ICrawler {

    public void init(Long id);

    public void start();

    public void finish();
}
