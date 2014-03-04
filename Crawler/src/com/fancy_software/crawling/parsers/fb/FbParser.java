package com.fancy_software.crawling.parsers.fb;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.crawling.parsers.AbstractSampleParser;
import com.fancy_software.logger.Log;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Set;

public class FbParser extends AbstractSampleParser {

    private static final String TAG = FbParser.class.getSimpleName();

    private static final String DEFAULT_URI = "https://www.facebook.com/";
    private static final String FRIENDS_URI = "/friends_all";
    private static final String INFO_URI    = "/about";

    private final WebClient webClient = new WebClient(BrowserVersion.CHROME);

    public FbParser(AbstractCrawler crawler, Set<String> usersToParse){
        super(crawler,usersToParse);
    }

    public FbParser(AbstractCrawler crawler, String initialId){
        super(crawler,initialId);
    }

    @Override
    public void auth(String login, String password) {
        try {
            HtmlPage startPage = webClient.getPage(DEFAULT_URI);
            final HtmlForm form = (HtmlForm) startPage.getElementById("login_form");
            final HtmlTextInput loginField = form.getInputByName("email");
            final HtmlPasswordInput passwordField = form.getInputByName("pass");

            loginField.setValueAttribute(login);
            passwordField.setValueAttribute(password);

            //our own button for submitting
            HtmlElement button = (HtmlElement) startPage.createElement("button");
            button.setAttribute("type", "submit");
            form.appendChild(button);

            button.dblClick();
        } catch (IOException e) {
            System.out.println("FB authorization failed");
            Log.e(TAG, e);
        }
    }

    @Override
    public AccountVector extractAccountById(String id) {
        AccountVector result = null;
        try {
            result = extractAccountVector(id);
        } catch (IOException e) {
            Log.e(TAG, e);
        }
        try {
            extractAccountFriends(result);
        } catch (IOException e) {
            Log.e(TAG, e);
        }
//        System.out.println(result);
        return result;
    }

    private AccountVector extractAccountVector(String id) throws IOException {
        AccountVector result = new AccountVector();
        String source = getSource(getUserInfoUri(id));
//        System.out.println(source);
        Document doc = Jsoup.parse(source);
        Element nameSurname = doc.select(HtmlTags.USER_NAME_SURNAME).first();
        String link = nameSurname.attr("href");
        result.setId(InfoExtractor.extractId(link));
        String[] name = nameSurname.text().split(" ");
        result.setFirst_name(name[0]);
        result.setLast_name(name[1]);

        return result;
    }

    //todo extract numeric ids
    private void extractAccountFriends(AccountVector vector) throws IOException {
        String source = getSource(getUserFriendsUri(vector.getId()));
//        System.out.println(source);
        Set<String> friends = InfoExtractor.getAccountFriends(source);
        for (String s : friends) {
            if (s != null)
                vector.addFriend(s);
        }
    }

    private String getSource(String Uri) {
        HtmlPage page;
        try {
            page = webClient.getPage(Uri);
            return page.getWebResponse().getContentAsString().replace("<!--", "").replace("-->", "");
        } catch (IOException e) {
            Log.e(TAG, e);
        }
        return null;
    }

    public static String getUserInfoUri(String id) {
        StringBuilder result = new StringBuilder(DEFAULT_URI);
        result.append(id);
        result.append(INFO_URI);
        return result.toString();
    }

    public static String getUserFriendsUri(String id) {
        StringBuilder result = new StringBuilder(DEFAULT_URI);
        result.append(id);
        result.append(FRIENDS_URI);
        return result.toString();
    }

}
