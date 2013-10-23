package com.fancy_software.accounts_matching.crawling.parsers;

import com.fancy_software.accounts_matching.crawling.crawlers.AbstractCrawler;
import com.fancy_software.accounts_matching.crawling.crawlers.ICrawler;
import com.fancy_software.accounts_matching.io_local_base.Utils;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.BirthDate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VkParser extends AbstractParser {

    private static int counter = 0;
    private final boolean VERBOSE = false;
    private ObjectMapper mapper;
    private String ACCESS_TOKEN;

    public VkParser(SocialNetworkId networkId, ICrawler crawler) {
        super(networkId, crawler);
        mapper = new ObjectMapper();
    }

    public void setACCESS_TOKEN(String ACCESS_TOKEN) {
        this.ACCESS_TOKEN = ACCESS_TOKEN;
    }

    @Override
    public AccountVector parse(String id) {
        return _Parse(id, null, true);
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
     * Parser
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
            if (VERBOSE) System.out.println(userInfo.get("error"));
            return null;
        }
        userInfo = (Map) ((ArrayList) userInfo.get("response")).get(0);
        result.setBdate(BirthDate.generateBirthDate((String) userInfo.get("bdate")));
        result.setFirst_name((String) userInfo.get("first_name"));
        result.setLast_name((String) userInfo.get("last_name"));
        setSex(result, (Integer) userInfo.get("sex"));
        result.setId((Integer) userInfo.get("uid"));
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
            if (VERBOSE) System.out.println(userInfo.get("error"));
        }

        // Получаем список друзей
        System.out.println(id);
        System.out.println(Long.toString(result.getId()));
        userInfo = ApiCall("friends.get", "uid=" + result.getId());
        if (userInfo.containsKey("response")) {
            list = (ArrayList) userInfo.get("response");
            for (Object o : list) {
                result.addFriend(Long.parseLong(o.toString()));
                if (crawler instanceof AbstractCrawler)
                    ((AbstractCrawler) crawler).addUserToParse(Long.parseLong(o.toString()));
//                    if (friendOfId == null) {
//                        writeToFile(friend, Utils.generatePathToAccounts(networkId, friend.getId()));
//                        System.out.print("\r\r\r\r\r" + step * 100 / size + " %");
//                        if (step == size) {
//                            System.out.println();
//                            System.out.println("Completed");
//                        }
//                    }
//                }

            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            if (VERBOSE)
                System.out.println(userInfo.get("error").toString());
        }
        writeToFile(result, Utils.generatePathToAccounts(networkId, result.getId()));
        counter++;
        System.out.println(result) ;
        return result;
    }

    private void setSex(AccountVector vector, int sexId) {
        switch (sexId) {
            case 1:
                vector.setSex(AccountVector.Sex.FEMALE);
                break;
            case 2:
                vector.setSex(AccountVector.Sex.MALE);
                break;
            default:
                vector.setSex(AccountVector.Sex.NA);
                break;
        }
    }

}
