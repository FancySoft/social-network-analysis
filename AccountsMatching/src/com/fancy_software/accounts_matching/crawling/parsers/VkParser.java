package com.fancy_software.accounts_matching.crawling.parsers;

import com.fancy_software.accounts_matching.crawling.PathGenerator;
import com.fancy_software.accounts_matching.model.*;
import com.fancy_software.logger.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class VkParser extends AbstractParser {

    private static final String TAG = VkParser.class.getSimpleName();
    private ObjectMapper mapper;
    private String ACCESS_TOKEN;
    private final String APP_ID = "3437182";
    private final String SCOPE = "262146";
    private final String REDIRECT_URI = "http://oauth.vk.com/blank.html";
    private final String DISPLAY = "page";
    private final String RESPONSE_TYPE = "token";

    public VkParser(SocialNetworkId networkId) {
        super(networkId);
        mapper = new ObjectMapper();
    }

    @Override
    public void auth(String login, String password) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://oauth.vk.com/authorize?" +
                    "client_id=" + APP_ID +
                    "&scope=" + SCOPE +
                    "&redirect_uri=" + REDIRECT_URI +
                    "&display=" + DISPLAY +
                    "&response_type=" + RESPONSE_TYPE);
            HttpResponse response;
            response = httpClient.execute(get);
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

            HttpPost post = new HttpPost("https://login.vk.com/?act=login&soft=1" +
                    "&q=1" +
                    "&ip_h=" + ip_h +
                    "&from_host=oauth.vk.com" +
                    "&to=" + to_h +
                    "&expire=0" +
                    "&email=" + login +
                    "&pass=" + password);
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
            ACCESS_TOKEN = HeaderLocation.split("#")[1].split("&")[0].split("=")[1];
        } catch (IOException ioexc) {
            ioexc.printStackTrace();
        }
    }

    /**
     * Выполнение методов vk api и получение ответа в json
     *
     * @param method метод api
     * @param params параметры
     * @return json
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> ApiCall(String method, String params) {
        String url = "https://api.vk.com/method/" +
                method +
                "?" + params +
                "&access_token=" + ACCESS_TOKEN;
        String line = "";
        try {
            URL url2 = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url2.openStream(), "utf-8"));
            line = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> result = null;
        try {
            result = mapper.readValue(line, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result != null && result.containsKey("error")) {
            if (((Map) result.get("error")).get("error_code") == 6) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return ApiCall(method, params);
            }
        }

        return result;
    }

    /**
     * Parser
     * @param id         идентификатор
     * @return вектор пользователя
     */
    @Override
    @SuppressWarnings("unchecked")
    public AccountVector parse(String id) {
        AccountVector result = new AccountVector();
        Map<String, Object> userInfo = ApiCall("users.get", "uids=" + id + "&fields=bdate,sex");
        if (userInfo == null) {
            Log.e(TAG, "User info is null");
            return null;
        }
        if (userInfo.containsKey("error")) {
            Log.e(TAG, (String) userInfo.get("error"));
            return null;
        }
        userInfo = (Map) ((ArrayList) userInfo.get("response")).get(0);
        result.setBdate(BirthDate.generateBirthDate((String) userInfo.get("bdate")));
        result.setFirst_name((String) userInfo.get("first_name"));
        result.setLast_name((String) userInfo.get("last_name"));
        result.setSex(convertSexFromApi((Integer) userInfo.get("sex")));
        result.setId((Integer) userInfo.get("uid"));
        Map m;
        List list;

        // Получаем информацию о группах
        userInfo = ApiCall("groups.get", "uid=" + result.getId() +
                "&extended=1&fields=description");
        if (userInfo == null) {
            Log.e(TAG, "User info is null for groups");
            return null;
        }
        if (userInfo.containsKey("response")) {
            list = (ArrayList) userInfo.get("response");
            list.remove(0);

            String temp;
            List<String> temporary = new ArrayList<>();
            for (Object o : list) {
                m = (Map) o;
                temp = (String) m.get("description");
                if (temp == null) continue;
                temp = temp.replaceAll("<br>", " ");
                temp = temp.replaceAll("\n|\r\n", " ");
                temporary.add(temp);
            }
            try {
                for (String s : temporary)
                    result.addGroup(s);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.e(TAG, (String) userInfo.get("error"));
        }

        // Получаем список друзей
        System.out.println(id);
        System.out.println(Long.toString(result.getId()));
        userInfo = ApiCall("friends.get", "uid=" + result.getId());
        if (userInfo.containsKey("response")) {
            list = (ArrayList) userInfo.get("response");
            for (Object o : list) {
                result.addFriend(Long.parseLong(o.toString()));
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, (String) userInfo.get("error"));
        }
        writeToFile(result, PathGenerator.generatePathToAccounts(networkId, result.getId()));
        System.out.println(result) ;
        return result;
    }

    /**
     * Gets user feed by user id
     * @return collection, containing the user feed
     */
    @Override
    public Collection<WallMessage> getFeed(IUserId id) {
        if (!(id instanceof VKUserId)) {
            throw new IllegalArgumentException("Trying to pass not VK user id to VK parser");
        }

        // TODO: Here the feed should be downloaded with ApiCall and converted to Collection<WallMessage>

        return null;
    }

    private AccountVector.Sex convertSexFromApi(int sexId) {
        switch (sexId) {
            case 1:
                return AccountVector.Sex.FEMALE;
            case 2:
                return AccountVector.Sex.MALE;
            default:
                return AccountVector.Sex.NA;
        }
    }
    
    @Override
    public AccountVector match(AccountVector goal) {
        Map<String, Object> user = ApiCall("users.search", makeQuery(goal));
        return parse(user.get("uid").toString());
    }

    /**
     *
     * @param user    AccountVector to be used in query
     * @return string like uid=123234234&first_name=Eugene to pass it to ApiCall("users.search", params)
     */
    private String makeQuery(AccountVector user) {
        StringBuilder response = new StringBuilder();

        response.append("first_name=");
        response.append(user.getFirst_name());
        response.append("&");

        response.append("last_name=");
        response.append(user.getLast_name());
        response.append("&");

        response.append("sex=");
        if (user.getSex()!=AccountVector.Sex.NA) {
            if (user.getSex()==AccountVector.Sex.FEMALE)
                response.append(1);
            else response.append(2);
        } else response.append(0);
        response.append("&");

        response.append("bDate=");
        response.append(user.getBdate().getDay());
        response.append('.');
        response.append(user.getBdate().getMonth());
        response.append('.');
        response.append(user.getBdate().getYear());

        return response.toString();
    }

}

