package com.fancy_software.utils.io;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocalAccountWriter {


    public static void writeAccountToLocalBase(AccountVector vector, String folder) {
        File f = new File(folder);
        if (!f.exists())
            f.mkdirs();
        XStream xstream = new XStream(new DomDriver());
        xstream.alias(AccountVector.class.getName(), AccountVector.class);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(
                    new File(generateDefaultPath(vector.getId(), folder)));
            xstream.toXML(vector, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateDefaultPath(String id, String folder) {
        StringBuilder builder = new StringBuilder(folder);
        builder.append(File.separator);
        builder.append(id);
        builder.append(".xml");
        return builder.toString();
    }
}
