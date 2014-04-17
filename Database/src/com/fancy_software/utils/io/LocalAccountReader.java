package com.fancy_software.utils.io;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.logger.Log;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.util.*;

public class LocalAccountReader {

    private static final String TAG = LocalAccountReader.class.getSimpleName();

    public static AccountVector readAccountFromLocalBase(String path) throws FileNotFoundException {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias(AccountVector.class.getName(), AccountVector.class);
        try {
            AccountVector accountVector = (AccountVector) xstream.fromXML(new FileReader(path));
            return accountVector;
        } catch (StreamException e) {
            Log.e(TAG, e);
        } catch (ClassCastException e) {
            Log.e(TAG, e);
        }
        return null;
    }

    public static Map<String, AccountVector> readAllAccounts(String path) throws FileNotFoundException {
        Map<String, AccountVector> accounts = new HashMap<>();
        File myFolder = new File(path);
        File[] files = myFolder.listFiles();
        if (files == null) return Collections.emptyMap();
        for (File f : files) {
            AccountVector vector = LocalAccountReader.readAccountFromLocalBase(f.toString());
            accounts.put(vector.getId(), vector);
        }
        return accounts;
    }

    public static Set<String> getStartIds(String path) {
        try {
            Set<String> result = new HashSet<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String s;
            while ((s = reader.readLine()) != null) {
                result.add(s);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
