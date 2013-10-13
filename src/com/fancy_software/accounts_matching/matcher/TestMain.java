package com.fancy_software.accounts_matching.matcher;

import java.io.FileNotFoundException;

public class TestMain {

    private static final String PATH_FROM = "accounts/vk";
    private static final String PATH_TO = "accounts/social_network2";

    public static void main(String args[]) throws FileNotFoundException {
        String pathFrom;
        String pathTo;
        if (args.length < 2) {
            pathFrom = PATH_FROM;
            pathTo = PATH_TO;
        }
        else {
            pathFrom = args[0];
            pathTo = args[1];
        }

        AccountMatcher matcher = new AccountMatcher();
        matcher.init(pathFrom, pathTo);
        matcher.match(false);
    }
}
