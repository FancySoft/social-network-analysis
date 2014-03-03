package com.fancy_software.crawling.parsers.fb;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.crawling.parsers.IParser;
import com.fancy_software.logger.Log;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class FbParser implements IParser {

    private static final String TAG = FbParser.class.getSimpleName();

    private static final String DEFAULT_URI = "https://www.facebook.com/";
    private static final String FRIENDS_URI = "friends_all";
    private static final String INFO_URI    = "about";
    private final        String startUser   = "artem.kirienko";

    private final WebClient webClient;
    private       String    login;
    private       String    password;

    private Set<String> usersToParse;

    public FbParser() {
        this.webClient = new WebClient(BrowserVersion.CHROME);
        usersToParse = new TreeSet<>();
        usersToParse.add(startUser);
    }


    @Override
    public void auth(String login, String password) {
        this.login = login;
        this.password = password;
        try {
            final HtmlPage startPage = webClient.getPage(DEFAULT_URI);
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
    public void start() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
//                    String source = usersToParse.
                    final HtmlPage page = webClient.getPage("https://www.facebook.com/artem.kirienko/friends_all");

                    System.out.println(page.getWebResponse().getContentAsString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            webClient.closeAllWindows();
        }
    }

    @Override
    public AccountVector extractAccountById(String id) {
        return null;
    }

    public void testParse() throws IOException {
        AccountVector vector = extractAccountVector();
        extractAccountFriends(vector);
        System.out.println(vector);
    }

    private AccountVector extractAccountVector() throws IOException {
        AccountVector result = new AccountVector();
        String source = "";// Utils.readFile(com.fancy_software.utils.Settings.TEST_FILE_NAME, Charset.defaultCharset()).replace("<!--", "").replace(
//                "-->", "");
        Document doc = Jsoup.parse(source);
        Element nameSurname = doc.select(HtmlTags.USER_NAME_SURNAME).first();
        String link = nameSurname.attr("href");
        result.setId(InfoExtractor.extractId(link));
        String[] name = nameSurname.text().split(" ");
        result.setFirst_name(name[0]);
        result.setLast_name(name[1]);

        return result;
    }

    private void extractAccountFriends(AccountVector vector) throws IOException {
        String source = "";// Utils.readFile(com.fancy_software.utils.Settings.TEST_FRIENDS_FILE_NAME, Charset.defaultCharset()).replace("<!--",
//                                                                                                          "").replace(
//                "-->", "");
        Set<String> friends = InfoExtractor.getAccountFriends(source);
        for (String s : friends) {
            vector.addFriend(s);
        }
    }

    public static class UriGenerator {
        public static String getUserInfoUri(String id) {
            StringBuilder result = new StringBuilder(DEFAULT_URI);
            result.append(id);
            result.append(File.separator);
            result.append(INFO_URI);
            return result.toString();
        }

        public static String getUserFriendsUri(String id) {
            StringBuilder result = new StringBuilder(DEFAULT_URI);
            result.append(id);
            result.append(File.separator);
            result.append(FRIENDS_URI);
            return result.toString();
        }
    }

}
