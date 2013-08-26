package com.jule.bomb.test;

import java.io.*;
import java.net.*;

public class UDPClient {
    private static final int PORT = 4561;
    private DatagramSocket dataSocket;
    private DatagramPacket dataPacket;
    private byte sendDataByte[];
    private String sendStr;

    public UDPClient() {
        Init();
    }

    public void Init() {
        try {
            // 指定端口号，避免与其他应用程序发生冲突

            dataSocket = new DatagramSocket(PORT+1);

        } catch (SocketException se) {
            se.printStackTrace();
        }
    }
    
    public void Fini() {
		dataSocket.close();
	}
    
    public void sendUdpData(String str) {
    	 try {
             // 指定端口号，避免与其他应用程序发生冲突
             sendDataByte = new byte[1024];
             sendStr = str;
             sendDataByte = sendStr.getBytes();
             dataPacket = new DatagramPacket(sendDataByte, sendDataByte.length,
                     InetAddress.getByName("localhost"), PORT);
             dataSocket.send(dataPacket);
         } catch (SocketException se) {
             se.printStackTrace();
         } catch (IOException ie) {
             ie.printStackTrace();
         }		
	}
}