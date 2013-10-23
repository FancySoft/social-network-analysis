package com.fancy_software.accounts_matching.crawling.crawlers;

import com.fancy_software.accounts_matching.crawling.ParserFactory;
import com.fancy_software.accounts_matching.crawling.parsers.SocialNetworkId;
import com.fancy_software.accounts_matching.crawling.parsers.VkParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;

public class VkCrawler extends AbstractCrawler {

    private static String authConfigName = "config/authentication_config.txt";
    private static String VK_LOGIN;
    private static String VK_PASSWORD;
    private static boolean needVKAuth = true;
    private final String APP_ID = "3437182";
    private final String SCOPE = "262146";
    private final String REDIRECT_URI = "http://oauth.vk.com/blank.html";
    private final String DISPLAY = "page";
    private final String RESPONSE_TYPE = "token";

    public VkCrawler(){
        super();
    }

    @Override
    public void auth(String login, String password) {
        super.auth(login, password);
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

            if (parser instanceof VkParser)
                ((VkParser) parser).setACCESS_TOKEN(HeaderLocation.split("#")[1].split("&")[0].split("=")[1]);
        } catch (IOException ioexc) {
            ioexc.printStackTrace();
        }
    }

    @Override
    public void init(String id) {
        super.init(id);
        parser = ParserFactory.getApiWorkerInstance(SocialNetworkId.VK, this);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new DataInputStream(new FileInputStream(authConfigName))));
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (count == 0) {
                    count++;
                    VK_LOGIN = line;
                } else
                    VK_PASSWORD = line;
            }
            auth(VK_LOGIN,VK_PASSWORD);
            needVKAuth=false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        System.out.println(VK_LOGIN);
        System.out.println(VK_PASSWORD);
        if (needVKAuth) {
            auth(VK_LOGIN, VK_PASSWORD);
            needVKAuth = false;
        }
        parser.parse(id);
        while(usersToParse.size()>0){
            parser.parse(Long.toString(usersToParse.remove()));
        }

    }

}
