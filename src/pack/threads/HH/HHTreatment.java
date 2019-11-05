package pack.threads.HH;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import pack.db.entity.Category;
import pack.services.ThreadService;
import pack.threads.Parsing;
import pack.threads.TreatmentParser;
import pack.util.Vacancy;
import pack.view.controllers.CollectViewController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Exchanger;

/**
 * Обрабатывающий поток конкретного заявления
 *
 * @author v4e
 */
public class HHTreatment extends Thread implements TreatmentParser {

    private Exchanger<Vacancy> exchanger;
    private Exchanger<HashMap<String, Integer>> exchangerToAnalysis;
    private Exchanger<ArrayList<String>> exchangerToLog;
    private Exchanger<HashSet<String>> exchangerToCollect;

    private Vacancy vacancy;
    private volatile String name;
    // Статус потока
    private boolean active;
    // Попадания ключевых слов
    private HashMap<String, Integer> hits;
    private HashSet<String> competencyData;
    private Document doc;
    private ArrayList<String> logData;
    private Category category;
    private HHCategoryParser hhCategoryParser;

    /**
     * Инициализация объектов
     *
     * @param exchanger           объект обмена с распределяющим потоком
     * @param exchangerToAnalysis объект для обмена с финальным потоком
     * @param exchangerToLog      объект обмена для генерации лога
     * @param group               группа принадлежности распределяющего потока
     * @param threadName          имя потока
     * @param category            категория обработки
     * @param hhCategoryParser    парсер категории
     */
    HHTreatment(Exchanger<Vacancy> exchanger, Exchanger<HashMap<String, Integer>> exchangerToAnalysis,
                Exchanger<ArrayList<String>> exchangerToLog, ThreadGroup group, String threadName, Category category,
                HHCategoryParser hhCategoryParser, Exchanger<HashSet<String>> exchangerToCollect) {
        super(group, threadName);
        this.hhCategoryParser = hhCategoryParser;
        this.exchangerToCollect = exchangerToCollect;
        this.exchanger = exchanger;
        this.name = threadName;
        this.category = category;
        active = true;
        hits = new HashMap<>();
        competencyData = new HashSet<>();
        this.exchangerToAnalysis = exchangerToAnalysis;
        if (CollectViewController.getLogGenerate()) {
            logData = new ArrayList<>();
            this.exchangerToLog = exchangerToLog;
        }
    }

    @Override
    public void run() {
        while (isActive()) {
            try {
                if (Parsing.getParsingTarget().equals(CollectViewController.PARSING_TARGET.AnalysisCompetency))
                    analysisTreatment();
                else if (Parsing.getParsingTarget().equals(CollectViewController.PARSING_TARGET.CollectCompetency))
                    collectTreatment();
            }
            catch (IOException | InterruptedException ex) {
                if (!isActive())
                    break;
            }
        }

        hhCategoryParser.getProcessesCompletion().put(name, true);

        try {
            transferData();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Exchanger<Vacancy> getExchanger() {
        return exchanger;
    }

    private boolean isActive() {
        return active;
    }

    void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void analysisTreatment() throws InterruptedException, IOException {
        Vacancy v = null;
        vacancy = exchanger.exchange(v);
        if (vacancy != null) {
            doc = Jsoup.connect(vacancy.getUrlVacancy()).get();
            Elements keySkills = doc.getElementsByClass("bloko-tag bloko-tag_inline Bloko-TagList-Tag ");
            if (keySkills == null || keySkills.isEmpty()) {
                String textVacancy = doc.getElementsByClass("g-user-content").text();
                ThreadService.firstAnalysis(textVacancy, hits, hhCategoryParser.getSkillsForCategory());
                System.out.println(this.getName() + " " + vacancy.getUrlVacancy());
            }
            else {
                StringBuilder sb = new StringBuilder();
                keySkills.forEach((e) ->
                        sb.append(e.text()).append(" "));
                // Ключевые слова наше всё
                ThreadService.firstAnalysis(sb.toString(), hits, hhCategoryParser.getSkillsForCategory());
                System.out.println(this.getName() + " " + vacancy.getUrlVacancy());
            }
            if (CollectViewController.getLogGenerate())
                logData.add(name + " " + vacancy.getNameVacancy() + "\n");
            Thread.sleep(300);
        }
    }

    @Override
    public void collectTreatment() throws InterruptedException, IOException {
        Vacancy v = null;
        vacancy = exchanger.exchange(v);
        if (vacancy != null) {
            doc = Jsoup.connect(vacancy.getUrlVacancy()).get();
            Elements keySkills = doc.getElementsByClass("bloko-tag bloko-tag_inline Bloko-TagList-Tag ");
            if (keySkills != null && !keySkills.isEmpty()) {
                keySkills.forEach(e -> {
                    String competencyName = e.text().trim();
                    if (competencyName.contains("1C"))
                        competencyName = competencyName.replaceAll("1C", "1С");
                    competencyData.add(competencyName);
                    System.out.println(this.getName() + " " + vacancy.getUrlVacancy());
                });
            }
        }
    }

    @Override
    public void transferData() throws InterruptedException {
        if (Parsing.getParsingTarget().equals(CollectViewController.PARSING_TARGET.AnalysisCompetency)) {
            HashMap<String, Integer> tmp = null;
            ArrayList<String> tmpList = null;
            boolean stop = false;
            boolean mapSend = false, listSend = false;
            do {
                if (!mapSend)
                    tmp = exchangerToAnalysis.exchange(hits);
                if (!listSend && CollectViewController.getLogGenerate())
                    tmpList = exchangerToLog.exchange(logData);

                if (tmp == null && !mapSend) {
                    mapSend = true;
                    System.out.println(name + " отправил карту");
                }
                if (tmpList == null && !listSend && CollectViewController.getLogGenerate()) {
                    listSend = true;
                    System.out.println(name + " отправил лист");
                }
                if (tmp == null && tmpList == null)
                    stop = true;
            }
            while (!stop);
        }
        else {
            HashSet<String> tmpSet = new HashSet<>();
            boolean stop = false;
            boolean setSend = false;
            do {
                if (!setSend)
                    tmpSet = exchangerToCollect.exchange(competencyData);
                if (tmpSet == null && !setSend) {
                    setSend = true;
                    System.out.println(name + " отправил сет");
                }
                if (tmpSet == null)
                    stop = true;
            } while (!stop);
        }
    }


}
