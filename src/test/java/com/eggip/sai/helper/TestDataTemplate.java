package com.eggip.sai.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.builtin.fn1.Id;

import static com.jnape.palatable.lambda.adt.Try.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

public class TestDataTemplate {

    public static class TestData {

        public TestData(String caseNo, String title, String assertion, boolean isTableAssertion, boolean ignore,
                Map<String, Object> parameters) {
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

        public final Map<String, Object> parameters;
    }

    public static Maybe<List<TestData>> read(File xlsFile) {

        if (xlsFile == null)
            return Maybe.nothing();

        return withResources(
            () -> new HSSFWorkbook(new FileInputStream(xlsFile)), 
            (r) -> {
                List<TestData> testDatas = new ArrayList<>();
                HSSFSheet sheet = r.getSheetAt(0);
                Row firstRow = sheet.getRow(0);
                sheet.removeRow(firstRow);
                List<Triple<String, Class<?>, Function<String, ?>>> paramInfos = parseParamInfos(firstRow);
                sheet.forEach(row -> {
                    testDatas.add(toTestData(paramInfos, row));
                });
                return success(testDatas);
        }).projectB();
    }




    
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
                    paramInfo = Triple.of(paramName, Integer.class, Integer::parseInt);
                    break;

                case "short":
                    paramInfo = Triple.of(paramName, Short.class, Short::parseShort);
                    break;

                case "long":
                    paramInfo = Triple.of(paramName, Long.class, Long::parseLong);
                    break;

                case "double":
                    paramInfo = Triple.of(paramName, Double.class, Double::parseDouble);
                    break;

                case "string":
                    paramInfo = Triple.of(paramName, String.class, s -> s);
                    break;

                case "date":
                    final String dateFormat = leftBracketIndex == -1 ? "yyyy-MM-dd"
                            : paramMetaInfo.substring(leftBracketIndex).replaceAll("[\"()]", "");

                    paramInfo = Triple.of(paramName, Date.class,
                            s -> trying(() -> DateUtils.parseDate(s, dateFormat)).orThrow());
                    break;
                }
            }
            ret.add(paramInfo);
        });

        return ret;
    }


    
    /**
     * 解析参数信息 参数名格式：{paramName}[@@{paramType[(simple format string)]}] 默认参数类型为string
     * 例如：param1@@int, param2@@string = param2, param3@@date("yyyy-MM-dd")
     * 目前只有date类型支持格式化，若date类型没有指定日期格式化字符串，默认之为yyyy-MM-dd
     */
    private static Maybe<Triple<String, Class<?>, Fn1<String, ?>>> parseParamInfo(String paramMetaInfo) {
        if (StringUtils.isAllBlank(paramMetaInfo)) return Maybe.nothing();
        String[] splits = paramMetaInfo.split("@@");
        String paramName = splits[0];

        if (splits.length > 1) {

        } else {
            return Maybe.just(Triple.of(paramName, String.class, Id.id()));
        }

    }

    // {case_no, title, assertion, is_table_assertion, ignore, [parameters]}
    private static TestData toTestData(List<Triple<String, Class<?>, Function<String, ?>>> paramInfos, Row row) {
        String caseNo = row.getCell(0).getStringCellValue();
        String title = row.getCell(1).getStringCellValue();
        String assertion = row.getCell(2).getStringCellValue();
        boolean isTableAssertion = row.getCell(3).getBooleanCellValue();
        boolean ignore = row.getCell(4).getBooleanCellValue();

        Map<String, Object> parameters = new HashMap<>();
        for (int i = 0; i < paramInfos.size(); i++) {
            Triple<String, Class<?>, Function<String, ?>> paramInfo = paramInfos.get(i);
            if (row.getLastCellNum() > i + 5) {
                Object value = paramInfo.getRight().apply(row.getCell(i + 5).getStringCellValue());
                parameters.put(paramInfo.getLeft(), value);
            }
        }

        return new TestData(caseNo, title, assertion, isTableAssertion, ignore, parameters);
    }

}