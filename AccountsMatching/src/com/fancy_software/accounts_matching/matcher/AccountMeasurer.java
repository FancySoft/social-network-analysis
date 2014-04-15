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
    private static final double UNIVERSITIES_WEIGHT = 1;
    private static final double SCHOOLS_WEIGHT = 0.5;
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

    @SuppressWarnings("unused")
    public static double getSexWeight() {
        return SEX_WEIGHT;
    }

    @SuppressWarnings("unused")
    public static double getBdateWeight() {

        return BDATE_WEIGHT;
    }

    @SuppressWarnings("unused")
    public static double getNameWeight() {

        return NAME_WEIGHT;
    }

    @SuppressWarnings("unused")
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
        Log.d(TAG, String.format("measure: %s, %s", vector1.getId(), vector2.getId()));

        double result = 0;

        double vector[] = getMeasuresVector(vector1, vector2, enableLDA);

        result += SEX_WEIGHT * vector[0];
        result += NAME_WEIGHT * vector[1];
        result += NAME_WEIGHT * vector[2];
        result += BDATE_WEIGHT * vector[3];
        result += UNIVERSITIES_WEIGHT * vector[4];
        result += SCHOOLS_WEIGHT * vector[5];

        if (enableLDA) {
            result += LDA_WEIGHT * vector[6];
        }
        Log.d(TAG, String.format("%d : %.3f", counter.get(), result));
        return result;
    }

    public static double[] getMeasuresVector(AccountVector vector1, AccountVector vector2, boolean enableLDA) throws IOException {
        double result[] = new double[7];

        if (vector1.getSex() != vector2.getSex() && vector1.getSex() != Sex.NA && vector2.getSex() != Sex.NA) {
            result[0] = 1;
        }

        NameMatcher nameMatcher = NameMatcher.getInstance();
        if (!nameMatcher.match(vector1.getFirst_name(), vector2.getFirst_name())) {
            result[1] = Measures.stringMeasure(vector1.getFirst_name(), vector2.getFirst_name());
        }

        result[2] = Measures.stringMeasure(vector1.getLast_name(), vector2.getLast_name());
        result[3] = Measures.measureBirthdate(vector1.getBdate(), vector2.getBdate());
        result[4] = Measures.measureUniversitiesLists(vector1.getUniversities(), vector2.getUniversities());
        result[5] = Measures.measureSchoolsLists(vector1.getSchools(), vector2.getSchools());

        if (enableLDA) {
            result[6] = Measures.measureWithLDA(vector1.getGroups(), vector2.getGroups());
        }

        return result;
    }

    public static int getMeasuredVectorSize() {
        return 7;
    }
}
