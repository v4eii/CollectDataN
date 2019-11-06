package pack.threads.gorrab;

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
    private Category category;

    /**
     * Инициализация объектов передачи и обработки
     * @param exchanger объект для обмена между парсером категории и данным потоком
     * @param category категория обработки
     * @param gorRabCategoryParser парсер категории, используется для доступа к финальным потокам, обрабатываемых слов и т.д.
     */
    GorRabDistribution(Exchanger<Vacancy> exchanger, Category category, GorRabCategoryParser gorRabCategoryParser)
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
            GorRabTreatment thread = new GorRabTreatment(exchanger, gorRabCategoryParser.getFinalThread(), treatmentGroup,
                    "THREAD@Treatment GorRab#" + (GorRabCategoryParser.getNumberThread()), gorRabCategoryParser);
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
