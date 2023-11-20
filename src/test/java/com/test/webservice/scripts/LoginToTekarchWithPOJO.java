package com.test.webservice.scripts;

import java.util.List;

import static org.hamcrest.Matchers.*;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.test.webservice.models.response.LoginResponsePOJO;
import com.test.webservice.models.request.LoginDataPOJO;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

public class LoginToTekarchWithPOJO {
	private static String mytoken=null;
	
	
	@BeforeTest
	public void setUp() {
		RestAssured.baseURI="https://us-central1-qa01-tekarch-accmanager.cloudfunctions.net";
	}
	
	
	
	@Test
	public void logintoTekarchRequestWithPOJO() {
		
		LoginDataPOJO data=new LoginDataPOJO();
		data.setUsername("divyashree@ta.com");
		data.setPassword("divya@123"); 
		List<LoginResponsePOJO> reslist=RestAssured.given()
		.contentType(ContentType.JSON)
		.body(data)
		.when()
		.post("login").as(new TypeRef<List<LoginResponsePOJO>>() {});// = LoginResponsePOJO[].class
		mytoken=	reslist.get(0).getToken();
		System.out.println("token extracted from jsonpath="+mytoken);
		
	}
	
	
	
	
	@Test(dependsOnMethods = "logintoTekarchRequest")
	public void getAllRecords() {
		Header header=new Header("token",mytoken);
		Response res=RestAssured
		.given()
		.header(header)
		.when()
		.get("/getdata");
		res.prettyPrint();
		
		
	}
		
	
	
}
