package com.jule.bomb.test;

public class randomtest {

	// 获取0-max随机数，不包括max
		private static int getRandom(int max) {
			int b=(int)(Math.random()*max);//产生0-max的整数随机数		
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
