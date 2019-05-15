package com.eggip.sai.helper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import com.eggip.sai.helper.TestDataTemplate.TestData;
import com.jnape.palatable.lambda.adt.Either;

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
    @DataProvider
    public static Object[][] provide(Method testMethod) {
        String testDataFileName = String.format("%s-%s.xls", testMethod.getDeclaringClass().getSimpleName(),
                testMethod.getName());

        return getTestDataFile(testDataFileName)
                        .orThrow(t -> t)
                        .map(f -> transferToObjsArray(TestDataTemplate.read(f).orThrow(e -> e)))
                        .orElse(new Object[0][0]);

    }



    private static Object[][] transferToObjsArray(List<TestData> testDatas) {
        if (testDatas.size() == 0) return new Object[0][0];

        int paramNum = testDatas.get(0).parameters.size();
        Object[][] ret = new Object[testDatas.size()][paramNum];

        for (int i = 0; i < testDatas.size(); i++) {
            for (int j = 0; j < testDatas.get(i).parameters.size(); j++) {
                ret[i][j] = testDatas.get(i).parameters.get(j);
            }
        }

        return ret;
    }


    
    private static Either<RuntimeException, Optional<File>> getTestDataFile(String filename) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(String.format("classpath*:test-assets/**/%s", filename));
            if (resources.length > 1) {
                return Either.left(new RuntimeException(String.format("ambiguous file: %s", filename)));
            } else if (resources.length == 0) {
                return Either.right(Optional.empty());
            } else {
                return Either.right(Optional.of(resources[0].getFile()));
            }
        } catch (IOException e) {
            return Either.left(new RuntimeException(e));
        }
    }




}