package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

//import static io.restassured.RestAssured.given;		//we need to import this static package manually in order to work for given(), when() and then()
import static io.restassured.RestAssured.*; //we need to type this import manually

import java.util.HashMap;
import java.util.Map;

public class CreateOneProduct {
	String baseURI;
	String createPayloadPath;
	HashMap<String, String> createPayload;
	String firstProductId;
	/*
	CreateOneProduct
	HTTP method = POST 
	EndpointUrl = https://techfios.com/api-prod/api/product/create.php
	Authorization : (Basic Auth)
	username = demo@techfios.com
	password = abc123
	Header/s:
	Content-Type = application/json; charset=UTF-8
	Status Code: 201
	ResponseTime = <1500ms
	Body:
	
	to Create One Product, we need these steps to perform to avoid any issues due to any changes/updates made by others ,
	1- Create Product
	2- Read All Products and extract the firstProductID
	3- Read One Product (firstProductID) and compare
	*/

	public CreateOneProduct() {
		baseURI = "https://techfios.com/api-prod/api/product";
		createPayloadPath = "src/main/java/data/CreatePayload.json";
		createPayload = new HashMap<String, String>();
	}
	
	public Map<String, String> createPayloadMap(){	//we can use Map and get our Payload without using File ***
		createPayload.put("name", "Trip to Moon");
		createPayload.put("description", "Elan Mask and SpaceX want your money!");
		createPayload.put("price", "100000000");
		createPayload.put("category_id", "3");
		createPayload.put("category_name", "Motors");
		
		return createPayload;
		
	}
			
	@Test(priority=1)
	public void createOneProduct() {
		
		System.out.println("Create Payload Map :" + createPayloadMap());

		Response response = 
			given()
				.baseUri(baseURI)
				.header("Content-Type", "application/json; charset=UTF-8")
				.auth().preemptive().basic("demo@techfios.com", "abc123")
	//			.body(new File(createPayload)). //to get the "name" or "price" out of our file (CreatePayload.json), we can use JsonPath **
				.body(createPayloadMap()).	//OR to get the "name", "price", "description" and ... we can use HashMap ***
			when()
				.post("/create.php").
			then()
				.extract().response();
		
		int responseStatusCode = response.getStatusCode();
		Assert.assertEquals(responseStatusCode, 201);
		System.out.println("Response Status Code :" + responseStatusCode);

		String responseHeaderContentType = response.getHeader("Content-Type");
		Assert.assertEquals(responseHeaderContentType, "application/json; charset=UTF-8");
		System.out.println("Response Header ContentType :" + responseHeaderContentType);

		String responseBody = response.getBody().asString();
		System.out.println("Response Body : " + responseBody);

		JsonPath jp = new JsonPath(responseBody); 
		
		String ProductMessage = jp.getString("message");
		Assert.assertEquals(ProductMessage, "Product was created.");
		System.out.println("Product Message : " + ProductMessage);
		
//		JsonPath jp2 = new JsonPath(new File(createPayloadPath)); // ** to get the "name" or "price" out of our file (CreatePayload.json) 
//		String name = jp2.getString("name");
//		System.out.println("Expected Product Name" + name);
	}
	
	@Test(priority=2)
	public void readAllProducts() {
		Response response = 
			given()
				.baseUri(baseURI)
				.header("Content-Type", "application/json; charset=UTF-8").
				auth().preemptive().basic("demo@techfios.com", "abc123").
			when()
				.get("/read.php").
			then()
				.extract().response();
		
		String responseBody = response.getBody().asString();
//		System.out.println("Response Body : " + responseBody);

		JsonPath jp = new JsonPath(responseBody);
		firstProductId = jp.getString("records[0].id");
		System.out.println("First Product ID : " + firstProductId);
	}
	
	@Test(priority=3)
	public void readOneProduct() {

		Response response = 
			given()
				.baseUri(baseURI)
				.header("Content-Type", "application/json")
				.auth().preemptive().basic("demo@techfios.com", "abc123")
				.queryParam("id", firstProductId).
			when()
				.get("/read_one.php").
			then()
				.extract().response();
		
		String actualResponseBody = response.getBody().asString();
		System.out.println("Actual Response Body : " + actualResponseBody);
	
		JsonPath jp = new JsonPath(actualResponseBody); 

		String actualProductName = jp.getString("name");
		Assert.assertEquals(actualProductName, createPayloadMap().get("name"));
		System.out.println("Actual Product Name : " + actualProductName);
		
		String actualProductDescription = jp.getString("description");
		Assert.assertEquals(actualProductDescription, createPayloadMap().get("description"));
		System.out.println("Actual Product Description : " + actualProductDescription);
		
		String actualProductPrice = jp.getString("price");
		Assert.assertEquals(actualProductPrice, createPayloadMap().get("price"));
		System.out.println("Actual Product Price : " + actualProductPrice);
		
		String actualCategoryID = jp.getString("category_id");
		Assert.assertEquals(actualCategoryID, createPayloadMap().get("category_id"));
		System.out.println("Actual Category ID : " + actualCategoryID);
		
		String actualCategoryName = jp.getString("category_name");
		Assert.assertEquals(actualCategoryName, createPayloadMap().get("category_name"));
		System.out.println("Actual Category Name : " + actualCategoryName);
	}
}
