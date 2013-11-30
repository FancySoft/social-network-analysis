package com.fancy_software.accounts_matching.crawling;

import com.fancy_software.accounts_matching.crawling.parsers.*;
import com.fancy_software.accounts_matching.model.SocialNetworkId;

public class ParserFactory {

    public static AbstractParser getApiWorkerInstance(SocialNetworkId networkId) {
        switch (networkId) {
            case VK:
                return new VkParser(networkId);
            case Facebook:

            default:
                return null;

        }
    }

}
