package com.fancy_software.accounts_matching.model.user_id;

import com.fancy_software.accounts_matching.model.SocialNetworkId;

/**
 * ************************************************************************
 * Created by akirienko on 30.11.13
 * Copyright (c) 2013 Artem Kirienko. All rights reserved.
 * ************************************************************************
 */
public class VKUserId implements IUserId {
    private long mId;

    public VKUserId(long id) {
        mId = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VKUserId)) return false;
        VKUserId that = (VKUserId) obj;
        return mId == that.mId;
    }

    @Override
    public String toString() {
        return Long.toString(mId);
    }

    @Override
    public SocialNetworkId getNetworkId() {
        return SocialNetworkId.VK;
    }
}
