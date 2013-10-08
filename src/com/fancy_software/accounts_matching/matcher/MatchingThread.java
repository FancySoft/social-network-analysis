package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.model.AccountVector;

import java.util.Map;

public class MatchingThread extends Thread {

    private AccountMatcher matcher;
    private AccountVector accountVector;
    private Map<Long, AccountVector> accountVectorMap;
    private boolean enableLDA = false;

    public MatchingThread(AccountMatcher matcher, AccountVector accountVector, Map<Long, AccountVector> accountVectorMap) {
        this.matcher = matcher;
        this.accountVector = accountVector;
        this.accountVectorMap = accountVectorMap;
    }

    @Override
    public void run() {
        ProbableMatch probableMatch = firstMatch(accountVector);
        AccountVector matchingResult = findInProbableMatch(probableMatch);
        System.out.println(matchingResult);
        if (matchingResult != null)
            matcher.putMatching(accountVector.getId(), matchingResult.getId());
    }

    private ProbableMatch firstMatch(AccountVector vector) {
        try {
            ProbableMatch probableMatch = new ProbableMatch(vector);
            AccountMeasurer measurer = new AccountMeasurer(vector);
            for (Long key : accountVectorMap.keySet()) {
                AccountVector vector2 = accountVectorMap.get(key);
                measurer.setVector2(vector2);
                double result = measurer.measure(enableLDA);
                if (result < AccountMatcher.getMeasureBarrier()) {
                    System.out.println("success");
                    probableMatch.addProbableMatch(vector2, result);
                }
            }
            return probableMatch;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private AccountVector findInProbableMatch(ProbableMatch probableMatch) {
        //todo something better than this shit
        double min = Double.MAX_VALUE;
        AccountVector result = null;
        for (AccountVector vector : probableMatch.getProbableMatchesForVector().keySet())
            if (probableMatch.getProbableMatchesForVector().get(vector) < min) {
                min = probableMatch.getProbableMatchesForVector().get(vector);
                result = vector;
            }
        return result;
    }
}
