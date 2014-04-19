package com.fancy_software.crawling.parsers;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.logger.Log;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractSampleParser extends AbstractDefaultParser {

    private static final String TAG = AbstractSampleParser.class.getSimpleName();

    protected String initialId;

    protected Set<String> usersToParse;
    protected Set<String> parsed;

    {
        usersToParse = new LinkedHashSet<>();
        parsed = new LinkedHashSet<>();
    }

    protected AbstractSampleParser() {

    }

    protected AbstractSampleParser(AbstractCrawler crawler, Set<String> usersToParse) {
        this.crawler = crawler;
        this.usersToParse = usersToParse;
        initialId = getUserToParse();
    }

    protected AbstractSampleParser(AbstractCrawler crawler, String initialId) {
        this.crawler = crawler;
        this.initialId = initialId;
    }

    @Override
    public void start() {
        AccountVector vector = extractAccountById(initialId);
        if (vector != null) {
            notifyCrawler(vector);
            parsed.add(vector.getId());
            for (String friendId : vector.getFriends())
                usersToParse.add(friendId);
            while (!Thread.currentThread().isInterrupted() && !usersToParse.isEmpty()) {
                String idToParse = getUserToParse();
                System.out.println(idToParse);
                if (isParsed(idToParse)) {
                    usersToParse.remove(idToParse);
                    continue;
                }
                try {
                    vector = extractAccountById(idToParse);
                } catch (Exception e) {
                    vector = null;
                    Log.e(TAG, e.getMessage());
                }
                if (vector != null) {
                    notifyCrawler(vector);
                    for (String friendId : vector.getFriends())
                        usersToParse.add(friendId);
                    removeParsed(vector.getId());
                }
            }
        }
    }

    public String getInitialId() {
        return initialId;
    }

    public boolean isParsed(String userID) {
        return parsed.contains(userID);
    }

    public String getUserToParse() {
        Iterator<String> iterator = usersToParse.iterator();
        return iterator.next();
    }

    public void removeParsed(String userId) {
        parsed.add(userId);
        usersToParse.remove(userId);
    }

}
