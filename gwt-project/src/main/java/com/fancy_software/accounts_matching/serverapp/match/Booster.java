package com.fancy_software.accounts_matching.serverapp.match;

import com.fancy_software.accounts_matching.core.entities.AccountVector;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ************************************************************************
 * Created by akirienko on 18.10.13
 * Copyright (c) 2013 Desman, Inc. All rights reserved.
 * ************************************************************************
 */
public class Booster implements IMatcher {

    private Collection<IMatcher> matchers;
    private Map<IMatcher, Double> weights;

    public Booster(List<IMatcher> matchers) {
        this.matchers = matchers;
        initWeights();
    }

    private void initWeights() {
        weights = new HashMap<IMatcher, Double>(matchers.size());
        for (IMatcher matcher : matchers) {
            weights.put(matcher, 1.);
        }
    }

    @Override
    public AccountVector match(AccountVector goal) {
        Map<AccountVector, Double> results = new HashMap<AccountVector, Double>(matchers.size());
        AccountVector cur = null;
        for (IMatcher matcher : matchers) {
            cur = matcher.match(goal);
            if (results.containsKey(cur)) results.put(cur, results.get(cur) + weights.get(matcher));
            else results.put(cur, weights.get(matcher));
        }

        Double max = 0.;
        for (AccountVector vector : results.keySet()) {
            if (results.get(vector) > max) {
                max = results.get(vector);
                cur = vector;
            }
        }

        return cur;
    }
}
