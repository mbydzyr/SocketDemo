package com.zyr.socketdemo.socketclient.bio;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;

/**
 * @author Created by zhongyunrui on 2018-02-07
 */
public class BlockingSocketClient {
    /* public static void main(String[] args) throws IOException {
         String message = "MERX0428INVS                                            20171101095314cDDXbv7gAS_1MCq_0_1           8888021653       9975B2FC71BE76A9<?xml version=\\\"1.0\\\" encoding=\\\"GBK\\\"?><Document><INVS><Batch>0IJ551xNyv7_Q9U</Batch><Count>1</Count><Detail><MchSerial>cDDXbv7gAS_1MCq_0_1</MchSerial><PayUID>7945416</PayUID><PayACC>1219142070102080004807205</PayACC><RecvUID>50098265</RecvUID><RecvACC>1219142070102080023452948</RecvACC><TrnAmt>58.00</TrnAmt><TrnType>01</TrnType><TrnTxt></TrnTxt><CapitalAmt>0.00</CapitalAmt><RelaLID>80996634</RelaLID></Detail></INVS></Document>";
         Charset charset = Charset.forName("GB18030");
        // writeToServer("127.0.0.1", 7556, message, charset, 30000, 30000);
     }*/
    private static Logger logger = Logger.getLogger(BlockingSocketClient.class);
    private static int port;
    private static String host;
    private static int connTimeout;
    private static int readTimeout;
    private static Socket client = null;
    private static OutputStreamWriter writer = null;
    private static InputStreamReader reader = null;
    private static Charset charset;

    public BlockingSocketClient(String host, int port, int connTimeout, int readTimeout,Charset charset) {
        this.host = host;
        this.port = port;
        this.connTimeout = connTimeout;
        this.readTimeout = readTimeout;
        this.charset=charset;
        getSocketInstance();
    }

 /*   private static BlockingSocketClient instance;

    public static BlockingSocketClient getInstance(String host, int port, int connTimeout, int readTimeout) {
        if (instance == null) {
            synchronized (BlockingSocketClient.class) {
                if (instance == null) {
                    instance = new BlockingSocketClient(host, port, connTimeout, readTimeout);
                }
            }
        }
        return instance;
    }*/

    private static Socket getSocketInstance() {
        if (null == client || client.isClosed() || client.isConnected()) {
            try {
                SocketAddress endpoint = new InetSocketAddress(host, port);
                client = new Socket();
                //client.setKeepAlive(true);
                if (readTimeout > 0) {
                    client.setSoTimeout(readTimeout);
                }
                if (connTimeout > 0) {
                    client.connect(endpoint, connTimeout);
                } else {
                    client.connect(endpoint);
                }
            } catch (IOException e) {
                logger.error("BlockingSocketClient connect error", e);
            }
        }
        return client;
    }

    private static OutputStreamWriter getWriterInstance() {
        if (null == writer || null == client || client.isClosed() || client.isConnected()) {
            try {
                writer = new OutputStreamWriter(getSocketInstance().getOutputStream(), charset);
            } catch (Exception e) {
                writer = null;
            }
        }
        return writer;
    }

    private static InputStreamReader getReaderInstance() {
        if (null == reader || null == client || client.isClosed() || client.isConnected()) {
            try {
                reader = new InputStreamReader(getSocketInstance().getInputStream(), charset);
            } catch (Exception e) {
                reader = null;
            }
        }
        return reader;
    }

    public String writeToServer(String message) {
        StringBuilder result = new StringBuilder();
        try {
            getWriterInstance().write(message);
            getWriterInstance().flush();

            BufferedReader bufferedReader = new BufferedReader(getReaderInstance());
            char[] buff = new char[10240];
            int len = bufferedReader.read(buff);
            String temp = new String(buff, 0, len);
            result.append(temp);
            System.out.println("客户端收到信息：" + result.toString());
        } catch (IOException e) {
            //关闭socket
            closeQuietly(client);
            logger.error("writeToServer error", e);
        } finally {
         /*   //关闭输入流
            closeQuietly(bufferedReader);
            closeQuietly(reader);

            //关闭输出流
            closeQuietly(writer);*/
        }
        return result.toString();
    }

    private void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // Ignored
            }
        }
    }

    private void closeQuietly(Closeable close) {
        if (close != null) {
            try {
                close.close();
            } catch (IOException e) {
                // Ignored
            }
        }
    }

}
