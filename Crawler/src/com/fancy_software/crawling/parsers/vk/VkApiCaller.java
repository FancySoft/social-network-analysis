package com.fancy_software.crawling.parsers.vk;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountReader;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.crawling.crawlers.vk.VkCrawler;
import com.fancy_software.crawling.crawlers.vk.VkCrawler.ExtractType;
import com.fancy_software.crawling.parsers.AbstractParser;
import com.fancy_software.logger.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaro
 * Date: 07.11.13
 * Time: 15:19
 */

public class VkApiCaller extends AbstractParser {

    private final String TAG = VkCrawler.class.getSimpleName();

    private final String APP_ID            = "3437182";
    private final String SCOPE             = "notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions," +
                                             "wall,groups,messages,notifications,stats,ads,offline";
    private final String REDIRECT_URI      = "http://oauth.vk.com/blank.html";
    private final String DISPLAY           = "page";
    private final String RESPONSE_TYPE     = "token";
    private final String RESPONSE_ENCODING = "utf-8";
    private final int    MAX_API_CALL      = 3; //per second
    private final int    APP_INSTALLS      = 1;
    private final int    BARRIER           = MAX_API_CALL * APP_INSTALLS;
    private final int    FOR_DELAY         = 1000;
    private long              lastCallTime;
    private String            access_token;
    private ResponseProcessor responseProcessor;
    private int max_ids_for_call = 1000; //it's max possible value because of vk api restriction
    private long           startUser;
    private long           finishUser;
    private QueryGenerator queryGenerator;

    public VkApiCaller(AbstractCrawler crawler, long startUser, long finishUser) {
        this.crawler = crawler;
        this.startUser = startUser;
        this.finishUser = finishUser;
        responseProcessor = new ResponseProcessor();
        queryGenerator = new QueryGenerator();
    }

    @Override
    public void auth(String login, String password) {
        try {
            HttpClient httpClient = new DefaultHttpClient();

            String authQuery = queryGenerator.getQueryForAuth();
            HttpGet get = new HttpGet(authQuery);
            HttpResponse response = httpClient.execute(get);

            InputStream in = response.getEntity().getContent();

            String loginQuery = queryGenerator.getQueryForLogin(in, login, password);

            HttpPost post = new HttpPost(loginQuery);
            response = httpClient.execute(post);
            post.abort();
            String headerLocation = response.getFirstHeader("location").getValue();

            // Нужно только в первый раз, пока не даны разрешения
            get = new HttpGet(headerLocation);
            response = httpClient.execute(get);
            String toFind = "<form method=\"post\" action=\"";
            String link = null;
            in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, RESPONSE_ENCODING));
            boolean needLink = true;
            String line;
            while ((line = reader.readLine()) != null) {
                if (needLink && line.contains(toFind)) {
                    link = line.substring(toFind.length(), line.indexOf("\"", toFind.length() + 1));
                    needLink = false;
                }
            }

            if (link == null) {
                // Если разрешения уже даны, мы тут
                post = new HttpPost(headerLocation);
                response = httpClient.execute(post);
                post.abort();
                headerLocation = response.getFirstHeader("location").getValue();
            } else {
                headerLocation = link;
            }

