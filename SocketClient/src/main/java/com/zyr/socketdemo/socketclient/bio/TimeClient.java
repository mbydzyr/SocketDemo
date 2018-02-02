package com.zyr.socketdemo.socketclient.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by JiaPeng on 2017/2/2.
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 7555;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(port);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket("127.0.0.1", port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("MERX0428INVS                                            20171101095314cDDXbv7gAS_1MCq_0_1           8888021653       9975B2FC71BE76A9<?xml version=\\\"1.0\\\" encoding=\\\"GBK\\\"?><Document><INVS><Batch>0IJ551xNyv7_Q9U</Batch><Count>1</Count><Detail><MchSerial>cDDXbv7gAS_1MCq_0_1</MchSerial><PayUID>7945416</PayUID><PayACC>1219142070102080004807205</PayACC><RecvUID>50098265</RecvUID><RecvACC>1219142070102080023452948</RecvACC><TrnAmt>58.00</TrnAmt><TrnType>01</TrnType><TrnTxt></TrnTxt><CapitalAmt>0.00</CapitalAmt><RelaLID>80996634</RelaLID></Detail></INVS></Document>");
            String resp = in.readLine();
            System.out.println("客户端收到信息：" + resp);
        } catch (Exception e) {

        } finally {
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }
    }

}