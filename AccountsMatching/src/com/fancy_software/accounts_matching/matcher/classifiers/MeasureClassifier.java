package com.fancy_software.accounts_matching.matcher.classifiers;

import com.fancy_software.accounts_matching.matcher.AccountMeasurer;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.tester.entities.Couple;
import libsvm.*;

import java.util.Collection;

/**
 * ************************************************************************
 * Created by akirienko on 10.04.14
 * Copyright (c) 2014 Artem Kirienko
 * ************************************************************************
 */
public class MeasureClassifier implements IClassifier {

    private svm_model model;
    private svm_node[] testValues;
    private double barrier = 0;

    @Override
    public void learn(Collection<Couple> trainSet) {
        svm_problem prob = new svm_problem();
        prob.l = trainSet.size();
        prob.y = new double[prob.l];
        prob.x = new svm_node[prob.l][AccountMeasurer.getMeasuredVectorSize()];
        int i = 0;
        for (Couple cur : trainSet) {
            double[] features = AccountMeasurer.getMeasuresVector(cur.getV1(), cur.getV2(), false);
            for (int j = 0; j < features.length; j++) {
                prob.x[i][j] = new svm_node();
                prob.x[i][j].index = j + 1;
                prob.x[i][j].value = features[j];
            }
            prob.y[i] = cur.areSame() ? 1 : -1;
            i++;
        }

        svm_parameter param = new svm_parameter();

        // Magic! Don't touch.
        param.svm_type = svm_parameter.C_SVC; // Default
        param.kernel_type = svm_parameter.RBF; // Default
        param.degree = 3; // Default
        param.gamma = 1.0 / AccountMeasurer.getMeasuredVectorSize(); // Default
        param.coef0 = 0; // Default
        param.nu = 0.5; // Default
        param.cache_size = 100; // Default
        param.C = 10000; // CUSTOM
        param.eps = 1e-3; // Default
        param.p = 0.1;
        param.shrinking = 1; // Default
        param.probability = 0; // Default
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];

        model = svm.svm_train(prob, param);

        testValues = new svm_node[AccountMeasurer.getMeasuredVectorSize()];
        for (int j = 0; j < testValues.length; j++) {
            testValues[j] = new svm_node();
            testValues[j].index = j + 1;
        }
    }

    @Override
    public boolean areSame(AccountVector v1, AccountVector v2) {
        double[] features = AccountMeasurer.getMeasuresVector(v1, v2, false);
        for (int i = 0; i < features.length; i++) {
            testValues[i].value = features[i];
        }
        double result = svm.svm_predict(model, testValues);
        return result > barrier;
    }

    @Override
    public void vary(double barrierMultiplier) {
        barrier = -1 + barrierMultiplier * 2;
    }
}
