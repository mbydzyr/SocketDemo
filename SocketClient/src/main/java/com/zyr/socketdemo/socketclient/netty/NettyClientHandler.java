package com.zyr.socketdemo.socketclient.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhongyunrui on 2018-02-07
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {
    private volatile boolean reconnect = true;
    private int attempts;
    Logger logger = Logger.getLogger(NettyClientHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
           /* IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case WRITER_IDLE:
                    PingMsg pingMsg=new PingMsg();
                    ctx.writeAndFlush(pingMsg);
                    System.out.println("send ping to server----------");
                    break;
                default:
                    break;
            }*/
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                    /*读超时. 即当在指定的事件间隔内没有从 Channel 读取到数据时, 会触发一个 READER_IDLE 的 IdleStateEvent 事件*/
                System.out.println("===服务端===(READER_IDLE 读超时)");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println("send ping to server----------");
                    /*写超时. 即当在指定的事件间隔内没有数据写入到 Channel 时, 会触发一个 WRITER_IDLE 的 IdleStateEvent 事件*/
                System.out.println("===服务端===(WRITER_IDLE 写超时)");
            } else if (event.state() == IdleState.ALL_IDLE) {
                    /*读/写超时. 即当在指定的事件间隔内没有读或写操作时, 会触发一个 ALL_IDLE 的 IdleStateEvent 事件*/
                System.out.println("===服务端===(ALL_IDLE 总超时)");
            }
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, String msg) {
        try {
            System.out.println("客户端接收到的消息为：" + msg);
        } catch (Exception e) {
            logger.error("messageReceived error",e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            System.out.println("错误原因：" + cause.getMessage());
            ctx.channel().close();
        }catch (Exception e){
            logger.error("exceptionCaught error",e);
        }
    }

    //Channel是活跃状态（连接到某个远端），可以收发数据
    @Override
    public void channelActive(ChannelHandlerContext ctx)  {
        try {
            System.out.println("Client active ");
            super.channelActive(ctx);
        }catch (Exception e){
            logger.error("channelActive error",e);
        }
    }

    //Channel未连接到远端
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //关闭，等待重连
        ctx.close();
        super.channelInactive(ctx);
        System.out.println("===服务端===(客户端失效)");
        System.out.println("链接关闭");
        //重新连接服务器
        ctx.channel().eventLoop().schedule(new Runnable() {
            public void run() {
                System.out.println("重连开始....");
                NettyClientBootstrap.doConnect();
                System.out.println("重连结束....");
            }
        }, 2, TimeUnit.SECONDS);

    }
}
