package com.fancy_software.crawling;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountReader;
import com.fancy_software.accounts_matching.io_local_base.PathGenerator;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.logger.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Yaro
 * Date: 07.11.13
 * Time: 15:19
 */

public class VkApiCaller {

    private final String APP_ID = "3437182";
    private final String SCOPE = "262146";
    private final String REDIRECT_URI = "http://oauth.vk.com/blank.html";
    private final String DISPLAY = "page";
    private final String RESPONSE_TYPE = "token";
    private final String TAG = VkCrawler.class.getSimpleName();
    private final int max_api_call = 5;
    private final int app_installs = 1;
    private final int barrier = max_api_call * app_installs - 1;
    private final int for_delay = 1000;
    private long lastCallTime;
    private String access_token;
    private ResponseProcessor responseProcessor;
    private Queue<AccountVector> usersToWrite;
    private int max_ids_for_call = 390;//actually, it's 1000, but there is restriction on id length
    private int users_max_amount = 2000;

    public VkApiCaller() {
        usersToWrite = new ConcurrentLinkedQueue<AccountVector>();
        responseProcessor = new ResponseProcessor();
    }

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
//            System.out.println((HeaderLocation.split("#")[1].split("&")[0].split("=")[1]));
            access_token = HeaderLocation.split("#")[1].split("&")[0].split("=")[1];
        } catch (IOException ioexc) {
            ioexc.printStackTrace();
        }
    }

    private StringBuilder generateQueryForAccounts(long userCounter, ExtractType type) {
        StringBuilder builder = new StringBuilder();
        builder.append("https://api.vk.com/method/");
        builder.append("users.get?");
        builder.append("uids=");

        for (int i = 0; i < max_ids_for_call; i++) {
            builder.append(userCounter + i);
            Log.d(TAG, String.format("userCounter = %d", userCounter + i));
            builder.append(",");
        }
        //todo other fields
        builder.deleteCharAt(builder.length() - 1);
        builder.append("&fields=");
        builder.append(FieldNames.ID);
        builder.append("s,");
        builder.append(FieldNames.FIRST_NAME);
        builder.append(",");
        builder.append(FieldNames.LAST_NAME);
        builder.append(",");
        builder.append(FieldNames.BIRTH_DATE);
        builder.append(",");
        builder.append(FieldNames.SEX);
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
        builder.append("&access_token=");
        builder.append(access_token);
        return builder;

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

    public void start(ExtractType extractType) {
        UserWriter writer = new UserWriter(usersToWrite, Thread.currentThread());
        Thread writingThread = new Thread(writer);
        writingThread.start();
        switch (extractType) {
            case ACCOUNTS:
                startAccounts(extractType);
                break;
            default:
                startAdditionalInfo(extractType);
        }
    }

    private void startAccounts(ExtractType extractType) {
        long userCounter = 0;
        int callCounter = 0;
        lastCallTime = System.currentTimeMillis();
        while (true) {
//            Log.d(TAG, String.format("userCounter = %d", userCounter));
            if (userCounter > users_max_amount)
                break;
            callCounter++;
            if (delay(callCounter))
                callCounter = 0;
            StringBuilder query = generateQueryForAccounts(userCounter, extractType);
            HttpPost post = new HttpPost(query.toString());
            try {
                URL url = new URL(query.toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
                String line = reader.readLine();
                reader.close();
                System.out.println(line);
                addUsersToWrite(responseProcessor.processResponse(line));
            } catch (MalformedURLException e) {
                Log.e(TAG, e);
            } catch (IOException e) {
                Log.e(TAG, e);
            } catch (NullPointerException e) {
                Log.e(TAG, e);
                try {
                    Thread.sleep(for_delay);
                } catch (InterruptedException e1) {
                    Log.e(TAG, e1);
                }
                continue;
            }
            post.abort();
            lastCallTime = System.currentTimeMillis();
            userCounter += max_ids_for_call;
        }
    }

    private void startAdditionalInfo(ExtractType extractType) {
        long userCounter = 0;
        int callCounter = 0;
        lastCallTime = System.currentTimeMillis();
        while (true) {
//            Log.d(TAG, String.format("userCounter = %d", userCounter));
            if (userCounter > users_max_amount)
                break;
            callCounter++;
            if (delay(callCounter))
                callCounter = 0;
            StringBuilder query = generateQueryForAdditionalInfo(userCounter, extractType);
            HttpPost post = new HttpPost(query.toString());
            try {
                URL url = new URL(query.toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
                String line = reader.readLine();
                reader.close();
                System.out.println(line);
                AccountVector vector = LocalAccountReader.readAccountFromLocalBase(PathGenerator.generateDefaultPath(userCounter));
                List<Long> response = responseProcessor.processInfo(line);
                switch (extractType) {
                    case GROUPS: {
                        for (long i : response)
                            vector.addGroup(Long.toString(i));
                    }
                    break;
                    case FRIENDS: {
                        for (long i : response)
                            vector.addFriend(i);
                    }
                    break;
                    default:
                        break;
                }
                addUsersToWrite(vector);
            } catch (MalformedURLException e) {
                Log.e(TAG, e);
            } catch (IOException e) {
                Log.e(TAG, e);
            } catch (NullPointerException e) {
                Log.e(TAG, e);
                try {
                    Thread.sleep(for_delay);
                } catch (InterruptedException e1) {
                    Log.e(TAG, e1);
                }
                userCounter++;
                continue;
            }
            post.abort();
            lastCallTime = System.currentTimeMillis();
            userCounter++;
        }
    }

    private void addUsersToWrite(List<AccountVector> users) {
        for (AccountVector vector : users)
            usersToWrite.add(vector);
    }

    private void addUsersToWrite(AccountVector user) {
        usersToWrite.add(user);
    }

    private boolean delay(int callCounter) {
        if (callCounter > barrier) {
            long timeDif = System.currentTimeMillis() - lastCallTime;
            if (timeDif < for_delay) {
                try {
                    Log.d(TAG, "wait");
                    Thread.sleep(for_delay - timeDif);

                } catch (InterruptedException e) {
                    Log.e(TAG, e);
                }
            }
            return true;
        }
        return false;
    }

}
