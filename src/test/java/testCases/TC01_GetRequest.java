package testCases;

import java.io.IOException;

import org.hamcrest.MatcherAssert;
import org.testng.Assert;
import org.testng.annotations.Test;

import baseClass.*;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import utilities.XLUtils;

public class TC01_GetRequest extends TestBase{
		@Test(priority = 1)
		public void GetAllJobs () {
			RestAssured.baseURI="https://jobs123.herokuapp.com/Jobs";
			httpRequest = RestAssured.given();
			response=httpRequest.request(Method.GET);
		}
		
		@Test (priority = 2)
		void CheckResponseBody() //Validating the Response
		{
			String responseBody =  response.getBody().asPrettyString();
			System.out.println("Response Body is " + responseBody);
			Assert.assertTrue(responseBody!=null);
		}
		
		@Test(priority = 3)
		void CheckStatusCode() 
		{
			int statusCode = response.getStatusCode();
			System.out.println("Status code is: " + statusCode);
			Assert.assertEquals(statusCode, 200);
		}
		
		@Test(priority = 4)
		void ValidateJsonSchema() //Vaidating with JSON Schema
		{
			String responseBody =  response.getBody().asString().replaceAll("NaN", "\"1 hr\"");
			MatcherAssert.assertThat(responseBody,JsonSchemaValidator.matchesJsonSchemaInClasspath(jsonpath));
			//writeToExcel();
		}
	
		
		void writeToExcel() throws IOException 
		{
			String sheet = "Get";
						
			String responseBody =  response.getBody().asPrettyString();
			JsonPath jpath=JsonPath.from(responseBody.replaceAll("NaN", "\"1 hr\""));
			int maxjobs=jpath.get("data['Job Id'].size()");
			for(int i=1;i<=maxjobs;i++) 
			{
				XLUtils.setCellData(excelPath, sheet,i,0, jpath.get("data['Job Id']." + (i-1)).toString());
				XLUtils.setCellData(excelPath, sheet,i,1, jpath.get("data['Job Title']." + (i-1)).toString());
				XLUtils.setCellData(excelPath, sheet,i,2, jpath.get("data['Job Company Name']." + (i-1)).toString());
				XLUtils.setCellData(excelPath, sheet,i,3, jpath.get("data['Job Location']." + (i-1)).toString());
				XLUtils.setCellData(excelPath, sheet,i,4, jpath.get("data['Job Type']." + (i-1)).toString());
				XLUtils.setCellData(excelPath, sheet,i,5, jpath.get("data['Job Posted time']." + (i-1)).toString());
				XLUtils.setCellData(excelPath, sheet,i,6, jpath.get("data['Job Description']." + (i-1)).toString());
			}
			
		}
}
