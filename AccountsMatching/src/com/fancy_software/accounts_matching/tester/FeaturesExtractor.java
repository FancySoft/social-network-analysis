package com.fancy_software.accounts_matching.tester;

import com.fancy_software.accounts_matching.matcher.AccountMeasurer;
import com.fancy_software.accounts_matching.matcher.classifiers.SubtreeClassifier;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.tester.entities.Couple;
import com.fancy_software.accounts_matching.tester.entities.SimpleCouple;
import com.fancy_software.logger.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fancy_software.utils.CachedAccountReader.getFBAccount;
import static com.fancy_software.utils.CachedAccountReader.getVKAccount;

/**
 * ************************************************************************
 * Created by akirienko on 02.05.14
 * Copyright (c) 2014 Artem Kirienko
 * ************************************************************************
 */
public class FeaturesExtractor {
    private static final String TAG = FeaturesExtractor.class.getSimpleName();

    private static final String FEATURES_PATH = "config/features/";
    private static final String MEASURE_TRAIN_PATH = "measure_train.txt";
    private static final String MEASURE_TEST_PATH = "measure_test.txt";
    private static final String SUBTREE_INITIAL_TRAIN_PATH = "subtree_initial_train.txt";
    private static final String SUBTREE_INITIAL_TEST_PATH = "subtree_initial_test.txt";

    private static void print(Collection<Couple> couples, String path) {
        File f = new File(FEATURES_PATH);
        if (!f.exists()) {
            f.mkdirs();
        }
        try {
            PrintWriter writer = new PrintWriter(FEATURES_PATH + path);
            for (Couple current : couples) {
                writer.print(current.areSame() ? "-1 " : "+1 ");
                double[] features = AccountMeasurer.getMeasuresVector(current.getV1(), current.getV2(), false);
                for (int i = 0; i < features.length; i++) {
                    writer.printf(Locale.US, "%d:%.6f ", i + 1, features[i]);
                }
                writer.println();
            }
            writer.flush();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e);
        }
    }

    private static void printSubtree(Collection<Couple> couples, SubtreeClassifier classifier, String path) {
        File f = new File(FEATURES_PATH);
        if (!f.exists()) {
            f.mkdirs();
        }
        try {
            PrintWriter writer = new PrintWriter(FEATURES_PATH + path);
            for (Couple current : couples) {
                writer.print(current.areSame() ? "-1 " : "+1 ");
                double[] features = classifier.extractFeatures(current.getV1(), current.getV2());
                for (int i = 0; i < features.length; i++) {
                    writer.printf(Locale.US, "%d:%.6f ", i + 1, features[i]);
                }
                writer.println();
            }
            writer.flush();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e);
        }
    }

    private static void extractMeasureVector() {
        Map<String, Set<SimpleCouple>> sets = SetsGenerator.getSets();
        Collection<Couple> trainSet = sets.get("trainSet").parallelStream()
                       .map(simpleCouple -> {
                           AccountVector v1 = getVKAccount(simpleCouple.getId1());
                           AccountVector v2 = getFBAccount(simpleCouple.getId2());
                           return new Couple(v1, v2, simpleCouple.areEqual());
                       })
                       .collect(Collectors.toSet());

        print(trainSet, MEASURE_TRAIN_PATH);
        Log.d(TAG, "Measure Train completed");

        Collection<Couple> testSet = sets.get("testSet").parallelStream()
                                         .map(simpleCouple -> {
                                             AccountVector v1 = getVKAccount(simpleCouple.getId1());
                                             AccountVector v2 = getFBAccount(simpleCouple.getId2());
                                             return new Couple(v1, v2, simpleCouple.areEqual());
                                         })
                                         .collect(Collectors.toSet());

        print(testSet, MEASURE_TEST_PATH);
        Log.d(TAG, "Measure Test completed");
    }

    private static void extractSubtreeVector() {
        Map<String, Set<SimpleCouple>> sets = SetsGenerator.getSets();
        Collection<Couple> trainSet = sets.get("trainSet").parallelStream()
                                          .map(simpleCouple -> {
                                              AccountVector v1 = getVKAccount(simpleCouple.getId1());
                                              AccountVector v2 = getFBAccount(simpleCouple.getId2());
                                              return new Couple(v1, v2, simpleCouple.areEqual());
                                          })
                                          .collect(Collectors.toSet());

        SubtreeClassifier classifier = new SubtreeClassifier();
        classifier.learn(trainSet);

        printSubtree(trainSet, classifier, SUBTREE_INITIAL_TRAIN_PATH);
        Log.d(TAG, "Subtree Train completed");

        Collection<Couple> testSet = sets.get("testSet").parallelStream()
                                         .map(simpleCouple -> {
                                             AccountVector v1 = getVKAccount(simpleCouple.getId1());
                                             AccountVector v2 = getFBAccount(simpleCouple.getId2());
                                             return new Couple(v1, v2, simpleCouple.areEqual());
                                         })
                                         .collect(Collectors.toSet());

        printSubtree(testSet, classifier, SUBTREE_INITIAL_TEST_PATH);
        Log.d(TAG, "Subtree Test completed");
    }

    public static void main(String[] args) {
        extractMeasureVector();
        Log.d(TAG, "Measure completed");
        extractSubtreeVector();
        Log.d(TAG, "Subtree completed");
    }
}
