package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

//import static io.restassured.RestAssured.given;		//we need to import this static package manually in order to work for given(), when() and then()
import static io.restassured.RestAssured.*; //we need to type this import manually

import java.util.concurrent.TimeUnit;

public class ReadAllProducts {
	/*
	ReadAllProducts
	HTTP method = GET 
	EndpointUrl = https://techfios.com/api-prod/api/product/read.php
	Authorization : (Basic Auth)
	username = demo@techfios.com
	password = abc123
	Header/s:
	Content-Type = application/json; charset=UTF-8
	Status Code: 200
	ResponseTime = <1500ms
	  
	given()= all input details (baseURI, Headers, Authorization, Payload/Body,
	QueryParameters) when()= submit API requests (HTTP method, Endpoint/Resource)
	then()= validate response (status code, Headers, responseTime, Payload/Body)
	  
	EndpoinUrl = BaseURI + Resource/Endpoint BaseURI =
	https://techfios.com/api-prod/api/product Endpoint = /read.php
	*/

	@Test
	public void readAllProducts() {

		Response response = 
		given()
//			.log().all() //will print all in console
			.baseUri("https://techfios.com/api-prod/api/product")
			.header("Content-Type", "application/json; charset=UTF-8")
			.auth().preemptive().basic("demo@techfios.com", "abc123").
		when()
//			.log().all()
			.get("/read.php").
		then()
//			.log().all()
			.extract().response();
		
		long responseTime = response.getTimeIn(TimeUnit.MILLISECONDS);
		System.out.println("Respoonse Time :" + responseTime);

		if (responseTime <= 2500) {
			System.out.println("Response time is within Range.");
		} else {
			System.out.println("Response time is out of Range.");
		}

		int responseStatusCode = response.getStatusCode();
		System.out.println("Response Status Code :" + responseStatusCode);
		Assert.assertEquals(responseStatusCode, 200);

		String responseHeaderContentType = response.getHeader("Content-Type");
		System.out.println("Response Header ContentType :" + responseHeaderContentType);
		Assert.assertEquals(responseHeaderContentType, "application/json; charset=UTF-8");

		String responseBody = response.getBody().asString();
//		System.out.println("Response Body : " + responseBody);

		JsonPath jp = new JsonPath(responseBody); // by calling JsonPath it changes the String into Json
		String firstProductId = jp.getString("records[0].id"); // this goes to the first products and gets the Product ID to validate the body
		System.out.println("First Product ID : " + firstProductId);

		if (firstProductId != null) {
			System.out.println("Products list is not empty.");
		} else {
			System.out.println("Products list is empty!");
		}

	}

}
