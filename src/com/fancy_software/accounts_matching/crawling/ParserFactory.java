package com.fancy_software.accounts_matching.crawling;

import com.fancy_software.accounts_matching.crawling.crawlers.ICrawler;
import com.fancy_software.accounts_matching.crawling.parsers.IParser;
import com.fancy_software.accounts_matching.crawling.parsers.OdnoklassnikiParser;
import com.fancy_software.accounts_matching.crawling.parsers.SocialNetworkId;
import com.fancy_software.accounts_matching.crawling.parsers.VkParser;

public class ParserFactory {

    public static IParser getApiWorkerInstance(SocialNetworkId networkId, ICrawler crawler) {
        switch (networkId) {
            case VK:
                return new VkParser(networkId, crawler);
            case Odnoklassniki:
                return new OdnoklassnikiParser(networkId, crawler);
            case Facebook:

            default:
                return null;

        }
    }

}
