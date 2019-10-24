package pack.services;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pack.util.Config;

import java.io.FileInputStream;
import java.io.IOException;

/*
 *  Created by v4e on 14.07.2019
 */

/**
 * Класс для работы с Excel. На данный момент не нужен
 * @deprecated
 * @author v4e
 */
public class ExcelService extends Service {

    private static XSSFWorkbook xWorkbook;
    private static HSSFWorkbook hWorkbook;

    /**
     * Создание объекта для управления обрабатываемым файлом Excel
     */
    public static void connectFile()
    {
        if (Config.getExcelType().equals("xlsx")) {
            try (FileInputStream fis = new FileInputStream(Config.getExcelPath())) {
                xWorkbook = new XSSFWorkbook(fis);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (Config.getExcelType().equals("xls")) {
            try (FileInputStream fis = new FileInputStream(Config.getExcelPath())) {
                hWorkbook = new HSSFWorkbook(fis);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static XSSFWorkbook getXWorkbook() {
        return xWorkbook;
    }
    
    public static HSSFWorkbook getHWorkbook() {
        return hWorkbook;
    }
}
