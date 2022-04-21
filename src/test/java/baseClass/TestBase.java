package baseClass;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class TestBase {
	public static RequestSpecification httpRequest;
	public static Response response;
	
	public static String excelPath =System.getProperty("user.dir")+"/src/test/java/utilities/JobsAPI.xlsx";
	public String jsonpath ="JsonSchema\\JobsSchema.json";
	
}

