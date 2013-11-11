package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.matcher.namematching.NameMatcher;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.AccountVector.Sex;
import com.fancy_software.logger.Log;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountMeasurer {

    private static final String TAG = AccountMeasurer.class.getSimpleName();

    // Веса разных составляющих меры
    private static final double LDA_WEIGHT = 5;
    private static final double NAME_WEIGHT = 0.05;
    private static final double BDATE_WEIGHT = 0.2;
    private static final double SEX_WEIGHT = 0.2;
    static AtomicInteger counter = new AtomicInteger(0);

    private AccountVector vector1;

    public void setVector2(AccountVector vector2) {
        this.vector2 = vector2;
    }

    private AccountVector vector2;

    public AccountMeasurer(AccountVector vector1) {
        this.vector1 = vector1;
    }

    public AccountMeasurer(AccountVector vector1, AccountVector vector2) {
        this.vector1 = vector1;
        this.vector2 = vector2;
    }

    public static double getSexWeight() {
        return SEX_WEIGHT;
    }

    public static double getBdateWeight() {

        return BDATE_WEIGHT;
    }

    public static double getNameWeight() {

        return NAME_WEIGHT;
    }

    public static double getLdaWeight() {

        return LDA_WEIGHT;
    }

    /**
     * Мера для определения схожести векторов
     *
     * @param enableLDA включаем/выключаем LDA
     * @return числовое значение меры
     */
    public double measure(boolean enableLDA) throws IOException {
        //TODO адекватное увеличение для почти пустых аккаунтов

        Log.d(TAG, Integer.toString(counter.incrementAndGet()));
        Log.d(TAG, String.format("measure: %d, %d", vector1.getId(), vector2.getId()));

        double result = 0;

        if (vector1.getSex() != vector2.getSex() && vector1.getSex() != Sex.NA && vector2.getSex() != Sex.NA) {
            result += SEX_WEIGHT;
        }

        NameMatcher nameMatcher = NameMatcher.getInstance();
        if (!nameMatcher.match(vector1.getFirst_name(), vector2.getFirst_name())) {
            result += NAME_WEIGHT * Measures.stringMeasure(vector1.getFirst_name(), vector2.getFirst_name());
        }
        result += NAME_WEIGHT * Measures.stringMeasure(vector1.getLast_name(), vector2.getLast_name());

        result += BDATE_WEIGHT * Measures.measureBirthdate(vector1.getBdate(), vector2.getBdate());

        if (enableLDA) {
            result += LDA_WEIGHT * Measures.measureWithLDA(vector1.getGroups(), vector2.getGroups());
        }
        Log.d(TAG, String.format("%d : %.3f", counter.get(), result));
        return result;
    }

}
