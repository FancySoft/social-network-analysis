package com.fancy_software.accounts_matching.crawler;

import com.fancy_software.accounts_matching.crawler.parsers.*;

public class ParserFactory {

    public static IParser getApiWorkerInstance(String path, SocialNetworkId networkId) {
        switch (networkId) {
            case VK:
                return new VKapiWorker(path, networkId);
            case Odnoklassniki:
                return new OdnoklassnikiParser(path, networkId);
            case Facebook:

            default:
                return null;

        }
    }

}
