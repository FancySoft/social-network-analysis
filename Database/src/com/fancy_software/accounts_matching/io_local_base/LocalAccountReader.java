package com.fancy_software.accounts_matching.io_local_base;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.logger.Log;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LocalAccountReader {

    private static final String TAG = LocalAccountReader.class.getSimpleName();

    public static AccountVector readAccountFromLocalBase(String path) throws FileNotFoundException{
        XStream xstream = new XStream(new DomDriver());
        xstream.alias(AccountVector.class.getName(), AccountVector.class);
        try {
            AccountVector accountVector = (AccountVector)xstream.fromXML(new FileReader(path));
            return accountVector;
        } catch (StreamException e) {
            Log.e(TAG,e);
        }
        catch (ClassCastException e){
            Log.e(TAG,e);
        }
        return null;
    }

    public static Map<String, AccountVector> readAllAccounts(String path) throws FileNotFoundException{
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
}
