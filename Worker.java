package crawler;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Worker extends Thread {

    private QueueManager queueManager;
    private PagesSummary pagesSummary;
    private Counter pageCounter;

    public Worker(QueueManager queueManager, PagesSummary pagesSummary, Counter counter) {
        this.queueManager = queueManager;
        this.pagesSummary = pagesSummary;
        this.pageCounter = counter;
    }

    @Override
    public void run() {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::nextSchedule, 0, 500, TimeUnit.MICROSECONDS);

        while (!ParserFactory.flag) {
            executor.shutdownNow();
        }
    }

    private void nextSchedule() {
        Map.Entry<String, Integer> next;
        try {
            while ((next = queueManager.next()) != null) {
                parseURL(next);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseURL(Map.Entry<String, Integer> el) {

        try {
            String url = el.getKey();
            int deep = el.getValue() + 1;

            String defaultHost = HTMLParser.parseHost(url);
            String modifyURL = HTMLParser.parseURL(url, defaultHost);

            String content = HTMLParser.parseContent(modifyURL);
            String title = HTMLParser.parseTitle(content);

            pagesSummary.put(List.of(modifyURL, title));
            pageCounter.increment();

            List<String> links = HTMLParser.getLinks(content);

            for (String link : links) {
                queueManager.put(Map.entry(link, deep));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
