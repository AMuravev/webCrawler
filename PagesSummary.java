package crawler;

import java.util.ArrayList;
import java.util.List;

public class PagesSummary {

    List<String> rawData = new ArrayList<>();

    public synchronized void put(List<String> data) {
        rawData.addAll(data);
    }

    public synchronized List<String> get() {
        return rawData;
    }
}
