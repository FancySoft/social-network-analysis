package com.fancy_software.crawling.crawlers;

import com.fancy_software.accounts_matching.io_local_base.Settings;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.SocialNetworkId;
import com.fancy_software.crawling.parsers.IParser;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractCrawler implements ICrawler {

    protected SocialNetworkId      socialNetworkId;
    protected AtomicBoolean        stop;
    private   Queue<AccountVector> usersToWrite;

    {
        usersToWrite = new ConcurrentLinkedQueue<>();
        stop = new AtomicBoolean(false);
    }

    @Override
    public void init() {
        String folderToWrite = getFolderForWrite(socialNetworkId);
        UserWriter writer = new UserWriter(usersToWrite, folderToWrite, stop);
        Thread writingThread = new Thread(writer);
        writingThread.start();
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {
        stop.set(true);
    }

    public static String getFolderForWrite(SocialNetworkId socialNetworkId) {
        switch (socialNetworkId) {
            case VK:
                return Settings.getInstance().get(Settings.VK_ACCOUNT_FOLDER);
            case Facebook:
                return Settings.getInstance().get(Settings.FB_ACCOUNT_FOLDER);
            default:
                return null;
        }
    }

    public void addUsersToWrite(List<AccountVector> users) {
        for (AccountVector vector : users)
            usersToWrite.add(vector);
    }

    public void addUsersToWrite(AccountVector user) {
        usersToWrite.add(user);
    }

    public class ParserRunner implements Runnable {

        IParser parser;
        String  login;
        String  password;

        public ParserRunner(IParser parser, String login, String password) {
            this.parser = parser;
            this.login = login;
            this.password = password;
        }

        @Override
        public void run() {
            parser.auth(login, password);
            parser.start();
        }
    }
}
