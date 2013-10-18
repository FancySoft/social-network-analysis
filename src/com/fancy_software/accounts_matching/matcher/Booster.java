package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.model.AccountVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * ************************************************************************
 * Created by akirienko on 18.10.13
 * Copyright (c) 2013 Transparent Language, Inc. All rights reserved.
 * ************************************************************************
 */
public class Booster implements IMatcher {

    private Collection<IMatcher> matchers;

    public Booster() {
        // init matchers
        matchers = new ArrayList<IMatcher>();
    }

    @Override
    public AccountVector match(AccountVector goal) {
        Map<AccountVector, Integer> results = new HashMap<AccountVector, Integer>(matchers.size());
        AccountVector cur = null;
        for (IMatcher matcher : matchers) {
            cur = matcher.match(goal);
            if (results.containsKey(cur)) results.put(cur, results.get(cur) + 1);
            else results.put(cur, 1);
        }

        int max = 0;
        for (AccountVector vector : results.keySet()) {
            if (results.get(vector) > max) {
                max = results.get(vector);
                cur = vector;
            }
        }

        return cur;
    }
}
