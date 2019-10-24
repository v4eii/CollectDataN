package pack.threads;

import pack.services.ThreadService;
import pack.util.Config;
import pack.view.controllers.CollectViewController;
import pack.view.controllers.MainViewController;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Финальный поток для подсчета
 * @author v4e
 */
public class FinalProcessing extends Thread {
    
    /**
     * Карта объединяющяя все карты потоков
     */
    private Map<String, Integer> allHits;
    private List<String> fullLogList;
    private Exchanger<HashMap<String, Integer>> exchanger;
    private Exchanger<ArrayList<String>> exchangerToLog;
    /**
     * Список карт потоков
     */
    private List<HashMap<String, Integer>> mapList;
    private List<List<String>> logList;
    // Общее число ключевых слов
    private int countHits = 0;

    public FinalProcessing()
    {
        exchanger = new Exchanger<>();
        allHits = null;
        mapList = new ArrayList<>();
        if (CollectViewController.getLogGenerate()) {
            exchangerToLog = new Exchanger<>();
            logList = new ArrayList<>();
        }
    }
    
    @Override
    public void run()
    {
        try
        {
            sleep(2000);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(FinalProcessing.class.getName()).log(Level.SEVERE, null, ex);
        }

//        Platform.runLater(() -> {
//            MainViewController.getCollectViewController().getTextInfo().appendText("Начался сбор результатов..." + "\n");
//        });

        // Сбор карт из потоков.
        int threadCount = Config.getThreadCount();
        for (int i = 0; i < threadCount; i++)
        {
            try
            {
                HashMap<String, Integer> x = null;
                mapList.add(exchanger.exchange(x));
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(FinalProcessing.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Сбор листов для лога
        if (CollectViewController.getLogGenerate()) {
            for (int i = 0; i < threadCount; i++) {
                try {
                    ArrayList<String> x = null;
                    logList.add(exchangerToLog.exchange(x));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    
//        MainViewController.getCollectViewController().getTextInfo().appendText("Начался подсчет результатов..." + "\n");
        allHits = ThreadService.collectMap(mapList);
        if (CollectViewController.getLogGenerate())
            ThreadService.generateLog(logList);
        allHits.values().forEach((Integer t) ->
                countHits += t);
        allHits.forEach((String str, Integer i) ->
        {
            double tmp = i.doubleValue()/countHits*100;
            MainViewController.getCollectViewController().getTextInfo().appendText(str + " " + new DecimalFormat("#.##").format(tmp) + "%" + "\n");
        });
        
        

//        Platform.runLater(() -> {
//            MainViewController.getCollectViewController().getTextInfo().appendText("Завершение процесса...\n");
//        });
        System.out.println(getName() + " Stopped");

//        Platform.runLater((Runnable) () -> {
//            if (Service.showConfirmDialog("Сохранение данных","Сохранить данные?",
//                    "Процесс сбора данных завершен, сохранить результат?").get() == ButtonType.OK) {
//                System.out.println("TMP");
//            }
//        });
    }
    
    

    public Exchanger<HashMap<String, Integer>> getExchanger()
    {
        return exchanger;
    }
    
    public Exchanger<ArrayList<String>> getExchangerToLog() {
        return exchangerToLog;
    }
}
