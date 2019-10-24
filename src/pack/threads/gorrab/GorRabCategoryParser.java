package pack.threads.gorrab;

import javafx.application.Platform;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pack.db.DBBean;
import pack.db.entity.Category;
import pack.db.entity.Skills;
import pack.threads.FinalProcessing;
import pack.util.Config;
import pack.util.Vacancy;
import pack.view.controllers.MainViewController;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Парсер конкретной категории (ex: IT, информатика)
 * @author v4e
 */
public class GorRabCategoryParser extends Thread {

    private Exchanger<Vacancy> exchanger;
    // Распределяющий поток
    private GorRabDistribution dist;
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
    public GorRabCategoryParser(String URL, Category category) {
        setPriority(9);
        exchanger = new Exchanger<>();
        skillsForCategory = DBBean.getInstance().getSkillsJPAController().findSkillsEntities().stream().filter(skills -> skills.getIdCategory().equals(category)).collect(Collectors.toList());
        finalThread = new FinalProcessing();
        finalThread.setName("THREAD@" + category.getName() + " GorRab#" + getNumberThread());
        addNumberThread();
        processesCompletion = new HashMap<>();
        this.URL = URL;
        this.category = category;
        dist = new GorRabDistribution(exchanger, category, this);
    }

    @Override
    public void run() {
        dist.start();
        try {
            System.out.println(URL + " start");
            doc = Jsoup.connect(URL).get();

            while (true) {
                Elements vacancyList = doc.getElementsByTag("h2");
                for (Element e : vacancyList) {
                    Elements temp = e.getElementsByTag("a");
                    String nameVacancy = temp.text();
                    String urlVacancy = temp.attr("abs:href");
                    Vacancy tmp = new Vacancy(nameVacancy, urlVacancy);
                    exchanger.exchange(tmp);
                    GorRabParser.addElementCount();

                }

                Element nextPage = doc.getElementsByClass("pager-item pager-item_type_after").get(0);

                dist.setActive(false);
                System.out.println(this.getName() + " Stopped");
                break;

//               if (nextPage == null)
//               {
//                   dist.setActive(false);
//                   break;
//               }
//               else
//               {
//                   doc = Jsoup.connect(nextPage.attr("abs:href")).get();
//               }
            }

            finalThread.start();
            dist.interrupt();
//            Platform.runLater(() -> MainViewController.getCollectViewController().getTextInfo()
//                    .appendText("Обработка ГородРабот почти завершена, всего объявлений: " + GorRabParser.getElementCount() + "\n"));
            numberThread = 0;

        }
        //TODO: Проблема "Connection timed out: connect". Огромная нагрузка на сеть? Не хватает скорости? (при обработке HH + GR). Попытка реконекта не сильно помогает
        catch (ConnectException ex) {
            try {
                Thread.sleep(5000);
                doc = Jsoup.connect(doc.getElementsByClass("pager-item pager-item_type_after").get(0).attr("abs:href")).get();
            }
            catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    static synchronized Integer getNumberThread() {
        return numberThread;
    }

    static synchronized void addNumberThread() {
        numberThread++;
    }

    public Category getCategory() {
        return category;
    }

    public String getURL() {
        return URL;
    }

    public FinalProcessing getFinalThread() {
        return finalThread;
    }

    public HashMap<String, Boolean> getProcessesCompletion() {
        return processesCompletion;
    }

    public List<Skills> getSkillsForCategory() {
        return skillsForCategory;
    }
}
