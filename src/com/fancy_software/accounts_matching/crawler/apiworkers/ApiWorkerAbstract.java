package com.fancy_software.accounts_matching.crawler.apiworkers;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountWriter;
import com.fancy_software.accounts_matching.model.AccountVector;

public abstract class ApiWorkerAbstract implements IApiWorker {

    protected SocialNetworkId networkId;

    public SocialNetworkId getNetworkId() {
        return networkId;
    }

    protected void writeToFile(AccountVector vector, String path) {
        LocalAccountWriter.writeAccountToLocalBase(vector, path);
    }

}
