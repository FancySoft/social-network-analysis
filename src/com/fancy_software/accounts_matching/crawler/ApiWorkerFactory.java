package com.fancy_software.accounts_matching.crawler;

import com.fancy_software.accounts_matching.crawler.apiworkers.*;

public class ApiWorkerFactory {

    public static IApiWorker getApiWorkerInstance(String path, SocialNetworkId networkId) {
        switch (networkId) {
            case VK:
                return new VKapiWorker(path, networkId);
            case Odnoklassniki:
                return new OdnoklassnikiApiWorker(path, networkId);
            case Facebook:

            default:
                return null;

        }
    }

}
