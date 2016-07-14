package com.pearson.psoc.util;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFReader {

	public static void main(String args[]) throws IOException {
		readPDF();
	}
	
	private static void readPDF() throws IOException {
		PDDocument document = PDDocument.load(new File("D:\\Documents\\Invoices\\inv-9103007947.PDF"));
		PDFTextStripper s = new PDFTextStripper();
		s.setLineSeparator("|");
		String content = s.getText(document);
		System.out.println(content);
	}
}
