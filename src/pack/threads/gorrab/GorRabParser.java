package pack.threads.gorrab;

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
import pack.view.controllers.CollectViewController;

import java.io.IOException;

/**
 * Created by v4e on 12.10.2019
 */

/**
 * Отвечает за запуск парсеров по категориям, аналог HHParser
 */
public class GorRabParser implements Parser {

    private static Document doc;
    private static int elementCount;

    /**
     * Сбор всех категорий вакансий с сайта и сопоставление их с категориями из БД
     * запуск отдельных парсеров на каждую категорию. ЭТО НЕ МЕТОД КЛАССА Thread!!
     */
    public static void run() {
        try {
            if (!Parsing.getParsingTarget().equals(CollectViewController.PARSING_TARGET.CollectCompetency)) {
                doc = Jsoup.connect(Config.getGorRabUrl()).get();
                Elements profs = doc.getElementById("vacancy").getElementsByClass("clearfix str-crop");
                for (Element e : profs) {
                    String href = e.attr("abs:href");
                    Category category = DBBean.getInstance().getCategoryJPAController().findCategoryByName(e.text().replaceAll("[^-А-я,A-z ]", "").trim());
                    if (category != null && category.getName().equals("Юристы")) {
                        GorRabCategoryParser tmp = new GorRabCategoryParser(href, category);
                        tmp.start();
                    }
                }
            }
            else {
                System.err.println("Сбор компетенций на данный момент не поддерживается на сайте ГородРабот");
            }
        }
        catch (IOException e) {
            ThreadService.showDialog("Ошибка", "Не удалось соединиться с сайтом обработки" +
                    "\n проверьте интернет-соединение", Alert.AlertType.ERROR);
        }
    }

    public synchronized static int getElementCount() {
        return elementCount;
    }

    public synchronized static void addElementCount() {
        elementCount++;
    }
}
