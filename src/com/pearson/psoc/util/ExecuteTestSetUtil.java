package com.pearson.psoc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExecuteTestSetUtil {
	
	private static final String TASKLIST = "tasklist";
	private static final String KILL = "taskkill /F /IM ";
	
	public static String callCommandPrompt(String debugFolderPath, String msTestExePath, String testSettingsPath, String testMethodName, String dllName) {
		String status = "Inconclusive";
		Runtime rt = Runtime.getRuntime();
		System.out.println(new File("test.bat").getAbsolutePath());
		String[] commands = {"test.bat", debugFolderPath, msTestExePath, testSettingsPath, testMethodName, dllName};
		Process proc = null;
		try {
			proc = rt.exec(commands);
		} catch (IOException e) {
			System.out.println("1. Error while executing test: "+testMethodName);
		}
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String s = null;
		int lineNum = 1;
		try {
			while ((s = stdInput.readLine()) != null) {
				if(lineNum == 14) {
					break;
				}
				lineNum++;
			}
		} catch (IOException e) {
			System.out.println("2. Error while executing test: "+testMethodName);
		}
		if(s != null) {
			if(s.startsWith("Pass")) {
				status = "Passed";
			} else if(s.startsWith("Fail")) {
				status = "Failed";
			}
		}
		return status;
	}
	
	public static String getTestCaseName(String rootFolder, String word) throws IOException {
		
		File classFilesFolder = new File(rootFolder);
    	
    	FilenameFilter fileNameFilter = new FilenameFilter() {
    		   
            @Override
            public boolean accept(File dir, String name) {
               if(name.lastIndexOf('.')>0)
               {
                  int lastIndex = name.lastIndexOf('.');
                  String str = name.substring(lastIndex);
                  if(str.equals(".cs"))
                  {
                     return true;
                  }
               }
               return false;
            }
        };
        
    	File classFiles[] = classFilesFolder.listFiles(fileNameFilter);
		
    	for (File classFile:classFiles) {
		    LineNumberReader rdr = new LineNumberReader(new FileReader(classFile));
		    try {
		        String line = rdr.readLine();
		        while(line != null) {
			        if (line.indexOf(word) >= 0) {
			        	int lineOfWorkItem = rdr.getLineNumber();
			        	return getLine(rdr, lineOfWorkItem+2);
			        }
			        line = rdr.readLine();
		        }
		    } finally {
		        rdr.close();
		    }
    	}
	    return null;
	}
	
	public static String getLine(LineNumberReader rdr, int lineNumber) throws IOException {
		String line = "";
	    try {
	        line = rdr.readLine();
	        line = rdr.readLine();
	        if(null != line) {
	        	line = line.replace("public void", "").trim().replace("()", "");
	        }
	    } finally {
	        rdr.close();
	    }
	    return line;
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
		Process process = new ProcessBuilder(serviceName).start();
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}
	
	public static Map<String, String> readInputFile(String inputSheet) throws IOException {
		Map<String, String> testCases = new HashMap<String, String>();
		File myFile = new File(inputSheet);
        FileInputStream fis = new FileInputStream(myFile);
        HSSFWorkbook myWorkBook = new HSSFWorkbook (fis);
        HSSFSheet mySheet = myWorkBook.getSheetAt(0);
        Iterator<HSSFRow> rowIterator = mySheet.rowIterator();
        while (rowIterator.hasNext()) {
        	HSSFRow row = rowIterator.next();
        	testCases.put(row.getCell(Short.parseShort("0")).getStringCellValue(), row.getCell(Short.parseShort("1")).getStringCellValue());
        }
        return testCases;
	}

}
