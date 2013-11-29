package com.fancy_software.accounts_matching.serverapp.match;

import com.fancy_software.accounts_matching.core.entities.AccountVector;
import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

public class AccountMeasurer {
    private static final Logger log = Logger.getLogger(AccountMeasurer.class.getName());
    
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
    public double measure(boolean enableLDA) throws Exception {
        //TODO адекватное увеличение для почти пустых аккаунтов
        log.info("Start measure with atomic counter=" + String.valueOf(counter.incrementAndGet()));
        log.info("Measure: " + vector1.getId() + ", " + vector2.getId());
        double result = 0;

        if (vector1.getSex() != vector2.getSex() && vector1.getSex() != AccountVector.Sex.NA && vector2.getSex() != AccountVector.Sex.NA)
            result += SEX_WEIGHT;

        result += NAME_WEIGHT * Measures.stringMeasure(vector1.getFirstName(), vector2.getFirstName());
        result += Measures.stringMeasure(vector1.getLastName(), vector2.getLastName());
        result += BDATE_WEIGHT * Measures.measureBirthdate(vector1.getBdate(), vector2.getBdate());

        if (enableLDA)
            result += LDA_WEIGHT * Measures.measureWithLDA(vector1.getGroups(), vector2.getGroups());
        log.info("End measure with atomic counter=" + counter + ". Result=" + result);
        return result;
    }

}
