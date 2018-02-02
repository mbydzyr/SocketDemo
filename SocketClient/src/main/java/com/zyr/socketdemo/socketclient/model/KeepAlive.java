package com.zyr.socketdemo.socketclient.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 心跳包对象
 * @author zhouy
 *
 */
public class KeepAlive implements Serializable{
    private static final long serialVersionUID = 2949229070705266367L;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public String toString() {
        return sdf.format(new Date());
    }

    public  static String  getDate(){
        String msg="MERX0428INVS                                            20171101095314cDDXbv7gAS_1MCq_0_1           8888021653       9975B2FC71BE76A9<?xml version=\\\"1.0\\\" encoding=\\\"GBK\\\"?><Document><INVS><Batch>0IJ551xNyv7_Q9U</Batch><Count>1</Count><Detail><MchSerial>cDDXbv7gAS_1MCq_0_1</MchSerial><PayUID>7945416</PayUID><PayACC>1219142070102080004807205</PayACC><RecvUID>50098265</RecvUID><RecvACC>1219142070102080023452948</RecvACC><TrnAmt>58.00</TrnAmt><TrnType>01</TrnType><TrnTxt></TrnTxt><CapitalAmt>0.00</CapitalAmt><RelaLID>80996634</RelaLID></Detail></INVS></Document>";
        return msg;
    }
}