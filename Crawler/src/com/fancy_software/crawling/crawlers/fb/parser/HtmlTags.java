package com.fancy_software.crawling.crawlers.fb.parser;

public final class HtmlTags {
    public static final String USER_NAME_SURNAME                 = "a[class=_8_2]";
    public static final String USER_INFO_TABLE                   = "table[class=_5e7- profileInfoTable _3stp _3stn]";
    public static final String USER_ADDITIONAL_INFO              = "tr[class=_5jsb _5nyi";
    public static final String FRIEND_IDENTIFIER_FOR_FRIEND_NAME = "fref=pb&hc_location=friends_tab";     //for accounts like 'vip.katierinka'
    public static final String FRIEND_IDENTIFIER_FOR_FRIEND_ID   = "sk=friends_mutual";       //for accounts like 'id=100001128680467'
    public static final String SEPARATOR1                        = "\\?";
    public static final String SEPARATOR2                        = "/";
    public static final String SEPARATOR3                        = "&";
}
