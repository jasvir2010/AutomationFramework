package servicesutilities;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * March 4, 2022
 * @author saurabh.shukla
 */

public class RequestOperations {

    public void getRequest(String username, String password, String baseURL, String serviceURI){

        RestAssured.baseURI = baseURL;
        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.request(Method.GET, "/"+serviceURI+"?username="+username+ "&password="+password);

        String responseBody = response.getBody().asString();

        System.out.println("Response Body is =>  " + responseBody);

        System.out.println("Hello webservices has been executed");
    }

    public void postRequest(String username, String password, String baseURL, String serviceURI){

        RestAssured.baseURI = baseURL;
        RequestSpecification httpRequest = RestAssured.given();

        Response response = httpRequest.request(Method.POST, "/"+serviceURI+"?username="+username+ "&password="+password);

        String responseBody = response.getBody().asString();

        System.out.println("Response Body is =>  " + responseBody);

        System.out.println("Hello webservices has been executed");

    }

    public void formatHeader(){
    	
    	//to be implemented
    }

    public void formatRequest(){
    	
    	//to be implemented
    }


    public void setHeader(Object key, Object value){

    	//to be implemented
    	
    }

    public void setBodyParam(Object key, Object value){

    	
    	//to be implemented
    }
}
