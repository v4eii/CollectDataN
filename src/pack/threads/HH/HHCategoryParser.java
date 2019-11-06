package pack.threads.HH;

import javafx.scene.control.Alert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pack.db.entity.Category;
import pack.db.entity.Skills;
import pack.services.ThreadService;
import pack.threads.CategoryParser;
import pack.threads.FinalProcessing;
import pack.threads.Parsing;
import pack.util.Vacancy;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Парсер конкретной категории (ex: IT, информатика)
 * @author v4e
 */
public class HHCategoryParser extends Thread implements CategoryParser {

    private Exchanger<Vacancy> exchanger;
    // Распределяющий поток
    private HHDistribution dist;
    private Document doc;
    private final String URL;
    private final Category category;
    private static Integer numberThread = 0;
    private FinalProcessing finalThread;
    private HashMap<String, Boolean> processesCompletion;
    private List<Skills> skillsForCategory;

    /**
     * Инициализация объектов, сбор необходимых слов для анализа
     * @param URL ссылка на страницу с объявлениями определенной категории
     * @param category категория, для поиска ключевых слов из БД
     */
    public HHCategoryParser(String URL, Category category) {
        setPriority(9);
        exchanger = new Exchanger<>();
        skillsForCategory = (List<Skills>) category.getSkillsCollection();
        Parsing.getFinalProcessingList().forEach(finalProcessing -> {
            if (finalProcessing.getCategory().equals(category))
                finalThread = finalProcessing;
        });
        addNumberThread();
        processesCompletion = new HashMap<>();
        this.URL = URL;
        this.category = category;
        dist = new HHDistribution(exchanger, category, this);
    }

    @Override
    public void run() {
        dist.start();
        try {
            System.out.println(URL + " start");
            doc = Jsoup.connect(URL).get();

            do {
                parsePage();
//                dist.setActive(false);
//                System.out.println(this.getName() + " Stopped");
//                break;
            } while (getNextPage());

            if (!finalThread.isAlive())
                finalThread.start();
            dist.interrupt();
            numberThread = 0;

        }
        //TODO: Проблема "Connection timed out: connect". Огромная нагрузка на сеть? Не хватает скорости? (при обработке HH + GR). Попытка реконекта не сильно помогает
        catch (ConnectException ex) {
            ThreadService.showDialog("Ошибка подключения",
                    "Произошла ошибка подключения, проверьте ваше интернет соединение", Alert.AlertType.WARNING);
        }
        catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    static synchronized void addNumberThread() {
        numberThread++;
    }

    static synchronized Integer getNumberThread() {
        return numberThread;
    }

    public String getURL() {
        return URL;
    }

    public Category getCategory() {
        return category;
    }

    public FinalProcessing getFinalProcessing() {
        return finalThread;
    }

    public HashMap<String, Boolean> getProcessesCompletion() {
        return processesCompletion;
    }

    public List<Skills> getSkillsForCategory() {
        return skillsForCategory;
    }

    @Override
    public void parsePage() throws InterruptedException {
        Elements vacancyList = doc.getElementsByClass("vacancy-serp");
        vacancyList = vacancyList.get(0).getElementsByClass("resume-search-item__name");
        for (Element e : vacancyList) {
            Elements temp = e.getElementsByClass("bloko-link HH-LinkModifier");
            String nameVacancy = temp.text();

            String urlVacancy = temp.attr("abs:href");
            Vacancy tmp = new Vacancy(nameVacancy, urlVacancy);
            exchanger.exchange(tmp);
            HHParser.addElementsCount();
        }
    }

    @Override
    public boolean getNextPage() throws IOException {
        Elements nextPage = doc.getElementsByClass("bloko-button HH-Pager-Controls-Next HH-Pager-Control");
        if (nextPage.isEmpty())
        {
            dist.setActive(false);
            return false;
        }
        else
        {
            doc = Jsoup.connect(nextPage.attr("abs:href")).get();
            return true;
        }
    }
}
