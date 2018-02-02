package com.zyr.socketdemo.socketclient.service;

import com.zyr.socketdemo.socketclient.bio.Client;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;

/**
 * Created by zhongyunrui on 2017/11/29.
 */
public class ThreadBlockingSocketClientTest implements Runnable {
    Logger logger = Logger.getLogger(TestController.class);
    private Client blockingSocketClient;
    private String message;
    private int count;
    private   Charset charset;

    public ThreadBlockingSocketClientTest(Client blockingSocketClient, String message, int count,Charset charset) {
        this.blockingSocketClient = blockingSocketClient;
        this.message = message + count;
        this.count = count;
        this.charset=charset;
    }

    @Override
    public void run() {
        blockingSocketClient.writeToServer(message);
        logger.info("ThreadBlockingSocketClientTest run:" + count);
    }
}
