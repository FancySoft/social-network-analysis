package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.model.AccountVector;

import java.util.HashMap;
import java.util.Map;

public class ProbableMatch {

    private AccountVector vector;
    private Map<AccountVector, Double> probableMatchesForVector;

    public ProbableMatch(AccountVector vector) {
        this.vector = vector;
        probableMatchesForVector = new HashMap<>();
    }

    @SuppressWarnings("unused")
    public AccountVector getAccountVector() {
        return vector;
    }

    public Map<AccountVector, Double> getProbableMatchesForVector() {
        return probableMatchesForVector;
    }

    public void addProbableMatch(AccountVector probableMatch, Double measureValue) {
        probableMatchesForVector.put(probableMatch, measureValue);
    }

    @SuppressWarnings("unused")
    public AccountVector getProbableMatchById(String id) {
        for (AccountVector accountVector : probableMatchesForVector.keySet())
            if (accountVector.getId().equals(id))
                return accountVector;
        return null;
    }

    @SuppressWarnings("unused")
    public void removeProbableMatch(AccountVector accountVector) {
        probableMatchesForVector.remove(accountVector);
    }

    @SuppressWarnings("unused")
    public void removeProbableMatchById(String id) {
        for (AccountVector accountVector : probableMatchesForVector.keySet())
            if (accountVector.getId().equals(id))
                probableMatchesForVector.remove(accountVector);
    }

    public double getMeasureValueForAccount(AccountVector accountVector) {
        return probableMatchesForVector.get(accountVector);
    }

    @Override
    public String toString() {
        return "ProbableMatch{" +
                "probableMatchesForVector=" + probableMatchesForVector +
                '}';
    }
}
