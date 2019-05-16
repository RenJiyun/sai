package com.eggip.sai;

import java.net.MalformedURLException;
import java.net.URL;

import com.eggip.sai.helper.XlsDataDrivenTestRunner;
import com.eggip.sai.helper.XlsDataProvider;
import com.eggip.sai.helper.TestDataTemplate.TestData;
import com.eggip.sai.helper.XlsDataDrivenTestRunner.RequestType;

import org.apache.commons.lang3.tuple.Pair;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@Feature("测试类")
public class TestClass {

    // 测试或开发人员需要定义好测试方法之间的依赖关系
    @Test(dataProvider = "xlsDataProvider", dataProviderClass = XlsDataProvider.class, dependsOnMethods = "", dependsOnGroups = "")
    @Story("测试方法１")
    @Description("接口测试")
    public void testMethod1(TestData testData) throws MalformedURLException {
        Pair<Boolean, String> result = XlsDataDrivenTestRunner.run(new URL("url-to-be-checked"), RequestType.POST, testData);
        Assert.assertTrue(result.getLeft(), result.getRight());
    }



    @Test
    @Story("测试方法２")
    public void testMethod2(TestData testData) {

    }

}