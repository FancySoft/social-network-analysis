package com.fancy_software.accounts_matching.matcher.math;

/**
 * ************************************************************************
 * Created by akirienko on 13.11.13
 * Copyright (c) 2013 Artem Kirienko. All rights reserved.
 * ************************************************************************
 */
public interface IFunction {
    /**
     * f(v) = x
     * @param v input vector
     * @return  result
     */
    public double evaluate(double[] v);
}
