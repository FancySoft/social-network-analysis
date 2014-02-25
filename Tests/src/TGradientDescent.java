import com.fancy_software.accounts_matching.matcher.math.GradientDescent;
import com.fancy_software.accounts_matching.matcher.math.IFunction;
import junit.framework.Assert;
import org.junit.Test;


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
        Assert.assertTrue(result[0] + " is far from 0", Math.abs(result[0]) < 0.01);

        f = new IFunction() {
            @Override
            public double evaluate(double[] v) {
                return v[0] * v[0] + v[0];
            }
        };
        result = GradientDescent.minimize(f, init);
        Assert.assertTrue(result[0] + " is far from -0.5", Math.abs(result[0] + 0.5) < 0.01);
    }

    public class TestFunction implements IFunction {
        private final double A = -5;
        private final double B = 5;
        private final double H = (B - A)/20;

        public double evaluate(double[] v) {
            double cur = A;
            for (int i = 0; i < 20; i++) {
                if ((v[0] - cur) < H) {
                    return cur*cur;
                } else {
                    cur += H;
                }
            }
            return 0;
        }
    }

    @Test
    public void testMinimize() {
        IFunction function1 = new TestFunction();
        double[] vector = {6};
        double[] expected = {0};
        double[] result = GradientDescent.minimize(function1, vector);
        Assert.assertTrue("Result: " + result[0] + ", expected: " + expected[0], Math.abs(result[0] - expected[0]) < 0.5);
    }

    public class TestFunction1 implements IFunction {
        public double evaluate(double[] v) {

            /*double a = -5;
            double b = 5;
            double h = (b-a)/20;
            double[] cur = {a, a};
            for (int i = 0; i < 20; i++) {
                if ((v[0] - cur[0]) < h) {
                    for (int j = 0; j < 20; j++) {
                        if ((v[1] - cur[1]) < h) {
                            return cur[0]+ cur[1];

                    } else
                        cur[1] += h;
                    }

                    }
                    else {
                    cur[0] += h;
                }
            }*/
            return 3;
        }
    }

    @Test
    public void testMinimize1() {
        IFunction function1 = new TestFunction1();
        double[] vector1 = {3};
        double[] res1 = {3};
        double[] result = GradientDescent.minimize(function1, vector1);
        Assert.assertTrue("Result: " + result[0] + ", expected: " + res1[0], Math.abs(result[0] - res1[0]) < 0.5);
    }
}
