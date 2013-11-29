package com.fancy_software.accounts_matching.serverapp.localio;

import com.fancy_software.accounts_matching.core.entities.AccountVector;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class LocalAccountWriter {

    public static void writeAccountToLocalBase(AccountVector vector, String path) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("AccountVector", AccountVector.class);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            xstream.toXML(vector, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeAccountsToLocalBase(List<AccountVector> vectorList, String path) {
        Iterator iter = vectorList.listIterator();
        while (iter.hasNext()) {
            AccountVector vector = (AccountVector) iter.next();
            writeAccountToLocalBase(vector, PathGenerator.generateDefaultPath(vector.getId()));
        }
    }
}
