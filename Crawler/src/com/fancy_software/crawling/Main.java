package com.fancy_software.crawling;

import com.fancy_software.crawling.crawlers.vk.VkCrawler;
import org.apache.commons.logging.LogFactory;

import java.util.logging.Level;

/**
 * Created by Yaro
 * Date: 01.11.13
 * Time: 23:03
 */

public class Main {

    public static void main(String[] args) {
        init();
        VkCrawler vkCrawler = new VkCrawler();
        vkCrawler.init();
        vkCrawler.start();
    }

    /**
     * Logs of htmlunit off
     */
    private static void init() {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }


}