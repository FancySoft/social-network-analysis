package com.fancy_software.accounts_matching.tester;

import com.fancy_software.accounts_matching.matcher.classifiers.IClassifier;
import com.fancy_software.accounts_matching.matcher.classifiers.MeasureClassifier;
import com.fancy_software.accounts_matching.matcher.classifiers.SimpleNamesClassifier;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.tester.entities.Couple;
import com.fancy_software.accounts_matching.tester.entities.SimpleCouple;
import com.fancy_software.logger.Log;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fancy_software.utils.CachedAccountReader.getFBAccount;
import static com.fancy_software.utils.CachedAccountReader.getVKAccount;


/**
 * ************************************************************************
 * Created by akirienko on 10.04.14
 * Copyright (c) Artem Kirienko
 * ************************************************************************
 */
public class ClassifiersTester {
    private static final String TAG = ClassifiersTester.class.getSimpleName();

    private static final double STEP = 0.05;
    private static Collection<Couple> testSet;
    private static Collection<Couple> trainSet;

    public static void main(String[] args) {
        Map<String, Set<SimpleCouple>> sets = SetsGenerator.getSets();
        trainSet = sets.get("trainSet").parallelStream()
                       .map(simpleCouple -> {
                           AccountVector v1 = getVKAccount(simpleCouple.getId1());
                           AccountVector v2 = getFBAccount(simpleCouple.getId2());
                           return new Couple(v1, v2, simpleCouple.areEqual());
                       })
                       .collect(Collectors.toSet());
        testSet = sets.get("testSet").parallelStream()
                      .map(simpleCouple -> {
                          AccountVector v1 = getVKAccount(simpleCouple.getId1());
                          AccountVector v2 = getFBAccount(simpleCouple.getId2());
                          return new Couple(v1, v2, simpleCouple.areEqual());
                      })
                      .collect(Collectors.toSet());
        Log.d(TAG, "Sets initialized");


        MeasureClassifier measureClassifier = new MeasureClassifier();
        System.out.println("------------- Measure classifier begin -------------");
        test(measureClassifier);
        System.out.println("------------- Measure classifier end -------------");

        SimpleNamesClassifier simpleNamesClassifier = new SimpleNamesClassifier();
        System.out.println("------------- Simple names classifier begin -------------");
        test(simpleNamesClassifier);
        System.out.println("------------- Simple names classifier end -------------");
    }

    private static void test(IClassifier classifier) {
        if (testSet == null || trainSet == null || testSet.isEmpty() || trainSet.isEmpty()) {
            throw new IllegalStateException("test executed before initialization");
        }

        classifier.learn(trainSet);

        // ROC data
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

        // precision in %
        long allCount = 0;
        long rightCount = 0;
        for (Couple current : testSet) {
            allCount++;
            boolean result = classifier.areSame(current.getV1(), current.getV2());
            if (result == current.areSame()) rightCount++;
        }
        System.out.println("Precision: " + (double) rightCount / (double) allCount + " %");
    }
}
