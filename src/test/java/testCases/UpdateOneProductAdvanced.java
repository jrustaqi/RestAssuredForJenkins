package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

//import static io.restassured.RestAssured.given;		//we need to import this static package manually in order to work for given(), when() and then()
import static io.restassured.RestAssured.*; //we need to type this import manually

import java.util.HashMap;
import java.util.Map;

public class UpdateOneProductAdvanced {
	String baseURI;
	String createPayloadPath;
	HashMap<String, String> createPayload;
	String firstProductId;
	HashMap<String, String> updatePayload;
	String updatedProductId;
	/*
	UpdateOneProduct
	HTTP method = PUT  
	EndpointUrl = https://techfios.com/api-prod/api/product/update.php
	Authorization : (Basic Auth)
	username = demo@techfios.com
	password = abc123
	Header/s:
	Content-Type = application/json; charset=UTF-8
	Status Code: 200
	ResponseTime = <1500ms
	Body:
	{
	"id" : "9055",
    "name" : "Tesla Model X White Color",
	"description" : "Make Elan Mask Happy and More Rich!",
    "price" : "50,0000",
    "category_id" : 3
	}
	
	to Update One Product, we need these steps to perform to avoid any issues due to any changes/updates made by others ,
	1- Create a Product
	2- Read All Products and extract the firstProductID
	3- Update Product (firstProductID) and compare
	4- Read Updated Product (firstProductID) and compare
	*/

	public UpdateOneProductAdvanced() {
		baseURI = "https://techfios.com/api-prod/api/product";
		createPayloadPath = "src/main/java/data/CreatePayload.json";
		createPayload = new HashMap<String, String>();
		updatePayload = new HashMap<String, String>();
	}
	
	public Map<String, String> createPayloadMap(){	//we can use Map and get our Payload without using File ***
		createPayload.put("name", "Trip to Moon");
		createPayload.put("description", "Elan Mask and SpaceX want your money!");
		createPayload.put("price", "100000000");
		createPayload.put("category_id", "3");
		createPayload.put("category_name", "Motors");
		return createPayload;
	}
	
	public Map<String, String> updatePayloadMap(){	//we can use Map and get our Payload without using File ***
		updatePayload.put("id", updatedProductId);
		updatePayload.put("name", "My MotoryCycle");
		updatePayload.put("description", "The fastest you ever ride!!");
		updatePayload.put("price", "290");
		updatePayload.put("category_id", "3");
		updatePayload.put("category_name", "Motors");
		return updatePayload;
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
		updatedProductId = firstProductId;
	}
			
	@Test(priority=3)
	public void updateOneProduct() {
		Response response = 
			given()
				.baseUri(baseURI)
				.header("Content-Type", "application/json; charset=UTF-8")
				.auth().preemptive().basic("demo@techfios.com", "abc123")
	//			.body(new File(createPayload)). //to get the "name" or "price" out of our file (CreatePayload.json), we can use JsonPath **
				.body(updatePayloadMap()).	//OR to get the "name", "price", "description" and ... we can use HashMap ***
			when()
				.post("/update.php").
			then()
				.extract().response();
		
		int responseStatusCode = response.getStatusCode();
		Assert.assertEquals(responseStatusCode, 200);
		System.out.println("Response Status Code :" + responseStatusCode);

		String responseHeaderContentType = response.getHeader("Content-Type");
		Assert.assertEquals(responseHeaderContentType, "application/json; charset=UTF-8");
		System.out.println("Response Header ContentType :" + responseHeaderContentType);

		String responseBody = response.getBody().asString();
		System.out.println("Response Body : " + responseBody);

		JsonPath jp = new JsonPath(responseBody); 
		
		String ProductMessage = jp.getString("message");
		Assert.assertEquals(ProductMessage, "Product was updated.");
		System.out.println("Product Message : " + ProductMessage);
	}
	
	@Test(priority=4)
	public void readOneUpdatedProduct() {

		Response response = 
			given()
				.baseUri(baseURI)
				.header("Content-Type", "application/json")
				.auth().preemptive().basic("demo@techfios.com", "abc123")
				.queryParam("id", updatePayloadMap().get("id")).
			when()
				.get("/read_one.php").
			then()
				.extract().response();
		
		String actualResponseBody = response.getBody().asString();
		System.out.println("Actual Response Body : " + actualResponseBody);
	
		JsonPath jp = new JsonPath(actualResponseBody); 

		String actualProductName = jp.getString("name");
		Assert.assertEquals(actualProductName, updatePayloadMap().get("name"));
		System.out.println("Actual Product Name : " + actualProductName);
		
		String actualProductDescription = jp.getString("description");
		Assert.assertEquals(actualProductDescription, updatePayloadMap().get("description"));
		System.out.println("Actual Product Description : " + actualProductDescription);
		
		String actualProductPrice = jp.getString("price");
		Assert.assertEquals(actualProductPrice, updatePayloadMap().get("price"));
		System.out.println("Actual Product Price : " + actualProductPrice);
		
		String actualCategoryID = jp.getString("category_id");
		Assert.assertEquals(actualCategoryID, updatePayloadMap().get("category_id"));
		System.out.println("Actual Category ID : " + actualCategoryID);
		
		String actualCategoryName = jp.getString("category_name");
		Assert.assertEquals(actualCategoryName, updatePayloadMap().get("category_name"));
		System.out.println("Actual Category Name : " + actualCategoryName);
	}
}
