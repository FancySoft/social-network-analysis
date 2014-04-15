package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.tester.Couple;
import libsvm.svm_node;
import libsvm.svm_problem;

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
        svm_problem prob = new svm_problem();
        prob.l = trainSet.size();
        prob.y = new double[prob.l];
        prob.x = new svm_node[prob.l][2];
        int i = 0;
        for (Couple cur : trainSet)
        {
//            prob.x[i][0] = new svm_node();
//            prob.x[i][0].index = 1;
//            prob.x[i][0].value = p.x;
//            prob.x[i][1] = new svm_node();
//            prob.x[i][1].index = 2;
//            prob.x[i][1].value = p.y;
//            prob.y[i] = p.value;
        }
    }

    @Override
    public boolean areSame(AccountVector v1, AccountVector v2) {
        return false;
    }

    @Override
    public void vary(double barrierMultiplier) {

    }
}
