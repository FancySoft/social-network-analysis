package com.fancy_software.accounts_matching.crawling.parsers;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.IUserId;
import com.fancy_software.accounts_matching.model.WallMessage;

import java.util.Collection;

public interface IParser {

    public AccountVector parse(String id);

    public Collection<WallMessage> getFeed(IUserId id);

    public void auth(String login, String password);

}
