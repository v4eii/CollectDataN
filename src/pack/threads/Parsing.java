package pack.threads;

import javafx.application.Platform;
import pack.db.DBBean;
import pack.threads.HH.HHParser;
import pack.threads.gorrab.GorRabParser;
import pack.util.Config;
import pack.view.controllers.MainViewController;

/**
 *  Created by v4e on 13.07.2019
 */

/**
 * Класс запускающий процесс парсинга всех сайтов
 */
public class Parsing {

    public static void run() {

        // TODO: Пришла гениальная мысль, потоки одной категории, но с разных сайтов никак не связаны и выдают только результаты своих сайтов.
        //  Придумать как это попровить, добавить по еще одному уже прям финальному потоку на категорию или исправить уже существующий Final
        //  Я устал босс.
        if (Config.getHHSelect()) {
            HHParser.run();
        }
        if (Config.getGorRabSelect()){
            GorRabParser.run();
        }
        Platform.runLater(() -> MainViewController.getCollectViewController().getTextInfo().appendText("Началась обработка сайтов...\n"));

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
