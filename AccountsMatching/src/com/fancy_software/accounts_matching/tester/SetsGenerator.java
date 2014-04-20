package com.fancy_software.accounts_matching.tester;

import com.fancy_software.logger.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ************************************************************************
 * Created by akirienko on 20.04.14
 * Copyright (c) 2014 Artem Kirienko
 * ************************************************************************
 */
public class SetsGenerator {
    private static final String TAG = SetsGenerator.class.getSimpleName();

    /** Map<VK id, FB id> */
    private static Map<String, String> matched;

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

    public static void main(String[] args) {
        matched = new HashMap<>(256);
        fillMatchings("config/matchings/matchings1.txt");
        fillMatchings("config/matchings/matchings2.txt");
        Log.d(TAG, "Matchings filled up from XMLs");
    }
}
