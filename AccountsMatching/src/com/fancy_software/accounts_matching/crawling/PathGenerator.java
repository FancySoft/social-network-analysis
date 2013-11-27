package com.fancy_software.accounts_matching.crawling;

import com.fancy_software.accounts_matching.crawling.parsers.SocialNetworkId;

public class PathGenerator {

    private static final String pathToAccounts = "accounts/";
    private static final String pathToVkAccounts = "D:/vk/";

    public static String generatePathToAccounts(SocialNetworkId networkId, long accountId) {
        StringBuilder builder = new StringBuilder(pathToAccounts);
        if (networkId.equals(SocialNetworkId.VK))
            builder.append(pathToVkAccounts);
        return String.valueOf(builder.append(accountId).append(".xml"));
    }

    public static String generateDefaultPath(long id){
        StringBuilder builder = new StringBuilder("D:/vk_new/");
        builder.append(id);
        builder.append(".xml");
        return builder.toString();
    }
}
