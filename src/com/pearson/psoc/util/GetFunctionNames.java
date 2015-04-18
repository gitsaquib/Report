package com.pearson.psoc.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

public class GetFunctionNames {
	
	public static void main(String args[]) throws IOException {
		GetFunctionNames functionNames = new GetFunctionNames();
		Map<String, String> lines = functionNames.find("22472", "D:\\Project\\PSoC\\code\\WinPSCAutomation\\Pearson.PSCWinAutomation.K1App\\BookBuilderTests.cs");
		System.out.println(lines);
	}
	
	public Map<String, String> find(String word, String file) throws IOException {
	    LineNumberReader rdr = new LineNumberReader(new FileReader(file));
	    Map<String, String> results = new HashMap<String, String>();
	    try {
	        String line = rdr.readLine();
	        while(line != null) {
		        if (line.indexOf(word) >= 0) {
		        	int lineOfFunction = rdr.getLineNumber();
		            //results.put(word, lineOfFunction+2);
		            return results;
		        }
		        line = rdr.readLine();
	        }
	    } finally {
	        rdr.close();
	    }
	    return null;
	}
	
	public String readLine(LineNumberReader rdr, int lineNumber) throws IOException {
		String line = "";
	    try {
	    	int lineNum = 1;
	        line = rdr.readLine();
	        while(lineNum != lineNumber) {
		        line = rdr.readLine();
	        }
	    } finally {
	        rdr.close();
	    }
	    return line;
	}

}
