package com.jule.bomb.srv;

import java.io.IOException;
import java.net.*;

import org.omg.CORBA.UShortSeqHelper;

public class udpserver {
	private static final int PORT = 4561;
	private DatagramSocket dataSocket =null;
	private DatagramPacket dataPacket;
	private byte receiveByte[];
	private String receiveStr;
	private IRecvDataHandle recverHandler;
	
	Thread theThread;

	public udpserver() {
	}

	public interface IRecvDataHandle {
		public void OnRecvData(String recvData, String destIp, int port);
	}

	class UDPRecvThread implements Runnable {
		String s = "";
		int time = 0;

		public void run() {
			try {
				System.out.println("UDPRecvThread started!");
				int i = 0;
				while (i == 0)// 无数据，则循环
				{
					dataSocket.receive(dataPacket);
					i = dataPacket.getLength();
					// 接收数据
					if (i > 0) {
						// 指定接收到数据的长度,可使接收数据正常显示,开始时很容易忽略这一点
						receiveStr = new String(receiveByte, 0,dataPacket.getLength(),"utf-8");
						System.out.println("收到udp数据 。。。" + receiveStr);
						i = 0;// 循环接收
						
						if(receiveStr.equals("$$killmyself$$"))
						{
							System.out.println("kill myself....");
							dataSocket.close();
							return;
						}
						recverHandler.OnRecvData(receiveStr, dataPacket.getAddress().toString(), dataPacket.getPort());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void Init(IRecvDataHandle handler) {
		try {
			
			if(dataSocket==null){
				dataSocket = new DatagramSocket(null);
				dataSocket.setReuseAddress(true);
				dataSocket.bind(new InetSocketAddress(PORT));
			}
			
			recverHandler = handler;
			receiveByte = new byte[1024];
			dataPacket = new DatagramPacket(receiveByte, receiveByte.length);
			receiveStr = "";
			
			UDPRecvThread udpservThrd = new UDPRecvThread();
			theThread = new Thread(udpservThrd);
			theThread.start();
			System.out.println("call start()");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendUdpData(String str, String hostName, int port ) {
		try {
		 byte[] sendDataByte = new byte[1024];
         sendDataByte = str.getBytes();
         dataPacket = new DatagramPacket(sendDataByte, sendDataByte.length,InetAddress.getByName(hostName), port);
         dataSocket.send(dataPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}        
	}
	
	public void Fini() {
		try {
			sendUdpData("$$killmyself$$", "localhost", PORT);
			Thread.sleep(500);
			if(theThread.isAlive())
			{
				System.out.println("you still alived!!! close socket!");
				dataSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}