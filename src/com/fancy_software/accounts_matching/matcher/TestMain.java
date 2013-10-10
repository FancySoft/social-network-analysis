package com.fancy_software.accounts_matching.matcher;

import java.io.FileNotFoundException;

public class TestMain {

    public static void main(String args[]) throws FileNotFoundException {
        //don't commit
        AccountMatcher matcher = new AccountMatcher();
        matcher.init("accounts/vk/", "accounts/social_network2/");
        matcher.match();
//        System.out.println(Utils.generatePathToAccounts(SocialNetworkId.VK,22));
//        LocalAccountWriter.writeAccountToLocalBase(new AccountVector(), "accounts/22.xml");
    }
}
