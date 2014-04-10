package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.tester.Couple;

import java.util.Collection;

/**
 * ************************************************************************
 * Created by akirienko on 10.04.14
 * Copyright (c) 2014 Artem Kirienko
 * ************************************************************************
 */
public class MeasureClassifier implements IClassifier {
    @Override
    public void learn(Collection<Couple> trainSet) {

    }

    @Override
    public boolean areSame(AccountVector v1, AccountVector v2) {
        return false;
    }

    @Override
    public void vary(double barrierMultiplier) {

    }
}
