package com.fancy_software.accounts_matching.crawling.parsers;

import com.fancy_software.accounts_matching.crawling.CrawlingUtils;
import com.fancy_software.accounts_matching.crawling.crawlers.ICrawler;
import com.fancy_software.accounts_matching.model.AccountVector;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import sun.misc.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: yaro
 * Date: 23.10.13
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
public class FacebookParser extends AbstractParser {

    private DefaultHttpClient client;

    public FacebookParser(SocialNetworkId networkId, ICrawler crawler) {
        super(networkId);
    }

    @Override
    public AccountVector parse(String id) {
        return null;
    }

    /**
     * @author John Khandygo
     */
    @Override
    public void auth(String login, String password) {
        client = new DefaultHttpClient();
        client.setRedirectStrategy(new LaxRedirectStrategy());
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext context = new BasicHttpContext();
        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        HttpGet firstRequest = new HttpGet("https://www.facebook.com/");

        try {
            HttpResponse firstResponse = client.execute(firstRequest, context);
            Document doc = Jsoup.parse(EntityUtils.toString(firstResponse.getEntity()));
            firstRequest.abort();

            Element loginForm = doc.getElementById("login_form");
            String param = "login_attempt=1&default-persistent=0&timezone=-240&locale=ru_RU";
            for (Element e : loginForm.getElementsByTag("input")) {
                if (e.outerHtml().contains("lsd")) {
                    param += "&lsd=" + CrawlingUtils.encodeParamValue(e.val());
                }
                if (e.outerHtml().contains("lgnrnd")) {
                    param += "&lgnrnd=" + CrawlingUtils.encodeParamValue(e.val());
                }
                if (e.outerHtml().contains("lgnjs")) {
                    param += "&lgnjs=" + CrawlingUtils.encodeParamValue(e.val());
                }
            }
            param += "&email=" + CrawlingUtils.encodeParamValue(login) + "&pass=" + CrawlingUtils.encodeParamValue(password);

            HttpPost loginRequest = new HttpPost("https://www.facebook.com/login.php?" + param);
            client.execute(loginRequest, context);
            loginRequest.abort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AccountVector match(AccountVector goal) {
        return null;
    }


}
