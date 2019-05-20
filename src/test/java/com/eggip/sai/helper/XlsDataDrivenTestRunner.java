package com.eggip.sai.helper;

import java.net.URL;

import com.eggip.sai.helper.TestDataTemplate.TestData;

import org.apache.commons.lang3.tuple.Pair;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class XlsDataDrivenTestRunner {

    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

    
    public static Pair<Boolean, String> run(URL url, RequestType requestType, TestData testData) {
        RequestSpecification specification = RestAssured.given().contentType(JSON_CONTENT_TYPE).request()
                .body(prepareRequestBody(testData));
        requestType = requestType == null ? RequestType.GET : requestType;
        Response response = requestType == RequestType.GET ? specification.get(url) : specification.post(url);

        return calculateResult(response, testData);

    }

    private static Pair<Boolean, String> calculateResult(Response response, TestData testData) {
        return null;
    }

    private static String prepareRequestBody(TestData testData) {
        return null;
    }

    public static enum RequestType {
        GET, POST
    }
    

}