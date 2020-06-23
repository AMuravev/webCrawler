package crawler;

import java.util.ArrayList;
import java.util.List;

public class PagesSummary {

    List<String> rawData = new ArrayList<>();

    public synchronized void put(List<String> data) {
        if (!rawData.contains(data.get(0))) {
            rawData.addAll(data);
        }
    }

    public synchronized List<String> get() {
        return rawData;
    }
}
