package com.fancy_software.crawling;

/**
 * Created with IntelliJ IDEA.
 * User: yaro
 * Date: 13.11.13
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
public class CallRunner implements Runnable {

    VkApiCaller apiCaller;
    String login;
    String password;
    ExtractType extractType;

    public CallRunner(VkApiCaller apiCaller, String login, String password, ExtractType extractType) {
        this.apiCaller = apiCaller;
        this.login = login;
        this.password = password;
        this.extractType = extractType;
    }

    @Override
    public void run() {
        apiCaller.auth(login, password);
        apiCaller.start(extractType);
    }
}
