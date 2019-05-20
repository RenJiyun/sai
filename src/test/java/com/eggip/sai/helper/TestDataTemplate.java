package com.eggip.sai.helper;

import static com.jnape.palatable.lambda.adt.Try.failure;
import static com.jnape.palatable.lambda.adt.Try.success;
import static com.jnape.palatable.lambda.adt.Try.withResources;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jnape.palatable.lambda.adt.Try;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class TestDataTemplate {

    private static final int START_CELL_NUM_OF_PARAMS = 5;

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

    public static Try<List<TestData>> read(File xlsFile) {
        if (xlsFile == null)
            return failure(new NullPointerException("xlsFile is null"));

        return withResources(() -> new HSSFWorkbook(new FileInputStream(xlsFile)), (r) -> {
            List<TestData> testDatas = new ArrayList<>();
            HSSFSheet sheet = r.getSheetAt(0);
            Row firstRow = sheet.getRow(0);
            sheet.removeRow(firstRow);
            List<Tuple2<String, Fn1<String, ?>>> paramInfos = parseParamInfos(firstRow, START_CELL_NUM_OF_PARAMS);
            sheet.forEach(row -> {
                testDatas.add(toTestData(paramInfos, row, START_CELL_NUM_OF_PARAMS));
            });
            return success(testDatas);
        });
    }

    /**
     * 获取测试参数的元信息
     * 参数名格式：
     * {paramName}[@@
     *      {
     *          int | short | long | string | double | 
     *          numeric[(1,12)] | 
     *          date[(yyyy-MM-dd)] | 
     *          sql |
     *          fn
     *      }
     * ]
     * @param <T>
     * @param keyRow
     * @param startCellNum
     * @return
     */
    private static List<Tuple2<String, Fn1<String, ?>>> parseParamInfos(Row keyRow, int startCellNum) {
        List<Tuple2<String, Fn1<String, ?>>> ret = new ArrayList<>();
        if (keyRow.getLastCellNum() < startCellNum)
            return ret;

        Tuple2<String, Fn1<String, ?>> paramInfo = null;
        for (int i = startCellNum; i < keyRow.getLastCellNum(); i++) {
            String[] temp = keyRow.getCell(i).getStringCellValue().split("@@");
            String paramName = temp[0];
            if (temp.length <= 1) {
                paramInfo = Tuple2.tuple(paramName, s -> s);
            } else {
                Tuple2<String, String> metaInfo = parseMetaInfo(temp[1]);
                Fn1<String, ?> parseFn = null;
                switch (metaInfo._1()) {
                case "int":
                    parseFn = Integer::parseInt;
                    break;

                case "short":
                    parseFn = Short::parseShort;
                    break;

                case "long":
                    parseFn = Long::parseLong;
                    break;

                case "double":
                    parseFn = Double::parseDouble;
                    break;

                case "numeric":
                    parseFn = s -> {
                        BigDecimal bigDecimal = new BigDecimal(s);
                        if (metaInfo._2() != null) {
                            int scale = Integer.parseInt(metaInfo._2().split(",")[1].trim());
                            bigDecimal.setScale(scale);
                        }
                        return bigDecimal;
                    };
                    break;

                case "string":
                    parseFn = s -> s;
                    break;

                case "date":
                    parseFn = s -> {
                        String format = metaInfo._2() == null ? "yyyy-MM-dd" : metaInfo._2();
                        return DateUtils.parseDate(s, format);
                    };
                    break;
                
                case "sql": 
                    parseFn = s -> s;
                    break;
                
                case "fn":
                    parseFn = s -> s;
                    break;

                default:
                    throw new IllegalArgumentException(String.format("unknown param type: %s", metaInfo._1()));
                }

                paramInfo = Tuple2.tuple(paramName, parseFn);

            }

            ret.add(paramInfo);

        }

        return ret;
    }

    private static Tuple2<String, String> parseMetaInfo(String s) {
        int indexOfLeftBracket = s.indexOf("(");
        if (indexOfLeftBracket == -1)
            return Tuple2.tuple(s, null);
        else
            return Tuple2.tuple(s.substring(0, indexOfLeftBracket),
                    s.substring(indexOfLeftBracket).replaceAll("[()\"]", ""));
    }

    

    

    /**
     * 测试运行时，需要做以下三件事情： 
     * 1. 断言解析
     * 2. 内部函数解析
     * 3. 运行用户指定的sql获取参数值
     */
    private static TestData toTestData(List<Tuple2<String, Fn1<String, ?>>> paramInfos, Row row, int startCellNumOfParams) {
        String caseNo = row.getCell(0).getStringCellValue();
        String title = row.getCell(1).getStringCellValue();
        String assertion = row.getCell(2).getStringCellValue();    
        boolean isTableAssertion = row.getCell(3).getBooleanCellValue();
        boolean ignore = row.getCell(4).getBooleanCellValue();

        Map<String, Object> parameters = new HashMap<>();
        for (int i = 0; i < paramInfos.size(); i++) {
            Tuple2<String, Fn1<String, ?>> paramInfo = paramInfos.get(i);
            if (row.getLastCellNum() > i + startCellNumOfParams) {
                Object value = paramInfo._2().apply(getCellValue(row.getCell(i + startCellNumOfParams)));
                parameters.put(paramInfo._1(), value);
            }
        }

        return new TestData(caseNo, title, assertion, isTableAssertion, ignore, parameters);
    }

    
    @SuppressWarnings("deprecation")
    private static String getCellValue(Cell cell) {
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

}