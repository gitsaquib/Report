package com.pearson.psoc.util;

import java.util.Date;

public class TestResult {
	
	private String status;
	private String testCase;
	private String testSet;
	private Date date;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTestCase() {
		return testCase;
	}
	public void setTestCase(String testCase) {
		this.testCase = testCase;
	}
	public String getTestSet() {
		return testSet;
	}
	public void setTestSet(String testSet) {
		this.testSet = testSet;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	

}
