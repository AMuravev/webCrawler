package crawler;

import java.util.ArrayList;
import java.util.List;

public class PagesSummary {

    List<String> rawData = new ArrayList<>();

    public synchronized boolean put(List<String> data) {
        if (!rawData.contains(data.get(0))) {
            rawData.addAll(data);
            return true;
        }
        return false;
    }

    public synchronized List<String> get() {
        return rawData;
    }
}
