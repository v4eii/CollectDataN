package pack.threads.gorrab;

import javafx.application.Platform;
import pack.threads.Parsing;
import pack.util.Config;
import pack.util.Vacancy;
import pack.view.controllers.MainViewController;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Распределение объявления по обрабатывающим потокам
 * @author v4e
 */
public class GorRabDistribution extends Thread {
    
    /**
     * mainExchanger - обмен с парсером
     * exchanger - обмен с обрабатывающими потоками
     */

    private Exchanger<Vacancy> mainExchanger,
                               exchanger;
    // Группа обрабатывающих потоков
    private ThreadGroup treatmentGroup;
    // Список обрабатывающих потоков
    private List<GorRabTreatment> threads;
    // Статус потока
    private boolean active;
    private GorRabCategoryParser gorRabCategoryParser;
    private String category;

    GorRabDistribution(Exchanger<Vacancy> exchanger, String category, GorRabCategoryParser gorRabCategoryParser)
    {
        this.gorRabCategoryParser = gorRabCategoryParser;
        this.mainExchanger = exchanger;
        this.exchanger = new Exchanger<>();
        this.category = category;
        treatmentGroup = new ThreadGroup("GorRabTreatment");
        threads = new ArrayList<>();
        active = true;
        this.setName("THREAD@Dist GorRab#" + GorRabCategoryParser.getNumberThread());
        GorRabCategoryParser.addNumberThread();
    }
        
    @Override
    public void run()
    {
        for (int i = 0; i < Config.getThreadCount(); i++)
        {
            GorRabTreatment thread = new GorRabTreatment(exchanger, gorRabCategoryParser.getFinalThread().getExchanger(),
                    gorRabCategoryParser.getFinalThread().getExchangerToLog(), treatmentGroup,
                    "THREAD@Treatment GorRab#" + (GorRabCategoryParser.getNumberThread()),
                    category, gorRabCategoryParser);
            gorRabCategoryParser.getProcessesCompletion().put("THREAD@Treatment GorRab#" + (GorRabCategoryParser.getNumberThread()), false);
            GorRabCategoryParser.addNumberThread();
            thread.start();
            threads.add(thread);
        }

        while (isActive())
        {
            try
            {
                Vacancy v = null;
                Vacancy newVacancy = mainExchanger.exchange(v);
                exchanger.exchange(newVacancy);
            }
            catch (InterruptedException ex)
            {
                //Logger.getLogger(GorRabDistribution.class.getName()).log(Level.SEVERE, null, ex);
                if (!isActive())
                    break;
            }
        }

        threads.forEach(t ->
                t.setActive(false));
        treatmentGroup.interrupt();
        System.out.println(this.getName() + " Stopped");
//        Platform.runLater(() -> MainViewController.getCollectViewController().getTextInfo().appendText("Завершение обработки ГородРабот... \n"));

    }

    private boolean isActive()
    {
        return active;
    }

    void setActive(boolean active)
    {
        this.active = active;
    }

}
