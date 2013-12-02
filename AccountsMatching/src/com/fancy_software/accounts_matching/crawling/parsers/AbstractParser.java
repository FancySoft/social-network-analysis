package com.fancy_software.accounts_matching.crawling.parsers;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountWriter;
import com.fancy_software.accounts_matching.matcher.IMatcher;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.SocialNetworkId;

/**
 * AbstractParser implements an IMatcher as it should
 * have an ability to search people (which we can use as
 * another one matcher in boosting).
 */
public abstract class AbstractParser implements IParser, IMatcher {

    protected SocialNetworkId networkId;

    public AbstractParser(SocialNetworkId networkId) {
        this.networkId = networkId;
    }

    @SuppressWarnings("unused")
    public SocialNetworkId getNetworkId() {
        return networkId;
    }

    protected void writeToFile(AccountVector vector, String path) {
        LocalAccountWriter.writeAccountToLocalBase(vector, path);
    }

}
