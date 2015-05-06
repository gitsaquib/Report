package com.pearson.psoc.util;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ExecuteTestSet {

	public static void main(String args[]) throws IOException {
		//String testMethodName = ExecuteTestSetUtil.getTestCaseName("D:\\Project\\PSoC\\code\\WinPSCAutomation\\Pearson.PSCWinAutomation.K1App\\", "22472");
		Map<String, String> testCases = ExecuteTestSetUtil.readInputFile("D:\\Input\\Input.xls");
		
		Set<String> testCaseIds = testCases.keySet();
		for(String testCaseId:testCaseIds) {
			String testMethodName = testCases.get(testCaseId);
			String status = ExecuteTestSetUtil.callCommandPrompt(
					  "D:\\Project\\PSoC\\code\\WinPSCAutomation\\Pearson.PSCWinAutomation.K1App\\bin\\Debug"
					, "C:\\Program Files (x86)\\Microsoft Visual Studio 12.0\\Common7\\IDE\\MSTest.exe"
					, "D:\\Project\\PSoC\\code\\WinPSCAutomation\\LocalTestSettings.testsettings"
					, testMethodName
					, "Pearson.PSCWinAutomation.K1App.dll");
			System.out.println(testCaseId + "\t" +status);
		}
	}
}
