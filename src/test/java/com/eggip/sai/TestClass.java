package com.eggip.sai;

import java.net.MalformedURLException;
import java.net.URL;

import com.eggip.sai.helper.TestDataTemplate.TestData;
import com.eggip.sai.helper.XlsDataDrivenTestRunner;
import com.eggip.sai.helper.XlsDataDrivenTestRunner.RequestType;
import com.eggip.sai.helper.XlsDataProvider;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;


/**
 * 在测试运行前，需要保证使用的是测试数据库，并通过迁移工具，将测试数据库恢复到一致的状态
 */
@Feature("测试类")
public class TestClass {

    @Autowired
    private XlsDataDrivenTestRunner testRunner;


    // 测试或开发人员需要定义好测试方法之间的依赖关系
    @Test(dataProvider = "xlsDataProvider", dataProviderClass = XlsDataProvider.class, dependsOnMethods = "", dependsOnGroups = "")
    @Story("测试方法１")
    @Description("接口测试")
    public void testMethod1(TestData testData) throws MalformedURLException {
        Tuple2<Boolean, String> result = testRunner.run(new URL("url-to-be-checked"), RequestType.POST, testData);
        Assert.assertTrue(result._1(), result._2());
    }



    @Test
    @Story("测试方法２")
    public void testMethod2(TestData testData) {

    }

}