class MessageExtractor {

    public ArrayList<WallMessage> extract(Map<String, Object> messages) {
        ArrayList<WallMessage> result = new ArrayList<>();
        ArrayList responseBody = (ArrayList)messages.get("response");
        int amount = (Integer)responseBody.get(0);
        LinkedHashMap rareMessage;
        for (int i = 1; i < responseBody.size(); ++i) {
            rareMessage = (LinkedHashMap)responseBody.get(i);
            result.add(extractMessage(rareMessage));
        }
        return result;
    }

    public WallMessage extractMessage(LinkedHashMap source) {
        WallMessage message = new WallMessage();

        Integer id = (Integer)source.get("id");
        message.setId(id.longValue());
        Integer from_id = (Integer)source.get("from_id");
        message.setFromId(from_id.longValue());
        Integer to_id = (Integer)source.get("to_id");
        message.setToId(to_id.longValue());
        String text = (String)source.get("text");
        message.setText(text);
        if (source.containsKey("copy_owner_id")) {
            Integer copy_owner_id = (Integer)source.get("copy_owner_id");
            message.setCopyOwnerId(copy_owner_id.longValue());
        }
        if (source.containsKey("copy_owner_id")) {
            Integer copy_post_id = (Integer)source.get("copy_post_id");
            message.setCopyPostId(copy_post_id.longValue());
        }
        if (source.containsKey("copy_text")) {
            String copy_text = (String)source.get("copy_text");
            message.setCopyText(copy_text);
        }
        return message;
    }
}
