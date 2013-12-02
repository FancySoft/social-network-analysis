package com.fancy_software.accounts_matching.serverapp.localio;

import com.fancy_software.accounts_matching.core.entities.AccountVector;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class LocalAccountReader {

    public static AccountVector readAccountFromLocalBase(String path) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("AccountVector", AccountVector.class);
        try {
            return (AccountVector) xstream.fromXML(new InputStreamReader(new FileInputStream(path), "UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (StreamException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HashMap<Long,AccountVector> readAllAccounts(String path){
        HashMap<Long,AccountVector> accounts = new HashMap<Long, AccountVector>();
        File myFolder = new File(path);
        File[] files = myFolder.listFiles();
        for (File f : files) {
            AccountVector vector = LocalAccountReader.readAccountFromLocalBase(f.toString());
            accounts.put(vector.getId(), vector);
        }
        return accounts;
    }
}
