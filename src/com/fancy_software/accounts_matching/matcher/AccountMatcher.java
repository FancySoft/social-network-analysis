package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.data_randomizer.Randomizer;
import com.fancy_software.accounts_matching.io_local_base.LocalAccountReader;
import com.fancy_software.accounts_matching.model.AccountVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountMatcher {

    private static final double MEASURE_BARRIER = 8;
    public Map<Long, AccountVector> accountVectorMap1;
    public Map<Long, AccountVector> accountVectorMap2;
    private volatile Map<Long, Long> matchMap;

    public AccountMatcher() {
    }
    //public static Map<Long,Long> match21;

    public void putMatching(long id1, long id2) {
        matchMap.put(id1, id2);
    }

    public static double getMeasureBarrier() {
        return MEASURE_BARRIER;
    }

    public void init(long accountID, String path1, String path2) throws FileNotFoundException {
        accountVectorMap1 = new HashMap<Long, AccountVector>();
        accountVectorMap2 = new HashMap<Long, AccountVector>();
        matchMap = new HashMap<Long, Long>();
        //match21=new HashMap<Long,Long>();
        String fileName = path1 + accountID + ".xml";
        AccountVector fixAccount = LocalAccountReader.readAccountFromLocalBase(fileName);
//        for (long frienID : fixAccount.friends) {
//            AccountVector friend = LocalAccountReader.readAccountFromLocalBase(path1 + frienID + ".xml");
//            accountVectorMap1.put(frienID, friend);
//        }
//        accountVectorMap1.put(accountID, fixAccount);
        fileName = path2;
        File myFolder = new File(fileName);
        File[] files = myFolder.listFiles();
        for (File f : files) {
            AccountVector vector1 = LocalAccountReader.readAccountFromLocalBase(f.toString());
            AccountVector vector2 = LocalAccountReader.readAccountFromLocalBase(f.toString());
            Randomizer.randomize(vector2);
            accountVectorMap1.put(vector1.getId(), vector1);
            accountVectorMap2.put(vector2.getId(), vector2);
        }
    }

    public AccountVector getByIdFromMap1(int id) {
        return accountVectorMap1.get(id);
    }

    public AccountVector getByIdFromMap2(int id) {
        return accountVectorMap2.get(id);
    }

    public void match() {
        ExecutorService executor = Executors.newFixedThreadPool(accountVectorMap1.keySet().size());
        for (Long key : accountVectorMap1.keySet()) {
            executor.execute(new MatchingThread(this, accountVectorMap1.get(key), accountVectorMap2));
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("RESULT:");
        Utils.print(matchMap);
    }
}