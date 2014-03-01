package com.fancy_software.crawling.parsers.vk;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountReader;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.crawling.crawlers.vk.VkCrawler;
import com.fancy_software.crawling.parsers.AbstractParser;
import com.fancy_software.crawling.utils.ExtractType;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaro
 * Date: 07.11.13
 * Time: 15:19
 */

public class VkApiParser extends AbstractParser {

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
    private long                startUserId;
    private long                finishUserId;
    private long                currentUserId;
    private ApiRequestGenerator apiRequestGenerator;

    {
        responseProcessor = new ResponseProcessor();
        apiRequestGenerator = new ApiRequestGenerator();
    }

    public VkApiParser(AbstractCrawler crawler, long startUserId, long finishUserId) {
        this.crawler = crawler;
        this.startUserId = startUserId;
        this.finishUserId = finishUserId;
        currentUserId = startUserId;
    }

    public VkApiParser(AbstractCrawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public void auth(String login, String password) {
        try {
            HttpClient httpClient = new DefaultHttpClient();

            String authQuery = apiRequestGenerator.getUriForAuth();
            HttpGet get = new HttpGet(authQuery);
            HttpResponse response = httpClient.execute(get);

            InputStream in = response.getEntity().getContent();
            String loginQuery = apiRequestGenerator.getUriForLogin(in, login, password);

            HttpPost post = new HttpPost(loginQuery);
            response = httpClient.execute(post);
            String headerLocation = response.getFirstHeader("location").getValue();
            post.abort();

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
                headerLocation = response.getFirstHeader("location").getValue();
                post.abort();
            } else {
                headerLocation = link;
            }

            post = new HttpPost(headerLocation);
            response = httpClient.execute(post);
            headerLocation = response.getFirstHeader("location").getValue();
            post.abort();
            System.out.println("Authorization succeeded");
            access_token = headerLocation.split("#")[1].split("&")[0].split("=")[1];
            System.out.println("ACCESS TOKEN " + access_token);
        } catch (IOException e) {
            Log.e(TAG, e);
        }
    }

    @Override
    public void start() {
        _start(crawler.getExtractType());
    }

