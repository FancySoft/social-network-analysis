package com.fancy_software.accounts_matching.graph;

import com.fancy_software.accounts_matching.model.AccountVector;

import java.util.*;

public class CliquesExtractor {

    public static List<Clique> getKCliques(List<AccountVector> graph, int k) {
        CombinationVouager callback = new CombinationVouager();
        generate(graph.size(), k, -1, new ArrayList<AccountVector>(), graph, callback);

        return callback.getCompleteSubgraphs();
    }

    private static void generate(int n, int k, int maxUsed, List<AccountVector> cur, List<AccountVector> graph,
                                 CombinationVouager callback) {
        if (cur.size() == k) {
            callback.onCombination(Clique.fromCollection(cur));
            return;
        }
        for (int i = maxUsed + 1; i < n; i++) {
            List<AccountVector> next = new ArrayList<AccountVector>(cur);
            next.add(graph.get(i));
            generate(n, k, i, next, graph, callback);
        }
    }

    private static void printAll(Collection<AccountVector> c) {
        for (AccountVector v : c) System.out.println(v.getFirst_name() + " " + v.getLast_name());
    }

    public static List<Clique> mergeKCliques(List<Clique> kCliques, int k) {
        Map<Integer, Set<Integer>> adjacent = new TreeMap<Integer, Set<Integer>>();
        for (int i = 0; i < kCliques.size(); i++) {
            for (int j = i + 1; j < kCliques.size(); j++) {
                if (kCliques.get(i).mutualVertices(kCliques.get(j)) == k - 1) {
                    if (adjacent.containsKey(i)) {
                        adjacent.get(i).add(j);
                    } else {
                        Set<Integer> temp = new TreeSet<Integer>();
                        temp.add(j);
                        adjacent.put(i, temp);
                    }
                }
            }
        }
        List<Clique> result = new LinkedList<Clique>();
        Clique temp;
        for (int i = 0; i < kCliques.size(); i++) {
            temp = merge(adjacent, kCliques, i);
            if (temp.size() > 0) {
                result.add(temp);
            }
        }
        return result;
    }

    private static Clique merge(Map<Integer, Set<Integer>> adjacent, List<Clique> kCliques, int current) {

        if (kCliques.get(current).isUsed())
            return new Clique();
        Clique result = kCliques.get(current);
        Set<Integer> toWatch = adjacent.get(current);
        kCliques.get(current).setUsed();
        if (toWatch != null) {
            for (Integer i : toWatch) {
                result.add(kCliques.get(i));
                result.add(merge(adjacent, kCliques, i));
            }
        }

        return result;
    }

    public static List<Clique> filter(List<Clique> cliques) {
        List<Clique> result = cliques;

        int minSize;
        for (int i = 0; i < result.size(); i++) {
            for (int j = i + 1; j < result.size(); j++) {
                minSize = result.get(i).size();
                if (result.get(j).size() < minSize) minSize = result.get(j).size();
                if (result.get(i).mutualVertices(result.get(j)) > minSize - 2) {
                    result.get(i).add(result.get(j));
                    result.remove(j);
                    j--;
                }
            }
        }

        return result;
    }
}
