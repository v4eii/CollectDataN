package pack.threads.HH;

import javafx.scene.control.Alert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pack.db.DBBean;
import pack.db.entity.Category;
import pack.services.ThreadService;
import pack.threads.Parser;
import pack.threads.Parsing;
import pack.util.Config;

import java.io.IOException;

/**
 * Created by v4e on 12.10.2019
 */

/**
 * Отвечает за запуск парсеров по категориям, аналог GorRabParser
 */
public class HHParser implements Parser {

    private static Document doc;
    private static int elementsCount;

    /**
     * Сбор всех категорий вакансий с сайта и сопоставление их с категориями из БД
     * запуск отдельных парсеров на каждую категорию. ЭТО НЕ МЕТОД КЛАССА Thread!!
     */
    public static void run() {

        try {
            doc = Jsoup.connect(Config.getHHUrl()).get();
            Elements profs = doc.getElementsByClass("g-row g-nopaddings").get(0).getElementsByClass("catalog__item-link");
            for (Element e : profs) {
                String href = e.attr("abs:href");
                Category category = DBBean.getInstance().getCategoryJPAController().findCategoryByName(e.text().trim());
                if (category != null && category.getName().equals("Юристы")) {//Информационные технологии, интернет, телеком
                    HHCategoryParser tmp = new HHCategoryParser(href, category);
                    tmp.start();
                }
            }
        }
        catch (IOException e) {
            ThreadService.showDialog("Ошибка", "Не удалось соединиться с сайтом обработки" +
                    "\n проверьте интернет-соединение", Alert.AlertType.ERROR);
        }
    }

    public synchronized static void addElementsCount() {
        elementsCount++;
    }

    public synchronized static int getElementsCount() {
        return elementsCount;
    }
}
