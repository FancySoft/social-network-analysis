package com.fancy_software.accounts_matching.tester;

/**
 * ************************************************************************
 * Created by akirienko on 29.04.14
 * Copyright (c) 2014 Artem Kirienko
 * ************************************************************************
 */
public class SimpleCouple {
    private String id1;
    private String id2;
    private boolean areEqual;

    public SimpleCouple(String id1, String id2, boolean areEqual) {
        this.id1 = id1;
        this.id2 = id2;
        this.areEqual = areEqual;
    }

    public String getId1() {
        return id1;
    }

    public String getId2() {
        return id2;
    }

    public boolean areEqual() {
        return areEqual;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleCouple that = (SimpleCouple) o;

        if (!id1.equals(that.id1)) return false;
        if (!id2.equals(that.id2)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id1.hashCode();
        result = 31 * result + id2.hashCode();
        return result;
    }
}
