package com.fancy_software.crawling.commands;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountWriter;
import com.fancy_software.crawling.VkCrawler;

/**
 * User: Yaro
 * Date: 25.11.13
 * Time: 16:54
 */
public class StartCommand implements ICommand {

    @Override
    public void execute(String[] args) {
        if (checkArgs(args)) {
            VkCrawler vkCrawler = new VkCrawler();
            if (args.length > 0) {
                try {
                    long startId = Long.parseLong(args[0]);
                    long finishId = Long.parseLong(args[1]);
                    vkCrawler.setStartId(startId);
                    vkCrawler.setFinishId(finishId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            if (args.length == 3)
                LocalAccountWriter.setPath(args[2]);

            vkCrawler.init();
            vkCrawler.start();
        }

    }

    @Override
    public boolean checkArgs(String[] args) {
        return args.length == 0 || args.length == 2 || args.length == 3;
    }

}
