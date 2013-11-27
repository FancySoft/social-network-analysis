package com.fancy_software.accounts_matching.io_local_base;

import com.fancy_software.logger.Log;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

/**
 * Created by Yaro
 * * Date: 15.11.13
 * Time: 15:09
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    @SuppressWarnings("unchecked")
    public static Map<String, String> getAuthInfo(String path) {
        XStream xstream = new XStream(new DomDriver());
        try {
            return (Map<String, String>) xstream.fromXML(new FileReader(path));

        } catch (FileNotFoundException | StreamException e) {
            Log.e(TAG, e);
        }
        return null;
    }

}
