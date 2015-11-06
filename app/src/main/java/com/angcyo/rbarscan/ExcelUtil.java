package com.angcyo.rbarscan;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by angcyo on 15-11-06-006.
 */
public class ExcelUtil {
    public static String EXCEL_FILE_NAME = "RBarCode.xls";
    public static String EXCEL_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + EXCEL_FILE_NAME;

    public static void deleteFile() {
        File xlsFile = new File(EXCEL_FILE_PATH);
        if (xlsFile.exists()) {
            xlsFile.delete();
        }
    }

    public static void init(Context context) {
        EXCEL_FILE_PATH = context.getFilesDir().getAbsolutePath() + File.separator + "RBarCode.xls";
    }

    public static List<List<String>> read() throws IOException, BiffException {
        return read(EXCEL_FILE_PATH);
    }

    public static List<List<String>> read(String filePath) throws IOException, BiffException {
        List<List<String>> cellDatas = new ArrayList<>();//按照第几行,第几列的顺序读取, 从0开始

        Workbook workbook = null;//整个xls文档
        Sheet sheet = null;//文档中的工作表
        Cell cell = null;//单元格

        workbook = Workbook.getWorkbook(new File(filePath));
        sheet = workbook.getSheet(0);

        for (int i = 0; i < sheet.getRows(); i++) {//枚举行数
            List<String> rowDatas = new ArrayList<>();//每一行的文本数据
            for (int j = 0; j < sheet.getColumns(); j++) {//枚举列数
                cell = sheet.getCell(j, i);//得到单元格
                rowDatas.add(TextUtils.isEmpty(cell.getContents()) ? "" : cell.getContents());//得到单元格内容,并添加
            }

            cellDatas.add(rowDatas);//保存每一行的数据
        }
        return cellDatas;
    }

    public static void write(List<List<String>> cellDatas) throws IOException, BiffException, WriteException {
        write(cellDatas, EXCEL_FILE_PATH);
    }

    public static void write(List<List<String>> cellDatas, String filePath) throws IOException, BiffException, WriteException {

        WritableWorkbook writableWorkbook = null; //可写入的工作薄
        WritableSheet writableSheet = null;//可写入的工作表
        Label writableCell = null;//可写入的单元格

        writableWorkbook = Workbook.createWorkbook(new File(filePath));
        writableSheet = writableWorkbook.createSheet("sheet1", 0);//创建工作表

        for (int i = 0; i < cellDatas.size(); i++) {//行数
            for (int j = 0; j < cellDatas.get(i).size(); j++) {//列数
                writableCell = new Label(j, i, cellDatas.get(i).get(j));
                writableSheet.addCell(writableCell);
            }
        }

        writableWorkbook.write();
        writableWorkbook.close();
    }

    public static String getDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return simpleDateFormat.format(new Date());
    }

}
