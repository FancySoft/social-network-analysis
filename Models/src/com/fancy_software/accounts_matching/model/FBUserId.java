package com.fancy_software.accounts_matching.model;

/**
 * ************************************************************************
 * Created by akirienko on 30.11.13
 * Copyright (c) 2013 Artem Kirienko. All rights reserved.
 * ************************************************************************
 */
public class FBUserId implements IUserId {
    private String mId;

    public FBUserId(String id) {
        mId = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FBUserId)) return false;
        FBUserId that = (FBUserId) obj;
        return mId.equals(that.mId);
    }

    @Override
    public String toString() {
        return mId;
    }

    @Override
    public SocialNetworkId getNetworkId() {
        return SocialNetworkId.Facebook;
    }
}
