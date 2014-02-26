package com.fancy_software.crawling.commands;

public class CommandFactory {

    public static ICommand createCommand(String command) {

        switch (command) {
            case "START": {
                return new StartCommand();
            }
            case "FINISH": {
                return new FinishCommand();
            }
            case "HELP": {
                return new HelpCommand();
            }
            default:
                return null;

        }
    }
}
