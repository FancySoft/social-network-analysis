import com.fancy_software.accounts_matching.matcher.math.GradientDescent;
import com.fancy_software.accounts_matching.matcher.math.IFunction;
import org.junit.Test;
import sun.jvm.hotspot.utilities.Assert;

/**
 * ************************************************************************
 * Created by akirienko on 27.11.13
 * Copyright (c) 2013 Artem Kirienko. All rights reserved.
 * ************************************************************************
 */
public class TGradientDescent {
    @Test
    public void xSquare() {
        IFunction f = new IFunction() {
            @Override
            public double evaluate(double[] v) {
                return v[0] * v[0];
            }
        };
        double[] init = new double[1];
        init[0] = 2;
        double[] result = GradientDescent.minimize(f, init);
        Assert.that(Math.abs(result[0]) < 0.01, result[0] + " is far from 0");

        f = new IFunction() {
            @Override
            public double evaluate(double[] v) {
                return v[0] * v[0] + v[0];
            }
        };
        result = GradientDescent.minimize(f, init);
        Assert.that(Math.abs(result[0] + 0.5) < 0.01, result[0] + " is far from -0.5");
    }
}
