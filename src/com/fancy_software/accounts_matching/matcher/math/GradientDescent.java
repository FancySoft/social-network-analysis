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

    /**
     * Приближенное значение градиента
     * @param f  функция
     * @param v1 начальная точка
     * @param v2 конечная точка
     * @return   вектор-градиент
     */
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

    /**
     * Сложение векторов
     * @param v1 вектор
     * @param v2 вектор
     * @return   сумма
     */
    public static double[] add(double[] v1, double[] v2) {
        int dim = v1.length;
        double result[] = new double[v1.length];
        for (int i = 0; i < dim; i++) {
            result[i] = v1[i] + v2[i];
        }
        return result;
    }

    /**
     * Вычитание векторов
     * @param v1 из вектора v1
     * @param v2 вычитается v2
     * @return   разность
     */
    public static double[] subtract(double[] v1, double[] v2) {
        int dim = v1.length;
        double result[] = new double[v1.length];
        for (int i = 0; i < dim; i++) {
            result[i] = v1[i] - v2[i];
        }
        return result;
    }

    /**
     * Нормализация вектора (деление на меру и на step)
     * @param v вектор
     * @return  нормализованный вектор
     */
    private static double[] normalize(double[] v) {
        double measure = 0;
        double result[] = new double[v.length];
        for (double x : v) {
            measure += x*x;
        }
        for (int i = 0; i < v.length; i++) {
            result[i] = v[i] / (measure * step);
        }
        return result;
    }

    private static int step;

    /**
     * Минимизация f методом градиентного спуска
     * @param f    функция
     * @param init начальная точка
     * @return     локальный минимум
     */
    public static double[] minimize(IFunction f, double[] init) {
        step = 2;
        int dim = init.length;

        double[] direction = new double[dim];
        for (int i = 0; i < dim; i++) {
            direction[i] = 1. / i;
        }

        double[] prev;
        double[] cur = add(init, direction);

        int ITERATIONS = 5;
        while (ITERATIONS > 0) {
            direction = normalize(grad(f, init, cur));
            prev = cur;
            cur = subtract(prev, direction);
            step *= 2;
        }

        return cur;
    }
}
