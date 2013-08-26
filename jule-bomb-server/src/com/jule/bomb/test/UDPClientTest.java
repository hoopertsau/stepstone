package com.jule.bomb.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class UDPClientTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UDPClient client = new UDPClient();
		while(true)
		{
			BufferedReader br = new BufferedReader(new InputStreamReader( System.in ) ) ;  //java.io.InputStreamReaderºÃ≥–¡ÀReader¿‡
			String tx = "null";
			try {
				tx = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(tx=="exit")
				break;
			client.sendUdpData(tx);			
		}		
	}

}
