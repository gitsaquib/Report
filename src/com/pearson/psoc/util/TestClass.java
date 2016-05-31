package com.pearson.psoc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.jsoup.Jsoup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.request.UpdateRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.response.UpdateResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

public class TestClass {
    public static void main(String[] args) throws URISyntaxException, IOException, ParseException {

    	//2-12: 21028059357
        //K1: 23240411122
    	//Content: 18146085650
    	
    	RallyRestApi restApi = loginRally();
    	//exportTestCases(restApi);
    	//retrieveSteps(restApi, "D:\\input\\");
    	//retrieveTestCaseMethodDetails(restApi, "D:\\Assessment TC Analysis\\");
    	//clearTestSet(restApi, "TS1058");
    	//readTrxFile(restApi, "D:\\Regression\\trx\\");
    	//createTestSet(restApi, "TS1064", "D:\\Regression");
    	//updateTestCaseResults(restApi, "1.6.0.2477", "mohammed.saquib@pearson.com", "D:\\Regression\\08192015\\212iOS\\");
    	//updateTestCaseResults(restApi, "1.6.0.2364", "madhav.purohit@pearson.com", "D:\\Regression\\08192015\\212Win\\");
    	//updateTestCaseResults(restApi, "1.6.0.2477", "godwin.terence@pearson.com", "D:\\Regression\\08192015\\212Win\\");
    	//updateTestCaseResults(restApi, "1.6.0.2390", "mohammed.saquib@pearson.com", "D:\\Regression\\08192015\\212Win\\");
    	//updateTestCaseResults(restApi, "1.6.0.2364", "silky.manocha@pearson.com", "D:\\Regression\\08192015\\212Win\\");
    	//updateTestCaseResults(restApi, "1.6.0.1931", "varun-b2@hcl.com", "D:\\Regression\\08192015\\K1Win\\");
    	//updateTestCaseResults(restApi, "1.6.0.1962", "lakshmi.brunda@pearson.com", "D:\\Regression\\08192015\\K1iOS\\");
    	//updateTestCaseResults(restApi, "F-K1-iOS-Test-Harness-1.6.0.4585", "mohammed.saquib@pearson.com", "D:\\Regression\\08192015\\K1iOS\\");
    	//updateTestSet(restApi);
    	//retrieveTestSets(restApi, "TS1056,TS1058,TS1061,TS1062", "21028059357");
    	//retrieveTestSets(restApi, "TS1065,TS1066", "23240411122");
    	//retrieveTestSets(restApi, "TS1063", "31962999784");
    	//retrieveTestSetsResult(restApi, "TS846");
    	//retrieveTestCases(restApi);
    	//retrieveDefects(restApi);
    	//populateTestFolder(restApi, "23240411122", "TF1213", "D:\\Regression");
    	//retrieveTestResults(restApi, "TS1002", true);
    	//getIteration(restApi, "21028059357");
    	//getIteration(restApi, "23240411122");
    	//getUserStory(restApi, "US9167,US9168,US9738".split(","));
    	//getTestDetails(restApi, "TC19380".split(","));
    	//getContentTestCases(restApi, "21028059357");
    	retrieveTestFolder(restApi, "21028059357", "TF739, TF740, TF741, TF742, TF743, TF744, TF746, TF747, TF748, TF749, TF750, TF751, TF752, TF753, TF754");
    	restApi.close();
    	//postJenkinsJob();
    }
    
    private static void retrieveTestCaseMethodDetails(RallyRestApi restApi, String rootFolder) throws IOException {
    	File inFolder = new File(rootFolder+File.separator+"in"+File.separator);
    	FilenameFilter fileNameFilter = new FilenameFilter() {
 		   
            @Override
            public boolean accept(File dir, String name) {
               if(name.lastIndexOf('.')>0)
               {
                  int lastIndex = name.lastIndexOf('.');
                  String str = name.substring(lastIndex);
                  if(str.equals(".txt"))
                  {
                     return true;
                  }
               }
               return false;
            }
        };
    	File files[] = inFolder.listFiles(fileNameFilter);
    	for(File file:files) {
    		Scanner sc=new Scanner(new FileReader(file));
    		while (sc.hasNextLine()){
	        	String testCaseId = sc.nextLine();
        		retrieveTestCase(restApi, testCaseId);
	        }
	        sc.close();
	        file.renameTo(new File(file.getAbsolutePath().replace("txt", "done")));
    	}

    }
    
    private static void clearTestSet(RallyRestApi restApi, String testSetId) throws IOException {
    	QueryRequest testSetRequest = new QueryRequest("TestSet");
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
        
        testSetRequest.setQueryFilter(new QueryFilter("FormattedID", "=", testSetId));
        QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
        JsonArray testSetArray = testSetQueryResponse.getResults();
    	JsonElement elements =  testSetArray.get(0);
		JsonObject object = elements.getAsJsonObject();
        String ref = object.get("_ref").getAsString();
        ref = ref.substring(ref.indexOf("/testset/")).replace(".js", "");
    	object.add("TestCases", new JsonArray());
    	UpdateRequest request = new UpdateRequest(ref, object);
        UpdateResponse response = restApi.update(request);
        System.out.println(response);
    }
    
    private static void populateTestFolder(RallyRestApi restApi, String project, String testFolderId, String rootFolder) throws IOException, URISyntaxException {
    	File inFolder = new File(rootFolder+File.separator+"in"+File.separator);
    	FilenameFilter fileNameFilter = new FilenameFilter() {
 		   
            @Override
            public boolean accept(File dir, String name) {
               if(name.lastIndexOf('.')>0)
               {
                  int lastIndex = name.lastIndexOf('.');
                  String str = name.substring(lastIndex);
                  if(str.equals(".txt"))
                  {
                     return true;
                  }
               }
               return false;
            }
        };
    	File files[] = inFolder.listFiles(fileNameFilter);
    	for(File file:files) {
    		Scanner sc=new Scanner(new FileReader(file));
    		String[] testCases = new String[50];
    		int count = 0;
    		while (sc.hasNextLine()){
	        	String words[] = sc.nextLine().split("\t");
        		addTestCaseToTestFolder(restApi, project, testFolderId, words[0]);
	        }
	        sc.close();
	        file.renameTo(new File(file.getAbsolutePath().replace("txt", "done")));
    	}
    }
    
    private static void createTestSet(RallyRestApi restApi, String testSetId, String rootFolder) throws IOException {
    	File inFolder = new File(rootFolder+File.separator+"in"+File.separator);
    	FilenameFilter fileNameFilter = new FilenameFilter() {
 		   
            @Override
            public boolean accept(File dir, String name) {
               if(name.lastIndexOf('.')>0)
               {
                  int lastIndex = name.lastIndexOf('.');
                  String str = name.substring(lastIndex);
                  if(str.equals(".txt"))
                  {
                     return true;
                  }
               }
               return false;
            }
        };
    	File files[] = inFolder.listFiles(fileNameFilter);
    	for(File file:files) {
    		Scanner sc=new Scanner(new FileReader(file));
    		String[] testCases = new String[50];
    		int count = 0;
    		while (sc.hasNextLine()){
	        	String words[] = sc.nextLine().split("\t");
	        	testCases[count] = words[0];
	        	count++;
	        	if(!sc.hasNextLine() || count == 50) {
	        		updateTestSet(restApi, testSetId, testCases);
	        		count = 0;
	        	}
	        }
	        sc.close();
	        file.renameTo(new File(file.getAbsolutePath().replace("txt", "done")));
    	}
    }
    