    @Override
    public AccountVector extractAccountById(String id) {
        long userId = 0;

        try {
            userId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            Log.e(TAG, e);
        }


        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = getPostForApiCall(userId, ExtractType.SINGLE_ACCOUNT);
            HttpResponse httpResponse = httpClient.execute(post);
            HttpEntity httpEntity = httpResponse.getEntity();
            String response = EntityUtils.toString(httpEntity, RESPONSE_ENCODING);
            post.abort();
            System.out.println(response);

            AccountVector result = responseProcessor.processSingleAccount(response);

            post = getPostForApiCall(userId, ExtractType.FRIENDS);
            httpResponse = httpClient.execute(post);
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity, RESPONSE_ENCODING);
            post.abort();
            System.out.println(response);
            try{
            List<Long> friends = responseProcessor.processGroupsOrFriendsResponse(response);
            for (long i : friends)
                result.addFriend(Long.toString(i));
            }
            catch (NullPointerException e){
                Log.e(TAG,e);
            }
            post = getPostForApiCall(userId, ExtractType.GROUPS);
            httpResponse = httpClient.execute(post);
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity, RESPONSE_ENCODING);
            post.abort();
            System.out.println(response);

            try{
            List<Long> groups = responseProcessor.processGroupsOrFriendsResponse(response);
            for (long i : groups)
                result.addGroup(Long.toString(i));
            }
            catch (NullPointerException e){
                Log.e(TAG,e);
            }
            return result;
        } catch (IOException e) {
            Log.e(TAG, e);
        }
        return null;
    }

    private void _start(ExtractType extractType) {
        int callCounter = 0;
        HttpPost post;
        HttpResponse httpResponse;
        HttpEntity httpEntity;

        HttpClient httpClient = new DefaultHttpClient();

        while (!Thread.currentThread().isInterrupted()) {
            if (currentUserId > finishUserId)
                break;
            callCounter++;
            try {
                if (needDelay(callCounter))
                    callCounter = 0;
            } catch (InterruptedException e) {
                Log.e(TAG, e);
                return;
            }
            post = getPostForApiCall(currentUserId, extractType);

            try {
                httpResponse = httpClient.execute(post);
                httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity, RESPONSE_ENCODING);
                System.out.println(response);
                processResponse(response, extractType);
            } catch (IOException e) {
                Log.e(TAG, e);
            } catch (NullPointerException e) {
                Log.e(TAG, e);
                try {
                    Thread.sleep(FOR_DELAY);
                } catch (InterruptedException e1) {
                    Log.e(TAG, e1);
                    return;
                }
            } finally {
                post.abort();
            }
            actAfterResponseReceived(extractType);
        }

    }

    private HttpPost getPostForApiCall(long userCounter, ExtractType extractType) {
        HttpPost result = null;
        List<NameValuePair> postParameters = new ArrayList<>();
        switch (extractType) {
            case ALL_ACCOUNTS: {
                result = new HttpPost("https://api.vk.com/method/users.get?");
                postParameters.add(
                        new BasicNameValuePair("user_ids",
                                               apiRequestGenerator.generateIds(userCounter, max_ids_for_call)));
                postParameters.add(new BasicNameValuePair("fields", apiRequestGenerator.generateFields()));
            }
            break;
            case SINGLE_ACCOUNT: {
                result = new HttpPost("https://api.vk.com/method/users.get?");
                postParameters.add(
                        new BasicNameValuePair("user_ids", Long.toString(userCounter)));
                postParameters.add(new BasicNameValuePair("fields", apiRequestGenerator.generateFields()));
            }
            break;
            case FRIENDS: {
                result = new HttpPost("https://api.vk.com/method/friends.get?");
                postParameters.add(new BasicNameValuePair("uid", Long.toString(userCounter)));
            }
            break;
            case GROUPS: {
                result = new HttpPost("https://api.vk.com/method/groups.get?");
                postParameters.add(new BasicNameValuePair("uid", Long.toString(userCounter)));
            }
            break;
            default:
                break;
        }
        postParameters.add(new BasicNameValuePair("access_token", access_token));
        try {
            result.setEntity(new UrlEncodedFormEntity(postParameters));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e);
        }
        return result;
    }

    private void processResponse(String response, ExtractType extractType) {
        switch (extractType) {
            case ALL_ACCOUNTS: {
                List<AccountVector> extractedAccounts = responseProcessor.processAccountInfoResponse(response);
                notifyCrawler(extractedAccounts);
            }
            break;
            case FRIENDS: {
                AccountVector vector;
                try {
                    vector = LocalAccountReader.readAccountFromLocalBase(crawler.getFolderForWrite());
                } catch (FileNotFoundException e) {
                    vector = new AccountVector();
                    vector.setId(Long.toString(currentUserId));
                }
                List<Long> friends = responseProcessor.processGroupsOrFriendsResponse(response);
                for (long i : friends)
                    vector.addFriend(Long.toString(i));
                notifyCrawler(vector);
            }
            break;
            case GROUPS: {
                AccountVector vector;
                try {
                    vector = LocalAccountReader.readAccountFromLocalBase(crawler.getFolderForWrite());
                } catch (FileNotFoundException e) {
                    vector = new AccountVector();
                    vector.setId(Long.toString(currentUserId));
                }
                List<Long> groups = responseProcessor.processGroupsOrFriendsResponse(response);
                for (long i : groups)
                    vector.addGroup(Long.toString(i));
                notifyCrawler(vector);
            }
            break;
            default:
                break;
        }
    }

    private void actAfterResponseReceived(ExtractType extractType) {
        lastCallTime = System.currentTimeMillis();

        switch (extractType) {
            case ALL_ACCOUNTS:
                currentUserId += max_ids_for_call;
                break;
            default:
                currentUserId++;
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

    private class ApiRequestGenerator {

        @SuppressWarnings("deprecated")
        private String getApiRequestForSingleAccount(long userId) {
            StringBuilder result = new StringBuilder();
            result.append("https://api.vk.com/method/");
            result.append("users.get?");
            result.append("access_token=");
            result.append(access_token);
            result.append("&user_ids=");
            result.append(userId);
            result.append("&fields=");
            result.append(generateFields());
            return result.toString();

        }

        @SuppressWarnings("deprecated")
        private String getApiRequestForGroupsAndFriends(long userId, ExtractType type) {
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

        private String getUriForLogin(InputStream in, String login, String password) throws IOException {
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

        private String getUriForAuth() {
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
