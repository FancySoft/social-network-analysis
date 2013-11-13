package com.fancy_software.crawling;

/**
 * Created by Yaro
 * Date: 01.11.13
 * Time: 23:03
 */
public class Main {
    public static void main(String[] args) {
        VkCrawler c = new VkCrawler();
        c.init();
        c.start();
    }
}
