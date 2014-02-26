package com.fancy_software.crawling;

/**
 * Created by Yaro
 * Date: 01.11.13
 * Time: 23:03
 */

public class Main {

    public static void main(String[] args) {
        VkCrawler vkCrawler = new VkCrawler();
        vkCrawler.init();
        vkCrawler.start();
    }


}