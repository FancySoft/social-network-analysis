package com.fancy_software.crawling.utils;

import com.fancy_software.crawling.parsers.vk.VkApiParser;
import com.fancy_software.logger.Log;
import com.fancy_software.utils.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * ************************************************************************
 * Created by akirienko on 20.04.14
 * Copyright (c) 2014 Artem Kirienko
 * ************************************************************************
 */
public class VkIdsUpdater {
    private static final String TAG = VkIdsUpdater.class.getSimpleName();

    private static final VkApiParser parser;

    static {
        parser = new VkApiParser(null);

        Settings settings = Settings.getInstance();
        List<String> logins = settings.getArray(Settings.VK_LOGINS);
        List<String> passwords = settings.getArray(Settings.VK_PASSWORDS);

        String login = logins.get(0);
        String password = passwords.get(0);

        parser.auth(login, password);
    }

    private static String getLongId(String stringId) {
        return parser.convertId(stringId);
    }

    private static void updateXml(String path) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setIgnoringElementContentWhitespace(true);
        docFactory.setIgnoringComments(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(path);
        doc.getDocumentElement().normalize();

        NodeList vks = doc.getElementsByTagName("vk");
        for (int i = 0; i < vks.getLength(); i++) {
            Node vk = vks.item(i);
            Node id = vk.getFirstChild().getNextSibling();
            String stringId = id.getTextContent();
            String longId = getLongId(stringId);
            id.setTextContent(longId);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(path));
        transformer.transform(source, result);

        Log.d(TAG, path + " done");
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        updateXml("config/matchings/matchings1.txt");
        updateXml("config/matchings/matchings2.txt");
    }
}
