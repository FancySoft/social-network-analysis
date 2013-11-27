package com.fancy_software.accounts_matching.deprecated;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.BirthDate;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created with IntelliJ IDEA.
 * User: EmoCoder
 * Date: 21.03.13
 * Time: 20:32
 */
public class VKWorker {

    private HttpClient client;

    public VKWorker() {
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BROWSER_COMPATIBILITY);
        client.getParams().setParameter("http.useragent",
                "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:19.0) Gecko/20100101 Firefox/19.0");
    }

    /**
     * Авторизаця вконтакте, тысяча чертей!
     *
     * @param login    логин
     * @param password пароль
     */
    public void Auth(String login, String password) {
        HttpResponse response;
        HttpGet get = new HttpGet("http://vk.com");
        String ip_h = null;

        try {
            // Пытаемся получить ip_h (нужен для авторизации)
            response = client.execute(get);
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "windows-1251"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ip_h:")) {
                    int i = line.indexOf("'");
                    i++;
                    ip_h = line.substring(i, line.indexOf("'", i));
                }
            }
            // Не удалось получить ip_h, печаль
            assert ip_h == null;
            // Долго и больно авторизуемся
            HttpPost post = new HttpPost("https://login.vk.com/?act=login" +
                    "&role=al_frame" +
                    "&_origin=http%3A%2F%2Fvk.com" +
                    "ip_h=" + ip_h +
                    "&email=" + login +
                    "&pass=" + password);
            response = client.execute(post);
            post.abort();
            String HeaderLocation = response.getFirstHeader("location").getValue();
            post = new HttpPost(HeaderLocation);
            client.execute(post);
            post.abort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AccountVector Parse(String id) throws IOException {
        AccountVector result = new AccountVector();
        // Смотрим страничку пользователя
        HttpGet get = new HttpGet("https://vk.com/" + id);
        HttpResponse response = client.execute(get);
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "windows-1251"));
        String line;
        // Чтение и разбор страницы
        boolean needBday = true;
        boolean needName = true;
        boolean needId = true;
        String numericId = null;
        while ((line = reader.readLine()) != null) {
            // Имя
            if (needName && line.contains("<title>")) {
                String[] name = line.substring("<title>".length(), line.indexOf("</title>")).split(" ");
                result.setFirst_name(name[0]);
                result.setLast_name(name[1]);
                needName = false;
            }
            // День рождения
            else if (needBday && line.contains("[bday]=")) {
                String toFind = "[bday]=";
                int i = line.indexOf(toFind);
                StringBuilder builder = new StringBuilder("");
                if (i > -1) {
                    builder.append(line.substring(i + toFind.length(),
                            i + toFind.length() + 2).replaceAll("[\\D]", ""));
                }
                toFind = "[bmonth]=";
                i = line.indexOf(toFind);
                if (i > -1) {
                    builder.append(".").append(line.substring(i + toFind.length(),
                            i + toFind.length() + 2).replaceAll("[\\D]", ""));
                }
                result.setBdate(BirthDate.generateBirthDate(String.valueOf(builder)));
                needBday = false;
            } else if (needId && line.contains("groups?id=")) {
                String toFind = "groups?id=";
                int i = line.indexOf(toFind);
                numericId = line.substring(i + toFind.length(),
                        line.indexOf("\"", i));
                needId = false;
            }
        }
        // Смотрим страничку с группами пользователя
        if (numericId != null) {
            get = new HttpGet("https://vk.com/groups?id=" + numericId);
            response = client.execute(get);
            in = response.getEntity().getContent();
            reader = new BufferedReader(new InputStreamReader(in, "windows-1251"));
            while ((line = reader.readLine()) != null) {
                String toFind = "<div class=\"group_row_labeled\"><a href=";
                // Составляем список групп
                if (line.contains(toFind) && !line.contains("name")) {
                    result.addGroup(line.substring(line.indexOf("<b>") + 3, line.indexOf("</b>")));
                }
            }
        }
        return result;
    }
}
