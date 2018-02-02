package com.zyr.socketdemo.socketclient.service;

import com.zyr.socketdemo.socketclient.netty.NettyClientBootstrap;
import org.apache.log4j.Logger;

/**
 * Created by zhongyunrui on 2017/11/29.
 */
public class ThreadNettyClientTest implements Runnable {
    Logger logger = Logger.getLogger(TestController.class);
    private NettyClientBootstrap bootstrap;
    private String message;
    private int count;

    public ThreadNettyClientTest(NettyClientBootstrap bootstrap, String message, int count) {
        this.bootstrap = bootstrap;
        this.message = message + count;
        this.count = count;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++)
            bootstrap.channel.writeAndFlush(message);
        logger.info("ThreadNettyClientTest run:" + count);
    }
}
