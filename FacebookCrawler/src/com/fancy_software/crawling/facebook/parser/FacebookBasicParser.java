package com.fancy_software.crawling.facebook.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;


public class FacebookBasicParser {

    private static final String USER_NAME_SURNAME                 = "a[class=_8_2]";
    private static final String USER_INFO_TABLE                   = "table[class=_5e7- profileInfoTable _3stp _3stn]";
    private static final String USER_ADDITIONAL_INFO              = "tr[class=_5jsb _5nyi";
    private static final String FRIEND_IDENTIFIER_FOR_FRIEND_NAME = "fref=pb&hc_location=friends_tab";     //for accounts like 'vip.katierinka'
    private static final String FRIEND_IDENTIFIER_FOR_FRIEND_ID   = "sk=friends_mutual";       //for accounts like 'id=100001128680467'
    private static final String SEPARATOR1                        = "\\?";
    private static final String SEPARATOR2                        = "/";
    private static final String SEPARATOR3                        = "&";

    /**
     * For url like 'https://www.facebook.com/username/about'
     *
     * @param input url content
     * @return parsed data
     */
    public static List<Object> getAccountInfo(String input) {
        Document doc = Jsoup.parse(input);

        List<Object> friends = new LinkedList<>();

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
                //first - birth date, second - sex
                for (Object o : userInfoTable.select("div[class=clearfix]")) {
                    if (counter < 2)
                        friends.add(((Element) o).text());
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

    /**
     * For url like 'https://www.facebook.com/username/friends_all'
     *
     * @param input page text
     * @return friend ids
     */
    public static Set<String> getAccountFriends(String input) {
        Document doc = Jsoup.parse(input);
        Elements links = doc.select("a[href]");
        Set<String> friends = new LinkedHashSet<>();
        try {
            for (Element link : links) {
                String attr = link.attr("abs:href");
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
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return friends;
    }
}
