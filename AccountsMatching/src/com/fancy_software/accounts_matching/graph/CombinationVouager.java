package com.fancy_software.accounts_matching.graph;

import java.util.LinkedList;
import java.util.List;

/**
 * ************************************************************************
 * Created by akirienko on 5/20/13
 * Copyright (c) 2013 Desman, Inc. All rights reserved.
 * ************************************************************************
 */
public class CombinationVouager {
    private List<Clique> completeSubgraphs;

    public CombinationVouager() {
        completeSubgraphs = new LinkedList<Clique>();
    }

    public synchronized void onCombination(Clique clique) {
        if (clique.isCompleteSubgraph()) completeSubgraphs.add(clique);
    }

    public List<Clique> getCompleteSubgraphs() {
        return completeSubgraphs;
    }
}
