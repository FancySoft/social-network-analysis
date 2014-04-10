package com.fancy_software.accounts_matching.tester;

import com.fancy_software.accounts_matching.model.AccountVector;

/**
 * ************************************************************************
 * Created by akirienko on 10.04.14
 * Copyright (c) 2014 Artem Kirienko
 * ************************************************************************
 */
public class Couple {
    private AccountVector v1;
    private AccountVector v2;
    private boolean areSame;

    public Couple(AccountVector v1, AccountVector v2, boolean areSame) {
        this.v1 = v1;
        this.v2 = v2;
        this.areSame = areSame;
    }

    public AccountVector getV1() {
        return v1;
    }

    public AccountVector getV2() {
        return v2;
    }

    public boolean areSame() {
        return areSame;
    }
}
