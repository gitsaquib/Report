package com.pearson.psoc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectInputStream.GetField;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GenerateXlsReport {
	
	public static void main(String[] args) throws IOException {
		File file  = new File("D:\\SeetestXlsReport\\TestReport-04-17.xls");
		file.createNewFile();
		OutputStream out = new FileOutputStream(file);
		short columnIndex = 2;
    	short columnWidth = 10000;
    	File htmlsFolder = new File("D:\\Seetest Reports\\04-17\\Done");
    	
    	FilenameFilter fileNameFilter = new FilenameFilter() {
    		   
            @Override
            public boolean accept(File dir, String name) {
               if(name.lastIndexOf('.')>0)
               {
                  int lastIndex = name.lastIndexOf('.');
                  String str = name.substring(lastIndex);
                  if(str.equals(".html"))
                  {
                     return true;
                  }
               }
               return false;
            }
        };
        
    	File htmls[] = htmlsFolder.listFiles(fileNameFilter);
    	boolean headerCreated = false;
    	HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		sheet.setColumnWidth(columnIndex, columnWidth);
    	sheet.setColumnWidth(Short.valueOf("4"), Short.valueOf("2500"));
    	sheet.setColumnWidth(Short.valueOf("5"), Short.valueOf("2500"));
    	wb.setSheetName(0, "TestReport");
    	int rowNum = 1;
    	
    	for(File html:htmls) {
			Document doc = Jsoup.parse(html, "UTF-8");
			Elements tableElements = doc.select("table");		
			
			Elements tableHeaderEles = tableElements.select("thead tr th");
	
			Elements tableRowElements = tableElements.select(":not(thead) tr");
	
			if(!headerCreated) {
	    		createSheetHeader(sheet, wb, tableHeaderEles);
	    		headerCreated = true;
	    	}
	    	rowNum = createSheetData(sheet, wb, tableRowElements, rowNum);
    	}
    	wb.write(out);
    	out.close();
	}
	
	private static void createSheetHeader(HSSFSheet sheet, HSSFWorkbook wb, Elements tableHeaderEles) {
		HSSFRow row = sheet.createRow(0);
		
		HSSFFont font = wb.createFont();
		font.setFontName("Veranda");
		HSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.AQUA.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setFont(font);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        
        short cellNum = 0;
		
		HSSFCell cell = null;
		cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue("S.no");
		cellNum++;
		for (int i = 0; i < tableHeaderEles.size(); i++) {
			cell = row.createCell(cellNum);
			cell.setCellStyle(style);
			if(tableHeaderEles.get(i).text().equals("#")) {
				cell.setCellValue("TC#");
			} else {
				cell.setCellValue(tableHeaderEles.get(i).text());
			}
			cellNum++;
		}
	}
	
	private static int createSheetData(HSSFSheet sheet, HSSFWorkbook wb, Elements tableRowElements, int rowNum) {
		HSSFFont font = wb.createFont();
		font.setFontName("Veranda");
		HSSFCellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setWrapText(true);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        
        Map<String, String> testCasesMap = new HashMap<String, String>();
        
        for (int i = 0; i < tableRowElements.size(); i++) {
        	HSSFRow row = sheet.createRow(rowNum);
			Element eRow = tableRowElements.get(i);
			Elements rowItems = eRow.select("td");
			short cellNum = 0;
			HSSFCell cell = null;
			
			if(rowItems.size() > 4) {
				String cellText = rowItems.get(1).text();
				int firstIndex = cellText.indexOf(":");
				String testCaseId = cellText.substring(0, firstIndex);
				String testCaseDesc = cellText.substring(firstIndex+1);
				String testCaseStatus = rowItems.get(2).text();
				String testCaseExecutionStartDate = rowItems.get(3).text();
				String testCaseRunDuration = rowItems.get(4).text();
				
				int testCaseToBeAdded = testCaseShouldBeAdded(testCasesMap, testCaseId);
				if(testCaseToBeAdded == -1) {
					
					rowNum = addRow(rowNum, style, testCasesMap, row, cellNum,
							testCaseId, testCaseDesc, testCaseStatus,
							testCaseExecutionStartDate, testCaseRunDuration);
				} else if(testCaseToBeAdded > 0) {
					sheet.removeRow(sheet.getRow(testCaseToBeAdded));
					rowNum = addRow(rowNum, style, testCasesMap, row, cellNum,
							testCaseId, testCaseDesc, testCaseStatus,
							testCaseExecutionStartDate, testCaseRunDuration);
					
				}
			}
		}
        return rowNum;
	}

	private static int addRow(int rowNum, HSSFCellStyle style,
			Map<String, String> testCasesMap, HSSFRow row, short cellNum,
			String testCaseId, String testCaseDesc, String testCaseStatus,
			String testCaseExecutionStartDate, String testCaseRunDuration) {
		HSSFCell cell;
		testCasesMap.put(testCaseId, testCaseStatus+":"+rowNum);

		cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue(rowNum);
		cellNum++;
		
		cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue(testCaseId);
		cellNum++;
		
		cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue(testCaseDesc);
		cellNum++;
		
		cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue(testCaseStatus);
		cellNum++;
		
		cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue(testCaseExecutionStartDate);
		cellNum++;
		
		cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		cell.setCellValue(testCaseRunDuration);
		cellNum++;
		
		rowNum++;
		return rowNum;
	}
	
	private static int testCaseShouldBeAdded(Map<String, String> testCasesMap, String testCaseId) {
		String statusFromMap = testCasesMap.get(testCaseId);
		if(null != statusFromMap) {
			String status = statusFromMap.split(":")[0];
			if(status.equalsIgnoreCase("Passed")) {
				return 0;
			} else {
				testCasesMap.remove(testCaseId);
				return Integer.parseInt(statusFromMap.split(":")[1]);
			}
		} else {
			return -1;
		}
	}
}