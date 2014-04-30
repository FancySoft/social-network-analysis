package com.fancy_software.utils;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.logger.Log;
import com.fancy_software.utils.io.LocalAccountReader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * ************************************************************************
 * Created by akirienko on 30.04.14
 * Copyright (c) 2014 Artem Kirienko
 * ************************************************************************
 */
public class CachedAccountReader {
    private static final String TAG = CachedAccountReader.class.getSimpleName();

    private static LoadingCache<String, AccountVector> vkAccounts;
    private static LoadingCache<String, AccountVector> fbAccounts;

    static {
        final Settings settings = Settings.getInstance();
        final String vkPath = settings.get(Settings.VK_ACCOUNT_FOLDER) + File.separator;
        final String fbPath = settings.get(Settings.FB_ACCOUNT_FOLDER) + File.separator;

        vkAccounts = CacheBuilder.newBuilder()
                                 .maximumSize(1000)
                                 .expireAfterAccess(1, TimeUnit.MINUTES)
                                 .build(new CacheLoader<String, AccountVector>() {
                                     @Override
                                     public AccountVector load(String s) throws FileNotFoundException {
                                         return LocalAccountReader.readAccountFromLocalBase(vkPath + s + ".xml");
                                     }
                                 });

        fbAccounts = CacheBuilder.newBuilder()
                                 .maximumSize(1000)
                                 .expireAfterAccess(1, TimeUnit.MINUTES)
                                 .build(new CacheLoader<String, AccountVector>() {
                                     @Override
                                     public AccountVector load(String s) throws FileNotFoundException {
                                         return LocalAccountReader.readAccountFromLocalBase(fbPath + s + ".xml");
                                     }
                                 });
    }

    public static AccountVector getVKAccount(String id) {
        if (id == null)
            throw new IllegalArgumentException("id should not be null");
        try {
            return vkAccounts.get(id);
        } catch (ExecutionException e) {
            Log.e(TAG, "Unable to read vk account with id: " + id);
        }
        return null;
    }

    public static AccountVector getFBAccount(String id) {
        if (id == null)
            throw new IllegalArgumentException("id should not be null");
        try {
            return fbAccounts.get(id);
        } catch (ExecutionException e) {
            Log.e(TAG, "Unable to read fb account with id: " + id);
        }
        return null;
    }
}
