package com.pearson.psoc.util;

import java.util.Date;

public class RND {
	public static void main(String[] args) throws InterruptedException {
		Date date1 = new Date();
		Thread.sleep(60000);
		Date date2 = new Date();
		System.out.println((date2.getTime()-date1.getTime())/(60*1000));
	}
}
