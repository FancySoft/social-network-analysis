package com.fancy_software.accounts_matching.crawling.crawlers;

import com.fancy_software.accounts_matching.crawling.ParserFactory;
import com.fancy_software.accounts_matching.model.SocialNetworkId;
import com.fancy_software.accounts_matching.io_local_base.Settings;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.logger.Log;

public class VkCrawler extends AbstractCrawler {

    private static final String TAG = VkCrawler.class.getSimpleName();
    private static final String KEY_VK_LOGIN = "vk_login";
    private static final String KEY_VK_PASSWORD = "vk_password";
    private static String VK_LOGIN;
    private static String VK_PASSWORD;

    public VkCrawler() {
        super();
    }

    @Override
    public void init(Long id) {
        super.init(id);
        parser = ParserFactory.getApiWorkerInstance(SocialNetworkId.VK);
        Settings settings = Settings.getInstance();
        VK_LOGIN = settings.get(KEY_VK_LOGIN);
        VK_PASSWORD = settings.get(KEY_VK_PASSWORD);
        parser.auth(VK_LOGIN, VK_PASSWORD);
    }

    @Override
    public void start() {
        Log.d(TAG, String.format("VK account: %s", VK_LOGIN));
        AccountVector cur;
        while (usersToParse.size() > 0) {
            cur = parser.parse(Long.toString(usersToParse.remove()));
            for (Long l : cur.getFriends()) addUserToParse(l);
        }

    }

}
