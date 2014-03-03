package com.fancy_software.crawling.parsers;

import com.fancy_software.accounts_matching.model.AccountVector;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractSampleParser extends AbstractDefaultParser {

    protected String initialId = "27852602";//test version

    protected Set<String> usersToParse;
    protected Set<String> parsed;

    {
        usersToParse = new LinkedHashSet<>();
        parsed = new LinkedHashSet<>();
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
                vector = extractAccountById(idToParse);
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
