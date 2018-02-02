package com.zyr.socketdemo.socketclient.bio;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * c/s架构的客户端
 *
 * @author zhongyunrui
 */
public class Client {
    private String host;
    private int port;
    private int connTimeout;
    private int readTimeout;
    private volatile boolean running = false;//连接状态
    private static Socket socket;
    private static Logger logger = Logger.getLogger(Client.class);

    public Client(String host, int port, int connTimeout, int readTimeout) {
        this.host = host;
        this.port = port;
        this.connTimeout = connTimeout;
        this.readTimeout = readTimeout;
        start();
    }
    private long lastSendTime; //最后一次发送数据的时间

    public void start() {
        try {
            if(socket==null || socket.isClosed() || socket.isConnected()) {
                synchronized (Socket.class) {
                    if(socket==null || socket.isClosed() || socket.isConnected()) {
                        if (running) return;
                        SocketAddress endpoint = new InetSocketAddress(host, port);
                        socket = new Socket();
                        //socket.setKeepAlive(true);
                        if (readTimeout > 0) {
                            socket.setSoTimeout(readTimeout);
                        }
                        if (connTimeout > 0) {
                            socket.connect(endpoint, connTimeout);
                        } else {
                            socket.connect(endpoint);
                        }
                        System.out.println("本地端口：" + socket.getLocalPort());
                        lastSendTime = System.currentTimeMillis();
                        running = true;
                        //保持长连接的线程，每隔2秒项服务器发一个一个保持连接的心跳消息
                        new Thread(new KeepAliveWatchDog()).start();
                    }
                }
            }
        } catch (Exception e) {
             stop();
            logger.error("start error", e);
        }
    }

    public void stop() {
        running = false;
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            logger.error("stop error", e);
        }
    }

    public String writeToServer(String message) {
        String result="";
        try {
            lastSendTime = System.currentTimeMillis();

            //发送消息
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(changeCharset(message, "GB18030"));
            System.out.println("发送：\t" + message);
            oos.flush();

            //接收消息
            InputStream is = socket.getInputStream();
            if (is.available() > 0) {
                ObjectInputStream ois = new ObjectInputStream(is);
                Object objRevMsg = ois.readObject();
                result= changeCharset(objRevMsg.toString(), "GB18030");
                System.out.println("接收:\t" + result);
            }
        } catch (Exception e) {
            stop();
            logger.error("writeToServer error", e);
        }
        return result;
    }

    /**
     * 发送保持心跳信息线程类
     *
     * @author zhouy
     */
    private class KeepAliveWatchDog implements Runnable {
        private final int delay = 10;
        private final int keepAliveDelay = 2000;//间隔两秒发送一次

        @Override
        public void run() {
            while (running) {
                if (System.currentTimeMillis() - lastSendTime > keepAliveDelay) {
                    //String result=writeToServer(KeepAlive.getDate());
                    //System.out.println("心跳接收:\t" + result);
                    lastSendTime = System.currentTimeMillis();
                }
            }
        }
    }

    private String changeCharset(String str, String newCharset) {
        try {
            if (str != null) {
                byte[] bs = str.getBytes();
                return new String(bs, newCharset);
            }
            return null;
        } catch (UnsupportedEncodingException e) {
            logger.error("changeCharset error", e);
        }
        return null;
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 7556, 30000, 30000);

    }
}


