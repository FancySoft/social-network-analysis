package com.fancy_software.accounts_matching.crawling.parsers;

import com.fancy_software.accounts_matching.crawling.crawlers.ICrawler;
import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.BirthDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;

import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public class OdnoklassnikiParser extends AbstractParser {

    private static final String REDIRECT_URI = "http://ru.infameter.com";
    private static final String CLIENT_ID = "168330496";
    private static final String CLIENT_SECRET = "107006ABDAD32A1A6527F28A";
    private String ACCESS_TOKEN;
    private ObjectMapper mapper;

    public OdnoklassnikiParser(SocialNetworkId networkId, ICrawler crawler) {
        super(networkId,crawler);
        mapper = new ObjectMapper();
    }

    private static String md5(String input) throws NoSuchAlgorithmException {
        byte[] bytesOfMessage = input.getBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(bytesOfMessage);
        BigInteger bigInt = new BigInteger(1, thedigest);
        String sig = bigInt.toString(16);
        while (sig.length() < 32) {
            sig = "0" + sig;
        }
        return sig;
    }

    /*
    @Override
    public void auth(String login, String password) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpProtocolParams.setUserAgent(httpClient.getParams(),
                    "Opera/9.80 (Android; Opera Mini/7.5.31657/28.2555; U; ru) Presto/2.8.119 Version/11.10");

            // Авторизация
            // Шаг 1. Заходим на мобильную версию сайта и ищем форму
            HttpGet get = new HttpGet("http://m.odnoklassniki.ru");
            HttpResponse response = httpClient.execute(get);
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line;
            String toFind = "<form action=\"";
            String HeaderLocation = "";
            while ((line = reader.readLine()) != null) {
                if (line.contains(toFind)) {
                    // Нашли форму, смотрим ссылку для авторизации
                    int i = line.indexOf(toFind) + toFind.length();
                    line = line.substring(i, line.indexOf("\"", i));
                    HeaderLocation = "https://m.odnoklassniki.ru" + line.replaceAll("&amp;", "&") +
                            "&fr.posted=set&fr.needCaptcha=" + "&fr.login=" + login +
                            "&fr.password=" + password;
                }
            }

            // Шаг 2. Переходим по полученной ссылке + логин-пароль
            HttpPost post = new HttpPost(HeaderLocation);
            response = httpClient.execute(post);
            HeaderLocation = response.getFirstHeader("location").getValue();
            post.abort();

            // Шаг 3. Получаем перенаправление на сайт, авторизация успешна
            get = new HttpGet(HeaderLocation);
            httpClient.execute(get);
            get.abort();


            // Получаем code для работы с api
            post = new HttpPost("http://www.odnoklassniki.ru/oauth/authorize?client_id=" + CLIENT_ID +
                    "&response_type=code&redirect_uri=" + REDIRECT_URI + "&scope=VALUABLE_ACCESS");
            response = httpClient.execute(post);

            HeaderLocation = response.getFirstHeader("location").getValue();
            post.abort();

            post = new HttpPost(HeaderLocation);
            response = httpClient.execute(post);

            // На этом этаме в HeaderLocation находится нужный нам код (в ссылке)
            // http://ru.infameter.com?code=XXXXXXX
            HeaderLocation = response.getFirstHeader("location").getValue();
            post.abort();

            String CODE = HeaderLocation.substring(HeaderLocation.indexOf("=") + 1);
            post = new HttpPost("http://api.odnoklassniki.ru/oauth/token.do?" +
                    "code=" + CODE +
                    "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code" +
                    "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET);
            response = httpClient.execute(post);
            in = response.getEntity().getContent();
            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            line = reader.readLine();
            toFind = "\"access_token\"";
            line = line.substring(line.indexOf(toFind) + toFind.length());
            int i = line.indexOf("\"") + 1;
            ACCESS_TOKEN = line.substring(i, line.indexOf("\"", i));
            System.out.println(ACCESS_TOKEN);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public AccountVector parse(String id) {
        AccountVector result = new AccountVector();
        try {
            String sig = md5("application_key=CBAGIJALABABABABA" +
                    "fields=first_name,last_name,birthday,gender" +
                    "uids=" + id +
                    md5(ACCESS_TOKEN + CLIENT_SECRET));

            String url = "http://api.odnoklassniki.ru/api" +
                    "/users/getInfo?" +
                    "application_key=CBAGIJALABABABABA&" +
                    "fields=first_name,last_name,birthday,gender&" +
                    "uids=" + id + "&" +
                    "access_token=" + ACCESS_TOKEN + "&" +
                    "sig=" + sig;
            URL url2 = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url2.openStream(), "utf-8"));
            String line = reader.readLine();
            line = line.substring(1, line.length() - 1);
            Map userInfo = mapper.readValue(line, Map.class);
            reader.close();

            result.setBdate(BirthDate.generateBirthDate((String)userInfo.get("birthday")));
            result.setFirst_name((String) userInfo.get("first_name"));
            result.setLast_name((String) userInfo.get("last_name"));

            if (userInfo.get("gender").equals("male"))
                result.setSex(AccountVector.Sex.MALE);
            else if (userInfo.get("gender").equals("female"))
                result.setSex(AccountVector.Sex.FEMALE);
            else
                result.setSex(AccountVector.Sex.NA);

            Map m;
            List list;

            // Получаем uid групп
            sig = md5("application_key=CBAGIJALABABABABA" + "uid=" + id + md5(ACCESS_TOKEN + CLIENT_SECRET));
            url = "http://api.odnoklassniki.ru/api/group/getUserGroupsV2?" +
                    "application_key=CBAGIJALABABABABA&" +
                    "uid=" + id + "&" +
                    "sig=" + sig + "&" +
                    "access_token=" + ACCESS_TOKEN;
            url2 = new URL(url);
            reader = new BufferedReader(new InputStreamReader(url2.openStream(), "utf-8"));
            line = reader.readLine();
            System.out.println(line);
            line = line.substring(1, line.length() - 1);
            userInfo = mapper.readValue(line, Map.class);
            reader.close();

    /*
            // Друзья
            AccountVector friend;
            sig = md5("application_key=CBAGIJALABABABABA" + md5(ACCESS_TOKEN + CLIENT_SECRET));
            url = "http://api.odnoklassniki.ru/api/friends/get?" +
                    "application_key=CBAGIJALABABABABA&" +
                    "sig=" + sig + "&" +
                    "access_token=" + ACCESS_TOKEN;
            url2 = new URL(url);
            reader = new BufferedReader(new InputStreamReader(url2.openStream(), "utf-8"));
            line = reader.readLine();
            System.out.println(line);
            line = line.substring(1, line.length() - 1);
            userInfo = mapper.readValue(line, Map.class);
            reader.close();
   */
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AccountVector match(AccountVector goal) {
        return null;
    }
}
