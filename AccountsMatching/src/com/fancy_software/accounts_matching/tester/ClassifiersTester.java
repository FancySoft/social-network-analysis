package com.fancy_software.accounts_matching.tester;

import com.fancy_software.accounts_matching.matcher.classifiers.IClassifier;
import com.fancy_software.accounts_matching.tester.entities.Couple;

import java.util.ArrayList;
import java.util.Collection;


/**
 * ************************************************************************
 * Created by akirienko on 10.04.14
 * Copyright (c) Artem Kirienko
 * ************************************************************************
 */
public class ClassifiersTester {

    private static final double STEP = 0.05;
    private static Collection<Couple> testSet;
    private static Collection<Couple> trainSet;

    public static void main(String[] args) {
        System.out.println("Not implemented");
        // TODO: initialize test set, train set, classifiers
        trainSet = new ArrayList<>();
        testSet = new ArrayList<>();
    }

    private static void test(IClassifier classifier) {
        if (testSet == null || trainSet == null || testSet.isEmpty() || trainSet.isEmpty()) {
            throw new IllegalStateException("test executed before initialization");
        }

        classifier.learn(trainSet);
        double multiplier = 0;
        while (multiplier <= 1) {
            classifier.vary(multiplier);
            long truePositive = 0;
            long falsePositive = 0;
            for (Couple current : testSet) {
                boolean result = classifier.areSame(current.getV1(), current.getV2());
                if (result) {
                    if (current.areSame()) {
                        truePositive++;
                    }
                    else {
                        falsePositive++;
                    }
                }
            }
            // TODO this is a graph coordinates: x; y
            System.out.println(falsePositive + "; " + truePositive);
            multiplier += STEP;
        }
    }
}
