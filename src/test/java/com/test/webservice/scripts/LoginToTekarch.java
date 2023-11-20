package com.test.webservice.scripts;

import java.util.List;

import static org.hamcrest.Matchers.*;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

public class LoginToTekarch {
	private static String mytoken=null;
	
	
	@BeforeTest
	public void setUp() {
		RestAssured.baseURI="https://us-central1-qa01-tekarch-accmanager.cloudfunctions.net";
	}
	
	@Test
	public void logintoTekarchRequest() {
		
		Response response=RestAssured
		.given()
		.contentType(ContentType.JSON)
		.body("{\"username\":\"divyashree@ta.com\",\"password\":\"divya@123\"}")
		.when()
		.post("/login");
		
		response.then()
		.statusCode(201)
		.contentType(ContentType.JSON)
		.time(lessThan(4000L));
		
		mytoken=response.body().jsonPath().get("[0].token");
		System.out.println("token recieved="+mytoken);
		
		
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
