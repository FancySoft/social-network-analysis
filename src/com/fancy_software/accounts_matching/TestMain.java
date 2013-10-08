package com.fancy_software.accounts_matching;

import com.fancy_software.accounts_matching.matcher.AccountMatcher;

import java.io.FileNotFoundException;

public class TestMain {

    public static void main(String args[]) throws FileNotFoundException {
        //don't commit
        AccountMatcher matcher = new AccountMatcher();
        matcher.init(692471, "accounts/vk/", "accounts/vk/");
        matcher.match();
//        System.out.println(Utils.generatePathToAccounts(SocialNetworkId.VK,22));
//        LocalAccountWriter.writeAccountToLocalBase(new AccountVector(), "accounts/22.xml");
    }
}
