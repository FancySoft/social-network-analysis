package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.logger.Log;

import java.io.IOException;
import java.util.Map;

public class MatchingThreadForExact implements Runnable {

    private static final String TAG = MatchingThreadForExact.class.getSimpleName();

    private AccountMatcher matcher;
    private AccountVector accountVector;
    private Map<Long, AccountVector> accountVectorMap;
    private boolean enableLDA;

    public MatchingThreadForExact(AccountMatcher matcher, AccountVector accountVector, Map<Long, AccountVector> accountVectorMap, boolean enableLDA) {
        this.matcher = matcher;
        this.accountVector = accountVector;
        this.accountVectorMap = accountVectorMap;
        this.enableLDA = enableLDA;
    }

    @Override
    public void run() {
        firstMatch(accountVector);
    }

    private void firstMatch(AccountVector vector) {
        try {
            ProbableMatch probableMatch = new ProbableMatch(vector);
            AccountMeasurer measurer = new AccountMeasurer(vector);
            for (Long key : accountVectorMap.keySet()) {
                AccountVector vector2 = accountVectorMap.get(key);
                measurer.setVector2(vector2);
                double result = measurer.measure(enableLDA);
                if (result < AccountMatcher.getMeasureBarrierForExact()) {
                    Log.d(TAG, "Success - exact match");
                    matcher.putMatching(vector.getId(), vector2.getId());
//                    probableMatch.addProbableMatch(vector2, result);
                } else if (result < AccountMatcher.getMeasureBarrierForProbable())
                    probableMatch.addProbableMatch(vector2, result);
            }
            matcher.putProbableMatch(vector, probableMatch);
        } catch (IOException e) {
            Log.e(TAG, e);
        }
    }
}
