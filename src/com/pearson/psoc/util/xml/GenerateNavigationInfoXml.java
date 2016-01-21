package com.pearson.psoc.util.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GenerateNavigationInfoXml {

	public static void main(String[] args) throws IOException {
		readInputFile("D:\\Weekend Execution\\Wrapper Input\\21-Oct\\");
	}
	
	public static void readInputFile(String rootFolderPath) throws FileNotFoundException {
		File rootFolder = new File(rootFolderPath);
		
		Scanner elaScanner = new Scanner(new FileReader(rootFolder.getAbsolutePath()+File.separator+"ELA.txt"));
		StringBuffer sb = new StringBuffer();
		sb.append("<SubjectInfo SubjectName=\"ELA\">");
		sb.append("\n");
		while (elaScanner.hasNextLine()){
        	String words[] = elaScanner.nextLine().split("\t");
        	sb.append("\t<TaskInfo TaskName=\""+words[0]+"\">");
        	sb.append("\n\t\t"); 
        	if(words.length <= 1 || null == words[1]) {
        		sb.append("<Grade></Grade>");
        	} else {
        		sb.append("<Grade>"+words[1]+"</Grade>");
        	}
        	sb.append("\n\t\t");
        	if(words.length <= 2 || null == words[2]) {
        		sb.append("<Unit></Unit>");
        	} else {
        		sb.append("<Unit>"+words[2]+"</Unit>");
        	}
        	sb.append("\n\t\t");
        	if(words.length <= 3 || null == words[3]) {
        		sb.append("<Lesson></Lesson>");
        	} else {
        		sb.append("<Lesson>"+words[3]+"</Lesson>");
        	}
        	sb.append("\n\t\t");
        	if(words.length <= 4 || null == words[4]) {
        		sb.append("<TaskNumber></TaskNumber>");
        	} else {
        		sb.append("<TaskNumber>"+words[4]+"</TaskNumber>");
        	}
        	sb.append("\n\t\t");
        	if(words.length <= 5 || null == words[5]) {
        		sb.append("<Title></Title>");
        	} else {
        		sb.append("<Title>"+words[5]+"</Title>");
        	}
        	sb.append("\n\t\t");
        	if(words.length <= 6 || null == words[6]) {
        		sb.append("<AdditionalInfo></AdditionalInfo>");
        	} else {
        		sb.append("<AdditionalInfo>"+words[6]+"</AdditionalInfo>");
        	}
        	sb.append("\n\t");
        	sb.append("</TaskInfo>");
        	sb.append("\n");
		}
		sb.append("</SubjectInfo>");
		
		Scanner mathScanner = new Scanner(new FileReader(rootFolder.getAbsolutePath()+File.separator+"Math.txt"));
		sb.append("\n<SubjectInfo SubjectName=\"Math\">");
		sb.append("\n");
		while (mathScanner.hasNextLine()){
        	String words[] = mathScanner.nextLine().split("\t");
        	sb.append("\t<TaskInfo TaskName=\""+words[0]+"\">");
        	sb.append("\n\t\t"); 
        	if(words.length <= 1 || null == words[1]) {
        		sb.append("<Grade></Grade>");
        	} else {
        		sb.append("<Grade>"+words[1]+"</Grade>");
        	}
        	sb.append("\n\t\t");
        	if(words.length <= 2 || null == words[2]) {
        		sb.append("<Unit></Unit>");
        	} else {
        		sb.append("<Unit>"+words[2]+"</Unit>");
        	}
        	sb.append("\n\t\t");
        	if(words.length <= 3 || null == words[3]) {
        		sb.append("<Lesson></Lesson>");
        	} else {
        		sb.append("<Lesson>"+words[3]+"</Lesson>");
        	}
        	sb.append("\n\t\t");
        	if(words.length <= 4 || null == words[4]) {
        		sb.append("<TaskNumber></TaskNumber>");
        	} else {
        		sb.append("<TaskNumber>"+words[4]+"</TaskNumber>");
        	}
        	sb.append("\n\t\t");
        	if(words.length <= 5 || null == words[5]) {
        		sb.append("<Title></Title>");
        	} else {
        		sb.append("<Title>"+words[5]+"</Title>");
        	}
        	sb.append("\n\t\t");
        	if(words.length <= 6 || null == words[6]) {
        		sb.append("<AdditionalInfo></AdditionalInfo>");
        	} else {
        		sb.append("<AdditionalInfo>"+words[6]+"</AdditionalInfo>");
        	}
        	sb.append("\n\t");
        	sb.append("</TaskInfo>");
        	sb.append("\n");
		}
		sb.append("</SubjectInfo>");
		System.out.println(sb);
	}
}
