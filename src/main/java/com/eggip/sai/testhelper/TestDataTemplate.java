package com.eggip.sai.testhelper;

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

public class TestDataTemplate {

    public static class TestData {

        public TestData(String caseNo, String title, String assertion, boolean isTableAssertion, boolean ignore,
                Map<String, String> parameters) {
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

        public final Map<String, String> parameters;
    }

    public static Either<List<TestData>, Throwable> read(File xlsFile) {
        if (xlsFile == null)
            return Either.right(new NullPointerException("xlsFile is null"));

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
            return Either.right(e);
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
            } catch (IOException e) {
                return Either.right(e);
            }
        }

        return Either.left(testDatas);

    }


    // {case_no, title, assertion, is_table_assertion, ignore, [parameters]}
    private static TestData toTestData(Row keyRow, Row row) {
        String caseNo = row.getCell(0).getStringCellValue();
        String title = row.getCell(1).getStringCellValue();
        String assertion = row.getCell(2).getStringCellValue();
        boolean isTableAssertion = row.getCell(3).getBooleanCellValue();
        boolean ignore = row.getCell(4).getBooleanCellValue();

        Map<String, String> parameters = new HashMap<>();
        for (int i = 5; i < keyRow.getLastCellNum(); i++) {
            if (row.getLastCellNum() == i) break;
            parameters.put(keyRow.getCell(i).getStringCellValue(), row.getCell(i).getStringCellValue());
        }

        return new TestData(caseNo, title, assertion, isTableAssertion, ignore, parameters);
    }



}