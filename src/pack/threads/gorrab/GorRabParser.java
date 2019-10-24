package pack.threads.gorrab;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pack.db.DBBean;
import pack.db.entity.Category;
import pack.util.Config;

import java.io.IOException;

/**
 * Created by v4e on 12.10.2019
 */

/**
 * Отвечает за запуск парсеров по категориям, аналог HHParser
 */
public class GorRabParser {

    private static Document doc;
    private static int elementCount;

    public static void run() {

        try {
            doc = Jsoup.connect(Config.getGorRabUrl()).get();
            Elements profs = doc.getElementById("vacancy").getElementsByClass("clearfix str-crop");
            for (Element e : profs) {
                String href = e.attr("abs:href");
                Category category = DBBean.getInstance().getCategoryJPAController().findCategoryByName(e.text().replaceAll("[^-А-я,A-z ]", "").trim());
                if (category != null) {
                    GorRabCategoryParser tmp = new GorRabCategoryParser(href, category);
                    tmp.start();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized static int getElementCount() {
        return elementCount;
    }

    public synchronized static void addElementCount() {
        elementCount++;
    }
}
