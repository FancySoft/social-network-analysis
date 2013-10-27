package com.fancy_software.accounts_matching.crawling.parsers;

import com.fancy_software.accounts_matching.crawling.crawlers.ICrawler;
import com.fancy_software.accounts_matching.model.AccountVector;

/**
 * Created with IntelliJ IDEA.
 * User: yaro
 * Date: 23.10.13
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
public class FacebookParser extends AbstractParser {

    public FacebookParser(SocialNetworkId networkId, ICrawler crawler) {
        super(networkId, crawler);
    }

    @Override
    public AccountVector parse(String id) {
        return null;
    }

    @Override
    public AccountVector match(AccountVector goal) {
        return null;
    }
}
