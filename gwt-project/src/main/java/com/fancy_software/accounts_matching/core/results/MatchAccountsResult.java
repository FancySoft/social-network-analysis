package com.fancy_software.accounts_matching.core.results;

import com.fancy_software.accounts_matching.core.entities.AccountVector;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author John Khandygo
 */

public class MatchAccountsResult extends EmptyResult {
    private Map<Double, AccountVector> bestMatches = new TreeMap<Double, AccountVector>();

    public Map<Double, AccountVector> getBestMatches() {
        return bestMatches;
    }

    public void setBestMatches(Map<Double, AccountVector> bestMatches) {
        this.bestMatches = bestMatches;
    }
}
