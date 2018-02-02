package com.zyr.socketdemo.socketclient.bio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;

/**
 * @author zhangyicong
 * @since 2016/5/30
 */
public class SocketClient {
    private static Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private static Socket client = null;
    private static OutputStreamWriter writer = null;
    private static InputStreamReader reader = null;
    public static void main(String[] args) throws IOException{
        String message="MERX0428INVS                                            20171101095314cDDXbv7gAS_1MCq_0_1           8888021653       9975B2FC71BE76A9<?xml version=\\\"1.0\\\" encoding=\\\"GBK\\\"?><Document><INVS><Batch>0IJ551xNyv7_Q9U</Batch><Count>1</Count><Detail><MchSerial>cDDXbv7gAS_1MCq_0_1</MchSerial><PayUID>7945416</PayUID><PayACC>1219142070102080004807205</PayACC><RecvUID>50098265</RecvUID><RecvACC>1219142070102080023452948</RecvACC><TrnAmt>58.00</TrnAmt><TrnType>01</TrnType><TrnTxt></TrnTxt><CapitalAmt>0.00</CapitalAmt><RelaLID>80996634</RelaLID></Detail></INVS></Document>";
        Charset charset=Charset.forName("GB18030");
        writeToServer("127.0.0.1",7555,message,charset,30000,30000);
    }
    public static String writeToServer(String host, int port, String message, Charset charset, int connTimeout, int readTimeout) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            //打开连接
            connect(host,port,charset,connTimeout,readTimeout);

            //写消息
            writer.write(message);
            writer.flush();

            //读消息
/*            bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String resp = bufferedReader.readLine();
            result.append(resp);*/

            bufferedReader=new BufferedReader(reader);
            char[] buff = new char[10240];
            int len;
            while ((len = bufferedReader.read(buff)) != -1) {
                result.append(buff, 0, len);
            }
            logger.info("客户端收到信息："+result.toString());
        }catch (IOException e){
            //关闭输入流
            closeQuietly(reader);

            //关闭输出流
            closeQuietly(writer);

            //关闭socket
            closeQuietly(client);
             throw  e;
        }finally {
            closeQuietly(bufferedReader);
        }
        return result.toString();
    }


    public static void connect(String host, int port,Charset charset,int connTimeout, int readTimeout) throws IOException  {
        SocketAddress endpoint = new InetSocketAddress(host, port);
        client = new Socket();
        client.setKeepAlive(true);
        if (readTimeout > 0) {
            client.setSoTimeout(readTimeout);
        }
        if (connTimeout > 0) {
            client.connect(endpoint, connTimeout);
        } else {
            client.connect(endpoint);
        }
        reader = new InputStreamReader(client.getInputStream(), charset);
        writer = new OutputStreamWriter(client.getOutputStream(), charset);
    }

    private static void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // Ignored
            }
        }
    }

    private static void closeQuietly(Closeable close) {
        if (close != null) {
            try {
                close.close();
            } catch (IOException e) {
                // Ignored
            }
        }
    }

}
