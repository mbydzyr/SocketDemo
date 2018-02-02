package com.zyr.socketdemo.socketclient.service;

import com.zyr.socketdemo.socketclient.bio.Client;
import com.zyr.socketdemo.socketclient.model.Message;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 健康检查
 * author cash
 * create 2017/7/11-11:30
 **/


@RestController
@RequestMapping(value = "/")
public class TestController {

    Logger logger = Logger.getLogger(TestController.class);
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String test(@RequestBody Message message) {
        try {
           /* NettyClientBootstrap bootstrap = NettyClientBootstrap.getInstance(9666, "localhost");
            for(int i=0;i<1000;i++) {
                executorService.submit(new ThreadNettyClientTest(bootstrap,message.getMessage(),i));
                //bootstrap.channel.writeAndFlush(message.getMessage());
            }*/
/*            Charset charset = Charset.forName("GB18030");
            BlockingSocketClient blockingSocketClient =new  BlockingSocketClient("127.0.0.1", 7556, 30000, 30000,charset);*/
            Charset charset = Charset.forName("GB18030");
            Client client = new Client("127.0.0.1", 7556, 30000, 30000);
            for (int i = 0; i < 10; i++) {
                executorService.submit(new ThreadBlockingSocketClientTest(client, message.getMessage(), i, charset));
            }
        } catch (Exception e) {
            logger.error("TestController test error", e);
        }
        return message.getMessage();
    }
}
