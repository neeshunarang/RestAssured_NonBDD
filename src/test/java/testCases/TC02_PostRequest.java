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
import io.restassured.path.json.JsonPath;

public class TC02_PostRequest extends TestBase{
		@Test(dataProvider="jobDataProvider")
		public void CreateJobs(String jobId,String title,String compName,String location,String type,String postTime,String desc) {
			RestAssured.baseURI="https://jobs123.herokuapp.com/Jobs";
			httpRequest = RestAssured.given();
			
			JSONObject requestParams = new JSONObject();
		
			requestParams.put("Job Id", jobId);
			requestParams.put("Job Title", title);
			requestParams.put("Job Company Name", compName);
			requestParams.put("Job Location", location);
			requestParams.put("Job Type", type);
			requestParams.put("Job Posted time", postTime);
			requestParams.put("Job Description",desc);
			
			//Add a header stating the Request body is a JSON
			httpRequest.header("Content-Type","application/json");
			
			//Add the Json to the body of the request
			httpRequest.body(requestParams.toJSONString());  
			//POST Request
			response = httpRequest.request(Method.POST); 
				
			//capture response body to perform validations
			String responseBody =  response.getBody().asString().replaceAll("NaN", "\"1 hr\"");
			MatcherAssert.assertThat(responseBody,JsonSchemaValidator.matchesJsonSchemaInClasspath(jsonpath));	
		   //Status Code Validation
			int statusCode = response.getStatusCode();
			System.out.println("Status code for posting new Job is: " + statusCode);
			Assert.assertEquals(statusCode, 200);
	
			if (statusCode==200)
			{	
				//DB Validation - Checking DB through GET Request, records are created or not
				httpRequest = RestAssured.given();
				response=httpRequest.request(Method.GET);
				JsonPath jpath=JsonPath.from(response.getBody().asString().replaceAll("NaN", "null"));
				int maxJobs=jpath.get("data['Job Id'].size()");
				Assert.assertEquals(jpath.get("data['Job Id']."+ (maxJobs-1)),jobId);
				Assert.assertEquals(jpath.get("data['Job Title']."+ (maxJobs-1)),title);
				Assert.assertEquals(jpath.get("data['Job Company Name']."+ (maxJobs-1)),compName);
				Assert.assertEquals(jpath.get("data['Job Location']."+ (maxJobs-1)),location);
				Assert.assertEquals(jpath.get("data['Job Type']."+ (maxJobs-1)),type);
				Assert.assertEquals(jpath.get("data['Job Posted time']."+ (maxJobs-1)),postTime);
				Assert.assertEquals(jpath.get("data['Job Description']."+ (maxJobs-1)),desc);
			 }
		}
	
		@DataProvider(name="jobDataProvider")
		public Object[][] getData() throws IOException {
			
			String sheet = "Post";
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
