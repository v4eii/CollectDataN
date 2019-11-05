package pack.services;

import pack.db.DBBean;
import pack.db.entity.Category;
import pack.db.entity.Skills;
import pack.util.Config;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Сервис с методами для потоков
 * @author v4e
 */
public class ThreadService extends Service{
    
    /**
     * Первичная обработка по ключевым словам
     * @param text текст объявления
     * @param hits карта потока для заполнения
     */
    public static void firstAnalysis(String text, HashMap<String, Integer> hits,
                                     List<Skills> skillsForCategory)
    {
        // TODO: noSQL != SQL
        for (Skills s : skillsForCategory) {
            if (text.toLowerCase().contains(s.getName().toLowerCase())) {
                hits.put(s.getName(), hits.get(s.getName()) == null ? 0 : hits.get(s.getName()) + 1);
            }
        }
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

    public static Set<String> collectSet(List<HashSet<String>> setList) {
        Set<String> allSets = new HashSet<>();
        setList.forEach(allSets::addAll);
        return allSets;
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
