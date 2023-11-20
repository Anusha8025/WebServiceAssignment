package com.test.webservice.scripts;

import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Map;

import org.hamcrest.Matcher;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.webservice.models.response.LoginResponsePOJO;
import com.test.webservice.models.response.NewUserResPOJO;
import com.test.webservice.models.response.UpdateUserResPOJO;
import com.test.webservice.models.request.LoginDataPOJO;
import com.test.webservice.models.request.UpdateUserPOJO;
import com.test.webservice.models.response.DeleteUserResPOJO;
import com.test.webservice.models.response.GetAllUserPOJO;
import com.test.webservice.models.request.CreateNewUserPOJO;
import com.test.webservice.models.request.DeleteUserPOJO;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.module.jsv.JsonSchemaValidator;
//import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.response.ResponseBodyExtractionOptions;
import io.restassured.response.Validatable;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class TekarchApis {
	String extractedToken=null;
	@BeforeClass
	public void init() {
		RestAssured.baseURI="https://us-central1-qa01-tekarch-accmanager.cloudfunctions.net/";
		
	}


	@Test
	public void loginToApi() {
		LoginDataPOJO data=new LoginDataPOJO();
		data.setUsername("anusha@tekarch.com");
		data.setPassword("Admin123");	
		List<LoginResponsePOJO> reslist= RestAssured.given()
		.log().all() //log all info
		.contentType(ContentType.JSON)
		.body(data)
		.when()
		.post("login")
		//DESERIALIZATION
		.as(new TypeRef<List<LoginResponsePOJO>>() {});// = LoginResponsePOJO[].class
		
		extractedToken=	reslist.get(0).getToken();
		System.out.println("token extracted from jsonpath="+extractedToken);
		
		
		
		
	}
	
	//validate schema for get users
	@Test(dependsOnMethods = "loginToApi")
	public void getAllRecordsSchema() {
		
		Header ob=new Header("token", extractedToken);
		Response res=RestAssured
		.given()
		.log().all()
		.header(ob)
		.when()
		.get("/getdata");
		res.then()
		.log().body()
		.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("GetAllUsersSchema.json"));
		//res.prettyPrint();
	}
	
	//GET ALL USERS
	@Test(dependsOnMethods = "loginToApi")
	public void getUsers() {
		System.out.println("inside getUsers token="+extractedToken);
		Header ob=new Header("token", extractedToken);
		//SERIALIZATION
		List<GetAllUserPOJO> res=(RestAssured.given()
		.log().all()
		.header(ob)
		.when()
		.get("/getdata")
		.as(new TypeRef<List<GetAllUserPOJO>>() {}));	
		

	//	System.out.println("size = " +res.size());
		
		//System.out.println("total number of records="+res.body().jsonPath().get("size()"));
	}

	//validate schema for create new users
		@Test(dependsOnMethods = "loginToApi")
		public void createNewUSerSchema() {
			CreateNewUserPOJO data=new CreateNewUserPOJO();
			data.setAccountno("TA-DecAPI1");
			data.setDepartmentno("2");
			data.setSalary("8000");
			data.setPincode("123466");
			
			Header ob=new Header("token", extractedToken);
			Response res=RestAssured
			.given()
			.log().all()
			.header(ob)
			.contentType(ContentType.JSON)
			.body(data)
			.when()
			.post("/addData");
			res.then()
			.log().body()
			.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("CreateNewUserSchema.json"));
			
		}
		
	//Create USER
	@Test(dependsOnMethods = "loginToApi")
	public void createNewUser() {
		//CreateNewUserPOJO
		CreateNewUserPOJO data=new CreateNewUserPOJO();
		data.setAccountno("TA-DecAPI1");
		data.setDepartmentno("2");
		data.setSalary("8000");
		data.setPincode("123466");
		
		System.out.println("inside createNewUser token="+extractedToken);
	
		Header ob=new Header("token", extractedToken);
		//get the response as data -- working
		NewUserResPOJO res = RestAssured.given()
		.log().all()
		.header(ob)
		.contentType(ContentType.JSON)
		.body(data)
		.when()
		.post("/addData")
		.as(new TypeRef<NewUserResPOJO>() {});
		
		System.out.println("status = " + res.getstatus());
		Assert.assertEquals(res.getstatus(), "success");
		System.out.println("New User Created Successfully");
	}
	
	//validate schema for update new users
			@Test(dependsOnMethods = "loginToApi")
			public void updateUSerSchema() {
				UpdateUserPOJO data=new UpdateUserPOJO();
				data.setAccountno("TA-DecAPI1");
				data.setDepartmentno("2");
				data.setSalary("5000");
				data.setPincode("123466");
				data.setUserid("vAAqN9fUcxYvgeJkx2wH");
				data.setId("wLFG8tlrnroecB0pVIdS");
				
				Header ob=new Header("token", extractedToken);
				Response res=RestAssured
				.given()
				.log().all()
				.header(ob)
				.contentType(ContentType.JSON)
				.body(data)
				.when()
				.put("/updateData");
				res.then()
				.log().body()
				.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("UpdateUserSchema.json"));
				
			}
			
	//Update USER
	@Test(dependsOnMethods = {"loginToApi","createNewUser"})
	public void updateUser() {
		
		UpdateUserPOJO data=new UpdateUserPOJO();
		data.setAccountno("TA-DecAPI1");
		data.setDepartmentno("2");
		data.setSalary("5000");
		data.setPincode("123466");
		data.setUserid("vAAqN9fUcxYvgeJkx2wH");
		data.setId("wLFG8tlrnroecB0pVIdS");
		
		System.out.println("inside updateUser token="+extractedToken);
		Header ob=new Header("token", extractedToken);
		UpdateUserResPOJO res=RestAssured.given()
		.header(ob)
		.contentType(ContentType.JSON)
		.body(data)
		.when()
		.put("/updateData")
		.as(new TypeRef<UpdateUserResPOJO>() {});
		
		//.time(Matchers.lessThan(10000L));
		//res.then().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("GetAllUsersSchema.json"));
		Assert.assertEquals(res.getstatus(), "success");
		System.out.println(" User Updated Successfully");
	}
	
	//validate schema for update new users
	@Test(dependsOnMethods = "loginToApi")
	public void deleteUSerSchema() {
		DeleteUserPOJO data=new DeleteUserPOJO();
		data.setUserid("vAAqN9fUcxYvgeJkx2wH");
		data.setId("wLFG8tlrnroecB0pVIdS");
		
		Header ob=new Header("token", extractedToken);
		Response res=RestAssured
		.given()
		.log().all()
		.header(ob)
		.contentType(ContentType.JSON)
		.body(data)
		.when()
		.delete("/deleteData");
		res.then()
		.log().body()
		.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("DeleteUserSchema.json"));
		
	}
	
	//Delete USER
		@Test(dependsOnMethods = "loginToApi")
		public void deleteUser() {
			
			DeleteUserPOJO data=new DeleteUserPOJO();
			data.setUserid("vAAqN9fUcxYvgeJkx2wH");
			data.setId("wLFG8tlrnroecB0pVIdS");
			
			System.out.println("inside deleteUser token="+extractedToken);
			Header ob=new Header("token", extractedToken);
			DeleteUserResPOJO res=RestAssured.given()
			.header(ob)
			.contentType(ContentType.JSON)
			.body(data)
			.when()
			.delete("/deleteData")
			.as(new TypeRef<DeleteUserResPOJO>() {});
			
			
			//.time(Matchers.lessThan(10000L));
			//res.then().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("GetAllUsersSchema.json"));
			Assert.assertEquals(res.getstatus(), "success");
			System.out.println(" User Deleted Successfully");
		}
		
		
		
		
}














