package com.fancy_software.accounts_matching.crawling;

import com.fancy_software.accounts_matching.crawling.parsers.*;

public class ParserFactory {

    public static AbstractParser getApiWorkerInstance(SocialNetworkId networkId) {
        switch (networkId) {
            case VK:
                return new VkParser(networkId);
            case Odnoklassniki:
                return new OdnoklassnikiParser(networkId);
            case Facebook:

            default:
                return null;

        }
    }

}
