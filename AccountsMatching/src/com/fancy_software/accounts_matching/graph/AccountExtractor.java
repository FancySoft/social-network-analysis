package com.fancy_software.accounts_matching.graph;

import com.fancy_software.accounts_matching.model.AccountVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountExtractor {

    public static List<AccountVector> commonAccounts(Map<String, AccountVector> accountVectorMap, List<Clique> res) {

        List<AccountVector> result = new ArrayList<>();
        int counter = 0;
        for (String i : accountVectorMap.keySet()) {
            for (Clique clique : res) {
                if (clique.contains(i))
                    counter++;
            }
            if (counter > res.size() / 2)
                result.add(accountVectorMap.get(i));
            counter = 0;
        }
        return result;
    }
}
