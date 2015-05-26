package com.pearson.psoc.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExecuteTestSet {

	private static final String DLLNAME = "Pearson.PSCAutomation.212App.dll";
	private static final String TESTSETTINGS = "D:\\Project\\PSoC\\code\\SeeTestAutomation\\ParallelTestSettings.testsettings";
	private static final String MSTEST = "C:\\Program Files (x86)\\Microsoft Visual Studio 12.0\\Common7\\IDE\\MSTest.exe";
	private static final String DLLHOME = "D:\\Project\\PSoC\\code\\SeeTestAutomation\\Pearson.PSCAutomation.212App\\bin\\Debug";
	private static final String SEETEST = "D:\\Program Files (x86)\\Experitest\\SeeTest\\studio.exe";
	private static final int RUNCOUNT = 10;

	public static void main(String args[]) {
		//String testMethodName = ExecuteTestSetUtil.getTestCaseName("D:\\Project\\PSoC\\code\\WinPSCAutomation\\Pearson.PSCWinAutomation.K1App\\", "22472");
		/*Map<String, String> testCases = new HashMap<String, String>();
		try {
			testCases = ExecuteTestSetUtil.readInputFile("D:\\Input\\Input.xls");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Set<String> testCaseIds = testCases.keySet();
		int countOfRun = 0;
		for(String testCaseId:testCaseIds) {
			String testMethodName = testCases.get(testCaseId);
			countOfRun++;
			String status = ExecuteTestSetUtil.callCommandPrompt(
					  DLLHOME
					, MSTEST
					, TESTSETTINGS
					, testMethodName
					, DLLNAME);
			if(!status.equals("Passed")) {
				countOfRun++;
				status = ExecuteTestSetUtil.callCommandPrompt(
						  DLLHOME
						, MSTEST
						, TESTSETTINGS
						, testMethodName
						, DLLNAME);
			}
			if(countOfRun == RUNCOUNT) {
				try {
					ExecuteTestSetUtil.restartSeetest(SEETEST);
					countOfRun = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}*/
		try {
			ExecuteTestSetUtil.getTestCasesDetails("D:\\Project\\PSoC\\code\\SeeTestAutomation\\Pearson.PSCAutomation.212App");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
