package com.fancy_software.accounts_matching.crawler.facebook_parser_test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {
        FacebookBasicParser p = new FacebookBasicParser();
        List<Object> info = p.getAccountInfo("test_account_info_for_parser/user_info.htm");
        Set<String> friends = p.getAccountFriends("test_account_info_for_parser/user_friends.htm");
        System.out.println("RESULT info: " + info.size());
        System.out.println(info);
        System.out.println("RESULT friends: " + friends.size());
        System.out.println(friends);
    }
}