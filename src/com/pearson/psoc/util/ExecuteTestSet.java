package com.pearson.psoc.util;

import java.io.IOException;

public class ExecuteTestSet {

	public static void main(String args[]) throws IOException {
		String testMethodName = ExecuteTestSetUtil.find("D:\\Project\\PSoC\\code\\WinPSCAutomation\\Pearson.PSCWinAutomation.K1App\\", "22472");
		String status = ExecuteTestSetUtil.callCommandPrompt(
				  "D:\\Project\\PSoC\\code\\WinPSCAutomation\\Pearson.PSCWinAutomation.K1App\\bin\\Debug"
				, "C:\\Program Files (x86)\\Microsoft Visual Studio 12.0\\Common7\\IDE\\MSTest.exe"
				, "D:\\Project\\PSoC\\code\\WinPSCAutomation\\LocalTestSettings.testsettings"
				, testMethodName
				, "Pearson.PSCWinAutomation.K1App.dll");
		System.out.println(status);
	}
}
