package com.pearson.psoc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ExecuteTestSetUtil {
	
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
	
	public static String find(String rootFolder, String word) throws IOException {
		
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

}
