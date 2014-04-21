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

    protected Set<AccountToParse> usersToParse;
    protected Set<AccountToParse> parsed;

    public SampleParseType getParseType() {
        return parseType;
    }

    protected SampleParseType parseType;

    {
        usersToParse = new LinkedHashSet<>();
        parsed = new LinkedHashSet<>();
    }

    protected AbstractSampleParser() {

    }

    protected AbstractSampleParser(AbstractCrawler crawler, Set<String> usersToParse, SampleParseType parseType) {
        this.crawler = crawler;
        this.parseType = parseType;
        usersToParse.stream().forEach((entry) -> {
            System.out.println(entry);
            this.usersToParse.add(new AccountToParse(entry, parseType));
        });
        System.out.println(this.usersToParse)
        ;
        initialId = getUserToParse().id;
    }

    protected AbstractSampleParser(AbstractCrawler crawler, String initialId, SampleParseType parseType) {
        this.crawler = crawler;
        this.initialId = initialId;
        this.parseType = parseType;
    }

    @Override
    public void start() {
        AccountVector vector = extractAccountById(initialId);
//        System.out.println(usersToParse);
        if (vector != null) {
            notifyCrawler(vector);
            parsed.add(new AccountToParse(vector.getId(), parseType));
            for (String friendId : vector.getFriends())
                usersToParse.add(new AccountToParse(friendId, chooseRightParseType(parseType)));
            while (!Thread.currentThread().isInterrupted() && !usersToParse.isEmpty()) {
                AccountToParse toParse = getUserToParse();
                if (!needToParse(toParse)) {
                    usersToParse.remove(toParse);
                    continue;
                }
                try {
                    vector = extractAccountById(toParse.id);
                    if (vector != null) {
                        notifyCrawler(vector);
                        for (String friendId : vector.getFriends())
                            addAccountToParse(new AccountToParse(friendId, chooseRightParseType(toParse.parseType)));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Unable to extract account information");
                } finally {
                    removeParsed(toParse);
                }
            }
            crawler.finish();
        }
    }

    public String getInitialId() {
        return initialId;
    }

    private boolean needToParse(AccountToParse toParse) {
        if (toParse.parseType == SampleParseType.NO)
            return false;
        return !parsed.contains(toParse);
    }

    private boolean addAccountToParse(AccountToParse accountToParse) {
        if (accountToParse.parseType == SampleParseType.NO)
            return false;
        return usersToParse.add(accountToParse);
    }

    public AccountToParse getUserToParse() {
        Iterator<AccountToParse> iterator = usersToParse.iterator();
        return iterator.next();
    }

    public void removeParsed(AccountToParse toParse) {
        parsed.add(toParse);
        usersToParse.remove(toParse);
    }

    private class AccountToParse {
        String          id;
        SampleParseType parseType;

        private AccountToParse(String id, SampleParseType parseType) {
            this.id = id;
            this.parseType = parseType;
        }

        @Override
        public String toString() {
            return "AccountToParse{" +
                   "id='" + id + '\'' +
                   ", parseType=" + parseType +
                   '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AccountToParse that = (AccountToParse) o;

            if (id != null ? !id.equals(that.id) : that.id != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

    public enum SampleParseType {
        ALL, FRIENDS, FOAF, NOTHING_ELSE, NO
    }

    private SampleParseType chooseRightParseType(SampleParseType parseType) {
        switch (parseType) {
            case NO:
                return SampleParseType.NO;
            case NOTHING_ELSE:
                return SampleParseType.NO;
            case ALL:
                return SampleParseType.ALL;
            case FRIENDS:
                return SampleParseType.NOTHING_ELSE;
            case FOAF:
                return SampleParseType.FRIENDS;
        }
        return null;
    }

}
