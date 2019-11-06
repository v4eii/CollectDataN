package pack.services;

import pack.db.entity.Skills;
import pack.util.Config;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Сервис с методами для потоков
 *
 * @author v4e
 */
public class ThreadService extends Service {
    /**
     * Первичная обработка по ключевым словам
     *
     * @param text текст объявления
     * @param hits карта потока для заполнения
     */
    public static void firstAnalysis(String text, HashMap<String, Integer> hits,
                                     List<Skills> skillsForCategory) {
        // TODO: noSQL != SQL
        for (Skills s : skillsForCategory) {
            if (text.toLowerCase().contains(s.getName().toLowerCase())) {
                hits.put(s.getName(), hits.get(s.getName()) == null ? 0 : hits.get(s.getName()) + 1);
            }
        }
    }

    /**
     * Объединяет карты возвращенные потоками.
     *
     * @param mapList лист содержащий все карты
     * @return объединенную карту
     */
    public static Map<String, Integer> collectMap(List<HashMap<String, Integer>> mapList) {
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
     * Объединяет сеты возвращаемые потоками
     *
     * @param setList лист содержащий все сеты
     * @return объединненый сет
     */
    public static Set<String> collectSet(List<HashSet<String>> setList) {
        Set<String> allSets = new HashSet<>();
        setList.forEach(allSets::addAll);
        return allSets;
    }

    /**
     * Запись данных в лог-файл
     *
     * @param logData данные собранные потоком
     * @throws IOException при обработке файла соответствующая ошибка
     */
    public static synchronized void writeInLog(List<String> logData) throws IOException {
        try (FileWriter logWriter = new FileWriter(Config.getLogGeneratePath() + "\\parsingData.log", true)) {
            for (String str : logData) {
                logWriter.write(str);
            }
        }
    }


    /**
     * @param toRecombineCollection не переработанная коллекция компетенций
     * @return переработанную коллекцию, где все компетенции приведены в нижний регистр, компетенции состоящие
     * из английских символов (Ex: PostgreSQL/My SQL) будут обрезаны по ВСЕМ пробелам и так же приведены в нижний регистр
     */
    public static Collection<String> recombineCompetency(Collection<String> toRecombineCollection) {
        Collection<String> recombinedCollection = new HashSet<>();
        toRecombineCollection.forEach(e -> {
            if (e.contains("/")) {
                Arrays.asList(e.split("/")).forEach(e1 -> {
                    // Проверка на вхождение кириллицы в компетенцию, если их нет,
                    // то добавляется с заменой 1C (C - eng) на 1С (С - русс) в случае когда это возможно
                    if (e.chars().noneMatch(i -> i >= 'А' && i <= 'я')) {
                        recombinedCollection.add(e1.contains("1C") /*Eng*/ ?
                                e1.replaceAll("1C"/*Eng*/, "1С"/*Rus*/).toLowerCase()
                                        .replaceAll(" ", "").trim() :
                                e1.toLowerCase().replaceAll(" ", "").trim());
                    }
                    else
                        recombinedCollection.add(e1.toLowerCase().trim());
                });
            }
            else {
                if (e.chars().noneMatch(i -> i >= 'А' && i <= 'я')) {
                    recombinedCollection.add(e.contains("1C") /*Eng*/ ?
                            e.replaceAll("1C"/*Eng*/, "1С"/*Rus*/).toLowerCase()
                                    .replaceAll(" ", "").trim() :
                            e.toLowerCase().replaceAll(" ", "").trim());
                }
                else
                    recombinedCollection.add(e.toLowerCase().trim());
            }
        });
        return recombinedCollection;
    }
}
