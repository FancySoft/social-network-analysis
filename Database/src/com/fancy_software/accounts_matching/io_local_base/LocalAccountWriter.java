package com.fancy_software.accounts_matching.io_local_base;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocalAccountWriter {

    private static String path = "C:/accounts/vk/";

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        LocalAccountWriter.path = path;
    }

    public static void writeAccountToLocalBase(AccountVector vector) {
        File f = new File(path);
        if(!f.exists())
            f.mkdirs();
        XStream xstream = new XStream(new DomDriver());
        xstream.alias(AccountVector.class.getName(), AccountVector.class);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(generateDefaultPath(vector.getId())));
            xstream.toXML(vector, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateDefaultPath(String id){
        StringBuilder builder = new StringBuilder(path);
        builder.append(id);
        builder.append(".xml");
        return builder.toString();
    }
}
