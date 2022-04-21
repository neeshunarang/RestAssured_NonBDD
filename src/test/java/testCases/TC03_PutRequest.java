package testCases;


import java.io.IOException;
import java.util.Map;

import org.hamcrest.MatcherAssert;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import baseClass.TestBase;
import utilities.XLUtils;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class TC03_PutRequest extends TestBase{
		@Test(dataProvider="jobDataProvider")
		public void CreateJobs(String jobId,String title,String compName,String location,String type) {
			RestAssured.baseURI="https://jobs123.herokuapp.com/Jobs";
			httpRequest = RestAssured.given();
			
			
			JSONObject requestParams = new JSONObject();
			requestParams.put("Job Id", jobId);
			requestParams.put("Job Title", title);
			requestParams.put("Job Company Name", compName);
			requestParams.put("Job Location", location);
			requestParams.put("Job Type", type);
					
			
			httpRequest.header("Content-Type","application/json");
			httpRequest.body(requestParams.toJSONString());   //Attach data to the request
			response = httpRequest.request(Method.PUT); 
			
			//capture response body to perform validations
			String responseBody =  response.getBody().asString().replaceAll("NaN", "\"1 hr\"");
			MatcherAssert.assertThat(responseBody,JsonSchemaValidator.matchesJsonSchemaInClasspath(jsonpath));					
			//Status Code Validation
			int statusCode = response.getStatusCode();
			System.out.println("Status code for posting new Job is: " + statusCode);
			Assert.assertEquals(statusCode, 200);
		
			//Fetch the required Key to validate updated results
			if (statusCode==200)
			{					
				//DB Validation - Checking DB through GET Request, records are updated or not
				httpRequest = RestAssured.given();
				response=httpRequest.request(Method.GET);
				JsonPath jpath=JsonPath.from(response.getBody().asString().replaceAll("NaN", "\"1 hr\""));
				Map<String,String> jobIds =jpath.get("data['Job Id']");
				String reqKey=null;
				for(String key: jobIds.keySet()) 
				{
					if(jobIds.get(key).equals(jobId)) {
						reqKey=key; 
						break;
					}
				}
				//System.out.println(reqKey);
				Assert.assertEquals(jpath.get("data['Job Id']."+ reqKey),jobId);
				Assert.assertEquals(jpath.get("data['Job Title']."+ reqKey),title);
				Assert.assertEquals(jpath.get("data['Job Company Name']."+ reqKey),compName);
				Assert.assertEquals(jpath.get("data['Job Location']."+ reqKey),location);
				Assert.assertEquals(jpath.get("data['Job Type']."+ reqKey),type);
				
			 }
				
		}
	
		@DataProvider(name="jobDataProvider")
		public String[][] getData() throws IOException {
			String sheet = "Put";
			int rowNum = XLUtils.getRowCount(excelPath,sheet);
			int colNum= XLUtils.getCellCount(excelPath, sheet, 1);
			String jobData[][]=new String[rowNum][colNum];
			for(int i=1;i<=rowNum;i++) {
				for(int j=0;j<colNum;j++) {
					jobData[i-1][j]=XLUtils.getCellData(excelPath, sheet, i, j);
				}
			}
			return (jobData);
		}
}
