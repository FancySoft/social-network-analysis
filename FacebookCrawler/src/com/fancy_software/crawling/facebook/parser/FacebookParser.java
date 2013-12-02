package com.fancy_software.crawling.facebook.parser;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.logger.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yaro
 * Date: 23.10.13
 * Time: 16:04
 */
public class FacebookParser {

    private static final String TAG = FacebookParser.class.getSimpleName();
    private DefaultHttpClient client;
    private HttpContext       context;

    private static String encodeParamValue(String paramValue) {
        try {
            return URLEncoder.encode(paramValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AccountVector parse(UserID id) {
        HttpGet request = new HttpGet(String.format("https://www.facebook.com/%s/about", id.toString()));
        try {
            HttpResponse response = client.execute(request, context);
            String res = EntityUtils.toString(response.getEntity());
            // TODO returns null!
            List<Object> results = FacebookBasicParser.getAccountInfo(res);
        } catch (IOException e) {
            Log.e(TAG, e);
        }
        return null;
    }

    /**
     * author John Khandygo
     */
    public void auth(String login, String password) {
        client = new DefaultHttpClient();
        client.setRedirectStrategy(new LaxRedirectStrategy());
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        CookieStore cookieStore = new BasicCookieStore();
        context = new BasicHttpContext();
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
                    param += "&lsd=" + encodeParamValue(e.val());
                }
                if (e.outerHtml().contains("lgnrnd")) {
                    param += "&lgnrnd=" + encodeParamValue(e.val());
                }
                if (e.outerHtml().contains("lgnjs")) {
                    param += "&lgnjs=" + encodeParamValue(e.val());
                }
            }
            param += "&email=" + encodeParamValue(login) + "&pass=" + encodeParamValue(password);

            HttpPost loginRequest = new HttpPost("https://www.facebook.com/login.php?" + param);
            client.execute(loginRequest, context);
            loginRequest.abort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class UserID {
        private String mID;

        public UserID(String id) {
            mID = id;
        }

        @Override
        public String toString() {
            return mID;
        }
    }
}
