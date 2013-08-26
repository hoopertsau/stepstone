package com.jule.bomb.test;

import com.jule.bomb.srv.udpserver;
import com.jule.bomb.srv.udpserver.IRecvDataHandle;

public class udpServerTest {

	
	public static class OnRecvDataImp implements IRecvDataHandle
	{
		@Override
		public void OnRecvData(String recvData, String destIp, int port) {
			System.out.println("recved data from "+destIp+" port="+ Integer.toString(port) + " data:"+recvData );
		}		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IRecvDataHandle recvDataHandle = new OnRecvDataImp();
		udpserver srvUdpserver = new udpserver();
		srvUdpserver.Init(recvDataHandle);
	}
}
