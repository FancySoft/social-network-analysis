package com.fancy_software.crawling.parsers.vk;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.WallMessage;
import com.fancy_software.crawling.crawlers.AbstractCrawler;
import com.fancy_software.crawling.parsers.AbstractSampleParser;
import com.fancy_software.crawling.utils.ExtractType;
import com.fancy_software.logger.Log;
import com.fancy_software.utils.io.LocalAccountReader;
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
import java.util.Set;

/**
 * Created by Yaro
 * Date: 07.11.13
 * Time: 15:19
 */

public class VkApiParser extends AbstractSampleParser {

    private static final String TAG = VkApiParser.class.getSimpleName();

    private static final String APP_ID            = "3437182";
    private static final String RESPONSE_ENCODING = "utf-8";

    private static final int MAX_API_CALL         = 3; //per second
    private static final int APP_INSTALLS         = 1;
    private static final int BARRIER              = MAX_API_CALL * APP_INSTALLS;
    private static final int FOR_DELAY            = 1000; //milliseconds
    private static final int MAX_IDS_FOR_ONE_CALL = 1000; //it's max possible value because of vk api restriction

    private int apiCallCounter = 0;
    private String currentUserId;
    private long   finishUserId;
    private String access_token;
    private long   lastCallTime;

    private VkResponseProcessor responseProcessor;
    private ApiRequestGenerator apiRequestGenerator;

    {
        responseProcessor = new VkResponseProcessor();
        apiRequestGenerator = new ApiRequestGenerator();
    }

    public VkApiParser(AbstractCrawler crawler, long startUserId, long finishUserId) {
        this.crawler = crawler;
        this.currentUserId = Long.toString(startUserId);
        this.finishUserId = finishUserId;
        extractType = crawler.getExtractType();
    }

    public VkApiParser(AbstractCrawler crawler, Set<String> usersToParse, SampleParseType parseType) {
        super(crawler, usersToParse, parseType);
    }

    public VkApiParser(AbstractCrawler crawler, String initialId, SampleParseType parseType) {
        super(crawler, initialId, parseType);
        extractType = ExtractType.SAMPLE;
    }

