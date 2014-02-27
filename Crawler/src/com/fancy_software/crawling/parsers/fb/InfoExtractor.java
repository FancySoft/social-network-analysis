package com.fancy_software.crawling.parsers.fb;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class InfoExtractor {

    private static String extractFriendsId(String link) throws IllegalArgumentException {
        String[] attrSplit = link.split(HtmlTags.SEPARATOR1);
        if (attrSplit.length == 2)
            if (attrSplit[1].equals(HtmlTags.FRIEND_IDENTIFIER_FOR_FRIEND_NAME)) {
                String[] attrSplit2 = attrSplit[0].split(HtmlTags.SEPARATOR2);
                return attrSplit2[3];
            } else {
                String[] attrSplit1 = attrSplit[1].split(HtmlTags.SEPARATOR3);
                if (attrSplit1.length == 2 && attrSplit1[1].equals(HtmlTags.FRIEND_IDENTIFIER_FOR_FRIEND_ID)) {
                    return attrSplit1[0].substring(3, attrSplit1[0].length());
                }
            }
        return null;
    }

    public static String extractId(String link) throws IllegalArgumentException {
        String[] attrSplit = link.split(HtmlTags.SEPARATOR2);
        if (attrSplit.length == 4)
            return attrSplit[3];
        else
            throw new IllegalStateException();
    }

    public static Set<String> getAccountFriends(String source) throws IOException {
        Document doc = Jsoup.parse(source);
//        System.out.println(doc);
        Elements links = doc.select("a[href]");
//        System.out.println(links);
        Set<String> friends = new LinkedHashSet<>();
        try {
            for (Element link : links) {
                String attr = link.attr("abs:href");
                friends.add(extractFriendsId(attr));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return friends;
        } catch
                (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return friends;
    }
}