    private static void updateTestCaseResults(RallyRestApi restApi, String buildNumber, String userName, String path) throws IOException {
    	File inFolder = new File(path+"in\\");
    	FilenameFilter fileNameFilter = new FilenameFilter() {
 		   
            @Override
            public boolean accept(File dir, String name) {
               if(name.lastIndexOf('.')>0)
               {
                  int lastIndex = name.lastIndexOf('.');
                  String str = name.substring(lastIndex);
                  if(str.equals(".txt"))
                  {
                     return true;
                  }
               }
               return false;
            }
        };
        Date curDate = new Date();
    	File files[] = inFolder.listFiles(fileNameFilter);
    	for(File file:files) {
    		Scanner sc=new Scanner(new FileReader(file));
	        while (sc.hasNextLine()){
	        	String words[] = sc.nextLine().split("\t");
	        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"+".000Z");
	    		String dateToStr = format.format(curDate);
	            updateTestCase(restApi, words[0], words[1], words[2], dateToStr, buildNumber, userName);
	        }
	        sc.close();
	        file.renameTo(new File(path+"done\\"+file.getName()));
    	}
    	
    }
    
    
    private static void readTrxFile(RallyRestApi restApi, String path) throws IOException {
    	File inFolder = new File(path+"in\\");
    	FilenameFilter fileNameFilter = new FilenameFilter() {
 		   
            @Override
            public boolean accept(File dir, String name) {
               if(name.lastIndexOf('.')>0)
               {
                  int lastIndex = name.lastIndexOf('.');
                  String str = name.substring(lastIndex);
                  if(str.equals(".txt"))
                  {
                     return true;
                  }
               }
               return false;
            }
        };
        Date curDate = new Date();
    	File files[] = inFolder.listFiles(fileNameFilter);
    	for(File file:files) {
    		Scanner sc=new Scanner(new FileReader(file));
	        while (sc.hasNextLine()){
	        	String line = sc.nextLine();
	        	String testCases = "";
	        	String status = "";
	        	if(line.contains("Passed")) {
	        		testCases = line.substring(0, line.indexOf("Passed")).trim();
	        		status = "Pass";
	        	} else if(line.contains("Failed")) {
	        		testCases = line.substring(0, line.indexOf("Failed")).trim();
	        		status = "Fail";
	        	} else {
	        		continue;
	        	}
	        	String testCaseIds[] = testCases.split(",");
	        	for(String testCaseId:testCaseIds) {
	        		System.out.println("TC"+testCaseId + "\t" + status);
	        	}
	        	/*String words[] = sc.nextLine().split("\t");
	        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"+".000Z");
	    		String dateToStr = format.format(curDate);
	            updateTestCase(restApi, words[0], words[1], words[2], dateToStr, buildNumber, userName);*/
	        }
	        sc.close();
	        file.renameTo(new File(path+"done\\"+file.getName()));
    	}
    	
    }
    private static void updateTestSet(RallyRestApi restApi, String testSetId, String testCaseIds[]) throws IOException {
    	QueryRequest testCaseRequest = new QueryRequest("TestCase");
        testCaseRequest.setFetch(new Fetch("FormattedID","Name"));
        
        
        QueryFilter query = null;

        for(String testCaseId:testCaseIds) {
        	 if(null != query) {
        		 query = query.or(new QueryFilter("FormattedID", "=", testCaseId));
             } else {
            	 query = new QueryFilter("FormattedID", "=", testCaseId);
             }
        }
        
        testCaseRequest.setQueryFilter(query);
        QueryResponse testCaseQueryResponse = restApi.query(testCaseRequest);
        JsonArray testCases = new JsonArray();
        JsonArray testCasesArray = testCaseQueryResponse.getResults();
        
        for(int i=0; i<testCasesArray.size(); i++) {
    		JsonElement elements =  testCasesArray.get(i);
    		JsonObject object = elements.getAsJsonObject();
    		testCases.add(object);
    	}
        
        QueryRequest testSetRequest = new QueryRequest("TestSet");
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
        
        testSetRequest.setQueryFilter(new QueryFilter("FormattedID", "=", testSetId));
        QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
        JsonArray testSetArray = testSetQueryResponse.getResults();
        
        for(int i=0; i<testSetArray.size(); i++) {
    		JsonElement elements =  testSetArray.get(i);
    		JsonObject object = elements.getAsJsonObject();
            String ref = object.get("_ref").getAsString();
            ref = ref.substring(ref.indexOf("/testset/")).replace(".js", "");
            JsonArray exTCs = object.get("TestCases").getAsJsonArray();
            exTCs.addAll(testCases);
            object.add("TestCases", exTCs);
            UpdateRequest request = new UpdateRequest(ref, object);
            UpdateResponse response = restApi.update(request);
            System.out.println(response);
    	}
    }
    
    private static void getUserStory(RallyRestApi restApi, String[] userStories) throws IOException, ParseException {
    	QueryRequest storyRequest = new QueryRequest("HierarchicalRequirement");
        storyRequest.setLimit(1000);
        storyRequest.setScopedDown(true);
        storyRequest.setFetch(new Fetch("FormattedID","Name", "TestCases", "Priority", "Method", "Type"));
        QueryFilter queryFilter = null;
        for(String userStory:userStories) {
	        if(queryFilter == null) {
				queryFilter = new QueryFilter("FormattedID", "=", userStory);
			} else {
				queryFilter = queryFilter.or(new QueryFilter("FormattedID", "=", userStory));
			}
        }
        storyRequest.setQueryFilter(queryFilter);
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
        QueryResponse testSetQueryResponse = restApi.query(storyRequest);
        int ij=1;
        for (int i=0; i<testSetQueryResponse.getResults().size();i++){
            JsonObject testSetJsonObject = testSetQueryResponse.getResults().get(i).getAsJsonObject();
            int numberOfTestCases = testSetJsonObject.get("TestCases").getAsJsonArray().size();
            String userStoryId = testSetJsonObject.get("FormattedID").getAsString();
            String userStoryName = testSetJsonObject.get("Name").getAsString();
            if(numberOfTestCases>0){
                  for (int j=0;j<numberOfTestCases;j++){
            	  	JsonObject jsonObject = testSetJsonObject.get("TestCases").getAsJsonArray().get(j).getAsJsonObject();
            	  	System.out.println((ij)+"\t"+userStoryId+"\t"+ jsonObject.get("FormattedID")+"\t"+jsonObject.get("Name")+"\t"+userStoryName+"\t"+jsonObject.get("Priority")+"\t" + jsonObject.get("Method")+"\t" + jsonObject.get("Type"));
            	  	ij++;
                 }
            }
        }
        
    }
    
    private static void getIteration(RallyRestApi restApi, String projectId) throws IOException, ParseException {
    	QueryRequest iterationRequest = new QueryRequest("Iteration");
    	iterationRequest.setProject("/project/"+projectId);
    	iterationRequest.setLimit(2000);
        QueryResponse iterationResponse = restApi.query(iterationRequest);
        JsonArray iterationArray = iterationResponse.getResults();
        QueryFilter queryFilter = null;
        for(int i=0; i<iterationArray.size(); i++) {
    		JsonElement elements =  iterationArray.get(i);
    		JsonObject object = elements.getAsJsonObject();
    		String ref = object.get("_ref").getAsString();
    		ref = ref.substring(ref.indexOf("/iteration/"));
    		ref = ref.replace(".js", "");
    		DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
    		String strStartDate = object.get("StartDate").getAsString().substring(0, 10);
            Date startDate = (Date) formatter1.parse(strStartDate);
            String strEndDate = object.get("EndDate").getAsString().substring(0, 10);
            Date endDate = (Date) formatter1.parse(strEndDate);
    		Date dateNow = new Date();
    		if(dateNow.after(startDate) && dateNow.before(endDate)) {
    			if(queryFilter == null) {
    				queryFilter = new QueryFilter("Iteration", "=", ref);
    			} else {
    				queryFilter = queryFilter.or(new QueryFilter("Iteration", "=", ref));
    			}
    		}
    	}
        if(queryFilter != null) {
	        QueryRequest testSetRequest = new QueryRequest("TestSet");
	        testSetRequest.setQueryFilter(queryFilter);
	        String wsapiVersion = "1.43";
	        restApi.setWsapiVersion(wsapiVersion);
	        testSetRequest.setProject("/project/"+projectId);
	        testSetRequest.setScopedDown(true);
	    	QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
	        List<String> iosTestSetList = new ArrayList<String>();
	        List<String> winTestSetList = new ArrayList<String>();
	        for (int i=0; i<testSetQueryResponse.getResults().size();i++){
	            JsonObject testSetJsonObject = testSetQueryResponse.getResults().get(i).getAsJsonObject();
	            String testSetId = testSetJsonObject.get("FormattedID").getAsString();
	            String testSetName = testSetJsonObject.get("Name").getAsString();
	            if(testSetName.toUpperCase().contains("EOS")) {
	            	if(testSetName.toUpperCase().contains("WIN")) {
	            		winTestSetList.add(testSetId);
	            	} else {
	            		iosTestSetList.add(testSetId);
	            	}
	            }
	            
	        }
	        System.out.println(iosTestSetList);
	        System.out.println(winTestSetList);
        }
    }
    
