package com.fancy_software.accounts_matching.graph;

import com.fancy_software.accounts_matching.model.AccountVector;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Clique {
    private List<AccountVector> people;
    private boolean used;

    public Clique() {
        people = new LinkedList<AccountVector>();
        used = false;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed() {
        used = true;
    }

    public int size() {
        return people.size();
    }

    public List<String> getInterests() {
        List<String> result = new LinkedList<String>();
        for (AccountVector v : people) result.addAll(v.getGroups());
        return result;
    }

    public void add(AccountVector person) {
        people.add(person);
    }

    public void add(Clique clique) {
        boolean contains;
        for (AccountVector v : clique.people) {
            contains = false;
            for (AccountVector v2 : people) {
                if (v.getId() == v2.getId()) {
                    contains = true;
                    break;
                }
            }
            if (!contains) people.add(v);
        }
    }

    public void print() {
        for (AccountVector v : people)
            System.out.println(v.getFirst_name() + " " + v.getLast_name());
    }

    public boolean equals(Clique other) {
        if (people.size() != other.size()) return false;
        boolean found;
        for (AccountVector v1 : people) {
            found = false;
            for (AccountVector v2 : other.people) {
                if (v1.getId() == v2.getId()) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    public int mutualVertices(Clique other) {
        int result = 0;
        for (AccountVector v1 : people) {
            for (AccountVector v2 : other.people) {
                if (v1.getId() == v2.getId()) result++;
            }
        }
        return result;
    }

    public boolean isCompleteSubgraph() {
        for (AccountVector cur : people) {
            for (AccountVector check : people) {
                if (cur != check && !cur.hasFriend(check.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Clique fromCollection(Collection<AccountVector> vectors) {
        Clique res = new Clique();
        res.people = new LinkedList<AccountVector>();
        res.people.addAll(vectors);
        return res;
    }

    public boolean contains(String id) {
        for (AccountVector vector : people)
            if (vector.getId() == id)
                return true;
        return false;
    }
}
