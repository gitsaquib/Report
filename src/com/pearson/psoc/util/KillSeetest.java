package com.pearson.psoc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class KillSeetest {
	
	private static final String TASKLIST = "tasklist";
	private static final String KILL = "taskkill /F /IM ";
	private static final String SEETESTHOME = "C:\\Program Files (x86)\\Experitest\\SeeTest\\";
	
	public static void main(String args[]) throws Exception {
		String processName = "studio.exe";
		if (isProcessRunning(processName)) {
			killProcess(processName);
			Thread.sleep(10000);
		}
		startProcess(processName);
	}

	public static boolean isProcessRunning(String serviceName) throws Exception {
		Process p = Runtime.getRuntime().exec(TASKLIST);
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			if (line.contains(serviceName)) {
				return true;
			}
		}
		return false;
	}

	public static void killProcess(String serviceName) throws Exception {
		Runtime.getRuntime().exec(KILL + serviceName);
	}
	
	public static void startProcess(String serviceName) throws IOException {
		Process process = new ProcessBuilder(SEETESTHOME +  serviceName).start();
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
		  System.out.println(line);
		}
	}
}
