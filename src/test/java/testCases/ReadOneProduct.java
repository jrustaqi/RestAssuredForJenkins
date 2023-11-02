package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

//import static io.restassured.RestAssured.given;		//we need to import this static package manually in order to work for given(), when() and then()
import static io.restassured.RestAssured.*; //we need to type this import manually

import java.util.concurrent.TimeUnit;

public class ReadOneProduct {
	String baseURI;
	SoftAssert softAssert;
	/*
	ReadOneProduct
	HTTP method = GET 
	EndpointUrl = https://techfios.com/api-prod/api/product/read_one.php?id=9053
	Authorization : (Basic Auth)
	username = demo@techfios.com
	password = abc123
	Query Parameters:
	id = 9053
	Header/s:
	Content-Type = application/json
	Status Code: 200
	ResponseTime = <1500ms
	
	given()= all input details (baseURI, Headers, Authorization, Payload/Body,
	QueryParameters) when()= submit API requests (HTTP method, Endpoint/Resource)
	then()= validate response (status code, Headers, responseTime, Payload/Body)
	  
	 EndpoinUrl = BaseURI + Resource/Endpoint BaseURI =
	 https://techfios.com/api-prod/api/product Endpoint = /read.php
	 */
	
	public ReadOneProduct() {
		baseURI = "https://techfios.com/api-prod/api/product";
		softAssert = new SoftAssert();
	}
		
	@Test
	public void readOneProduct() {

		Response response = 
			given()
				.baseUri(baseURI)
				.header("Content-Type", "application/json")
				.auth().preemptive().basic("demo@techfios.com", "abc123")
				.queryParam("id", "9055").
			when()
				.get("/read_one.php").
			then()
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
		Assert.assertEquals(responseHeaderContentType, "application/json");

		String responseBody = response.getBody().asString();
		System.out.println("Response Body : " + responseBody);
	
		JsonPath jp = new JsonPath(responseBody); 
		String ProductName = jp.getString("name");
		System.out.println("Product Name : " + ProductName);
		Assert.assertEquals(ProductName, "Tesla Model X White Color");		//Hard Assert
		softAssert.assertEquals(ProductName, "Tesla Model X White Color", "Product Name is not matching!");	//Soft Assert
		
		String ProductDescription = jp.getString("description");
		System.out.println("Product Description : " + ProductDescription);
		Assert.assertEquals(ProductDescription, "Make Elan Mask Happy and More Rich!");
		
		String ProductPrice = jp.getString("price");
		System.out.println("Product Price : " + ProductPrice);
		Assert.assertEquals(ProductPrice, "1000000");
		
		String CategoryID = jp.getString("category_id");
		System.out.println("Category ID : " + CategoryID);
		Assert.assertEquals(CategoryID, "3");
		
		String CategoryName = jp.getString("category_name");
		System.out.println("Category Name : " + CategoryName);
		Assert.assertEquals(CategoryName, "Motors");
		
		softAssert.assertAll();	//we need this statement at the end of our method when we do soft assert

	}

}
