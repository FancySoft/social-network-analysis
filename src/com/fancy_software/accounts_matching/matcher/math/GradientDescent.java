package com.fancy_software.accounts_matching.matcher.math;

import com.fancy_software.logger.Log;

/**
 * ************************************************************************
 * Created by akirienko on 13.11.13
 * Copyright (c) 2013 Artem Kirienko. All rights reserved.
 * ************************************************************************
 */
public class GradientDescent {
    private static final String TAG = GradientDescent.class.getSimpleName();

    public static double[] grad(IFunction f, double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            Log.e(TAG, "Different dimensions!");
            return null;
        }

        int dim = v1.length;
        double result[] = new double[dim];
        double fv1 = f.evaluate(v1);
        double fv2 = f.evaluate(v2);
        double df = fv2 - fv1;
        for (int i = 0; i < dim; i++) {
            result[i] = df / (v2[i] - v1[i]);
        }

        return result;
    }

    public static double[] minimize(IFunction f, double[] init) {
        // TODO: follow negative gradient
        return init;
    }
}
