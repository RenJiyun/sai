package com.eggip.sai.helper;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.eggip.sai.helper.TestDataTemplate.TestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnape.palatable.lambda.adt.Try;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.adt.hlist.Tuple3;
import com.jnape.palatable.lambda.adt.hlist.HList.HCons;
import com.jnape.palatable.lambda.functions.Fn1;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Component
public class XlsDataDrivenTestRunner {

    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";


    private static final ConcurrentHashMap<String, Method> cachedMethods = new ConcurrentHashMap<>();


    @Autowired
    private JdbcTemplate jdbcTemplate;

    
    public Tuple2<Boolean, String> run(URL url, RequestType requestType, TestData testData) {
        RequestSpecification specification = RestAssured.given().contentType(JSON_CONTENT_TYPE).request()
                .body(prepareRequestBody(testData));
        requestType = requestType == null ? RequestType.GET : requestType;
        Response response = requestType == RequestType.GET ? specification.get(url) : specification.post(url);

        return calculateResult(response, testData);

    }


    
    /**
     * 目前一共有以下几种断言方式：
     * 1. 针对返回信息
     *    (1)字段equal断言，支持json字段导航，格式为：$.data.param1=123
     *    (2)contains断言，格式为：__contains("字段名称")
     *    (3)状态码断言，格式为：__statusCode=304
     * 
     * 2. 针对数据库信息
     *    (1)字段equal断言，格式为：#[表名].[字段名]=[值]@{筛选条件}
     */
    private Tuple2<Boolean, String> calculateResult(Response response, TestData testData) {
        String responseBody = response.asString();

        

        

        // 待续

        return null;



    }

    
    /**
     * 将测试参数转换成json字符串
     * 在转换之前，因为有些参数是会用到sql和内部函数，需要先进行处理
     * 目前参数不支持表达式
     * 内部函数格式 $functionName(val1, val1)  函数的参数只支持字符串类型，另外，内部函数的实现只能是static类型
     * sql调用格式  __sql(select count(*) from some_table) sql语句调用后只能返回单值
     */
    private String prepareRequestBody(TestData testData) {
        Map<String, Object> parsedParams = new HashMap<>();
        testData.parameters.stream().map(this::calculateParam).forEach(p -> parsedParams.put(p._1(), p._2()));
        return Try.trying(() -> new ObjectMapper().writeValueAsString(parsedParams)).orThrow();
    }



    private Tuple2<String, ?> calculateParam(Tuple3<String, String, Fn1<String, ?>> param) {
        if (StringUtils.startsWith(param._1(), TestDataTemplate.INTERNAL_FUNCTION_PREFIX)) {
            String functionName = param._1().replace(TestDataTemplate.INTERNAL_FUNCTION_PREFIX, "");
            Method method = loadInternalFunction(functionName);
            return HCons.tuple(
                        param._2(), 
                        Try.trying(() -> method.invoke(null, parseInternalFunctionParams(param._1()))).orThrow()
            );
        } else if (StringUtils.startsWith(param._1(), TestDataTemplate.SQL_PREFIX)) {
            String sql = param._1().substring(param._1().indexOf("("), param._1().indexOf(")")).replaceAll("[()\"]", "").trim();
            return HCons.tuple(param._2(), jdbcTemplate.queryForObject(sql, Object.class));
        } else {
            return HCons.tuple(param._2(), param._3().apply(param._2()));
        }
    }


    private Object[] parseInternalFunctionParams(String s) {
        return Arrays.asList(s.substring(s.indexOf("("), s.indexOf(")")).replaceAll("[()]", "")
                                    .split(",")).stream().map(String::trim).toArray();
    }




    private Method loadInternalFunction(String functionName) {
        if (cachedMethods.get(functionName) != null) return cachedMethods.get(functionName);
        Method method = ReflectionUtils.findMethod(InternalFunctions.class, functionName);
        if (method == null) throw new IllegalArgumentException(String.format("can't find internal function: %s", functionName));
        
        cachedMethods.put(functionName, method);

        return method;
        
    }




    


    public static enum RequestType {
        GET, POST
    }
    

}