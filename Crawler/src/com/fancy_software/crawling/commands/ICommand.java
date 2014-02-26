package com.fancy_software.crawling.commands;

public interface ICommand {

    public void execute(String[] args);

    public boolean checkArgs(String[] args);
}
