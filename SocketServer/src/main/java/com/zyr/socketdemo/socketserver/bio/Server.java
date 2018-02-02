package com.zyr.socketdemo.socketserver.bio;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端
 *
 * @author zhouy
 */
public class Server {
    private int port;
    private int maxConnect;
    private volatile boolean running = false;//运行状态
    private long receiveTimeDelay = 3000;
    private Thread connWatchDog;

    public Server(int port, int maxConnect) {
        this.port = port;
        this.maxConnect = maxConnect;
    }

    public void start() {
        if (running) return;
        running = true;
        connWatchDog = new Thread(new ConnWatchDog());
        connWatchDog.start();
        System.out.print("Server 启动成功...");
    }

    public void stop() {
        if (running) running = false;
    }

    /**
     * @author zhouy
     */
    private class ConnWatchDog implements Runnable {
        @Override
        public void run() {
            try {
                ServerSocket server = new ServerSocket();
                server.bind(new InetSocketAddress(port), maxConnect);
                while (running) {
                    Socket socket = server.accept();
                    Thread t = new Thread(new SocketAction(socket));
                    t.start();
                }
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
                Server.this.stop();
            }
        }
    }

    /**
     * 监听客户端请求，超时
     *
     * @author zhouy
     */
    class SocketAction implements Runnable {
        private Socket socket;
        private boolean run = true;
        private long lastReceiveTime = System.currentTimeMillis();
        public SocketAction(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            while (running && run) {
                try {
            /*        if (System.currentTimeMillis() - lastReceiveTime > receiveTimeDelay) {
                        //超时处理
                        overthis();
                    } else {*/
                      InputStream is = socket.getInputStream();
                    if (is.available() > 0) {
                        ObjectInputStream ois = new ObjectInputStream(is);
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        Object obj = ois.readObject();
                        //接受到服务端发送的数据，将时间重置
                        lastReceiveTime = System.currentTimeMillis();
                        String reveiveMsg = changeCharset(obj.toString(), "GBK");
                        System.out.println("服务端接受到的数据" + reveiveMsg);

                        //写回客户端
                        oos.writeObject(reveiveMsg);
                        oos.flush();
                    } else {
                        System.out.println("服务端休眠..");
                        Thread.currentThread().sleep(10);
                    }
                    // }
                } catch (Exception e) {
                    e.printStackTrace();
                    overthis();
                }
            }
        }

        private void overthis() {
            if (run) run = false;
            if (socket != null) {
                try {
                    System.out.println("关闭:" + socket.getRemoteSocketAddress());
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String changeCharset(String str, String newCharset) throws UnsupportedEncodingException {
        if (str != null) {
            byte[] bs = str.getBytes();
            return new String(bs, newCharset);
        }
        return null;
    }

    public static void main(String[] args) {
        Server server = new Server(7556, 3);
        server.start();
    }
}
