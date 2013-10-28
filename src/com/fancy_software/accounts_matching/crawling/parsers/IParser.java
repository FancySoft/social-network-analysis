package com.fancy_software.accounts_matching.crawling.parsers;

import com.fancy_software.accounts_matching.model.AccountVector;

public interface IParser {

    public AccountVector parse(String id);

    public void auth(String login, String password);

}
