package com.pearson.psoc.util;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.attribute.AclEntry.Builder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

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
    	//createTestSet(restApi);
    	//updateTestCaseResults(restApi, "1.6.0.1387", "mohammed.saquib@pearson.com");
    	//updateTestCaseResults(restApi, "K1_Merge_1.6.0.13", "madhav.purohit@pearson.com");
    	//updateTestSet(restApi);
    	//retrieveTestSets(restApi, "TS814,TS815,TS816,TS817,TS818,TS819,TS820,TS821,TS822,TS823,TS824,TS825,TS826", "23240411122");
    	//retrieveTestSetsResult(restApi, "TS846");
    	//retrieveTestCases(restApi);
    	//retrieveDefects(restApi);
    	//readTabDelimitedFileAddTestCaseToTestFolder();
    	//retrieveTestResults(restApi, "TS860", true);
    	//getIteration(restApi, "21028059357");
    	//getIteration(restApi, "23240411122");
    	//getUserStory(restApi, "US9579,US10035,US10041,US9536".split(","));
    	getTestDetails(restApi, "TC45134,TC45323,TC45324,TC45325,TC45326,TC51773,TC52809,TC52810,TC52811,TC52081,TC52089,TC52812,TC51945,TC52074,TC52092,TC51948,TC52083,TC51946,TC51947,TC52080,TC52935,TC52091,TC52934,TC52936,TC52093".split(","));
    	//getContentTestCases(restApi, "21028059357");
    	restApi.close();
    	//postJenkinsJob();
    }
    
    private static void createTestSet(RallyRestApi restApi) throws IOException {
    	File inFolder = new File("C:\\Users\\msaqib\\Downloads\\in\\");
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
	        	String words[] = sc.nextLine().split("\t");
	        	updateTestSet(restApi, "TS872", "39748630606", words[0]);
	        }
	        sc.close();
    	}
    	
    }
    
    private static void updateTestCaseResults(RallyRestApi restApi, String buildNumber, String userName) throws IOException {
    	File inFolder = new File("D:\\Regression\\07232015\\in\\");
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
	        file.renameTo(new File("D:\\Regression\\07232015\\done\\"+file.getName()));
    	}
    	
    }
    
    private static void updateTestSet(RallyRestApi restApi, String testSetId, String testSetRef, String testCaseId) throws IOException {
    	QueryRequest testCaseRequest = new QueryRequest("TestCase");
        testCaseRequest.setFetch(new Fetch("FormattedID","Name"));
        
        QueryFilter query = new QueryFilter("FormattedID", "=", testCaseId);
        
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
    		System.out.println(i+" "+object);
            System.out.println(i+" "+object.get("_ref"));
            JsonArray exTCs = object.get("TestCases").getAsJsonArray();
            exTCs.addAll(testCases);
            object.add("TestCases", exTCs);
            UpdateRequest request = new UpdateRequest("/testset/"+testSetRef, object);
            UpdateResponse response = restApi.update(request);
            System.out.println(response);
    	}
    }
    
    private static void getUserStory(RallyRestApi restApi, String[] userStories) throws IOException, ParseException {
    	QueryRequest storyRequest = new QueryRequest("HierarchicalRequirement");
        storyRequest.setLimit(1000);
        storyRequest.setScopedDown(true);
        storyRequest.setFetch(new Fetch("FormattedID","Name", "TestCases", "Priority", "Method"));
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
            	  	System.out.println((ij)+"\t"+ jsonObject.get("FormattedID")+"\t"+jsonObject.get("Name")+"\t"+userStoryName+"\t"+jsonObject.get("Priority")+"\t" + jsonObject.get("Method"));
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
		            System.out.println(String.format("Created %s", createResponse.getObject().get("_ref").getAsString()));          
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
               	  		System.out.println((ij)+"\t"+testSetId+"\t"+testSetName+"\t"+ testCaseId +"\t" + executed +"\t" + jsonObject.get("LastVerdict")+"\t" + jsonObject.get("Name")+"\t" + jsonObject.get("Priority")+"\t" + userStory);
               	  	} else {
               	  		System.out.println((ij)+"\t"+testSetId+"\t"+testSetName+"\t"+ testCaseId +"\t" + executed +"\t" + jsonObject.get("LastVerdict")+"\t" + jsonObject.get("Name")+"\t" + jsonObject.get("Priority")+"\t" + userStory);
               	  	}
               	  	ij++;
               	  	executed = false;
                 }
            }
        }

	}
    
    private static void addTestCaseToTestFolder(RallyRestApi restApi, String project, String testFolderId, String testCaseId, String testCaseRef) throws IOException, URISyntaxException {
		QueryFilter queryFilter = new QueryFilter("FormattedID", "=", testFolderId);
    	QueryRequest defectRequest = new QueryRequest("TestFolder");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("FormattedID", "TestFolder"));
    	defectRequest.setProject("/project/"+project); 
    	defectRequest.setScopedDown(true);
    	QueryResponse testFolder = restApi.query(defectRequest);
    	String testFolderRef = testFolder.getResults().get(0).getAsJsonObject().get("_ref").getAsString(); 
        String ref = testFolderRef.substring(testFolderRef.indexOf("/testfolder/"));
        
        
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
    
    private static void retrieveTestCases(RallyRestApi restApi)
			throws IOException {
		String testCases = "TC16006,TC16027,TC16215,TC20467,TC20629,TC20505,TC23259,TC23260,TC23358,TC22129,TC22130,TC22131,TC15871,TC15891,TC15893,TC15896,TC15990,TC16075,TC17685,TC17686,TC20187,TC20193,TC20194,TC20199,TC20203,TC20630,TC20631,TC15884,TC15885,TC15910,TC15992,TC16071,TC17671,TC17680,TC17688,TC17690,TC17691,TC17699,TC19457,TC20040,TC20472,TC20473,TC20474,TC20476,TC20493,TC20632,TC20633,TC20634,TC21721,TC21727,TC21957,TC22134,TC22411,TC22623,TC22624,TC15863,TC15879,TC15894,TC16302,TC19368,TC20490,TC22114,TC22115,TC22405,TC22610,TC15929,TC15941,TC16064,TC18609,TC18672,TC20531,TC22244,TC15998,TC16061,TC18600,TC18602,TC19140,TC19383,TC20523,TC20524,TC20526,TC20594,TC21998,TC21999,TC22000,TC22010,TC22012,TC22324,TC22326,TC22334,TC22336,TC22338,TC22339,TC22340,TC22341,TC22343,TC22344,TC22346,TC22348,TC22349,TC22351,TC22353,TC22354,TC22355,TC22357,TC22359,TC22446,TC22447,TC22463,TC22769,TC23175,TC23250,TC23319,TC18610,TC16030,TC20509,TC20510,TC22146,TC22761,TC22767,TC23256,TC23927,TC23964,TC23965,TC23967,TC23974,TC24222,TC24225,TC24228,TC24236,TC24237,TC24383,TC25090,TC25143,TC25469,TC25471,TC25822,TC27063,TC27181,TC27172,TC27168,TC16218,TC21932,TC20289,TC29945,TC31610,TC24381,TC24432,TC24385,TC24382,TC24377,TC24376,TC15631,TC2245,TC2187,TC2085,TC14784,TC8178,TC27451,TC27444,TC29920,TC27452,TC27446,TC29922,TC27221,TC20623,TC20492,TC20487,TC31813,TC21757,TC21758,TC34177,TC34183,TC34192,,TC18603,TC34178,TC34182,TC34190,TC17683,TC16024,TC20253,TC23360,TC29907,TC23266,TC22456,TC33956,TC9305,TC43537,TC9303,TC27212,TC27177,TC22152,TC22557,TC22611,TC22612,TC22613,TC23361,TC22128,TC22620,TC21839,TC21956,TC16303,TC22111,TC22112,TC22113,TC20273,TC21996,TC22037,TC22243,TC22245,TC22325,TC20528,TC20529,TC20771,TC21951,TC22001,TC22002,TC22003,TC22004,TC22005,TC22172,TC22246,TC22247,TC22322,TC22329,TC22330,TC22331,TC22332,TC22335,TC22430,TC22458,TC22460,TC22466,TC23157,TC23160,TC23161,TC23162,TC22145,TC22155,TC22156,TC23255,TC27026,TC27028,TC27033,TC27045,TC27046,TC27047,TC27105,TC20522,TC33494,TC29943,TC29942,TC31798,TC31786,TC31785,TC29824,TC16078,TC44083,TC20800,TC22429,TC22606,TC23113,TC20204,TC22408,TC22409,TC29810,TC29811,TC15944,TC16002,TC16003,TC16004,TC16005,TC16007,TC16009,TC16010,TC16013,TC16014,TC16042,TC16043,TC16045,TC16049,TC16050,TC16051,TC16052,TC16069,TC16070,TC16210,TC16216,TC19066,TC19303,TC19304,TC19306,TC19307,TC19309,TC19310,TC20422,TC22423,TC22465,TC23138,TC23155,TC15958,TC15964,TC15965,TC16011,TC16012,TC16023,TC16044,TC16047,TC18616,TC18617,TC18620,TC18622,TC18623,TC18625,TC18627,TC18632,TC18633,TC20518,TC19311,TC19315,TC19316,TC19317,TC19318,TC19319,TC19320,TC19321,TC19380,TC19398,TC19418,TC20151,TC20378,TC20387,TC20388,TC20390,TC20399,TC20448,TC20449,TC20450,TC20508,TC20511,TC20514,TC20516,TC20517,TC20518,TC20519,TC20806,TC20807,TC20808,TC20809,TC20810,TC20811,TC22249,TC22549,TC22550,TC22552,TC22555,TC22556,TC22603,TC22604,TC22605,TC22608,TC22609,TC22625,TC22626,TC22627,TC22628,TC22639,TC23156,TC23166,TC23168,TC23170,TC23257,TC23258,TC23262,TC23263,TC23264,TC23268,TC23269,TC23270,TC23272,TC23273,TC23274,TC15948,TC16001,TC22132,TC22133,TC15857,TC15860,TC15873,TC15889,TC15892,TC15906,TC15908,TC15918,TC15922,TC15981,TC15982,TC15983,TC15984,TC15985,TC16068,TC31820,TC16074,TC16077,TC16111,TC16193,TC16194,TC16195,TC17672,TC17677,TC17678,TC17681,TC17684,TC18573,TC18984,TC19059,TC19114,TC19362,TC19367,TC19370,TC19455,TC19456,TC19458,TC19459,TC19461,TC19462,TC19465,TC20021,TC20024,TC20026,TC20027,TC20028,TC20029,TC20030,TC20073,TC20074,TC20075,TC20077,TC20078,TC20189,TC20190,TC20191,TC20192,TC20195,TC20196,TC20197,TC20200,TC20201,TC20202,TC20205,TC20216,TC20477,TC20483,TC20515,TC20601,TC20602,TC21851,TC22107,TC22108,TC22406,TC22407,TC22410,TC22607,TC22619,TC15859,TC15864,TC15865,TC15874,TC15875,TC15876,TC15878,TC15880,TC15881,TC15882,TC15883,TC15886,TC15887,TC15888,TC15897,TC15899,TC15900,TC15901,TC15909,TC15966,TC15967,TC15968,TC15969,TC15970,TC15971,TC15972,TC15974,TC15975,TC15978,TC15980,TC16087,TC16090,TC16091,TC16092,TC16095,TC16105,TC16108,TC16109,TC16112,TC16113,TC16114,TC16124,TC17664,TC17665,TC17666,TC17667,TC17669,TC17670,TC17674,TC17675,TC17676,TC17679,TC17682,TC17687,TC17689,TC17696,TC17698,TC18572,TC18612,TC19361,TC19363,TC19365,TC19366,TC19371,TC19372,TC19373,TC19374,TC19460,TC20038,TC20039,TC20042,TC20046,TC20098,TC20099,TC20143,TC20440,TC20475,TC20684,TC20687,TC20689,TC20690,TC21705,TC21706,TC21707,TC21708,TC21710,TC21711,TC21725,TC21728,TC21732,TC21761,TC21762,TC21763,TC21764,TC21765,TC21778,TC21779,TC21780,TC21785,TC21786,TC21787,TC21792,TC21797,TC21827,TC21829,TC21837,TC21840,TC21841,TC21843,TC21845,TC21878,TC22106,TC22116,TC22315,TC22316,TC22318,TC22320,TC22614,TC22616,TC22617,TC22618,TC15861,TC15877,TC15895,TC15898,TC15904,TC15921,TC15932,TC15979,TC16028,TC16029,TC17668,TC17673,TC19058,TC19384,TC21736,TC21737,TC21738,TC21740,TC21741,TC21760,TC22110,TC22126,TC22621,TC22622,TC22716,TC15943,TC15954,TC15955,TC16059,TC16062,TC16063,TC16066,TC18606,TC20152,TC20266,TC20271,TC20272,TC20430,TC20431,TC22242,TC15999,TC18601,TC18604,TC18607,TC18673,TC19139,TC19299,TC20595,TC20596,TC20776,TC21726,TC21756,TC21759,TC21952,TC22173,TC22174,TC22327,TC22328,TC22337,TC22345,TC22347,TC22350,TC22461,TC22768,TC22770,TC23148,TC23149,TC23150,TC23151,TC23158,TC19953,TC25148,TC25491,TC26212,TC26213,TC25838,TC26215,TC26216,TC26217,TC16019,TC16088,TC22143,TC22144,TC22148,TC22149,TC22150,TC22151,TC22153,TC22157,TC22305,TC22307,TC22477,TC22487,TC22524,TC22760,TC22762,TC22763,TC22765,TC22766,TC22867,TC22868,TC23147,TC23153,TC23154,TC23252,TC23253,TC23254,TC23279,TC23280,TC23887,TC23888,TC23889,TC23923,TC23924,TC23925,TC23928,TC23936,TC23937,TC23938,TC23939,TC24163,TC24164,TC24165,TC24166,TC24356,TC25088,TC25089,TC25091,TC25092,TC25093,TC25094,TC25095,TC25096,TC25097,TC25112,TC25113,TC25114,TC25115,TC25116,TC25117,TC25118,TC25119,TC25121,TC25123,TC25125,TC25128,TC25129,TC25135,TC25140,TC25141,TC25142,TC25166,TC25168,TC25470,TC25472,TC25818,TC25821,TC26299,TC26310,TC26311,TC27034,TC27064,TC27066,TC29892,TC29893,TC29894,TC29895,TC29898,TC29899,TC29900,TC29902,TC29903,TC29904,TC29897,TC29901,TC29896,TC29936,TC29937,TC30096,TC30097,TC31230,TC31602,TC31603,TC27139,TC27142,TC27145,TC27213,TC27214,TC27215,TC27216,TC28346,TC31809,TC31810,TC33501,TC33502,TC33503,TC29929,TC29928,TC29927,TC29926,TC29923,TC29910,TC27179,TC27173,TC27171,TC27166,TC33496,TC29944,TC29909,TC29905,TC27175,TC27170,TC27169,TC27167,TC27165,TC27164,TC31801,TC31800,TC31799,TC31797,TC31793,TC31658,TC31657,TC31619,TC31608,TC31607,TC31659,TC31656,TC31609,TC31232,TC31223,TC31222,TC31220,TC31623,TC31620,TC31606,TC31605,TC31234,TC31233,TC31221,TC24408,TC24407,TC14961,TC27439,TC27448,TC27441,TC29823,TC27473,TC27450,TC28364,TC27470,TC27469,TC27224,TC29925,TC29924,TC28356,TC32206,TC30109,TC30110,TC31841,TC27443,TC27222,TC28367,TC28357,TC27445,TC27223,TC29921,TC27472,TC27471,TC27449,TC27442,TC27219,TC30112,TC31820,TC30108,TC34184,TC34180,TC34191,TC14404,TC34185,TC20269,TC20270,TC34181,TC20267,TC16018,TC19312,TC19313,TC19314,TC23163,TC23169,TC23359,TC20465,TC23164,TC23167,TC23261,TC27180,TC16017,TC16212,TC20376,TC20398,TC16214,TC22054,TC23165,TC29911,TC29939,TC22044,TC22138,TC23265,TC23271,TC27118,TC27185,TC29938,TC27178,TC34062,TC34063,TC34064,TC34065,TC43978,TC44280,TC44276,TC43971,TC44514,TC43948,TC45485,TC43541,TC43539,TC43540,TC43543,TC43587,TC43554,TC18634,TC43542,TC43555,TC43586,TC43588,TC43545,TC43553,TC43589,TC45567,TC45568,TC45569,TC45570,TC16073,TC16016,TC20211,TC16110,TC17697,TC17701,TC19369,TC20726,TC21793,TC21804,TC22109,TC22317,TC22615,TC20544,TC20546,TC20796,TC16067,TC22011,TC22171,TC22241,TC22356,TC22432,TC23159,TC26214,TC22147,TC23124,TC23125,TC23251,TC29946,TC24406,TC31822,TC34186,TC43544,TC33958,TC20513,TC23173,TC29812,TC29820,TC29821,TC24426,TC24423";
		String testCasesArray[] = testCases.split(",");
		//QueryFilter queryFilter = new QueryFilter("CreationDate", ">=", "2014-09-16").and(new QueryFilter("Type", "=", "Regression")).and(new QueryFilter("TestSets.ObjectID", "=", "29426036743"));
		for(String testCase:testCasesArray) {
			QueryFilter queryFilter = new QueryFilter("FormattedID", "=", testCase);
	    	QueryRequest defectRequest = new QueryRequest("testcases");
	    	defectRequest.setQueryFilter(queryFilter);
	    	defectRequest.setFetch(new Fetch("FormattedID", "LastVerdict", "TestSets", "Project"));
	    	//defectRequest.setProject("/project/23240411122"); 
	    	defectRequest.setScopedDown(true);
	    	defectRequest.setLimit(10000);
	    	QueryResponse projectDefects = restApi.query(defectRequest);
	    	JsonArray defectsArray = projectDefects.getResults();
	    	for(int i=0; i<defectsArray.size(); i++) {
	    		JsonElement elements =  defectsArray.get(i);
	    		JsonObject object = elements.getAsJsonObject();
	    		JsonObject project = object.get("Project").getAsJsonObject();
	            String projet = project.get("_refObjectName").getAsString();
	            String ref = object.get("_ref").getAsString();
	            System.out.println(i+"\t"+testCase+"\t"+ref.substring(ref.indexOf("/testcase/"))+"\t"+projet);
	    	}
		}
	}
    
    public static void postJenkinsJob() throws ClientProtocolException, IOException {
    	String username = "vsaqumo";
    	String password = "Welcome2";
		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), new UsernamePasswordCredentials(username, password));
		BasicScheme basicAuth = new BasicScheme();
		BasicHttpContext context = new BasicHttpContext();
		context.setAttribute("preemptive-auth", basicAuth);
		client.addRequestInterceptor(new PreemptiveAuth(), 0);
		String getUrl = "http://pjenkinsx.cloudapp.net:9090/view/212/job/212-iOS-1.6/lastBuild/";
		HttpGet get = new HttpGet(getUrl);
		try {
			HttpResponse response = client.execute(get, context);
    		HttpEntity entity = response.getEntity();
    		if (entity != null) {
	           String retSrc = EntityUtils.toString(entity); 
	           System.out.println(retSrc);
    		}
		}
    	catch (IOException e) {
			e.printStackTrace();
		}
    }

	private static void retrieveDefects(RallyRestApi restApi)
			throws IOException {
		
		QueryFilter queryFilter = new QueryFilter("FormattedID", "=", "DE8485");
    	QueryRequest defectRequest = new QueryRequest("defects");
    	defectRequest.setQueryFilter(queryFilter);
    	defectRequest.setFetch(new Fetch("State", "Name", "Tags", "Platform", "Release", "FormattedID", "Environment", "Priority", "LastUpdateDate", "SubmittedBy", "Owner", "Project", "ClosedDate"));
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
	            	  	System.out.println(jsonObj.get("Name"));
                     }
                }
            }
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

    static class PreemptiveAuth implements HttpRequestInterceptor {
    	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
			if (authState.getAuthScheme() == null) {
				AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if (authScheme != null) {
					Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost
							.getPort()));
					if (creds == null) {
						throw new HttpException("No credentials for preemptive authentication");
					}
					authState.setAuthScheme(authScheme);
					authState.setCredentials(creds);
				}
			}
		}
	}
}

