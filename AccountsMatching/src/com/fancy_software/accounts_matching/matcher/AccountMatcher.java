package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountReader;
import com.fancy_software.accounts_matching.model.AccountVector;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountMatcher {

    private static final double MEASURE_BARRIER_FOR_EXACT = 0.2;
    private static final double MEASURE_BARRIER_FOR_PROBABLE = 8.5;
    private static final int MIN_FRIENDS_AMOUNT = 5;
    protected volatile Map<String, String> matchMap;
    private Map<AccountVector, ProbableMatch> probableMatchForAccountsMap;
    private Map<String, AccountVector> accountVectorMap1;
    private Map<String, AccountVector> accountVectorMap2;

    public static double getMeasureBarrierForProbable() {
        return MEASURE_BARRIER_FOR_PROBABLE;
    }

    public static double getMeasureBarrierForExact() {
        return MEASURE_BARRIER_FOR_EXACT;
    }
    //public static Map<Long,Long> match21;

    public Map<AccountVector, ProbableMatch> getProbableMatchForAccountsMap() {
        return probableMatchForAccountsMap;
    }

    public void putMatching(String id1, String id2) {
        matchMap.put(id1, id2);
    }

    public void putProbableMatch(AccountVector vector, ProbableMatch probableMatch) {
        probableMatchForAccountsMap.put(vector, probableMatch);
    }

    public void init(String path1, String path2) throws FileNotFoundException {
        accountVectorMap1 = LocalAccountReader.readAllAccounts(path1);
        accountVectorMap2 = LocalAccountReader.readAllAccounts(path2);
        matchMap = new HashMap<>();
        probableMatchForAccountsMap = new HashMap<>();
    }

    public AccountVector getByIdFromMap1(Long id) {
        return accountVectorMap1.get(id);
    }

    public AccountVector getByIdFromMap2(Long id) {
        return accountVectorMap2.get(id);
    }

    public void match(boolean enableLda) {
        ExecutorService executor = Executors.newFixedThreadPool(accountVectorMap1.keySet().size());
        for (String key : accountVectorMap1.keySet()) {
            executor.execute(new MatchingThreadForExact(this, accountVectorMap1.get(key), accountVectorMap2, enableLda));
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.yield();
        }
        matchingForNotExact();
        System.out.println("RESULT:");
        Utils.print(matchMap);
        System.out.println(successPercent());
    }

    private double successPercent() {
        double counter = 0;
        for (String id : matchMap.keySet()) {
            if (id.equals(matchMap.get(id)))
                counter++;
        }
        return 100 * counter / matchMap.keySet().size();
    }

    public boolean hasMatch(String accountId) {
        return matchMap.containsKey(accountId);
    }

    //todo cases of no probable matches
    private void matchingForNotExact() {
        for (String key : accountVectorMap1.keySet())
            if (!hasMatch(key)) {
                AccountVector vector = accountVectorMap1.get(key);
                if (probableMatchForAccountsMap.get(vector).getProbableMatchesForVector().isEmpty())
                    matchMap.put(vector.getId(), null);
                else {
                    AccountVector result = findInProbableMatch(vector, probableMatchForAccountsMap.get(vector));
                    matchMap.put(vector.getId(), result.getId());
                }
            }
    }

    private AccountVector findInProbableMatch(AccountVector accountVector, ProbableMatch probableMatch) {
        //finding common friends, if their amount is enough - it's match
        int maxFriendAmount = Integer.MIN_VALUE;
        int friendCounter = 0;
        ProbableMatch matchProbable = new ProbableMatch(accountVector);
        AccountVector result = null;
        for (AccountVector probableFriend : probableMatch.getProbableMatchesForVector().keySet())    {
            if (countCommonFriends(accountVector, probableFriend) > maxFriendAmount) {
                maxFriendAmount = countCommonFriends(accountVector, probableFriend);
                result = probableFriend;
                friendCounter++;
            }
            if(friendCounter>1){
                matchProbable.addProbableMatch(probableFriend, probableMatch.getMeasureValueForAccount(probableFriend));
            }
        }
        if (friendCounter>1){
           return findWithMinMeasure(accountVector,matchProbable);
        }
        if (maxFriendAmount > MIN_FRIENDS_AMOUNT)
            return result;
        else
            return findWithMinMeasure(accountVector, probableMatch);
    }

    private AccountVector findWithMinMeasure(AccountVector accountVector, ProbableMatch probableMatch) {
        double measure = Double.MAX_VALUE;
        AccountVector result = null;
        for (AccountVector probableFriend : probableMatch.getProbableMatchesForVector().keySet())
            if (probableMatch.getMeasureValueForAccount(probableFriend) < measure) {
                measure = probableMatch.getMeasureValueForAccount(probableFriend);
                result = probableFriend;

            }

        return result;
    }

    private int countCommonFriends(AccountVector vector1, AccountVector vector2) {
        int counter = 0;
        for (String friendId : vector1.getFriends())
            if (hasMatch(friendId) && vector2.getFriends().contains(friendId))
                counter++;
        return counter;
    }
}