            post = new HttpPost(headerLocation);
            response = httpClient.execute(post);
            post.abort();
            headerLocation = response.getFirstHeader("location").getValue();
            System.out.println("Authorization succeeded");
            access_token = headerLocation.split("#")[1].split("&")[0].split("=")[1];
//            app_secret = headerLocation.split("#")[1].split("&")[3].split("=")[1];
            System.out.println("ACCESS TOKEN " + access_token);
//            System.out.println("APP SECRET "+app_secret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        start(ExtractType.ACCOUNTS);
    }

    public void start(ExtractType extractType) {
        switch (extractType) {
            case ACCOUNTS:
                startAccounts(extractType);
                break;
            default:
                startAdditionalInfo(extractType);
        }
    }

    private void startAccounts(VkCrawler.ExtractType extractType) {
        while (!Thread.currentThread().isInterrupted()) {
            long userCounter = 1;//startUser;
            int callCounter = 0;
            HttpPost post;
            HttpResponse response;
            HttpEntity entity;

            HttpClient client = new DefaultHttpClient();

            while (true) {
//            Log.d(TAG, String.format("userCounter = %d", userCounter));
                if (userCounter > finishUser)
                    break;
                callCounter++;
                try {
                    if (needDelay(callCounter))
                        callCounter = 0;
                } catch (InterruptedException e) {
                    Log.e(TAG, e);
                    return;
                }
                post = new HttpPost("https://api.vk.com/method/users.get?");
                addPostParameters(post, userCounter);

                try {
                    response = client.execute(post);
                    entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity, RESPONSE_ENCODING);
                    System.out.println(responseString);
                    notifyCrawler(responseProcessor.processResponse(responseString));
                } catch (IOException e) {
                    Log.e(TAG, e);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Log.e(TAG, e);
                    try {
                        Thread.sleep(FOR_DELAY);
                    } catch (InterruptedException e1) {
                        Log.e(TAG, e1);
                        return;
                    }
                    continue;
                }
                post.abort();
                lastCallTime = System.currentTimeMillis();
                userCounter += max_ids_for_call;
            }
        }
    }

    private void startAdditionalInfo(ExtractType extractType) {
        long userCounter = startUser;
        int callCounter = 0;
        lastCallTime = System.currentTimeMillis();
        while (true) {
//            Log.d(TAG, String.format("userCounter = %d", userCounter));
            if (userCounter > finishUser)
                break;
            callCounter++;
            try {
                if (needDelay(callCounter))
                    callCounter = 0;
            } catch (InterruptedException e) {
                Log.e(TAG, e);
                return;
            }
            String query = queryGenerator.getQueryForAdditionalInfo(userCounter, extractType);
            HttpPost post = new HttpPost(query);
            try {
                URL url = new URL(query.toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), RESPONSE_ENCODING));
                String line = reader.readLine();
                reader.close();
                System.out.println(line);
                //todo replace by normal path
                AccountVector vector = LocalAccountReader.readAccountFromLocalBase(
                        "");//PathGenerator.generateDefaultPath(userCounter));
                List<Long> response = responseProcessor.processInfo(line);
                switch (extractType) {
                    case GROUPS: {
                        for (long i : response)
                            vector.addGroup(Long.toString(i));
                    }
                    break;
                    case FRIENDS: {
                        for (long i : response)
                            vector.addFriend(Long.toString(i));
                    }
                    break;
                    default:
                        break;
                }
                notifyCrawler(vector);
            } catch (IOException e) {
                Log.e(TAG, e);
            } catch (NullPointerException e) {
                Log.e(TAG, e);
                try {
                    Thread.sleep(FOR_DELAY);
                } catch (InterruptedException e1) {
                    Log.e(TAG, e1);
                }
            }
            post.abort();
            lastCallTime = System.currentTimeMillis();
            userCounter++;
        }
    }

    private void addPostParameters(HttpPost post, long userCounter) {
        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(
                new BasicNameValuePair("user_ids", queryGenerator.generateIds(userCounter, max_ids_for_call)));
        postParameters.add(new BasicNameValuePair("fields", queryGenerator.generateFields()));
        postParameters.add(new BasicNameValuePair("access_token", access_token));
        try {
            post.setEntity(new UrlEncodedFormEntity(postParameters));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private boolean needDelay(int callCounter) throws InterruptedException {
        if (callCounter > BARRIER) {
            long timeDif = System.currentTimeMillis() - lastCallTime;
            if (timeDif < FOR_DELAY) {
                Log.d(TAG, "wait");
                Thread.sleep(FOR_DELAY - timeDif);
            }
            return true;
        }
        return false;
    }

    private class QueryGenerator {

        /**
         * @param start select ids to extract from start to start+1000
         * @return GET request for vk api
         */
        @SuppressWarnings("unused")
        private String getQueryForAccounts(long start) {
            StringBuilder builder = new StringBuilder("https://api.vk.com/method/");

            builder.append("users.get?");
            builder.append("access_token=");
            builder.append(access_token);
            builder.append("&user_ids=");
            builder.append(generateIds(start, max_ids_for_call));
            builder.append("&fields=");
            builder.append(generateFields());

            return builder.toString();

        }

        @SuppressWarnings("deprecated")
        private String getQueryForAdditionalInfo(long userId, ExtractType type) {
            StringBuilder result = new StringBuilder();
            result.append("https://api.vk.com/method/");
            switch (type) {
                case FRIENDS:
                    result.append("friends.get?");
                    break;
                case GROUPS:
                    result.append("groups.get?");
                default:
                    break;
            }
            result.append("uid=");
            result.append(userId);

            result.append("&access_token=");
            result.append(access_token);
            return result.toString();
        }

        private String getQueryForLogin(InputStream in, String login, String password) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, RESPONSE_ENCODING));
            String line;
            String ip_h = null;
            String to_h = null;
            String toFindIp = "<input type=\"hidden\" name=\"ip_h\" value=\"";
            String toFindTo = "<input type=\"hidden\" name=\"to\" value=\"";
            boolean needIph = true;
            boolean needToh = true;

            while ((line = reader.readLine()) != null) {
                if (needIph && line.contains(toFindIp)) {
                    ip_h = line.substring(toFindIp.length(), line.indexOf("\"", toFindIp.length() + 1));
                    needIph = false;
                } else if (needToh && line.contains(toFindTo)) {
                    to_h = line.substring(toFindTo.length(), line.indexOf("\"", toFindTo.length() + 1));
                    needToh = false;
                }
            }
            StringBuilder result = new StringBuilder();
            result.append("https://login.vk.com/?act=login&soft=1");
            result.append("&q=1");
            result.append("&ip_h=");
            result.append(ip_h);
            result.append("&from_host=oauth.vk.com");
            result.append("&to=");
            result.append(to_h);
            result.append("&expire=0");
            result.append("&email=");
            result.append(login);
            result.append("&pass=");
            result.append(password);
            return result.toString();
        }

        private String getQueryForAuth() {
            StringBuilder result = new StringBuilder();
            result.append("http://oauth.vk.com/authorize?");
            result.append("client_id=");
            result.append(APP_ID);
            result.append("&scope=");
            result.append(SCOPE);
            result.append("&redirect_uri=");
            result.append(REDIRECT_URI);
            result.append("&display=");
            result.append(DISPLAY);
            result.append("&response_type=");
            result.append(RESPONSE_TYPE);
            return result.toString();
        }

        public String generateIds(long first, long last) {
            StringBuilder result = new StringBuilder();
            for (long i = 0; i < last; i++) {
                result.append(first + i);
                result.append(",");
            }
            result.deleteCharAt(result.length() - 1);
            return result.toString();
        }

        public String generateFields() {
            StringBuilder result = new StringBuilder();
            result.append(FieldNames.BIRTH_DATE);
            result.append(",");
            result.append(FieldNames.FIRST_NAME);
            result.append(",");
            result.append(FieldNames.LAST_NAME);
            result.append(",");
            result.append(FieldNames.SEX);
            result.append(",");
            result.append(FieldNames.ID);
//        builder.append(",");
//        builder.append("city,");
//        builder.append("country,");
//        builder.append("photo_max,");
//        builder.append("contacts,");
//        builder.append("education,");
//        builder.append("universities,");
//        builder.append("schools,");
//        builder.append("activity,");
//        builder.append("relation");
            return result.toString();
        }
    }


}
