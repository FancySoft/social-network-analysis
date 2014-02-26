package com.fancy_software.crawling.commands;

/**
 * Created by Yaro
 * Date: 25.11.13
 * Time: 17:15
 */
public class HelpCommand implements ICommand {
    @Override
    public void execute(String[] args) {
        printInfo();
    }

    @Override
    public boolean checkArgs(String[] args) {
        return true;
    }

    private void printInfo() {
        System.out.println("Commands: help, start, finish");
        System.out.println();
        System.out.println("Arguments for start: download from, download to, path to save");
        System.out.println("Example for start: start");
        System.out.println("Example for start: start 0 100000");
        System.out.println("Example for start: start 0 100000 C:/temp/accounts.xml");
    }
}
