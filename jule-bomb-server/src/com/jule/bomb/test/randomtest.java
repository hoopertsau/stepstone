package com.jule.bomb.test;

public class randomtest {

	// ��ȡ0-max�������������max
		private static int getRandom(int max) {
			int b=(int)(Math.random()*max);//����0-max�����������		
			return b;
		}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(int i=0;i<100;i++)
		{
			System.out.println(Integer.toString(getRandom(4)));
		}
	}

}
