package com.fancy_software.accounts_matching.crawling.facebook_parser_test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;

//works with local files
public class FacebookBasicParser {

    private static final String USER_NAME_SURNAME = "a[class=_8_2]";
    private static final String USER_INFO_TABLE = "table[class=_5e7- profileInfoTable _3stp _3stn]";
    private static final String USER_ADDITIONAL_INFO = "tr[class=_5jsb _5nyi";
    private static final String FRIEND_IDENTIFIER_FOR_FRIEND_NAME = "fref=pb&hc_location=friends_tab";     //for accounts like 'vip.katierinka'
    private static final String FRIEND_IDENTIFIER_FOR_FRIEND_ID = "sk=friends_mutual";       //for accounts like 'id=100001128680467'
    private static final String SEPARATOR1 = "\\?";
    private static final String SEPARATOR2 = "/";
    private static final String SEPARATOR3 = "&";

    //for url like 'https://www.facebook.com/username/about'
    public List<Object> getAccountInfo(String input) throws IOException {
        File inputFile = new File(input);
        Document doc = Jsoup.parse(inputFile, "UTF-8", "http://example.com/");

        List<Object> friends = new LinkedList<Object>();

        Element userInfoTable = doc.select(USER_INFO_TABLE).first();
        Elements nameLinks = doc.select(USER_NAME_SURNAME);
        try {
            if (nameLinks.size() == 1) {
                //todo check names like 'Arthur john smith'
                System.out.println(nameLinks.first().text());
                String[] name_and_surname = nameLinks.first().text().split(" ");
                friends.add(name_and_surname[0]);
                friends.add(name_and_surname[1]);
            } else
                return null;
            Iterator<Element> iterator = userInfoTable.select(USER_ADDITIONAL_INFO).iterator();

            int counter = 0;
            //we need only birth date and sex now
            while (iterator.hasNext()) {
                Iterator<Element> iterator1 = userInfoTable.select("div[class=clearfix]").iterator();
                //first - birth date, second - sex
                while (iterator1.hasNext()) {
                    if (counter < 2)
                        friends.add(iterator1.next().text());
                    else
                        return friends;
                    counter++;
                }
                iterator.next();
            }
            return friends;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return null;
    }

    //for url like 'https://www.facebook.com/username/friends_all'
    public Set<String> getAccountFriends(String input) throws IOException {

        File inputFile = new File(input);
        Document doc = Jsoup.parse(inputFile, "UTF-8", "http://example.com/");
        Elements links = doc.select("a[href]");
        Set<String> friends = new LinkedHashSet<String>();
        try {
            for (Element link : links) {
                String attr = link.attr("abs:href");
//            System.out.println("Current link " + attr);
                String[] attrSplit = attr.split(SEPARATOR1);
                if (attrSplit.length == 2)
                    if (attrSplit[1].equals(FRIEND_IDENTIFIER_FOR_FRIEND_NAME)) {
                        String[] attrSplit2 = attrSplit[0].split(SEPARATOR2);
                        friends.add(attrSplit2[3]);
                    } else {
                        String[] attrSplit1 = attrSplit[1].split(SEPARATOR3);
                        if (attrSplit1.length == 2 && attrSplit1[1].equals(FRIEND_IDENTIFIER_FOR_FRIEND_ID)) {
                            friends.add(attrSplit1[0].substring(3, attrSplit1[0].length()));
                        }
                    }
            }
        /*
        System.out.println("RESULT: " + friends.size());
        for (String s : friends) {
            System.out.println(s);
        } */
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return friends;
    }
}
