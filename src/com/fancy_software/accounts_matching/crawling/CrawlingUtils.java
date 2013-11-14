package com.fancy_software.accounts_matching.crawling;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CrawlingUtils {
    public static String encodeParamValue(String paramValue) {
        try {
            return URLEncoder.encode(paramValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
