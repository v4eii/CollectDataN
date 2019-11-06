package pack.threads.HH;

import javafx.application.Platform;
import pack.db.entity.Category;
import pack.threads.Parsing;
import pack.util.Config;
import pack.util.Vacancy;
import pack.view.controllers.MainViewController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Распределение объявлений по обрабатывающим потокам
 * @author v4e
 */
public class HHDistribution extends Thread {
    
    /**
     * mainExchanger - обмен с парсером
     * exchanger - обмен с обрабатывающими потоками
     */
    private Exchanger<Vacancy> mainExchanger,
                               exchanger;
    // Группа обрабатывающих потоков
    private ThreadGroup treatmentGroup;
    // Список обрабатывающих потоков
    private List<HHTreatment> threads;
    // Статус потока
    private boolean active;
    private HHCategoryParser hhCategoryParser;
    private final Category category;

    /**
     * Инициализация объектов передачи и обработки
     * @param exchanger объект для обмена между парсером категории и данным потоком
     * @param category категория обработки
     * @param hhCategoryParser парсер категории, используется для доступа к финальным потокам, обрабатываемых слов и т.д.
     */
    HHDistribution(Exchanger<Vacancy> exchanger, Category category, HHCategoryParser hhCategoryParser)
    {
        this.hhCategoryParser = hhCategoryParser;
        this.mainExchanger = exchanger;
        this.exchanger = new Exchanger<>();
        treatmentGroup = new ThreadGroup("HHTreatment");
        threads = new ArrayList<>();
        active = true;
        this.category = category;
        this.setName("THREAD@Dist HH#" + (HHCategoryParser.getNumberThread()));
        HHCategoryParser.addNumberThread();
    }

    @Override
    public void run()
    {
        for (int i = 0; i < Config.getThreadCount(); i++)
        {
            HHTreatment thread = new HHTreatment(exchanger, hhCategoryParser.getFinalProcessing(), treatmentGroup,
                    "THREAD@Treatment HH#" + (HHCategoryParser.getNumberThread()), hhCategoryParser);
            hhCategoryParser.getProcessesCompletion().put("THREAD@Treatment HH#" + (HHCategoryParser.getNumberThread()), false);
            HHCategoryParser.addNumberThread();
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
                if (!isActive())
                    break;
            }
        }
        
        // Команда на завершение потоков обработки
        threads.forEach((t) ->
                t.setActive(false));
        treatmentGroup.interrupt();
        System.out.println(this.getName() + " Stopped");
//        Platform.runLater(() -> MainViewController.getCollectViewController().getTextInfo().appendText("Завершение обработки HeadHunter... \n"));

    }

    void setActive(boolean active)
    {
        this.active = active;
    }

    private boolean isActive()
    {
        return active;
    }

}
