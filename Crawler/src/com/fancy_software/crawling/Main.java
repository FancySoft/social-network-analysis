package com.fancy_software.crawling;

import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.crawling.crawlers.fb.FbCrawler;
import com.fancy_software.crawling.crawlers.vk.VkCrawler;
import com.fancy_software.crawling.utils.ExtractType;
import org.apache.commons.logging.LogFactory;

import java.util.logging.Level;

/**
 * Created by Yaro
 * Date: 01.11.13
 * Time: 23:03
 */

public class Main {

    private static AbstractCrawler crawler;

    public static void main(String[] args) {
        init();
        runCrawlers();
    }

    @SuppressWarnings("unused")
    public static void testCrawlers() {
        boolean useIdList = true;
        crawler = new FbCrawler(useIdList);
        crawler.init();
        crawler.start();
        try {
            Thread.sleep(43000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            crawler.finish();
        }
        System.out.println("stop");
    }

    public static void runCrawlers() {
        crawler = new FbCrawler(true);
        crawler.init();
        crawler.start();
        // TODO: vk crawler
    }

    /**
     * Logs of htmlunit off
     */
    private static void init() {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
                                             "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }


}