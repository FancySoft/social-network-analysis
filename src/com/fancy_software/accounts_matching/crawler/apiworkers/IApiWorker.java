package com.fancy_software.accounts_matching.crawler.apiworkers;

import com.fancy_software.accounts_matching.model.AccountVector;

public interface IApiWorker {

    public void Auth(String login, String password);

    public AccountVector Parse(String id);

}
