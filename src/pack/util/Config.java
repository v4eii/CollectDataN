package pack.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *  Created by v4e on 14.07.2019
 */

/**
 * Объект для работы с параметрами программы
 * @author v4e
 */
public class Config {
    private static Properties prop;
    
    private static String logGenerateSource,
            HHUrl,
            GorRabUrl,
            excelType,
            excelPath;
    private static Integer threadCount;
    private static Boolean HHSelect,
            GorRabSelect;

    /**
     * Открытие файла конфигурации и сохранение всех параметров в текущем состоянии
     */
    public static void initializeConfig()
    {
        prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/pack/resources/config.properties")) {
            prop.load(fis);
    
            logGenerateSource = prop.getProperty("logGenerateSource");
            if (logGenerateSource.equals("null"))
            {
                logGenerateSource = System.getProperty("user.home");
                prop.setProperty("logGenerateSource", logGenerateSource);
            }
    
            HHSelect = Boolean.valueOf(prop.getProperty("site.HHSelect"));
            GorRabSelect = Boolean.valueOf(prop.getProperty("site.GorRabSelect"));
            threadCount = Integer.parseInt(prop.getProperty("treatmentThreadCount"));
            HHUrl = prop.getProperty("site.HHUrl");
            GorRabUrl = prop.getProperty("site.GorRabUrl");
            excelType = prop.getProperty("excel.type");
            excelPath = prop.getProperty("excel.path");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обновление файла конфигурации в соответствтии с внесенными изменениями
     */
    public static void saveConfig()
    {
        try (FileOutputStream fos = new FileOutputStream("src/pack/resources/config.properties"))
        {
            prop.setProperty("site.HHSelect", HHSelect.toString());
            prop.setProperty("site.GorRabSelect", GorRabSelect.toString());
            prop.setProperty("treatmentThreadCount", threadCount.toString());
            prop.setProperty("logGenerateSource", logGenerateSource);
            prop.setProperty("site.HHUrl", HHUrl);
            prop.setProperty("site.GorRabUrl", GorRabUrl);
            prop.setProperty("excel.type", excelType);
            prop.setProperty("excel.path", excelPath);
            prop.store(fos, null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Boolean getGorRabSelect() {
        return GorRabSelect;
    }
    
    public static void setGorRabSelect(Boolean gorRabSelect) {
        GorRabSelect = gorRabSelect;
    }
    
    public static String getLogGeneratePath() {
        return logGenerateSource;
    }
    
    public static void setLogGenerateSource(String logGenerateSource) {
        Config.logGenerateSource = logGenerateSource;
    }
    
    public static Integer getThreadCount() {
        return threadCount;
    }
    
    public static void setThreadCount(Integer threadCount) {
        Config.threadCount = threadCount;
    }
    
    public static Boolean getHHSelect() {
        return HHSelect;
    }
    
    public static void setHHSelect(Boolean HHSelect) {
        Config.HHSelect = HHSelect;
    }
    
    public static String getHHUrl() {
        return HHUrl;
    }
    
    public static void setHHUrl(String HHUrl) {
        Config.HHUrl = HHUrl;
    }
    
    public static String getGorRabUrl() {
        return GorRabUrl;
    }
    
    public static void setGorRabUrl(String gorRabUrl) {
        GorRabUrl = gorRabUrl;
    }
    
    public static void setExcelType(String excelType) {
        Config.excelType = excelType;
    }
    
    public static String getExcelType() {
        return excelType;
    }
    
    public static String getExcelPath() {
        return excelPath;
    }
    
    public static void setExcelPath(String excelPath) {
        Config.excelPath = excelPath;
    }
}
