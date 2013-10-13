package com.fancy_software.accounts_matching.crawler.parsers;

import com.fancy_software.accounts_matching.model.AccountVector;

public interface IParser {

    public void Auth(String login, String password);

    public AccountVector Parse(String id);

}
