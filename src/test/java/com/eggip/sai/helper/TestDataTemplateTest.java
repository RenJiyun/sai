package com.eggip.sai.helper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.eggip.sai.helper.TestDataTemplate.TestData;
import com.jnape.palatable.lambda.adt.Try;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class TestDataTemplateTest {

    private File xlsFile;


    @Before
    public void prepareXlsFile() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("test/for_test.xls");
        xlsFile = classPathResource.getFile();  
    }


    @Test
    public void testReadNullXlsFile() {
        Try<List<TestData>> result = TestDataTemplate.read(null);
        Assert.assertEquals("xlsFile is null", result.projectA().orElse(null).getMessage());
    }


    @Test
    public void testRowNum() {
        List<TestData> testDatas = TestDataTemplate.read(xlsFile).orThrow();
        Assert.assertEquals(2, testDatas.size());
    }


    @Test
    public void testRowValue() {
        List<TestData> testDatas = TestDataTemplate.read(xlsFile).orThrow();
        Assert.assertTrue(testDatas.get(0).isTableAssertion);
        Assert.assertFalse(testDatas.get(0).ignore);
    }


}