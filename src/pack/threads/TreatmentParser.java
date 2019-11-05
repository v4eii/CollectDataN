package pack.threads;

import java.io.IOException;

/**
 * Created by v4e on 05.11.2019
 */
public interface TreatmentParser extends Parser {

    void analysisTreatment() throws InterruptedException, IOException;
    void collectTreatment() throws InterruptedException, IOException;
    void transferData() throws InterruptedException;

}
