package com.undsf.util;

/**
 * 扩展数学库
 * @author Arathi
 *
 */
public class ExtMath {
	
	public static int gcd(int a, int b) {
		int temp;
		if(a<b) {
			// 交换两个数，使大数放在a上
			temp=a;
			a=b;
			b=temp;
		}
		while(b!=0){
			//利用辗除法，直到b为0为止
			temp=a%b;
			a=b;
			b=temp;
		}
		return a;
	}
	
}
