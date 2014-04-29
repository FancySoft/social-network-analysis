package com.fancy_software.accounts_matching.tester;

import com.fancy_software.logger.Log;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

/**
 * ************************************************************************
 * Created by akirienko on 20.04.14
 * Copyright (c) 2014 Artem Kirienko
 * ************************************************************************
 */
public class SetsGenerator {
    private static final String TAG = SetsGenerator.class.getSimpleName();

    /**
     * Map<VK id, FB id>
     */
    private static Map<String, String> matched;

    /**
     * Test & Train sets
     */
    private static Set<SimpleCouple> trainSet;
    private static Set<SimpleCouple> testSet;

    private static void fillMatchings(String path) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setIgnoringElementContentWhitespace(true);
        docFactory.setIgnoringComments(true);
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            Log.e(TAG, e);
            return;
        }
        Document doc;
        try {
            doc = docBuilder.parse(path);
        } catch (SAXException | IOException e) {
            Log.e(TAG, e);
            return;
        }
        doc.getDocumentElement().normalize();

        NodeList users = doc.getElementsByTagName("user");
        for (int i = 0; i < users.getLength(); i++) {
            Node user = users.item(i);
            Node vk = user.getFirstChild().getNextSibling();
            Node id = vk.getFirstChild().getNextSibling();
            String vkId = id.getTextContent();
            Node fb = vk.getNextSibling().getNextSibling();
            id = fb.getFirstChild().getNextSibling();
            String fbId = id.getTextContent();
            matched.put(vkId, fbId);
        }

        Log.d(TAG, path + " done");
    }

    private static void generate() {
        int counter = 0;
        int lowBarrier = matched.size() / 3;
        int highBarrier = lowBarrier * 2;
        int FALSE_VALUES_COUNT = 800;
        int barrier = FALSE_VALUES_COUNT / 2;
        trainSet = new HashSet<>(highBarrier + 1);
        testSet = new HashSet<>(barrier + 1);
        for (Map.Entry<String, String> pair : matched.entrySet()) {
            if (counter < highBarrier) {
                trainSet.add(new SimpleCouple(pair.getKey(), pair.getValue(), true));
            }
            if (counter > lowBarrier) {
                testSet.add(new SimpleCouple(pair.getKey(), pair.getValue(), true));
            }
            counter++;
        }

        Set<String> vkIds = matched.keySet();
        Set<String> fbIds = new HashSet<>(matched.values());

        if (vkIds.size() != fbIds.size()) {
            Log.e(TAG, "Something went wrong: " + vkIds.size() + ", " + fbIds.size());
        }

        for (String vkId : vkIds) {
            if (FALSE_VALUES_COUNT == 0) break;
            for (String fbId : fbIds) {
                if (FALSE_VALUES_COUNT == 0) break;

                if (!matched.get(vkId).equals(fbId)) {
                    if (FALSE_VALUES_COUNT < barrier) {
                        trainSet.add(new SimpleCouple(vkId, fbId, false));
                    } else {
                        testSet.add(new SimpleCouple(vkId, fbId, false));
                    }
                }

                FALSE_VALUES_COUNT--;
            }
        }
    }

    /**
     * "trainSet": Set<SimpleCouple>
     * "testSet": Set<SimpleCouple>
     * @return sets stored in Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Set<SimpleCouple>> getSets() {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias(Map.class.getName(), Map.class);
        try {
            Object sets = xstream.fromXML(new FileReader("config/sets.xml"));
            return (Map<String, Set<SimpleCouple>>) sets;
        } catch (StreamException | ClassCastException | FileNotFoundException e) {
            Log.e(TAG, e);
        }
        return null;
    }

    public static void main(String[] args) {
        matched = new HashMap<>(256);
        fillMatchings("config/matchings/matchings1.txt");
        fillMatchings("config/matchings/matchings2.txt");
        Log.d(TAG, "Matchings filled up from XMLs");

        generate();
        Log.d(TAG, "Sets generated");

        Map<String, Object> result = new HashMap<>(2);
        result.put("trainSet", trainSet);
        result.put("testSet", testSet);

        XStream xstream = new XStream(new DomDriver());
        xstream.alias(Map.class.getName(), Map.class);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(
                    new File("config/sets.xml"));
            xstream.toXML(result, fileOutputStream);
            fileOutputStream.close();
            Log.d(TAG, "sets.xml created");
        } catch (IOException e) {
            Log.e(TAG, e);
        }
    }
}
