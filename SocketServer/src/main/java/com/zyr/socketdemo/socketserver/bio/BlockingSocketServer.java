package com.zyr.socketdemo.socketserver.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * 基于BIO的Socket服务器端
 *
 * @author shirdrn
 */
public class BlockingSocketServer extends Thread {

    /**
     * 服务端口号
     */
    private int port = 7556;
    /**
     * 为客户端分配编号
     */
    private static int sequence = 0;

    public BlockingSocketServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            //通过死循环开启长连接，开启线程去处理消息
            while (true) {
                System.out.println("等待新的请求进来..." );
                socket = serverSocket.accept(); // 监听
                System.out.println("有一笔新的请求进来..." );
                this.handleMessage(socket); // 处理一个连接过来的客户端请求
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理一个客户端socket连接
     *
     * @param socket 客户端socket
     * @throws IOException
     */
    private void handleMessage(Socket socket) throws IOException {
        try {
            Charset charset = Charset.forName("GB18030");
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), charset);
            InputStreamReader reader = new InputStreamReader(socket.getInputStream(), charset);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder result = new StringBuilder();
            String clientMessage = "";
            char[] buff = new char[10240];
            int len = bufferedReader.read(buff);
            String temp = new String(buff, 0, len);
            result.append(temp);
      /*  int len;
        while ((len = bufferedReader.read(buff)) != -1) {
            String  temp= new String(buff, 0, len);
            String messageLengthStr=temp.substring(4,8);
            int messageLength=Integer.parseInt(messageLengthStr);
            result.append(temp);
        }*/
            if (len > 0) {
                clientMessage = result.toString();
                System.out.println("服务端收到信息：" + clientMessage);
                writer.write(clientMessage);
                writer.flush();
            } else {
                System.out.println("服务端收到信息为空");
            }
        }catch (Exception e){
            System.out.println("服务端异常："+e);
        }
    }

    public static void main(String[] args) {
        BlockingSocketServer server = new BlockingSocketServer(7556);
        server.start();
        System.out.println("Server 启动成功...");
    }
}