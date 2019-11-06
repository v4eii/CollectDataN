package pack.threads.gorrab;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pack.db.entity.Category;
import pack.services.ThreadService;
import pack.threads.FinalProcessing;
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
 * Обрабатывающие потоки
 *
 * @author v4e
 */
public class GorRabTreatment extends Thread implements TreatmentParser {

    private Exchanger<Vacancy> exchanger;
    private Exchanger<HashMap<String, Integer>> exchangerToAnalysis;
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
    private GorRabCategoryParser gorRabCategoryParser;

    /**
     * @param exchanger объект обмена с распределяющим потоком
     * @param finalProcessing объект потока для сбора всех данных
     * @param group группа принадлежности распределяющего потока
     * @param threadName имя потока
     * @param gorRabCategoryParser парсер категории
     */
    GorRabTreatment(Exchanger<Vacancy> exchanger, FinalProcessing finalProcessing,
                    ThreadGroup group, String threadName, GorRabCategoryParser gorRabCategoryParser) {
        super(group, threadName);
        this.gorRabCategoryParser = gorRabCategoryParser;
        this.exchangerToCollect = finalProcessing.getExchangerToCollect();
        this.exchanger = exchanger;
        this.name = threadName;
        active = true;
        hits = new HashMap<>();
        competencyData = new HashSet<>();
        this.exchangerToAnalysis = finalProcessing.getExchanger();
        if (CollectViewController.getLogGenerate())
            logData = new ArrayList<>();
    }

    @Override
    public void run() {
        while (isActive()) {
            try {
                if (Parsing.getParsingTarget().equals(CollectViewController.PARSING_TARGET.AnalysisCompetency))
                    analysisTreatment();
                else if (Parsing.getParsingTarget().equals(CollectViewController.PARSING_TARGET.CollectCompetency)) {
//                    collectTreatment();
                }
            }
            catch (InterruptedException | IOException ex) {
                if (!isActive())
                    break;
            }
        }

        if (CollectViewController.getLogGenerate()) {
            try {
                logData.sort(String::compareTo);
                ThreadService.writeInLog(logData);
            }
            catch (IOException e) {
                Platform.runLater(() -> {
                    ThreadService.showDialog("Ошибка", "Ошибка записи лога, проверьте указанный путь",
                            Alert.AlertType.ERROR);
                });
            }
        }

        gorRabCategoryParser.getProcessesCompletion().put(name, true);

        try {
            transferData();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            String textVacancy = doc.getElementsByClass("vacancy-description clearfix").text();
            ThreadService.firstAnalysis(textVacancy, hits, gorRabCategoryParser.getSkillsForCategory());
            System.out.println(this.getName() + " " + vacancy.getUrlVacancy());
            if (CollectViewController.getLogGenerate())
                logData.add(name + " " + vacancy.getNameVacancy() + "\n");
            Thread.sleep(300);
        }
    }

    @Override
    public void collectTreatment() throws InterruptedException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transferData() throws InterruptedException {
        if (Parsing.getParsingTarget().equals(CollectViewController.PARSING_TARGET.AnalysisCompetency)) {
            HashMap<String, Integer> tmp = null;
            ArrayList<String> tmpList = null;
            boolean stop = false;
            boolean mapSend = false;
            do {
                if (!mapSend)
                    tmp = exchangerToAnalysis.exchange(hits);
                if (tmp == null && !mapSend) {
                    mapSend = true;
                    System.out.println(name + " отправил карту");
                }

                if (tmp == null && tmpList == null)
                    stop = true;
            } while (!stop);
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
