package com.fancy_software.crawling;

import com.fancy_software.crawling.commands.CommandFactory;
import com.fancy_software.crawling.commands.ICommand;

import java.util.Scanner;

/**
 * Created by Yaro
 * Date: 01.11.13
 * Time: 23:03
 */

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter command. Enter help for more information");
            System.out.print("> ");
            String fullCommand = scanner.nextLine().toUpperCase();
            if(fullCommand.equals("EXIT"))
                break;
            String[] commandAndArgs = fullCommand.split(" ");
            ICommand command = CommandFactory.createCommand(commandAndArgs[0]);
            try {
                command.execute(Utils.getArgs(fullCommand));
            } catch (NullPointerException e) {
                System.out.println(fullCommand);
                System.out.println("No such command");
//                e.printStackTrace();
                System.out.println();
            }
        }
    }


}