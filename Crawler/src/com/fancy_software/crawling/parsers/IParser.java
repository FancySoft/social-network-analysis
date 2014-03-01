package com.fancy_software.crawling.parsers;

import com.fancy_software.accounts_matching.model.AccountVector;

public interface IParser {

    public void auth(String login, String password);

    public void start();

    public AccountVector extractAccountById(String id);

}
