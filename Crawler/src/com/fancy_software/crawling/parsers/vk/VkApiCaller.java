package com.fancy_software.crawling.parsers.vk;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountReader;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.crawling.crawlers.vk.VkCrawler;
import com.fancy_software.crawling.crawlers.vk.VkCrawler.ExtractType;
import com.fancy_software.crawling.parsers.AbstractParser;
import com.fancy_software.logger.Log;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by Yaro
 * Date: 07.11.13
 * Time: 15:19
 */

public class VkApiCaller extends AbstractParser {

    private final String APP_ID            = "3437182";
    private final String SCOPE             = "notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions," +
                                             "wall,groups,messages,notifications,stats,ads,offline";
    private final String REDIRECT_URI      = "http://oauth.vk.com/blank.html";
    private final String DISPLAY           = "page";
    private final String RESPONSE_TYPE     = "token";
    private final String RESPONSE_ENCODING = "utf-8";
    private final String TAG               = VkCrawler.class.getSimpleName();
    private final int    MAX_API_CALL      = 5;
    private final int    APP_INSTALLS      = 1;
    private final int    BARRIER           = MAX_API_CALL * APP_INSTALLS - 1;
    private final int    FOR_DELAY         = 1000;
    private long              lastCallTime;
    private String            access_token;
    private ResponseProcessor responseProcessor;
    private int max_ids_for_call = 1000;//actually, it's 1000, but there is restriction on id length
    private long startUser;
    private long finishUser;

    public VkApiCaller(AbstractCrawler crawler, long startUser, long finishUser) {
        this.crawler = crawler;
        this.startUser = startUser;
        this.finishUser = finishUser;
        responseProcessor = new ResponseProcessor();
    }

    @Override
    public void auth(String login, String password) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("http://oauth.vk.com/authorize?");
            builder.append("client_id=");
            builder.append(APP_ID);
            builder.append("&scope=");
            builder.append(SCOPE);
            builder.append("&redirect_uri=");
            builder.append(REDIRECT_URI);
            builder.append("&display=");
            builder.append(DISPLAY);
            builder.append("&response_type=");
            builder.append(RESPONSE_TYPE);

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(builder.toString());
            HttpResponse response = httpClient.execute(get);

            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
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
            builder = new StringBuilder();
            builder.append("https://login.vk.com/?act=login&soft=1");
            builder.append("&q=1");
            builder.append("&ip_h=");
            builder.append(ip_h);
            builder.append("&from_host=oauth.vk.com");
            builder.append("&to=");
            builder.append(to_h);
            builder.append("&expire=0");
            builder.append("&email=");
            builder.append(login);
            builder.append("&pass=");
            builder.append(password);

            HttpPost post = new HttpPost(builder.toString());
            response = httpClient.execute(post);
            post.abort();
            String HeaderLocation = response.getFirstHeader("location").getValue();

            // Нужно только в первый раз, пока не даны разрешения
            get = new HttpGet(HeaderLocation);
            response = httpClient.execute(get);
            String toFind = "<form method=\"post\" action=\"";
            String link = null;
            in = response.getEntity().getContent();
            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            boolean needLink = true;
            while ((line = reader.readLine()) != null) {
                if (needLink && line.contains(toFind)) {
                    link = line.substring(toFind.length(), line.indexOf("\"", toFind.length() + 1));
                    needLink = false;
                }
            }

            if (link == null) {
                // Если разрешения уже даны, мы тут
                post = new HttpPost(HeaderLocation);
                response = httpClient.execute(post);
                post.abort();
                HeaderLocation = response.getFirstHeader("location").getValue();
            } else {
                HeaderLocation = link;
            }

            post = new HttpPost(HeaderLocation);
            response = httpClient.execute(post);
            post.abort();
            HeaderLocation = response.getFirstHeader("location").getValue();
            System.out.println(HeaderLocation);
            System.out.println("Authorization succeded");
//            System.out.println((HeaderLocation.split("#")[1].split("&")[0].split("=")[1]));
            access_token = HeaderLocation.split("#")[1].split("&")[0].split("=")[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder generateQueryForAccounts(long userCounter, ExtractType type) {
        StringBuilder result = new StringBuilder();
        result.append("https://api.vk.com");
        result.append("/method/");
        result.append("users.get.xml?");
        result.append("access_token=");
        result.append(access_token);
        result.append("&sig=");

        StringBuilder builder = new StringBuilder("/method/");
        builder.append("users.get.xml?");
        builder.append("access_token=");
        builder.append(access_token);
        builder.append("&");


        builder.append("user_ids=");
        for (int i = 0; i < max_ids_for_call; i++) {
            builder.append(userCounter + i);
            //Log.d(TAG, String.format("userCounter = %d", userCounter + i));
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("&fields=");
        builder.append(FieldNames.BIRTH_DATE);
        builder.append(",");
        builder.append(FieldNames.FIRST_NAME);
        builder.append(",");
        builder.append(FieldNames.LAST_NAME);
        builder.append(",");
        builder.append(FieldNames.SEX);
        builder.append(",");
        builder.append(FieldNames.ID);

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
//        System.out.println(builder.toString());
        builder.append("&v=5.2");
        result.append(getHash(builder.toString()));

        return result;

    }

    private StringBuilder generateQueryForAdditionalInfo(long userId, ExtractType type) {
        StringBuilder builder = new StringBuilder();
        builder.append("https://api.vk.com/method/");
        switch (type) {
            case FRIENDS:
                builder.append("friends.get?");
                break;
            case GROUPS:
                builder.append("groups.get?");
            default:
                break;
        }
        builder.append("uid=");
        builder.append(userId);

        builder.append("&access_token=");
        builder.append(access_token);
        return builder;
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
                StringBuilder query = generateQueryForAccounts(userCounter, extractType);
                System.out.println(query.toString());
                post = new HttpPost(query.toString());

                try {
                    response = client.execute(post);
                    entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity, RESPONSE_ENCODING);
                    System.out.println(responseString);
                    notifyCrawler(responseProcessor.processResponse(responseString));
                } catch (IOException e) {
//                    Log.e(TAG, e);
                } catch (NullPointerException e) {
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
            StringBuilder query = generateQueryForAdditionalInfo(userCounter, extractType);
            HttpPost post = new HttpPost(query.toString());
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

    static public String hashBinary(byte[] source) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(source);
            BigInteger i = new BigInteger(1, m.digest());
            return String.format("%1$032x", i);
        } catch (NoSuchAlgorithmException e) {}
        return "";
    }

    static public String getHash(String str) {
        System.out.println(str);
//        try {
//            return hashBinary(str.getBytes("UTF-8"));
            return DigestUtils.md5Hex(str);
//        } catch (UnsupportedEncodingException e) {}
//        return "";
    }

}
