package com.fancy_software.crawling.crawlers;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.SocialNetworkId;
import com.fancy_software.crawling.parsers.IParser;
import com.fancy_software.crawling.utils.ExtractType;
import com.fancy_software.crawling.utils.UserWriter;
import com.fancy_software.utils.Settings;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractCrawler implements ICrawler {

    protected boolean useIdList;

    protected String initialId;

    protected List<String> loginList;
    protected List<String> passwordList;
    protected Set<String>  startIds;

    protected SocialNetworkId socialNetworkId;

    protected AtomicBoolean stop;

    private   Queue<AccountVector> usersToWrite;
    protected ExecutorService      executor;
    protected ExtractType          extractType;


    {
        usersToWrite = new ConcurrentLinkedQueue<>();
        stop = new AtomicBoolean(false);
    }

    public Set<String> getStartIds() {
        return startIds;
    }

    public String getInitialId() {
        return initialId;
    }

    @Override
    public void init() {
        String folderToWrite = getFolderForWrite();
        UserWriter writer = new UserWriter(usersToWrite, folderToWrite, stop);
        Thread writingThread = new Thread(writer);
        writingThread.start();
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {
        if (executor != null)
            executor.shutdownNow();
        stop.set(true);
    }

    public ExtractType getExtractType() {
        return extractType;
    }

    public String getFolderForWrite() {
        switch (socialNetworkId) {
            case VK:
                return Settings.getInstance().get(Settings.VK_ACCOUNT_FOLDER);
            case FB:
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
