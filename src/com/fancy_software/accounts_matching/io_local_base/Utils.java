package com.fancy_software.accounts_matching.io_local_base;

import com.fancy_software.accounts_matching.crawler.apiworkers.SocialNetworkId;

public class Utils {

    private static final String pathToAccounts = "accounts/";
    private static final String pathToVkAccounts = "vk/";

    public static String generatePathToAccounts(SocialNetworkId networkId, long accountId) {
        StringBuilder builder = new StringBuilder(pathToAccounts);
        if (networkId.equals(SocialNetworkId.VK))
            builder.append(pathToVkAccounts);
        return String.valueOf(builder.append(accountId).append(".xml"));
    }
}
