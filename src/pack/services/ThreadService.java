package pack.services;

import pack.db.DBBean;
import pack.db.entity.Category;
import pack.util.Config;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Набор обрабатывающих методов
 * @author v4e
 */
public class ThreadService extends Service{
    
    /**
     * Первичная обработка по ключевым словам
     * @param text текст объявления
     * @param hits карта потока для заполнения
     */
    public static void firstAnalysis(String text, HashMap<String, Integer> hits,
                                     String categoryName)
    {
        // TODO: Разобраться с запросом в базу и желательно изменить в потоках 'category' с String на Category для куда меньших запросов в БД
//        Category category = DBBean.getInstance().getCategoryJPAController().findCategoryByName(categoryName);
//        category.getSkillsCollection().forEach(skills -> {
//            if (text.toLowerCase().contains(skills.getName().toLowerCase()))
//                hits.put(skills.getName(), hits.get(skills.getName()) == null ? 0 : hits.get(skills.getName()) + 1);
//        });


//        if (text.contains("Java") || text.contains("java"))
//        {
//            hits.put("Java", (hits.get("Java") == null ? 0 : hits.get("Java")) + 1);
//        }
//        if (text.contains("C++") || text.contains("С++"))
//        {
//            hits.put("C++", (hits.get("C++") == null ? 0 : hits.get("C++")) + 1);
//        }
//        if (text.contains("PHP") || text.contains("php"))
//        {
//            hits.put("PHP", (hits.get("PHP") == null ? 0 : hits.get("PHP")) + 1);
//        }
//        if (text.contains("SQL") || text.contains("sql")) // TODO: noSQL != SQL
//        {
//            hits.put("SQL", (hits.get("SQL") == null ? 0 : hits.get("SQL")) + 1);
//        }
//        if (text.contains("JavaScript") || text.contains("js") || text.contains("JS") || text.contains("javascript") || text.contains("Javascript"))
//        {
//            hits.put("JavaScript", (hits.get("JavaScript") == null ? 0 : hits.get("JavaScript")) + 1);
//        }
//        if (text.contains("C#") || text.contains("С#"))
//        {
//            hits.put("C#", (hits.get("C#") == null ? 0 : hits.get("C#")) + 1);
//        }
//        if (text.contains("1C") || text.contains("1с") || text.contains("1С") || text.contains("1c"))
//        {
//            hits.put("1C", (hits.get("1C") == null ? 0 : hits.get("1C")) + 1);
//        }
//        if (text.contains("git") || text.contains("Git") || text.contains("GIT"))
//        {
//            hits.put("Git", (hits.get("Git") == null ? 0 : hits.get("Git")) + 1);
//        }
//        if (text.contains("Python") || text.contains("python"))
//        {
//            hits.put("Python", (hits.get("Python") == null ? 0 : hits.get("Python")) + 1);
//        }
//        if (text.contains("CSS") || text.contains("css") || text.contains("Css")
//                || text.contains("HTML") || text.contains("html") || text.contains("Html"))
//        {
//            hits.put("HTML|CSS", (hits.get("HTML|CSS") == null ? 0 : hits.get("HTML|CSS")) + 1);
//        }
//        if (text.contains("Oracle") || text.contains("oracle"))
//        {
//            hits.put("Oracle", (hits.get("Oracle") == null ? 0 : hits.get("Oracle")));
//        }
    }
    
    /**
     * Объединяет карты возвращенные потоками.
     * @param mapList лист содержащий все карты
     */
    public static Map<String, Integer> collectMap(List<HashMap<String, Integer>> mapList)
    {
        Map<String, Integer> allHits = new HashMap<>();
        mapList.forEach((map) ->
        {
            map.entrySet().forEach((entry) ->
            {
                String key = entry.getKey();
                Integer current = allHits.get(key);
                allHits.put(key, current == null ? entry.getValue() : entry.getValue() + current);
            });
        });
        return allHits;
    }
    
    /**
     * Генерирует файл с логом всех обработанных объявлений
     * @param logList логи всех отдельно взятых потоков
     */
    public static void generateLog(List<List<String>> logList)
    {
        try (FileWriter fw = new FileWriter(Config.getLogGeneratePath() + "\\pasrsingData.log"))
        {
            logList.forEach(strings -> {
                strings.forEach(s -> {
                    try {
                        fw.write(s + System.lineSeparator());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
