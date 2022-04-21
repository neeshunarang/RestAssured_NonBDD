package testCases;


import java.io.IOException;

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
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class TC04_DeleteRequest extends TestBase{
		@Test(dataProvider="jobDataProvider")
		public void CreateJobs(String jobId) {
			RestAssured.baseURI="https://jobs123.herokuapp.com/Jobs";
			httpRequest = RestAssured.given();
			
			JSONObject requestParams = new JSONObject();
			requestParams.put("Job Id", jobId);
			
			httpRequest.header("Content-Type","application/json");
			httpRequest.body(requestParams.toJSONString());   //Attach data to the request
			response = httpRequest.request(Method.DELETE); 
			
			String responseBody =  response.getBody().asString().replaceAll("NaN", "\"1 hr\"");
			MatcherAssert.assertThat(responseBody,JsonSchemaValidator.matchesJsonSchemaInClasspath(jsonpath));	
			//Status Code Validation
			int statusCode = response.getStatusCode();
			System.out.println("Status code for posting new Job is: " + statusCode);
			Assert.assertEquals(statusCode, 200);
							
		}
		@DataProvider(name="jobDataProvider")
		public String[][] getData() throws IOException {
			String sheet = "Delete";
			int rowNum = XLUtils.getRowCount(excelPath,sheet);
			int colNum= XLUtils.getCellCount(excelPath, sheet, 1);
			String jobData[][]=new String[rowNum][colNum];
			for(int i=1;i<=rowNum;i++) {
				jobData[i-1][0]=XLUtils.getCellData(excelPath, sheet, i,0);
			}
			
			return (jobData);
		}
}
