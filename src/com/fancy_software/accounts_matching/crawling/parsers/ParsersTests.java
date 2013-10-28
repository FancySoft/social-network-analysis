package com.fancy_software.accounts_matching.crawling.parsers;

import com.fancy_software.accounts_matching.crawling.ParserFactory;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.BirthDate;
import org.junit.Assert;
import org.junit.Test;

/**
 * ************************************************************************
 * Created by akirienko on 28.10.13
 * Copyright (c) 2013 Desman, Inc. All rights reserved.
 * ************************************************************************
 */
public class ParsersTests {

    // Do not commit this, if it is your actual data
    private final String VK_USERNAME = "";
    private final String VK_PASSWORD = "";

    @Test
    public void vkSearch() {
        AbstractParser parser = ParserFactory.getApiWorkerInstance(SocialNetworkId.VK);
        parser.auth(VK_USERNAME, VK_PASSWORD);
        AccountVector goal = new AccountVector();
        goal.setFirst_name("Artem");
        goal.setLast_name("Kirienko");
        goal.setBdate(new BirthDate(16, 3, 1992));
        Assert.assertNotNull(parser.match(goal));
    }
}
