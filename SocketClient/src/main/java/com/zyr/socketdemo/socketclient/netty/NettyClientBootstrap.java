package com.zyr.socketdemo.socketclient.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by yaozb on 15-4-11.
 */
public class NettyClientBootstrap {
    private static int port;
    private static String host;
    //private  SocketChannel socketChannel;
    public static Channel channel;
    private static Bootstrap bootstrap;
    private static ChannelFutureListener listener = null;
    private static ChannelFuture future;
    private static volatile NettyClientBootstrap instance;

    private NettyClientBootstrap(int port, String host) {
        this.port = port;
        this.host = host;
        start();
    }

    public static NettyClientBootstrap getInstance(int port, String host) {
        if (instance == null) {
            synchronized (NettyClientBootstrap.class) {
                if (instance == null) {
                    instance = new NettyClientBootstrap(port, host);
                }
            }
        }
        return instance;
    }

    private void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.group(eventLoopGroup);
        bootstrap.remoteAddress(host, port);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new IdleStateHandler(20, 10, 0));
                socketChannel.pipeline().addLast(new ObjectEncoder());
                socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                socketChannel.pipeline().addLast(new NettyClientHandler());
            }
        });
        doConnect();
    }

    public static void doConnect() {
        try {
            if (channel != null && channel.isActive()) {
                return;
            }
            listener = new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        channel = future.channel();
                        //socketChannel = (SocketChannel) future.channel();
                        System.out.println("Connect to server successfully!");
                    } else {
                        System.out.println("Failed to connect to server, try connect after 10s");
                        future.channel().eventLoop().schedule(new Runnable() {
                            public void run() {
                                doConnect();
                            }
                        }, 5, TimeUnit.SECONDS);
                    }
                }
            };
            future = bootstrap.connect(host, port).addListener(listener).sync();
            //wait until the connection is closed.
            //future=future.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("doConnect error" + e);
        }
    }

    public ChannelFuture getFuture() {
        return future;
    }


    public static void main(String[] args) throws InterruptedException {
        try {
            NettyClientBootstrap bootstrap = new NettyClientBootstrap(9666, "localhost");
            bootstrap.channel.writeAndFlush("测试测试....");
            while (true) {
                System.out.println("client runing---------------");
                TimeUnit.SECONDS.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("main:" + e);
        }
    }

}
