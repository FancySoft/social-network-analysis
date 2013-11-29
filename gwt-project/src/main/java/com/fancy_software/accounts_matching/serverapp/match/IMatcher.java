package com.fancy_software.accounts_matching.serverapp.match;

import com.fancy_software.accounts_matching.core.entities.AccountVector;

/**
 * ************************************************************************
 * Created by akirienko on 18.10.13
 * Copyright (c) 2013 Desman, Inc. All rights reserved.
 * ************************************************************************
 */
public interface IMatcher {
    public AccountVector match(AccountVector goal);
}
