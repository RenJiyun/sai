package com.eggip.sai.helper;

import java.net.URL;

import com.eggip.sai.helper.TestDataTemplate.TestData;

import org.apache.commons.lang3.tuple.Pair;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class XlsDataDrivenTestRunner {

    private static final String json_content_type = "application/json;charset=UTF-8";

    /**
     * 接口测试驱动器 1. 请求参数准备，例如一些参数可能需要执行用于指定的sql，以及内置的函数 2. 请求数据，返回结果 3.
     * 将请求结果应用于用户断言，目前只能做到两种断言 - equal assertion - contain assertion 4.
     * 将测试未通过的案例进行收集，放入新的xls文件，方便开发人员进行相应的调试 5. 返回TestNG框架，生成测试报告
     */
    public static Pair<Boolean, String> run(URL url, RequestType requestType, TestData testData) {
        RequestSpecification specification = RestAssured.given().contentType(json_content_type).request()
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