    public VkApiParser(ExtractType extractType) {
        this.extractType = extractType;
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
        AccountVector result = null;
        currentUserId = id;
        avoidApiRestrictions();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = getPostForApiCall(currentUserId, ExtractType.SINGLE_ACCOUNT);
            String response;
            try {
                response = getApiCallResult(httpClient, post);
                result = responseProcessor.processSingleAccount(response);
                currentUserId = result.getId();
                System.out.println(response);
            } catch (NullPointerException e) {
                Log.e(TAG, e);
                return null;
            } finally {
                post.abort();
                lastCallTime = System.currentTimeMillis();
            }

            avoidApiRestrictions();

            post = getPostForApiCall(currentUserId, ExtractType.FRIENDS);

            try {
                response = getApiCallResult(httpClient, post);
//                System.out.println(response);
                List<Long> friends = responseProcessor.processGroupsOrFriendsResponse(response);
                for (long i : friends)
                    result.addFriend(Long.toString(i));
            } catch (NullPointerException e) {
                Log.e(TAG, e);
            } finally {
                post.abort();
                lastCallTime = System.currentTimeMillis();

            }

            avoidApiRestrictions();

            post = getPostForApiCall(currentUserId, ExtractType.GROUPS);

            try {
                response = getApiCallResult(httpClient, post);
                System.out.println(response);
                List<Long> groups = responseProcessor.processGroupsOrFriendsResponse(response);
                for (long i : groups)
                    result.addGroup(Long.toString(i));
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e(TAG, e);
            } finally {
                post.abort();
                lastCallTime = System.currentTimeMillis();
            }

            return result;
        } catch (IOException e) {
            Log.e(TAG, e);
        }
        actAfterResponseReceived(ExtractType.SINGLE_ACCOUNT);
        return null;
    }

    /**
     * Get long id by string id
     * @param stringId string id
     * @return         long id
     */
    public String convertId(String stringId) {
        avoidApiRestrictions();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = getPostForApiCall(stringId, ExtractType.SINGLE_ACCOUNT);
            String response;
            try {
                response = getApiCallResult(httpClient, post);
                AccountVector result = responseProcessor.processSingleAccount(response);
                return result.getId();
            } catch (NullPointerException e) {
                Log.e(TAG, e);
                return null;
            } finally {
                post.abort();
                lastCallTime = System.currentTimeMillis();
            }
        } catch (IOException e) {
            Log.e(TAG, e);
        }
        return null;
    }

    private void _start(ExtractType extractType) {
        switch (extractType) {
            case SAMPLE: {
                super.start();
                return;
            }
            default:
                break;
        }
        HttpPost post;
        HttpClient httpClient = new DefaultHttpClient();

        long curId = 0;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                curId = Long.parseLong(currentUserId);
            } catch (NumberFormatException e) {
                break;
            }
            if (curId > finishUserId)
                break;
            avoidApiRestrictions();
            post = getPostForApiCall(currentUserId, extractType);

            try {
                String response = getApiCallResult(httpClient, post);
                System.out.println(response);
                processResponse(response, extractType);
            } catch (IOException e) {
                Log.e(TAG, e);
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e(TAG, e);
                try {
                    Thread.sleep(FOR_DELAY);
                } catch (InterruptedException e1) {
                    Log.e(TAG, e1);
                    Thread.currentThread().interrupt();
                    return;
                }
            } finally {
                post.abort();
                lastCallTime = System.currentTimeMillis();
            }
            actAfterResponseReceived(extractType);
        }

    }

    private String getApiCallResult(HttpClient client, HttpPost post) throws IOException {
        HttpResponse httpResponse = client.execute(post);
        HttpEntity httpEntity = httpResponse.getEntity();
        String response = EntityUtils.toString(httpEntity, RESPONSE_ENCODING);
        return response;
    }

    private HttpPost getPostForApiCall(String userId, ExtractType extractType) {
        HttpPost result = null;
        List<NameValuePair> postParameters = new ArrayList<>();
        switch (extractType) {
            case ALL_ACCOUNTS: {
                result = new HttpPost("https://api.vk.com/method/users.get?");
                long curId = 0;
                try {
                    curId = Long.parseLong(currentUserId);
                } catch (NumberFormatException e) {
                    return result;
                }
                postParameters.add(
                        new BasicNameValuePair("user_ids",
                                               apiRequestGenerator.generateIds(curId, MAX_IDS_FOR_ONE_CALL))
                                  );
                postParameters.add(new BasicNameValuePair("fields", apiRequestGenerator.generateFields()));
            }
            break;
            case SINGLE_ACCOUNT: {
                result = new HttpPost("https://api.vk.com/method/users.get?");
                postParameters.add(
                        new BasicNameValuePair("user_ids", userId));
                postParameters.add(new BasicNameValuePair("fields", apiRequestGenerator.generateFields()));
            }
            break;
            case FRIENDS: {
                result = new HttpPost("https://api.vk.com/method/friends.get?");
                postParameters.add(new BasicNameValuePair("uid", userId));
            }
            break;
            case GROUPS: {
                result = new HttpPost("https://api.vk.com/method/groups.get?");
                postParameters.add(new BasicNameValuePair("uid", userId));
            }
            break;
            case WALL: {
                result = new HttpPost("https://api.vk.com/method/wall.get?");
                postParameters.add(new BasicNameValuePair("owner_id", userId));
            }
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
                    vector = LocalAccountReader.readAccountFromLocalBase(
                            crawler.getFolderForWrite() + File.separator + currentUserId + ".xml");
                } catch (FileNotFoundException e) {
                    vector = new AccountVector();
                    vector.setId(currentUserId);
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
                    vector = LocalAccountReader.readAccountFromLocalBase(
                            crawler.getFolderForWrite() + File.separator + currentUserId + ".xml");
                } catch (FileNotFoundException e) {
                    vector = new AccountVector();
                    vector.setId(currentUserId);
                }
                List<Long> groups = responseProcessor.processGroupsOrFriendsResponse(response);
                for (long i : groups)
                    vector.addGroup(Long.toString(i));
                notifyCrawler(vector);
            }
            break;
            case WALL: {
                AccountVector vector;
                try {
                    vector = LocalAccountReader.readAccountFromLocalBase(
                            crawler.getFolderForWrite() + File.separator + currentUserId + ".xml");
                } catch (FileNotFoundException e) {
                    vector = new AccountVector();
                    vector.setId(currentUserId);
                }
                List<WallMessage> messages = responseProcessor.processWall(response);
                for (WallMessage message : messages)
                    vector.addWallMessage(message);
                notifyCrawler(vector);
            }
            break;
            default:
                break;
        }
    }

    private void actAfterResponseReceived(ExtractType extractType) {
        lastCallTime = System.currentTimeMillis();
        long curId = 0;
        try {
            curId = Long.parseLong(currentUserId);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Wrong format for id");
        }

        switch (extractType) {
            case ALL_ACCOUNTS: {
                curId += MAX_IDS_FOR_ONE_CALL;
                break;
            }
            case SINGLE_ACCOUNT:
                break;
            default: {
                curId++;
            }
        }
        currentUserId = Long.toString(curId);
    }

    private void avoidApiRestrictions() {
        try {
            apiCallCounter++;
            if (needDelay(apiCallCounter))
                apiCallCounter = 0;
        } catch (InterruptedException e) {
            Log.e(TAG, e);
            Thread.currentThread().interrupt();
        }
    }

    private boolean needDelay(int callCounter) throws InterruptedException {
        if (callCounter >= BARRIER) {
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
            String permissions = "notify,friends,photos,audio,video,docs,notes,pages,status,offers,questions," +
                                 "wall,groups,messages,notifications,stats,ads,offline";
            String redirectUri = "http://oauth.vk.com/blank.html";

            StringBuilder result = new StringBuilder();
            result.append("http://oauth.vk.com/authorize?");
            result.append("client_id=");
            result.append(APP_ID);
            result.append("&scope=");
            result.append(permissions);
            result.append("&redirect_uri=");
            result.append(redirectUri);
            result.append("&display=");
            result.append("page");
            result.append("&response_type=");
            result.append("token");
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
            result.append(",");
            result.append("city,");
            result.append("country,");
            result.append("photo_max,");
            result.append("contacts,");
            result.append("education,");
            result.append("universities,");
            result.append("schools,");
            result.append("activity,");
            result.append("relation");
            return result.toString();
        }


    }

}