    private static void updateTestCase(RallyRestApi restApi, String testSet, String testCase, String status, String date, String build, String userId) throws IOException {
    	
    	QueryRequest userRequest = new QueryRequest("User");
        userRequest.setFetch(new Fetch("UserName", "Subscription", "DisplayName", "SubscriptionAdmin"));
        userRequest.setQueryFilter(new QueryFilter("UserName", "=", userId));
        QueryResponse userQueryResponse = restApi.query(userRequest);
        JsonArray userQueryResults = userQueryResponse.getResults();
        JsonElement userQueryElement = userQueryResults.get(0);
        JsonObject userQueryObject = userQueryElement.getAsJsonObject();
        String userRef = userQueryObject.get("_ref").getAsString();  
        
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
        
        QueryRequest testSetRequest = new QueryRequest("TestSet");
        testSetRequest.setQueryFilter(new QueryFilter("FormattedID", "=", testSet));
        QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
        String testSetRef = testSetQueryResponse.getResults().get(0).getAsJsonObject().get("_ref").getAsString(); 
        
        String testCaseRef = "";
        QueryRequest testCaseRequest = new QueryRequest("TestCase");
        testCaseRequest.setFetch(new Fetch("FormattedID","Name"));
        testCaseRequest.setQueryFilter(new QueryFilter("FormattedID", "=", testCase));
        QueryResponse testCaseQueryResponse = restApi.query(testCaseRequest);
        if(null != testCaseQueryResponse.getResults() && testCaseQueryResponse.getResults().size() > 0) {
        	testCaseRef = testCaseQueryResponse.getResults().get(0).getAsJsonObject().get("_ref").getAsString(); 
	        
	        if(null != testCaseRef && !testCaseRef.equals("")){
		        JsonObject newTestCaseResult = new JsonObject();
		        newTestCaseResult.addProperty("Verdict", status);
		        newTestCaseResult.addProperty("Date", date);
		        newTestCaseResult.addProperty("Build", build);
		        newTestCaseResult.addProperty("TestCase", testCaseRef);
		        newTestCaseResult.addProperty("Tester", userRef);
		        newTestCaseResult.addProperty("TestSet", testSetRef);
		        
		        CreateRequest createRequest = new CreateRequest("testcaseresult", newTestCaseResult);
		        CreateResponse createResponse = restApi.create(createRequest);  
		        if (createResponse.wasSuccessful()) {
		            System.out.println(testCase +"\t"+createResponse.getObject().get("_ref").getAsString());          
		        } else {
		            System.out.println("Error occurred creating Test Case Result: "+createResponse.getErrors());
		        }
	        } else {
	        	System.out.println("Test Case doesn't exist");
	        }
        } else {
        	System.out.println("Test Case doesn't exist");
        }
    }
    
    private static void retrieveTestSets(RallyRestApi restApi, String testSetsString, String projectId)
			throws IOException, URISyntaxException, ParseException {
    	List<TestResult> testResults =  retrieveTestResults(restApi, testSetsString, false);
        QueryRequest testSetRequest = new QueryRequest("TestSet");
        testSetRequest.setProject("/project/"+projectId);
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);

        testSetRequest.setFetch(new Fetch(new String[] {"Name","WorkProduct",  "Iteration", "Description", "TestCases", "Results", "FormattedID", "LastVerdict", "LastBuild", "LastRun", "Priority", "Method"}));

        String[] testSets = testSetsString.split(",");
        QueryFilter query = new QueryFilter("FormattedID", "=", "TS0");
        for(String testSet:testSets) {
        	query = query.or(new QueryFilter("FormattedID", "=", testSet));
        }
	        
