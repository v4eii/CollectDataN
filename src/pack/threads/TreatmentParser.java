package pack.threads;

import java.io.IOException;

/**
 * Интерфейс парсеров обработки заявлений
 */
public interface TreatmentParser extends Parser {

    /**
     * Метод обработки заявления при анализе по существующим компетенциям
     * @throws InterruptedException может возникнуть в при получении данных из распределяющего потока
     * @throws IOException может возникнуть при получении страницы объявления
     */
    void analysisTreatment() throws InterruptedException, IOException;

    /**
     * Метод обработки заявления по сбору компетенций из заявлений
     * @throws InterruptedException может возникнуть в при получении данных из распределяющего потока
     * @throws IOException может возникнуть при получении страницы объявления
     */
    void collectTreatment() throws InterruptedException, IOException;

    /**
     * Метод передачи данных в завершающий поток
     * @throws InterruptedException может возникнуть при передачи данных в завершающий поток
     */
    void transferData() throws InterruptedException;

}
