package com.eggip.sai.helper;

import static com.jnape.palatable.lambda.adt.Try.trying;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.List;

import com.eggip.sai.helper.TestDataTemplate.TestData;
import com.jnape.palatable.lambda.adt.Try;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.testng.annotations.DataProvider;

public class XlsDataProvider {

    /**
     * 为每个接口测试提供数据 xls文件的命名方式为{TestClassName}-{TestMethodName}.xls
     * 所有的测试数据文件约定放置在classpath:resources/test-assets下， 在该路径下可以按模块进行组织
     * 若测试方法对应的测试数据文件不存在，则该测试方法将被忽略
     */
    @DataProvider(name = "xlsDataProvider")
    public static Object[][] provide(Method testMethod) {
        String testDataFileName = String.format("%s-%s.xls", testMethod.getDeclaringClass().getSimpleName(),
                testMethod.getName());
        
        return transferToObjsArray(getTestDataFile(testDataFileName).flatMap(f -> TestDataTemplate.read(f)).orThrow());
    }

    
    private static Object[][] transferToObjsArray(List<TestData> testDatas) {
        if (testDatas.size() == 0)
            return new Object[0][0];

        Object[][] ret = new Object[testDatas.size()][1];

        for (int i = 0; i < testDatas.size(); i++) {
            ret[i][0] = testDatas.get(i);
        }

        return ret;
    }

    private static Try<File> getTestDataFile(String filename) {
        return trying(() -> {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(String.format("classpath*:test-assets/**/%s", filename));
            if (resources.length > 1) {
                throw new RuntimeException(String.format("ambiguous file: %s", filename));
            } else if (resources.length == 0) {
                throw new FileNotFoundException(filename);
            } else {
                return resources[0].getFile();
            }
        });
    }
}