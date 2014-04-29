package com.fancy_software.accounts_matching.matcher.classifiers;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.tester.Couple;

import java.util.Collection;

/**
 * ************************************************************************
 * Created by akirienko on 29.04.14
 * Copyright (c) 2014 Artem Kirienko
 * ************************************************************************
 */
public class SimpleNamesClassifier implements IClassifier {
    @Override
    public void learn(Collection<Couple> trainSet) {
        // Do nothing
    }

    @Override
    public boolean areSame(AccountVector v1, AccountVector v2) {
        return v1.getFirst_name().equals(v2.getFirst_name()) && v1.getLast_name().equals(v2.getLast_name());
    }

    @Override
    public void vary(double barrierMultiplier) {
        // Nothing to do
    }
}
