package com.eggip.sai.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jnape.palatable.lambda.adt.Either;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.javatuples.Pair;

public class TestDataTemplate {

    public static class TestData {

        public TestData(String caseNo, String title, String assertion, boolean isTableAssertion, boolean ignore,
                List<Pair<String, String>> parameters) {
            this.caseNo = caseNo;
            this.title = title;
            this.assertion = assertion;
            this.isTableAssertion = isTableAssertion;
            this.ignore = ignore;
            this.parameters = parameters;
        }

        public final String caseNo;

        public final String title;

        public final String assertion;

        public final boolean isTableAssertion;

        public final boolean ignore;

        public final List<Pair<String, String>> parameters;
    }

    public static Either<RuntimeException, List<TestData>> read(File xlsFile) {
        if (xlsFile == null)
            return Either.left(new NullPointerException("xlsFile is null"));

        List<TestData> testDatas = new ArrayList<>();
        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(new FileInputStream(xlsFile));
            HSSFSheet sheet = workbook.getSheetAt(0);
            Row firstRow = sheet.getRow(0);
            sheet.removeRow(firstRow);
            sheet.forEach(row -> {
                testDatas.add(toTestData(firstRow, row));
            });
        } catch (Exception e) {
            return Either.left(new RuntimeException(e));
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
            } catch (IOException e) {
                return Either.left(new RuntimeException(e));
            }
        }

        return Either.right(testDatas);

    }


    // {case_no, title, assertion, is_table_assertion, ignore, [parameters]}
    private static TestData toTestData(Row keyRow, Row row) {
        String caseNo = row.getCell(0).getStringCellValue();
        String title = row.getCell(1).getStringCellValue();
        String assertion = row.getCell(2).getStringCellValue();
        boolean isTableAssertion = row.getCell(3).getBooleanCellValue();
        boolean ignore = row.getCell(4).getBooleanCellValue();

        List<Pair<String, String>> parameters = new ArrayList<>();
        for (int i = 5; i < keyRow.getLastCellNum(); i++) {
            String value = null;
            if (row.getLastCellNum() > i) {
                value = row.getCell(i).getStringCellValue();
            } 

            parameters.add(Pair.with(keyRow.getCell(i).getStringCellValue(), value));
        }

        return new TestData(caseNo, title, assertion, isTableAssertion, ignore, parameters);
    }



}