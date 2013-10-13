package com.fancy_software.accounts_matching.crawler.parsers;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountWriter;
import com.fancy_software.accounts_matching.model.AccountVector;

public abstract class ParserAbstract implements IParser {

    protected SocialNetworkId networkId;

    @SuppressWarnings("unused")
    public SocialNetworkId getNetworkId() {
        return networkId;
    }

    protected void writeToFile(AccountVector vector, String path) {
        LocalAccountWriter.writeAccountToLocalBase(vector, path);
    }

}
