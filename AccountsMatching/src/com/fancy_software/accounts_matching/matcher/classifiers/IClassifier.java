package com.fancy_software.accounts_matching.matcher.classifiers;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.tester.Couple;

import java.util.Collection;

/**
 * ************************************************************************
 * Created by akirienko on 10.04.14
 * Copyright (c) 2014 Artem Kirienko
 * ************************************************************************
 */
public interface IClassifier {
    public void learn(Collection<Couple> trainSet);
    public boolean areSame(AccountVector v1, AccountVector v2);

    /**
     * @param barrierMultiplier 0.0 = true for all, 1.0 = false for all
     */
    public void vary(double barrierMultiplier);
}
