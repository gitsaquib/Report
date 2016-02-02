package com.pearson.psoc.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ExecuteTestSet {

	public static void main(String args[]) throws Exception {
		Configuration configuration = ExecuteTestSetUtil.readConfigFile();
		if(null != configuration) {
			
			Map<String, String> testCases = new LinkedHashMap<String, String>();
			try {
				testCases = ExecuteTestSetUtil.readInputFile(configuration.getInputFile());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Set<String> testCaseIds = testCases.keySet();
			int countOfRun = 0;
			for(String testCaseId:testCaseIds) {
				String testMethodName = testCases.get(testCaseId);
				countOfRun++;
				Thread.sleep(15000);
				String status = executeCommand(testMethodName, configuration);
				if(!status.equals("Passed")) {
					countOfRun++;
					status = executeCommand(testMethodName, configuration);
					System.out.println(testCaseId+"\t"+status+"\t"+testMethodName);
				} else {
					System.out.println(testCaseId+"\t"+status+"\t"+testMethodName);
				}
				
				if(configuration.getRestartSeetest().equalsIgnoreCase("true")) 
				{
					if(countOfRun == Integer.parseInt(configuration.getRunCount())) {
						try {
							ExecuteTestSetUtil.restartSeetest(configuration.getSeeTest());
							countOfRun = 0;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		/*try {
			ExecuteTestSetUtil.getTestCasesDetails("D:\\Project\\PSoC\\code\\Win\\WinPSCAutomation\\Pearson.PSCWinAutomation.212App");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		/*try {
			ExecuteTestSetUtil.getLoginDetails("D:\\Project\\PSoC\\code\\SeeTestAutomation\\Pearson.PSCAutomation.212App");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	private static String executeCommand(String testMethodName, Configuration configuration) {
		String status = ExecuteTestSetUtil.callCommandPrompt(
				configuration.getDllHome()
				, configuration.getMsTest()
				, configuration.getTestSettings()
				, testMethodName
				, configuration.getDllName());
		return status;
	}
}
