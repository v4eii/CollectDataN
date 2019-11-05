package pack.threads;

import java.io.IOException;

/**
 * Created by v4e on 05.11.2019
 */
public interface CategoryParser extends Parser {

    void parsePage() throws InterruptedException;
    boolean getNextPage() throws IOException;

}
