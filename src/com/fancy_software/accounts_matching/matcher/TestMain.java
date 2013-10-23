package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountReader;
import com.fancy_software.accounts_matching.matcher.lda.TestTextComparator;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class TestMain {

    private static final String PATH_FROM = "accounts/vk";
    private static final String PATH_TO = "accounts/social_network2";

    public static void main(String args[]) throws Exception {
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
        matcher.match(true);
//        HashMap<Double, String[]> map = TestTextComparator.getLocalRes(LocalAccountReader.readAccountFromLocalBase("accounts/vk/248121.xml").getGroups());
//        for(Double s:map.keySet()){
//            for(String s1:map.get(s))
//                System.out.println(s1);
//        }
    }
}