        testSetRequest.setQueryFilter(query);
        QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
        int ij=1;
        for (int i=0; i<testSetQueryResponse.getResults().size();i++){
            JsonObject testSetJsonObject = testSetQueryResponse.getResults().get(i).getAsJsonObject();
            int numberOfTestCases = testSetJsonObject.get("TestCases").getAsJsonArray().size();
            String testSetId = testSetJsonObject.get("FormattedID").getAsString();
            String testSetName = testSetJsonObject.get("Name").getAsString();
            if(numberOfTestCases>0){
            	boolean executed = false;
                for (int j=0;j<numberOfTestCases;j++){
                	JsonObject jsonObject = testSetJsonObject.get("TestCases").getAsJsonArray().get(j).getAsJsonObject();
                	String userStory = "";
               	  	if(null != jsonObject.get("WorkProduct") && !jsonObject.get("WorkProduct").isJsonNull()) {
               	  		JsonObject workProductObj = jsonObject.get("WorkProduct").getAsJsonObject();
               	  		userStory = workProductObj.get("FormattedID").getAsString()+": "+workProductObj.get("Name").getAsString();
               	  	}
               	  	String testCaseId = jsonObject.get("FormattedID").getAsString();
               	  	for(TestResult result:testResults) {
               	  		if(result.getTestCase().equalsIgnoreCase(testCaseId)) {
               	  			executed = true;
               	  		} 
               	  	}
               	  	if(null != jsonObject.get("LastRun") && !jsonObject.get("LastRun").isJsonNull()) {
               	  		System.out.println((ij)+"\t"+testSetId+"\t"+testSetName+"\t"+ testCaseId +"\t" + executed +"\t" + jsonObject.get("LastVerdict")+"\t" + jsonObject.get("Name")+"\t" + jsonObject.get("Priority")+"\t" + userStory+"\t" + jsonObject.get("Method"));
               	  	} else {
               	  		System.out.println((ij)+"\t"+testSetId+"\t"+testSetName+"\t"+ testCaseId +"\t" + executed +"\t" + jsonObject.get("LastVerdict")+"\t" + jsonObject.get("Name")+"\t" + jsonObject.get("Priority")+"\t" + userStory+"\t" + jsonObject.get("Method"));
               	  	}
               	  	ij++;
               	  	executed = false;
                 }
            }
        }

	}
    
    private static void retrieveTestFolder(RallyRestApi restApi, String project, String testFolderIds) throws IOException, URISyntaxException {
		QueryFilter queryFilter = null;
		
		String testFolderIdArr[] = testFolderIds.split(",");
		for(String testFolderId:testFolderIdArr) {
			if(null != queryFilter) {
				queryFilter = queryFilter.or(new QueryFilter("FormattedID", "=", testFolderId));
			} else {
				queryFilter = new QueryFilter("FormattedID", "=", testFolderId);
			}
		}
		
    	QueryRequest defectRequest = new QueryRequest("TestFolder");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("FormattedID", "TestFolder", "TestCases", "Name", "WorkProduct", "Priority"));
    	defectRequest.setProject("/project/"+project); 
    	defectRequest.setScopedDown(true);
    	String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
    	QueryResponse testFolder = restApi.query(defectRequest);
        int ij = 1;
        for (int i=0; i<testFolder.getResults().size();i++){
            JsonObject testFolderJsonObject = testFolder.getResults().get(i).getAsJsonObject();
	        int numberOfTestCases = testFolderJsonObject.get("TestCases").getAsJsonArray().size();
	        if(numberOfTestCases>0){
	              for (int j=0;j<numberOfTestCases;j++){
	            	  	JsonObject jsonObject = testFolderJsonObject.get("TestCases").getAsJsonArray().get(j).getAsJsonObject();
	            	  	String userStory = "";
	               	  	if(null != jsonObject.get("WorkProduct") && !jsonObject.get("WorkProduct").isJsonNull()) {
	               	  		JsonObject workProductObj = jsonObject.get("WorkProduct").getAsJsonObject();
	               	  		userStory = workProductObj.get("FormattedID").getAsString()+": "+workProductObj.get("Name").getAsString();
	               	  	}
	               	  	
	            	  	System.out.println((ij)+"\t"+testFolderJsonObject.get("Name").getAsString()+ "\t"+ jsonObject.get("FormattedID") +"\t" + jsonObject.get("Name") +"\t" + userStory+"\t" + jsonObject.get("Priority"));
	            	  	ij++;
	             }
	        }
        }
    }
    
    private static void addTestCaseToTestFolder(RallyRestApi restApi, String project, String testFolderId, String testCaseId) throws IOException, URISyntaxException {
		QueryFilter queryFilter = new QueryFilter("FormattedID", "=", testFolderId);
    	QueryRequest defectRequest = new QueryRequest("TestFolder");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("FormattedID", "TestFolder"));
    	defectRequest.setProject("/project/"+project); 
    	defectRequest.setScopedDown(true);
    	QueryResponse testFolder = restApi.query(defectRequest);
    	String testFolderRef = testFolder.getResults().get(0).getAsJsonObject().get("_ref").getAsString(); 
        String ref = testFolderRef.substring(testFolderRef.indexOf("/testfolder/"));
        
        QueryRequest testCaseRequest = new QueryRequest("TestCase");
        testCaseRequest.setQueryFilter(new QueryFilter("FormattedID", "=", testCaseId));
        QueryResponse testCaseQueryResponse = restApi.query(testCaseRequest);
        JsonObject object = testCaseQueryResponse.getResults().get(0).getAsJsonObject();
        String testCaseRef  = object.get("_ref").getAsString();
        testCaseRef = testCaseRef.substring(testCaseRef.indexOf("/testcase/"));
        
        JsonObject tcUpdate = new JsonObject();
        tcUpdate.addProperty("Notes", "Added by automation team");
        tcUpdate.addProperty("Project", "/project/"+project);
        tcUpdate.addProperty("TestFolder", ref);
        UpdateRequest updateRequest = new UpdateRequest(testCaseRef, tcUpdate);
        UpdateResponse updateResponse = restApi.update(updateRequest);
        if (updateResponse.wasSuccessful()) {
            System.out.println("Successfully updated test case: " + testCaseId);
        } else {
        	System.out.println("Failed to update test case: " + testCaseId);
        }
    }
    
    private static void retrieveTestSetsResult(RallyRestApi restApi, String testSetsString)
			throws IOException, URISyntaxException, ParseException {

        QueryRequest testSetRequest = new QueryRequest("TestSet");
        
        testSetRequest.setProject("/project/21028059357"); //2-12
        //testSetRequest.setProject("/project/23240411122"); //K1
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);

        testSetRequest.setFetch(new Fetch(new String[] {"Name", "TestCases", "Results", "FormattedID", "LastVerdict", "LastBuild", "LastRun", "Priority", "Method"}));
        String[] testSets = testSetsString.split(",");
        QueryFilter queryFilter = new QueryFilter("TestSet.FormattedID", "=", testSets[0]);
        int q = 1;
        while(testSets.length > q) {
        	queryFilter = queryFilter.or(new QueryFilter("TestSet.FormattedID", "=", testSets[q]));
        	q++;
        }
        testSetRequest.setQueryFilter(queryFilter);
        QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
        int ij=1;
        for (int i=0; i<testSetQueryResponse.getResults().size();i++){
            JsonObject testSetJsonObject = testSetQueryResponse.getResults().get(i).getAsJsonObject();
            int numberOfTestCases = testSetJsonObject.get("TestCases").getAsJsonArray().size();
            if(numberOfTestCases>0){
                  for (int j=0;j<numberOfTestCases;j++){
                	  	JsonObject jsonObject = testSetJsonObject.get("TestCases").getAsJsonArray().get(j).getAsJsonObject();
                	  	JsonArray results = jsonObject.get("Results").getAsJsonArray();
                	  	String verdict = testSetResult(restApi, jsonObject.get("FormattedID").getAsString(), testSetJsonObject.get("FormattedID").getAsString());
                	  	if(verdict != null) {
                	  		System.out.println((ij)+"\t"+ jsonObject.get("FormattedID") +"\t" + verdict +"\t" + jsonObject.get("Name")+"\t" + jsonObject.get("Method"));
                	  	} else {
                	  		System.out.println((ij)+"\t"+ jsonObject.get("FormattedID") +"\t" + "" +"\t" + jsonObject.get("Name")+"\t" + jsonObject.get("Method"));
                	  	}
                	  	ij++;
                 }
            }
        }

	}
    
    private static String testSetResult(RallyRestApi restApi, String testCaseId, String testSetId) throws IOException {
    	QueryRequest testCaseResultsRequest = new QueryRequest("TestCaseResult");
        testCaseResultsRequest.setFetch(new Fetch("Build","TestCase","TestSet", "Verdict","FormattedID"));
        testCaseResultsRequest.setQueryFilter(new QueryFilter("TestCase.FormattedID", "=", testCaseId).and(
                new QueryFilter("TestSet.FormattedID", "=", testSetId)));
        QueryResponse testCaseResultResponse = restApi.query(testCaseResultsRequest);
        int numberTestCaseResults = testCaseResultResponse.getTotalResultCount();
        if(numberTestCaseResults >0)
            return testCaseResultResponse.getResults().get(0).getAsJsonObject().get("Verdict").getAsString();
        else
            return null;
    }
    
    private static List<TestResult> retrieveTestResults(RallyRestApi restApi, String testSetsString, boolean print) throws IOException, ParseException {
    	QueryRequest testCaseResultsRequest = new QueryRequest("TestCaseResult");
        testCaseResultsRequest.setFetch(new Fetch("Build","TestCase","TestSet", "Verdict","FormattedID", "Date"));
        String[] testSets = testSetsString.split(",");
        QueryFilter query = new QueryFilter("TestSet.FormattedID", "=", "TS0");
        for(String testSet:testSets) {
        	query = query.or(new QueryFilter("TestSet.FormattedID", "=", testSet));
        }
        testCaseResultsRequest.setLimit(3000);
        testCaseResultsRequest.setQueryFilter(query);
        QueryResponse testCaseResultResponse = restApi.query(testCaseResultsRequest);
        JsonArray array = testCaseResultResponse.getResults();
        int numberTestCaseResults = array.size();
        int pass = 0, fail = 0, inconclusive = 0, blocked = 0, error = 0;
        List<TestResult> testResults = new ArrayList<TestResult>();
        if(numberTestCaseResults >0) {
        	for(int i=0; i<numberTestCaseResults; i++) {
        		TestResult testResult = new TestResult();
        		String verdict = array.get(i).getAsJsonObject().get("Verdict").getAsString();
        		String build = array.get(i).getAsJsonObject().get("Build").getAsString();
        		DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        		String strDate = array.get(i).getAsJsonObject().get("Date").getAsString().substring(0, 10);
        		int hour = Integer.parseInt(array.get(i).getAsJsonObject().get("Date").getAsString().substring(11, 13));
        		int min = Integer.parseInt(array.get(i).getAsJsonObject().get("Date").getAsString().substring(14, 16));
                Date date = (Date) formatter1.parse(strDate);
                date.setHours(hour);
                date.setMinutes(min);
        		JsonObject testSetJsonObj = array.get(i).getAsJsonObject().get("TestSet").getAsJsonObject();
        		JsonObject testCaseJsonObj = array.get(i).getAsJsonObject().get("TestCase").getAsJsonObject();
        		String testSet = testSetJsonObj.get("FormattedID").getAsString();
        		String testCase = testCaseJsonObj.get("FormattedID").getAsString();
        		int resultExists = testResultExists(testSet, testCase, date, testResults); 
        		if(resultExists != 0) {
        			testResult.setDate(date);
        			testResult.setStatus(verdict);
        			testResult.setTestCase(testCase);
        			testResult.setTestSet(testSet);
        			testResult.setBuild(build);
        			testResults.add(testResult);
        		}
        	}
        	for(TestResult result:testResults) {
        		String verdict = result.getStatus();
        		if(verdict.equalsIgnoreCase("error")) {
        			error++;
        		} else if(verdict.equalsIgnoreCase("pass")) {
        			pass++;
        		} else if(verdict.equalsIgnoreCase("fail")) {
        			fail++;
        		} else if(verdict.equalsIgnoreCase("inconclusive")) {
        			inconclusive++;
        		} else if(verdict.equalsIgnoreCase("blocked")) {
        			blocked++;
        		}
        		if(print) {
        			System.out.println(result.getTestSet()+"\t"+result.getTestCase()+"\t"+result.getStatus()+"\t"+result.getDate()+"\t"+result.getBuild());
        		}
            }
        }
        
        QueryRequest testCaseCount = new QueryRequest("TestSet");
        testCaseCount.setFetch(new Fetch("FormattedID", "Name", "TestCaseCount"));
        query = new QueryFilter("FormattedID", "=", "TS0");
        for(String testSet:testSets) {
        	query = query.or(new QueryFilter("FormattedID", "=", testSet));
        }
        testCaseCount.setQueryFilter(query);
        QueryResponse testCaseResponse = restApi.query(testCaseCount);
        int totalCount = 0;
        for(int i=0; i<testCaseResponse.getResults().size(); i++) {
        	if(print) {
        		System.out.println(testCaseResponse.getResults().get(i).getAsJsonObject().get("FormattedID") + "\t" + testCaseResponse.getResults().get(i).getAsJsonObject().get("TestCaseCount").getAsInt());
        	}
        	totalCount = totalCount + testCaseResponse.getResults().get(i).getAsJsonObject().get("TestCaseCount").getAsInt();
        }
        if(print) {
        	System.out.println("Pass: "+pass+", Fail: "+fail+", Inconclusive: "+inconclusive+", Blocked: "+blocked+", Error: "+error + ", Total: "+ totalCount);
        }
        return testResults;
    }
    
    private static int testResultExists(String testSet, String testCase, Date date, List<TestResult> testResults) {
    	int index = 0;
    	for(TestResult testResult:testResults) {
    		if(testResult.getTestCase().equalsIgnoreCase(testCase) && testResult.getTestSet().equalsIgnoreCase(testSet)){
    			if( testResult.getDate().after(date)) {
    				return 0;
    			} else {
    				testResults.remove(testResult);
    				return 1;
    			}
    		} 
    		index++;
    	}
    	return -1;
    }
    
    private static String testSetResultExists(RallyRestApi restApi, String testSetName, JsonArray results) throws IOException {
    	int numberOfTestCaseResults = results.size();
    	for (int j=0; j<numberOfTestCaseResults; j++){
    		JsonObject testResult = results.get(j).getAsJsonObject();
    		String ref = testResult.get("_ref").getAsString();
    		GetRequest testCaseResultRequest = new GetRequest(ref.substring(ref.indexOf("/testcaseresult/")));
    	    GetResponse testCaseResultResponse = restApi.get(testCaseResultRequest);
    	    JsonObject testCaseResultObj = testCaseResultResponse.getObject();
    	    if(!testCaseResultObj.get("TestSet").isJsonNull()) {
    	    	JsonObject testSetInResult = testCaseResultObj.get("TestSet").getAsJsonObject();
    	    	if(testSetInResult.get("_refObjectName").getAsString().equalsIgnoreCase(testSetName))
    	    	{
    	    		return testCaseResultObj.get("Verdict").getAsString();
    	    	}
    	    }
    	}
    	return null;
    }
    
    private static void retrieveTestCase(RallyRestApi restApi, String testCaseId)
			throws IOException {
		QueryFilter queryFilter = new QueryFilter("FormattedID", "=", testCaseId);
    	QueryRequest defectRequest = new QueryRequest("testcases");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("FormattedID", "Name", "Method"));
    	defectRequest.setScopedDown(true);
    	defectRequest.setLimit(10000);
    	QueryResponse projectDefects = restApi.query(defectRequest);
    	JsonArray defectsArray = projectDefects.getResults();
    	for(int i=0; i<defectsArray.size(); i++) {
    		JsonElement elements =  defectsArray.get(i);
    		JsonObject object = elements.getAsJsonObject();
    		String name = object.get("Name").getAsString();
            String method = object.get("Method").getAsString();
            System.out.println(i+"\t"+testCaseId+"\t"+name+"\t"+method);
    	}
	}

	private static void retrieveDefects(RallyRestApi restApi)
			throws IOException {
		
		QueryFilter queryFilter = new QueryFilter("FormattedID", "=", "DE11165");
    	QueryRequest defectRequest = new QueryRequest("defects");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("State", "Name", "Tags", "Platform", "Release", "Components",  "FormattedID", "Environment", "Priority", "LastUpdateDate", "SubmittedBy", "Owner", "Project", "ClosedDate"));
    	defectRequest.setProject("/project/23240411122"); 
    	defectRequest.setScopedDown(true);
    	QueryResponse projectDefects = restApi.query(defectRequest);
    	JsonArray defectsArray = projectDefects.getResults();
    
    	for(int i=0; i<defectsArray.size(); i++) {
    		JsonElement elements =  defectsArray.get(i);
            JsonObject object = elements.getAsJsonObject();
            if(null != object.get("Tags") && !object.get("Tags").isJsonNull()) {
            	JsonObject jsonObject = object.get("Tags").getAsJsonObject();
            	
            	int numberOfTestCases = jsonObject.get("_tagsNameArray").getAsJsonArray().size();
                if(numberOfTestCases>0){
                      for (int j=0;j<numberOfTestCases;j++){
	            	  	JsonObject jsonObj = jsonObject.get("_tagsNameArray").getAsJsonArray().get(j).getAsJsonObject();
	            	  	//System.out.println(jsonObj.get("Name"));
                     }
                }
            }
            System.out.println(object.get("c_Components").getAsString());
    	}
	}
    
	private static void retrieveTags(RallyRestApi restApi)
			throws IOException, ParseException {
		
		QueryRequest testSetRequest = new QueryRequest("Tags");
        testSetRequest.setProject("/project/23240411122");
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
        
        testSetRequest.setFetch(new Fetch(new String[] {"Name", "Defects"}));
        testSetRequest.setQueryFilter(new QueryFilter("Name", "=", "Release 1.6 - CA Adoption"));
        QueryResponse testSetQueryResponse = restApi.query(testSetRequest);
        for (int i=0; i<testSetQueryResponse.getResults().size();i++){
            JsonObject testSetJsonObject = testSetQueryResponse.getResults().get(i).getAsJsonObject();
            int numberOfTestCases = testSetJsonObject.get("Defects").getAsJsonArray().size();
            if(numberOfTestCases>0){
                  for (int j=0;j<numberOfTestCases;j++){
                	  	JsonObject jsonObject = testSetJsonObject.get("Defects").getAsJsonArray().get(j).getAsJsonObject();
                	  	System.out.println(jsonObject.get("FormattedID"));
                 }
            }
        }
	}
	
    private static List<Release> retrieveRelease(RallyRestApi restApi) throws Exception{
    	List<Release> releases = new ArrayList<Release>();
    	QueryRequest query = new QueryRequest("Release");
    	QueryFilter queryFilter = new QueryFilter("State", "=", "Active").or(new QueryFilter("State", "=", "Planning"));
    	query.setQueryFilter(queryFilter);
    	QueryResponse projectDefects = restApi.query(query);
    	
    	JsonArray releasesArray = projectDefects.getResults();
    	for(int i=0; i<releasesArray.size(); i++) {
    		Release release = new Release();
            JsonElement elements =  releasesArray.get(i);
            JsonObject object = elements.getAsJsonObject();
            if(!isReleaseAlreadyAdded(releases, object.get("_refObjectName").getAsString())) {
	            release.setReleaseEndDate(object.get("ReleaseDate").getAsString());
	            release.setReleaseName(object.get("_refObjectName").getAsString());
	            release.setReleaseStartDate(object.get("ReleaseStartDate").getAsString());
	            releases.add(release);
            }
        }
    	return releases;
    }
    
    private static RallyRestApi loginRally() throws URISyntaxException {
    	String rallyURL = "https://rally1.rallydev.com";
     	String myUserName = "mohammed.saquib@pearson.com";
     	String myUserPassword = "Rally@123";
     	return new RallyRestApi(new URI(rallyURL), myUserName, myUserPassword);
    }
    
    private static boolean isReleaseAlreadyAdded(List<Release> releases, String releaseName) {
    	for(Release release:releases) {
    		if(release.getReleaseName().equals(releaseName)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private static void getTestDetails(RallyRestApi restApi, String[] testCaseIds) throws IOException {
    	String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
    	for(String testCaseId:testCaseIds) {
	    	QueryRequest testCaseRequest = new QueryRequest("TestCase");
	        testCaseRequest.setQueryFilter(new QueryFilter("FormattedID", "=", testCaseId));
	        QueryResponse testCaseQueryResponse = restApi.query(testCaseRequest);
	        JsonObject object = testCaseQueryResponse.getResults().get(0).getAsJsonObject();
	        String objective = object.get("Objective").getAsString();
	        String method = object.get("Method").getAsString();
	        String tag = "";
	        JsonObject jsonObject = object.get("Tags").getAsJsonObject();
	    	int numberOfTestCases = jsonObject.get("_tagsNameArray").getAsJsonArray().size();
	        if(numberOfTestCases>0){
	              for (int j=0;j<numberOfTestCases;j++){
	        	  	JsonObject jsonObj = jsonObject.get("_tagsNameArray").getAsJsonArray().get(j).getAsJsonObject();
	        	  	if(jsonObj.get("Name").getAsString().equals("Automation")) {
	        	  		tag = "Automation";
	        	  	}
	        	  	if(jsonObj.get("Name").getAsString().equals("WinAutomation")) {
	        	  		if(tag.isEmpty()) {
	        	  			tag = "WinAutomation";
	        	  		} else {
	        	  			tag += ", WinAutomation";
	        	  		}
	        	  	}
	             }
	        }
	        System.out.println(testCaseId+"\t"+objective+"\t"+method+"\t"+tag);
    	}
    }
    
    private static void exportTestCases(RallyRestApi restApi) throws IOException, ParseException {
    	String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
    	QueryRequest testCaseRequest = new QueryRequest("TestCase");
    	/*QueryFilter filter = new QueryFilter("Deprecated", "=", "false");
    	filter = filter.and(new QueryFilter("Type", "!=", "Regression"));
    	filter = filter.and(new QueryFilter("CreationDate", ">=", "2014-11-05"));*/
    	QueryFilter filter = new QueryFilter("FormattedID", "=", "TC20418");
        testCaseRequest.setQueryFilter(filter);
        testCaseRequest.setLimit(10000);
        
        QueryResponse testCaseQueryResponse = restApi.query(testCaseRequest);
        JsonArray array = testCaseQueryResponse.getResults();
        int numberTestCaseResults = array.size();
        if(numberTestCaseResults >0) {
        	List<TestCaseDTO> testCases = new ArrayList<TestCaseDTO>();
        	for(int i=0; i<numberTestCaseResults; i++) {
        		TestCaseDTO testCase = new TestCaseDTO();
        		JsonObject object = testCaseQueryResponse.getResults().get(i).getAsJsonObject();
    	        String formattedId = object.get("FormattedID").getAsString();
    	        String name = object.get("Name").getAsString();
    	        String workProduct = "";
    	        if(null != object.get("WorkProduct") && !object.get("WorkProduct").isJsonNull()) {
           	  		JsonObject workProductObj = object.get("WorkProduct").getAsJsonObject();
           	  		if(null != workProductObj) {
           	  			workProduct = retrieveUserStory(restApi, workProductObj.get("_ref").getAsString());
           	  		}
           	  	}
    	        String type = object.get("Type").getAsString();
    	        String priority = object.get("Priority").getAsString();
    	        String owner = "";
    	        if(null != object.get("Owner") && !object.get("Owner").isJsonNull()) {
           	  		JsonObject ownerObj = object.get("Owner").getAsJsonObject();
           	  		owner = ownerObj.get("_refObjectName").getAsString();
           	  	}
    	        String method = object.get("Method").getAsString();
    	        String lastVerdict = "";
    	        if(null != object.get("LastVerdict") && !object.get("LastVerdict").isJsonNull()) {
    	        	lastVerdict = object.get("LastVerdict").getAsString();
    	        }
    	        String lastBuild = "";
        		if(null != object.get("LastBuild") && !object.get("LastBuild").isJsonNull()) {
        			lastBuild = object.get("LastBuild").getAsString();
        		}
    	        String lastRun = "";
    	        if(null != object.get("LastRun") && !object.get("LastRun").isJsonNull()) {
    	        	lastRun = object.get("LastRun").getAsString();
    	        }
    	        String creationDate = object.get("CreationDate").getAsString();
    	        String description = object.get("Description").getAsString();
    	        String validationInput = "";
    	        String validationExpectedResult = "";
    	        if(null != object.get("Steps") && !object.get("Steps").isJsonNull()) {
           	  		JsonArray stepsObj = object.get("Steps").getAsJsonArray();
	                for(int j=0; j<stepsObj.size(); j++) {
	                	boolean added = false;
	            		JsonElement elements =  stepsObj.get(j);
	            		JsonObject step = elements.getAsJsonObject();
	            		if(null != step.get("ValidationInput") && !step.get("ValidationInput").isJsonNull()){
	            			if(validationInput.isEmpty()) {
	            				validationInput = step.get("ValidationInput").getAsString();
	            			} else {
	            				validationInput = validationInput + "\n" + step.get("ValidationInput").getAsString();
	            			}
	            		}
	            		if(null != step.get("ValidationExpectedResult") && !step.get("ValidationExpectedResult").isJsonNull()){
	            			if(validationExpectedResult.isEmpty()) {
	            				validationExpectedResult = step.get("ValidationExpectedResult").getAsString();
	            			} else {
	            				validationExpectedResult = validationExpectedResult + "\n" + step.get("ValidationExpectedResult").getAsString();
	            			}
	            		}
	            	}
           	  	}
    	        String discussionCount = "";
    	        if(null != object.get("Discussion") && !object.get("Discussion").isJsonNull()) {
           	  		JsonArray discussionObj = object.get("Discussion").getAsJsonArray();
           	  		discussionCount = discussionObj.size()+"";
    	        }
    	        String howmanyminutesdoesthistaketorun = "";
    	        if(null != object.get("Howmanyminutesdoesthistaketorun") && !object.get("Howmanyminutesdoesthistaketorun").isJsonNull()) {
    	        	howmanyminutesdoesthistaketorun = object.get("Howmanyminutesdoesthistaketorun").getAsInt()+"";
    	        }
    	        String notes = "";
    	        if(null != object.get("Notes") && !object.get("Notes").isJsonNull()) {
    	        	notes = object.get("Notes").getAsString();
    	        }
    	        String objectId = "";
    	        if(null != object.get("ObjectID") && !object.get("ObjectID").isJsonNull()) {
    	        	objectId = object.get("ObjectID").getAsString();
    	        }
    	        String objective = "";
    	        if(null != object.get("Objective") && !object.get("Objective").isJsonNull()) {
    	        	objective = object.get("Objective").getAsString();
    	        }
    	        String lastUpdateDate = "";
    	        if(null != object.get("LastUpdateDate") && !object.get("LastUpdateDate").isJsonNull()) {
    	        	lastUpdateDate = object.get("LastUpdateDate").getAsString();
    	        }
    	        String postConditions = "";
    	        if(null != object.get("PostConditions") && !object.get("PostConditions").isJsonNull()) {
    	        	postConditions = object.get("PostConditions").getAsString();
    	        }
    	        String preConditions = "";
    	        if(null != object.get("PreConditions") && !object.get("PreConditions").isJsonNull()) {
    	        	preConditions = object.get("PreConditions").getAsString();
    	        }
    	        String project = "";
    	        if(null != object.get("Project") && !object.get("Project").isJsonNull()) {
           	  		JsonObject projectObj = object.get("Project").getAsJsonObject();
           	  		if(null != projectObj) {
           	  			project = projectObj.get("_refObjectName").getAsString();
           	  		}
           	  	}
    	        String risk = "";
    	        if(null != object.get("Risk") && !object.get("Risk").isJsonNull()) {
    	        	risk = object.get("Risk").getAsString();
    	        }
    	        
    	        String tags = "";
    	        if(null != object.get("Tags") && !object.get("Tags").isJsonNull()) {
    	        	JsonArray tagsArray = object.get("Tags").getAsJsonArray();
    	        	for(int t=0; t<tagsArray.size(); t++) {
    	        		JsonObject tagObj = tagsArray.get(t).getAsJsonObject();
    	        		if(tags.isEmpty()) {
    	        			tags = tagObj.get("_refObjectName").getAsString();
    	        		} else {
    	        			tags = tags +", "+tagObj.get("_refObjectName").getAsString();
    	        		}
    	        	}
    	        }
    	        
    	        String testFolder = "";
    	        if(null != object.get("TestFolder") && !object.get("TestFolder").isJsonNull()) {
           	  		JsonObject testFolderObj = object.get("TestFolder").getAsJsonObject();
           	  		if(null != testFolderObj) {
           	  			testFolder = retrieveTestFolder(restApi, testFolderObj.get("_ref").getAsString());
           	  		}
           	  	}
    	        
    	        String isthisaCandidateforAutomation = "";
    	        if(null != object.get("IsthisaCandidateforAutomation") && !object.get("IsthisaCandidateforAutomation").isJsonNull()) {
    	        	isthisaCandidateforAutomation = Boolean.toString(object.get("IsthisaCandidateforAutomation").getAsBoolean());
    	        }
    	        
    	        String gherkinLanguage = "";
    	        if(null != object.get("GherkinLanguage") && !object.get("GherkinLanguage").isJsonNull()) {
    	        	gherkinLanguage = object.get("GherkinLanguage").getAsString();
    	        }
    	        
    	        String displayColor = "";
    	        if(null != object.get("DisplayColor") && !object.get("DisplayColor").isJsonNull()) {
    	        	displayColor = object.get("DisplayColor").getAsString();
    	        }
    	        
    	        testCase.setFormattedID(formattedId);
    	        testCase.setName(name);
    	        testCase.setWorkProduct(workProduct);
    	        testCase.setType(type);
    	        testCase.setPriority(priority);
    	        testCase.setOwner(owner);
    	        testCase.setMethod(method);
    	        testCase.setLastVerdict(lastVerdict);
    	        testCase.setLastBuild(lastBuild);
    	        testCase.setLastRun(lastRun);
    	        testCase.setCreationDate(creationDate);
    	        testCase.setDescription(Jsoup.parse(description).text());
    	        testCase.setActiveDefects("");
    	        testCase.setDiscussionCount(discussionCount);
    	        testCase.setHowManyMinutesDoesThisTakeToRun(howmanyminutesdoesthistaketorun);
    	        testCase.setLastUpdateDate(lastUpdateDate);
    	        testCase.setNotes(Jsoup.parse(notes).text());
    	        testCase.setObjectID(objectId);
    	        testCase.setObjective(Jsoup.parse(objective).text());
    	        testCase.setPostConditions(Jsoup.parse(postConditions).text());
    	        testCase.setPreConditions(Jsoup.parse(preConditions).text());
    	        testCase.setProject(project);
    	        testCase.setRisk(risk);
    	        testCase.setTags(tags);
    	        testCase.setTestFolder(testFolder);
    	        testCase.setValidationExpectedResult(Jsoup.parse(validationExpectedResult).text());
    	        testCase.setValidationInput(Jsoup.parse(validationInput).text());
    	        testCase.setIsThisACandidateForAutomation(isthisaCandidateforAutomation);
    	        testCase.setGherkinLanguage(Jsoup.parse(gherkinLanguage).text());
    	        testCase.setDisplayColor(displayColor);
    	        testCases.add(testCase);
        	}
        	
        	for(TestCaseDTO testCase: testCases) {
        		System.out.println(
	        		testCase.getFormattedID() + "\t" +
	    	        testCase.getName() + "\t" +
	    	        testCase.getWorkProduct() + "\t" +
	    	        testCase.getType() + "\t" +
	    	        testCase.getPriority() + "\t" +
	    	        testCase.getOwner() + "\t" +
	    	        testCase.getMethod() + "\t" +
	    	        testCase.getLastVerdict() + "\t" +
	    	        testCase.getLastBuild() + "\t" +
	    	        testCase.getLastRun() + "\t" +
	    	        testCase.getCreationDate() + "\t" +
	    	        testCase.getDescription() + "\t" +
	    	        testCase.getActiveDefects() + "\t" +
	    	        testCase.getDiscussionCount() + "\t" +
	    	        testCase.getHowManyMinutesDoesThisTakeToRun() + "\t" +
	    	        testCase.getLastUpdateDate() + "\t" +
	    	        testCase.getNotes() + "\t" +
	    	        testCase.getObjectID() + "\t" +
	    	        testCase.getObjective() + "\t" +
	    	        testCase.getPostConditions() + "\t" +
	    	        testCase.getPreConditions() + "\t" +
	    	        testCase.getProject() + "\t" +
	    	        testCase.getRisk() + "\t" +
	    	        testCase.getTags() + "\t" +
	    	        testCase.getTestFolder() + "\t" +
	    	        testCase.getValidationExpectedResult() + "\t" +
	    	        testCase.getValidationInput() + "\t" +
	    	        testCase.getIsThisACandidateForAutomation() + "\t" +
	    	        testCase.getGherkinLanguage() + "\t" +
	    	        testCase.getDisplayColor()
        			);
        	}
       }
    }
    
    private static String retrieveUserStory(RallyRestApi restApi, String userStory) throws IOException, ParseException {
    	QueryRequest storyRequest = new QueryRequest("HierarchicalRequirement");
        storyRequest.setLimit(1000);
        storyRequest.setScopedDown(true);
        storyRequest.setFetch(new Fetch("FormattedID","Name"));
        userStory = userStory.substring(userStory.lastIndexOf("/")+1);
        userStory = userStory.replace(".js", "");
        QueryFilter queryFilter = new QueryFilter("ObjectID", "=", userStory);
        storyRequest.setQueryFilter(queryFilter);
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
        QueryResponse testSetQueryResponse = restApi.query(storyRequest);
        if(testSetQueryResponse.getResults().size() > 0) {
        	JsonObject testSetJsonObject = testSetQueryResponse.getResults().get(0).getAsJsonObject();
        	return testSetJsonObject.get("FormattedID").getAsString()+": "+testSetJsonObject.get("Name").getAsString();
        }
        return "";
    }
    
    private static String retrieveTestFolder(RallyRestApi restApi, String testFolder) throws IOException, ParseException {
    	QueryRequest storyRequest = new QueryRequest("TestFolder");
        storyRequest.setLimit(1000);
        storyRequest.setScopedDown(true);
        storyRequest.setFetch(new Fetch("FormattedID","Name"));
        testFolder = testFolder.substring(testFolder.lastIndexOf("/")+1);
        testFolder = testFolder.replace(".js", "");
        QueryFilter queryFilter = new QueryFilter("ObjectID", "=", testFolder);
        storyRequest.setQueryFilter(queryFilter);
        String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
        QueryResponse testSetQueryResponse = restApi.query(storyRequest);
        if(testSetQueryResponse.getResults().size() > 0) {
        	JsonObject testSetJsonObject = testSetQueryResponse.getResults().get(0).getAsJsonObject();
        	return testSetJsonObject.get("FormattedID").getAsString()+": "+testSetJsonObject.get("Name").getAsString();
        }
        return "";
    }
    
    
    private static void getContentTestCases(RallyRestApi restApi, String projectId) throws IOException {
		QueryRequest testCaseRequest = new QueryRequest("TestCase");
		testCaseRequest.setProject("/project/"+projectId);
		testCaseRequest.setQueryFilter(new QueryFilter("CreationDate", ">=", "2014-09-15"));
        QueryResponse testCaseQueryResponse = restApi.query(testCaseRequest);
        JsonArray testCases = testCaseQueryResponse.getResults();
        int numberOfTestCases = testCases.size();
        for(int i=0; i<numberOfTestCases; i++) {
	        JsonObject object = testCases.get(i).getAsJsonObject();
	        String objective = object.get("Objective").getAsString();
	        String method = object.get("Method").getAsString();
	        String testCaseId = object.get("FormattedID").getAsString();
	        
	        JsonObject jsonObject = object.get("Tags").getAsJsonObject();
	    	int numberOfTags = jsonObject.get("_tagsNameArray").getAsJsonArray().size();
	        if(numberOfTags>0){
	              for (int j=0;j<numberOfTags;j++){
	        	  	JsonObject jsonObj = jsonObject.get("_tagsNameArray").getAsJsonArray().get(j).getAsJsonObject();
	        	  	String tagName = jsonObj.get("Name").getAsString();
	        	  	if(null != tagName && tagName.toLowerCase().contains("content")) {
	        	  		System.out.println(testCaseId+"\t"+objective+"\t"+method+"\t"+tagName);
	        	  	}
	             }
	        }
        }
    }
    
    private static void retrieveSteps(RallyRestApi restApi, String rootFolder) throws IOException, ParseException {
    	String wsapiVersion = "1.43";
        restApi.setWsapiVersion(wsapiVersion);
        
        File inFolder = new File(rootFolder+File.separator+"in"+File.separator);
    	FilenameFilter fileNameFilter = new FilenameFilter() {
 		   
            @Override
            public boolean accept(File dir, String name) {
               if(name.lastIndexOf('.')>0)
               {
                  int lastIndex = name.lastIndexOf('.');
                  String str = name.substring(lastIndex);
                  if(str.equals(".txt"))
                  {
                     return true;
                  }
               }
               return false;
            }
        };
    	File files[] = inFolder.listFiles(fileNameFilter);
    	for(File file:files) {
    		HSSFWorkbook workbook = new HSSFWorkbook();
        	HSSFSheet sheet = workbook.createSheet();
        	HSSFFont font = workbook.createFont();
    		font.setFontName("Trebuchet MS");
    		HSSFCellStyle style = workbook.createCellStyle();
            style.setFont(font);
            style.setWrapText(true);
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
    		Scanner sc=new Scanner(new FileReader(file));
    		int rowNum = 1;
    		while (sc.hasNextLine()){
	        	String testCaseId = sc.nextLine();
	        	QueryRequest testCaseRequest = new QueryRequest("TestCase");
	            testCaseRequest.setQueryFilter(new QueryFilter("FormattedID", "=", testCaseId));
	            QueryResponse testCaseQueryResponse = restApi.query(testCaseRequest);
	            JsonArray array = testCaseQueryResponse.getResults();
	            int numberTestCaseResults = array.size();
	            short nextColor = HSSFColor.WHITE.index;
	            if(numberTestCaseResults >0) {
	            	for(int i=0; i<numberTestCaseResults; i++) {
	            		if(nextColor == HSSFColor.LIGHT_BLUE.index) {
	            			style.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
	            			nextColor = HSSFColor.WHITE.index;
	            		} else {
	            			style.setFillBackgroundColor(HSSFColor.WHITE.index);
	            			nextColor = HSSFColor.LIGHT_BLUE.index;
	            		}
	            		
	            		JsonObject object = testCaseQueryResponse.getResults().get(i).getAsJsonObject();
	            		if(null != object.get("Steps") && !object.get("Steps").isJsonNull()) {
	               	  		JsonArray stepsObj = object.get("Steps").getAsJsonArray();
	    	                for(int j=0; j<stepsObj.size(); j++) {
	    	                	JsonElement elements =  stepsObj.get(j);
	    	            		JsonObject step = elements.getAsJsonObject();
	    	            		String ref = step.get("_ref").getAsString();
	    	            		ref = ref.substring(ref.indexOf("/testcasestep/")).replace(".js", "").replace("/testcasestep/", "");
	    	            		testCaseRequest = new QueryRequest("TestCaseStep");
	    	                    testCaseRequest.setQueryFilter(new QueryFilter("ObjectID", "=", ref));
	    	                    testCaseQueryResponse = restApi.query(testCaseRequest);
	    	                    int numberTestSteps = array.size();
	    	                    if(numberTestSteps >0) {
	    	                    	for(int k=0; k<numberTestSteps; k++) {
	    	                    		JsonObject stepObject = testCaseQueryResponse.getResults().get(k).getAsJsonObject();
	    	                    		if(null != stepObject.get("StepIndex") && !stepObject.get("StepIndex").isJsonNull()) {
	    		                    		HSSFRow row = sheet.createRow(rowNum);
	    		                    		short cellNum = 0;

	    		                    		HSSFCell cell = row.createCell(cellNum);
	    		                			cell.setCellStyle(style);
	    		                			cell.setCellValue(testCaseId);
	    		                			cellNum++;
	    		                			
	    		                    		cell = row.createCell(cellNum);
	    		                			cell.setCellStyle(style);
	    		                			cell.setCellValue(stepObject.get("StepIndex").getAsInt()+1);
	    		                			cellNum++;
	    		                			
	    		                			cell = row.createCell(cellNum);
	    		                			cell.setCellStyle(style);
	    		                			cell.setCellValue(stepObject.get("Input").getAsString());
	    		                			cellNum++;
	    		                			
	    		                			cell = row.createCell(cellNum);
	    		                			cell.setCellStyle(style);
	    		                			cell.setCellValue(stepObject.get("ExpectedResult").getAsString());
	    		                			cellNum++;
	    		                    		rowNum++;
	    	                    		}
	    	                    	}
	    	                    }
	    	                }
	            		}
	            	}
	            }
	        }
	        sc.close();
	        SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yy");
	    	Date today = new Date();
	    	String dateStr = formatter.format(today);
	    	FileOutputStream outputStream = new FileOutputStream(file.getName()+dateStr+".xls");
	    	file.renameTo(new File(file.getAbsolutePath().replace("txt", "done")));
	        workbook.write(outputStream);
    	}
    }
}

