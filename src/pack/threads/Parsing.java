package pack.threads;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import pack.db.DBBean;
import pack.db.entity.Category;
import pack.services.ThreadService;
import pack.threads.HH.HHParser;
import pack.threads.gorrab.GorRabParser;
import pack.util.Config;
import pack.view.controllers.CollectViewController;
import pack.view.controllers.MainViewController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Класс запускающий процесс парсинга всех сайтов
 */
public class Parsing {

    private static List<FinalProcessing> finalProcessingList;
    private static CollectViewController.PARSING_TARGET parsingTarget;

    public static void run(CollectViewController.PARSING_TARGET parsingTarget) {
        setParsingTarget(parsingTarget);
        // TODO: Пришла гениальная мысль, потоки одной категории, но с разных сайтов никак не связаны и выдают только результаты своих сайтов.
        //  Придумать как это попровить, добавить по еще одному уже прям финальному потоку на категорию или исправить уже существующий Final
        //  Я устал босс.
        //  Я даже не помню сделал я это или нет
        List<Category> categoryEntities = DBBean.getInstance().getCategoryJPAController().findCategoryEntities();
        finalProcessingList = new ArrayList<>();
        categoryEntities.forEach(category -> {
            FinalProcessing finalProcessing = new FinalProcessing(category, parsingTarget);
            finalProcessing.setName("THREAD@" + category.getName());
            finalProcessingList.add(finalProcessing);
        });

        if (Config.getHHSelect()) {
            HHParser.run();
        }
        if (Config.getGorRabSelect()){
            GorRabParser.run();
        }
        Platform.runLater(() -> MainViewController.getCollectViewController().getTextInfo().appendText("Началась обработка сайтов...\n"));

    }

    public static void setParsingTarget(CollectViewController.PARSING_TARGET parsingTarget) {
        Parsing.parsingTarget = parsingTarget;
    }

    public static CollectViewController.PARSING_TARGET getParsingTarget() {
        return parsingTarget;
    }

    public static List<FinalProcessing> getFinalProcessingList() {
        return finalProcessingList;
    }
    //    private static boolean checkStatus()
//    {
//        Collection<Boolean> statuses = processesCompletion.values();
//        for (Boolean b : statuses)
//        {
//            if (!b)
//            {
//                try
//                {
//                    sleep(3000);
//                    return false;
//                }
//                catch (InterruptedException ex)
//                {
//                    Logger.getLogger(Parsing.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//        return true;
//    }
    
}
