package pack.threads;

import java.io.IOException;

/**
 * Интерфейс парсеров страниц категорий
 */
public interface CategoryParser extends Parser {

    /**
     * Метод обработки главной страницы сайта в определённой категории
     * @throws InterruptedException возможно исключение при попытке передачи в распределяющий поток
     */
    void parsePage() throws InterruptedException;

    /**
     * Метод получения следующей страницы сайта
     * @return true - если следующая страница существует;
     * false - если следующей страницы нет
     * @throws IOException возможно искючение при попытке получения страницы
     */
    boolean getNextPage() throws IOException;

}
