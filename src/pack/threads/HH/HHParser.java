package pack.threads.HH;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pack.db.DBBean;
import pack.db.entity.Category;
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
            e.printStackTrace();
        }

    }

    public synchronized static void addElementsCount() {
        elementsCount++;
    }

    public synchronized static int getElementsCount() {
        return elementsCount;
    }
}
