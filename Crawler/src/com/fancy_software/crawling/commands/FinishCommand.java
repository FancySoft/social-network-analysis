package com.fancy_software.crawling.commands;

/**
 * Created by Yaro
 * Date: 25.11.13
 * Time: 16:53
 */
public class FinishCommand implements ICommand {

    String[] arguments = {"NOTSAVE"};

    @Override
    public void execute(String[] args) {
        if (checkArgs(args)) {
            //todo finish implementation
        } else {
            System.out.println("Illegal arguments. Use help command");
        }

    }

    @Override
    public boolean checkArgs(String[] args) {
        return args.length <= 2 && (args.length != 1 || (arguments[0].equals(args[0])));
    }

}
