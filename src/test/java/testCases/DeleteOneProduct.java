package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

//import static io.restassured.RestAssured.given;		//we need to import this static package manually in order to work for given(), when() and then()
import static io.restassured.RestAssured.*; //we need to type this import manually

import java.util.HashMap;
import java.util.Map;

public class DeleteOneProduct {
	String baseURI;
	String firstProductId;
	HashMap<String, String> deletePayload;
	/*
	DeleteOneProduct
	HTTP Method = DELETE
	EndpointUrl = https://techfios.com/api-prod/api/product/delete.php
	Authorization : (Basic Auth)
	username = demo@techfios.com
	password = abc123
	Header/s:
	Content-Type = application/json; charset=UTF-8
	Status Code: 200
	Payload/Body:
	{
    "id": "9070"
	}
	
	to Delete One Product, we need these steps to perform to avoid any issues due to any changes/updates made by others ,
	1- Delete Product (firstProductID) and compare
	2- Read Deleted Product (firstProductID) and compare
	*/

	public DeleteOneProduct() {
		baseURI = "https://techfios.com/api-prod/api/product";
		deletePayload = new HashMap<String, String>();
	}
	
	public Map<String, String> deletePayloadMap(){	//we can use Map and get our Payload without using File ***
		deletePayload.put("id", "9070");
		deletePayload.put("name", "Trip to Moon");
		deletePayload.put("description", "Elan Mask and SpaceX want your money!");
		deletePayload.put("price", "100000000");
		deletePayload.put("category_id", "3");
		deletePayload.put("category_name", "Motors");
		return deletePayload;
	}
	
	@Test(priority=1)
	public void deleteOneProduct() {
		Response response = 
			given()
				.baseUri(baseURI)
				.header("Content-Type", "application/json; charset=UTF-8")
				.auth().preemptive().basic("demo@techfios.com", "abc123")
	//			.body(new File(createPayload)). //to get the "name" or "price" out of our file (CreatePayload.json), we can use JsonPath **
				.body(deletePayloadMap()).	//OR to get the "name", "price", "description" and ... we can use HashMap ***
			when()
				.delete("/delete.php").
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
		Assert.assertEquals(ProductMessage, "Product was deleted.");
		System.out.println("Product Message : " + ProductMessage);
	}
	
	@Test(priority=2)
	public void readOneDeletedProduct() {

		Response response = 
			given()
				.baseUri(baseURI)
				.header("Content-Type", "application/json")
				.auth().preemptive().basic("demo@techfios.com", "abc123")
				.queryParam("id", deletePayloadMap().get("id")).
			when()
				.get("/read_one.php").
			then()
				.extract().response();
		
		int responseStatusCode = response.getStatusCode();
		Assert.assertEquals(responseStatusCode, 404);
		System.out.println("Response Status Code :" + responseStatusCode);
		
		String actualResponseBody = response.getBody().asString();
		System.out.println("Actual Response Body : " + actualResponseBody);
	
		JsonPath jp = new JsonPath(actualResponseBody); 

		String actualDeleteMessage = jp.getString("message");
		String expectedDeleteMessage = "Product does not exist.";
		Assert.assertEquals(actualDeleteMessage, expectedDeleteMessage);
		System.out.println("Actual Delete Message : " + actualDeleteMessage);
	}
}
