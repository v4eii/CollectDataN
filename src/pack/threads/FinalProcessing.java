package pack.threads;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pack.db.entity.Category;
import pack.services.ThreadService;
import pack.util.Config;
import pack.view.controllers.CollectViewController;
import pack.view.controllers.MainViewController;
import java.text.DecimalFormat;
import java.util.*;
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
    private Set<String> allCompetency;
    private Exchanger<HashMap<String, Integer>> exchanger;
    private Exchanger<HashSet<String>> exchangerToCollect;
    /**
     * Список карт потоков
     */
    private List<HashMap<String, Integer>> mapList;
    private List<HashSet<String>> setList;
    private Category category;
    // Общее число ключевых слов
    private int countHits = 0;
    private CollectViewController.PARSING_TARGET parsingTarget;

    public FinalProcessing(Category category, CollectViewController.PARSING_TARGET parsingTarget)
    {
        this.parsingTarget = parsingTarget;
        exchanger = new Exchanger<>();
        exchangerToCollect = new Exchanger<>();
        allHits = null;
        allCompetency = null;
        this.category = category;
        mapList = new ArrayList<>();
        setList = new ArrayList<>();
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
            ex.printStackTrace();
        }

        // Сбор карт из потоков.
        int threadCount = Config.getThreadCount();
        // TODO: Ну можно не x*2
        if (parsingTarget.equals(CollectViewController.PARSING_TARGET.AnalysisCompetency)) {
            for (int i = 0; i < threadCount * 2; i++) {
                try {
                    HashMap<String, Integer> x = null;
                    mapList.add(exchanger.exchange(x));
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            //Сбор листов для лога
            allHits = ThreadService.collectMap(mapList);
            allHits.values().forEach((Integer t) ->
                    countHits += t);
            allHits.forEach((String str, Integer i) ->
            {
                double tmp = i.doubleValue() / countHits * 100;
                System.out.println(str + " " + tmp + "%");
//                MainViewController.getCollectViewController().getTextInfo().appendText(str + " " + new DecimalFormat("#.##").format(tmp) + "%" + "\n");
            });
        }
        else {
            // FixMe: заделать как x*2, а мб и не на 2
            for (int i = 0; i < threadCount; i++) {
                try {
                    HashSet<String> x = null;
                    setList.add(exchangerToCollect.exchange(x));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            allCompetency = ThreadService.collectSet(setList);
//            Platform.runLater(() -> {
//                Stage stage = new Stage(StageStyle.UTILITY);
//                stage.setScene(new Scene(new BorderPane(new ListView<String>(FXCollections.observableArrayList(allCompetency)))));
//                stage.showAndWait();
//            });
            System.out.println(allCompetency.size());
            Collection<String> strings = ThreadService.recombineCompetency(allCompetency);
            strings.forEach(System.out::println);
        }

        System.out.println(getName() + " Stopped");
    }

    public Exchanger<HashSet<String>> getExchangerToCollect() {
        return exchangerToCollect;
    }

    public Exchanger<HashMap<String, Integer>> getExchanger()
    {
        return exchanger;
    }

    public Category getCategory() {
        return category;
    }
}
