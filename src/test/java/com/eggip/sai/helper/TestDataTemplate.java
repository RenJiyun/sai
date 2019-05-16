package com.eggip.sai.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import com.eggip.sai.util.Errors;
import com.jnape.palatable.lambda.adt.Either;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

public class TestDataTemplate {

    public static class TestData {

        public TestData(String caseNo, String title, String assertion, boolean isTableAssertion, boolean ignore,
                List<Pair<String, Object>> parameters) {
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

        public final List<Pair<String, Object>> parameters;
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

            List<Triple<String, Class<?>, Function<String, ?>>> paramInfos = parseParamInfos(firstRow);

            sheet.forEach(row -> {
                testDatas.add(toTestData(paramInfos, row));
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

    /**
     * 解析参数信息 参数名格式：{paramName}[@@{paramType[(simple format string)]}] 默认参数类型为string
     * 例如：param1@@int, param2@@string = param2, param3@@date("yyyy-MM-dd")
     * 目前只有date类型支持格式化，若date类型没有指定日期格式化字符串，默认之为yyyy-MM-dd
     */
    private static List<Triple<String, Class<?>, Function<String, ?>>> parseParamInfos(Row keyRow) {
        List<Triple<String, Class<?>, Function<String, ?>>> ret = new ArrayList<>();
        keyRow.forEach(cell -> {
            String[] cellValueSplited = cell.getStringCellValue().split("@@");
            String paramName = cellValueSplited[0];
            Triple<String, Class<?>, Function<String, ?>> paramInfo = null;
            if (cellValueSplited.length == 1) {
                paramInfo = Triple.of(paramName, String.class, s -> s);
            } else {
                String paramMetaInfo = cellValueSplited[1];
                int leftBracketIndex = paramMetaInfo.indexOf("(");
                String paramTypeName = leftBracketIndex == -1 ? paramMetaInfo
                        : paramMetaInfo.substring(0, leftBracketIndex);

                switch (paramTypeName) {
                case "int":
                    paramInfo = Triple.of(paramName, Integer.class, s -> Integer.parseInt(s));
                    break;

                case "short":
                    paramInfo = Triple.of(paramName, Short.class, s -> Short.parseShort(s));
                    break;

                case "long":
                    paramInfo = Triple.of(paramName, Long.class, s -> Long.parseLong(s));
                    break;

                case "double":
                    paramInfo = Triple.of(paramName, Double.class, s -> Double.parseDouble(s));
                    break;

                case "string":
                    paramInfo = Triple.of(paramName, String.class, s -> s);
                    break;

                case "date":
                    final String dateFormat = leftBracketIndex == -1 ? "yyyy-MM-dd" 
                                                                     : paramMetaInfo.substring(leftBracketIndex).replaceAll("[\"()]", "");

                    paramInfo = Triple.of(paramName, Date.class, 
                                    s -> Errors.wrap(t -> DateUtils.parseDate((String) t, dateFormat), s).orThrow(e -> e));
                    break;
                }
            }
            ret.add(paramInfo);
        });

        return ret;
    }

    // {case_no, title, assertion, is_table_assertion, ignore, [parameters]}
    private static TestData toTestData(List<Triple<String, Class<?>, Function<String, ?>>> paramInfos, Row row) {
        String caseNo = row.getCell(0).getStringCellValue();
        String title = row.getCell(1).getStringCellValue();
        String assertion = row.getCell(2).getStringCellValue();
        boolean isTableAssertion = row.getCell(3).getBooleanCellValue();
        boolean ignore = row.getCell(4).getBooleanCellValue();

        List<Pair<String, Object>> parameters = new ArrayList<>();
        for (int i = 0; i < paramInfos.size(); i++) {
            Triple<String, Class<?>, Function<String, ?>> paramInfo = paramInfos.get(i);
            Object value = null;
            if (row.getLastCellNum() > i + 5) {
                value = paramInfo.getRight().apply(row.getCell(i + 5).getStringCellValue());
            }

            parameters.add(Pair.of(paramInfo.getLeft(), value));

        }

        return new TestData(caseNo, title, assertion, isTableAssertion, ignore, parameters);
    }



}