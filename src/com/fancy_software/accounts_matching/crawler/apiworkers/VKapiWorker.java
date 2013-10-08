package com.fancy_software.accounts_matching.crawler.apiworkers;

import com.fancy_software.accounts_matching.io_local_base.Utils;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.BirthDate;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VKapiWorker extends ApiWorkerAbstract {

    private final String APP_ID = "3437182";
    private final String SCOPE = "262146";
    private final String REDIRECT_URI = "http://oauth.vk.com/blank.html";
    private final String DISPLAY = "page";
    private final String RESPONSE_TYPE = "token";
    private String ACCESS_TOKEN;
    private String path;
    private ObjectMapper mapper;

    public VKapiWorker(String path, SocialNetworkId networkId) {
        mapper = new ObjectMapper();
        this.path = path;
        this.networkId = networkId;
    }

    @Override
    public void Auth(String login, String password) {
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
     * Парсить аккаунт пользователся с id
     *
     * @param id идентификатор
     * @return вектор пользователя
     */
    @Override
    public AccountVector Parse(String id) {
        return _Parse(id, null, true);
    }

    /**
     * Выполнение методов vk api и получение ответа в json
     *
     * @param method метод api
     * @param params параметры
     * @return json
     */
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
            if ((Integer) ((Map) result.get("error")).get("error_code") == 6) {
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
     * Парсить аккаунт пользователя с возможностью отключить просмотр друзей
     * (нужно, чтобы не зациклиться)
     *
     * @param id         идентификатор
     * @param friendOfId ид пользователя, друга которого парсим
     * @return вектор пользователя
     */
    private AccountVector _Parse(String id, String friendOfId, boolean needFriends) {
        AccountVector result = new AccountVector();
        Map<String, Object> userInfo = ApiCall("users.get", "uids=" + id + "&fields=bdate,sex");
        assert userInfo == null;
        if (userInfo.containsKey("error")) {
            // TODO: "Verbose" flag
            if (false) System.out.println(userInfo.get("error"));
            return null;
        }
        userInfo = (Map) ((ArrayList) userInfo.get("response")).get(0);
        result.setBdate(BirthDate.generateBirthDate((String) userInfo.get("bdate")));
        result.setFirst_name((String) userInfo.get("first_name"));
        result.setLast_name((String) userInfo.get("last_name"));

        switch ((Integer) userInfo.get("sex")) {
            case 1:
                result.setSex(AccountVector.Sex.FEMALE);
                break;
            case 2:
                result.setSex(AccountVector.Sex.MALE);
                break;
            default:
                result.setSex(AccountVector.Sex.NA);
                break;
        }

        result.setId((Integer) userInfo.get("uid"));
        // От общих друзей нам большего не надо
        if (!needFriends)
            return result;
        Map m;
        List list;

        // Получаем информацию о группах
        userInfo = ApiCall("groups.get", "uid=" + result.getId() +
                "&extended=1&fields=description");
        assert userInfo == null;
        if (userInfo.containsKey("response")) {
            list = (ArrayList) userInfo.get("response");
            list.remove(0);

            String temp;
            List<String> temporary = new ArrayList<String>();
            for (Object o : list) {
                m = (Map) o;
                temp = (String) m.get("description");
                // temp = temp.replaceAll("<br>", " ");
                //temp.replaceAll("\n|\r\n", " ");
                temporary.add(temp);
            }
            try {
                //todo выпилить отсюда lda
                /*Map<Double, String[]> ldaResult = TestTextComparator.getLocalRes(temporary);
                for (Double d : ldaResult.keySet()) {
                    String s = d.toString() + " ";
                    for (String s1 : ldaResult.get(d))
                        s += s1 + " ";
                    result.addGroup(s);
                }     */
                for (String s : temporary)
                    result.addGroup(s);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            // TODO: "Verbose" flag
            if (false) System.out.println(userInfo.get("error"));
        }

        // Получаем список друзей
        AccountVector friend;
        if (friendOfId == null) {
            userInfo = ApiCall("friends.get", "uid=" + result.getId());
        } else {
            userInfo = ApiCall("friends.getMutual", "target_uid=" + result.getId() +
                    "&source_uid=" + friendOfId);
        }
        if (userInfo.containsKey("response")) {
            list = (ArrayList) userInfo.get("response");
            int step = 0;
            final int size = list.size();
            for (Object o : list) {
                step++;
                friend = _Parse(o.toString(), Long.toString(result.getId()), friendOfId == null);
                if (friend != null) {
                    result.addFriend(friend.getId());

                    if (friendOfId == null) {
                        writeToFile(friend, Utils.generatePathToAccounts(networkId, friend.getId()));
                        System.out.print("\r\r\r\r\r" + step * 100 / size + " %");
                        if (step == size) {
                            System.out.println();
                            System.out.println("Completed");
                        }
                    }
                    System.out.print("\r\r\r\r\r" + step * 100 / size + " %");
                    if (step == size) {
                        System.out.println();
                        System.out.println("Completed");
                    }
                }

            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // TODO: Make global config "Verbose" or "Debug" for such messages
            if (false) System.out.println(userInfo.get("error").toString());
        }
        writeToFile(result, Utils.generatePathToAccounts(networkId, result.getId()));

        return result;
    }

}
