package com.zyr.socketdemo.socketserver.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by yaozb on 15-4-11.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannelMap.remove((SocketChannel) ctx.channel());
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
        System.out.println("服务端接收到的消息为：" + message);
        channelHandlerContext.writeAndFlush(message);
        //channelHandlerContext.writeAndFlush("CMBX0390INVS2017110110031020171101100310651171101520228920171101095314cDDXbv7gAS_1MCq_0_1           8888021653CMBMB99FE23500F1CAC7CAC<?xml version=\"1.0\" encoding=\"GBK\"?>   <Document>  <INVS>  <Batch>0IJ551xNyv7_Q9U</Batch>  <Count>1</Count>  <RtnCode>CMBMB99</RtnCode>  <RtnMsg>处理成功</RtnMsg>  <Detail>  <MchSerial>cDDXbv7gAS_1MCq_0_1</MchSerial>  <CmbSerial>201711011003106511711015202293</CmbSerial>  <RtnCode>CMBMB99</RtnCode>  <RtnMsg>交易成功</RtnMsg>  <TrnDate>20171101</TrnDate>  </Detail>  </INVS>  </Document>  \n");
    }
}
