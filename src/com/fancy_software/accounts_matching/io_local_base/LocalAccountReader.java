package com.fancy_software.accounts_matching.io_local_base;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class LocalAccountReader {

    public static AccountVector readAccountFromLocalBase(String path) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("AccountVector", AccountVector.class);
        try {
            return (AccountVector) xstream.fromXML(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (StreamException e) {
            e.printStackTrace();
            return null;
        }
    }
}
