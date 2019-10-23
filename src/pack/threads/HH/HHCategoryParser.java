package pack.threads.HH;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pack.threads.FinalProcessing;
import pack.util.Vacancy;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.concurrent.Exchanger;

/**
 * Парсер главной страницы
 *
 * @author v4e
 */
public class HHCategoryParser extends Thread {

    private Exchanger<Vacancy> exchanger;
    // Распределяющий поток
    private HHDistribution dist;
    private Document doc;
    private final String URL,
            category;
    private static Integer numberThread = 0;
    private FinalProcessing finalThread;
    private HashMap<String, Boolean> processesCompletion;

    public HHCategoryParser(String URL, String category) {
        setPriority(9);
        exchanger = new Exchanger<>();
        finalThread = new FinalProcessing();
        finalThread.setName("THREAD@" + category + " HH#" + getNumberThread());
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
            while (true) {
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
                Elements nextPage = doc.getElementsByClass("bloko-button HH-Pager-Controls-Next HH-Pager-Control");

                dist.setActive(false);
                System.out.println(this.getName() + " Stopped");
                break;

                // TODO: Я бы не стал снимать этот коммент
//                if (nextPage.isEmpty())
//                {
//                    dist.setActive(false);
//                    break;
//                }
//                else
//                {
//                    doc = Jsoup.connect(nextPage.attr("abs:href")).get();
//                }
            }
            finalThread.start();
            dist.interrupt();
//            Platform.runLater(() -> MainViewController.getCollectViewController().getTextInfo()
//                    .appendText("Обработка HeadHunter почти завершена, всего объявлений: " + HHParser.getElementsCount() + "\n"));
            numberThread = 0;

        }
        //TODO: Проблема "Connection timed out: connect". Огромная нагрузка на сеть? Не хватает скорости? (при обработке HH + GR). Попытка реконекта не сильно помогает
        catch (ConnectException ex) {
            try {
                Thread.sleep(5000);
                doc = Jsoup.connect(doc.getElementsByClass("bloko-button HH-Pager-Controls-Next HH-Pager-Control").attr("abs:href")).get();
            }
            catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
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

    public String getCategory() {
        return category;
    }

    public FinalProcessing getFinalProcessing() {
        return finalThread;
    }

    public HashMap<String, Boolean> getProcessesCompletion()
    {
        return processesCompletion;
    }
}
