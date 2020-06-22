package crawler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ParserFactory {

    public static volatile boolean flag = true;
    Thread[] workers = new Worker[1];
    private QueueManager queueManager;
    private PagesSummary pagesSummary;
    private int timeLimit;
    private ParserFactoryEventListener parserFactoryEventListener;

    ParserFactory(ParserFactoryEventListener parserFactoryEventListener) {
        this.parserFactoryEventListener = parserFactoryEventListener;
        this.queueManager = new QueueManager();
        this.pagesSummary = new PagesSummary();
    }

    public void start(String url) {
        ParserFactory.flag = true;
        queueManager.put(Map.entry(url, 0));
        start();
        checkTimeLimit();
        setWorkersShutdownScheduled();
    }

    public boolean isRunning() {
        for (Thread worker : workers) {
            if (worker.getState() != Thread.State.WAITING && ParserFactory.flag) {
                return true;
            }
        }
        return false;
    }

    public void setTimeLimit(int time) {
        timeLimit = time;
    }

    public void setDepth(int depth) {
        queueManager.setDepth(depth);
    }

    public void setWorkers(int count) {
        workers = new Worker[Math.max(1, count)];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(new WorkerScheduleManager() {
                @Override
                public Map.Entry<String, Integer> nextTask() {
                    return queueManager.next();
                }

                @Override
                public void work(Map.Entry<String, Integer> task) throws IOException {
                    String url = task.getKey();
                    int deep = task.getValue() + 1;

                    String defaultHost = HTMLParser.parseHost(url);
                    String modifyURL = HTMLParser.parseURL(url, defaultHost);

                    String content = HTMLParser.parseContent(modifyURL);
                    String title = HTMLParser.parseTitle(content);

                    pagesSummary.put(List.of(modifyURL, title));

                    List<String> links = HTMLParser.getLinks(content);

                    for (String link : links) {
                        queueManager.put(Map.entry(link, deep));
                    }
                }

                @Override
                public void taskComplete() {
                    parserFactoryEventListener.eventIteration();
                }
            });
        }
    }

    public void checkTimeLimit() {
        if (timeLimit != 0) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stop();
                }
            }, timeLimit * 1000);
        }
    }

    public void setWorkersShutdownScheduled() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isRunning()) {
                    stop();
                    timer.cancel();
                }
            }
        }, 1000, 1000);
    }

    public void start() {
        for (Thread worker : workers) {
            worker.start();
        }
        parserFactoryEventListener.eventStart();
    }

    public void stop() {
        if (ParserFactory.flag) {
            ParserFactory.flag = false;
            queueManager.clearQueue();
            parserFactoryEventListener.eventStop();
        }
    }

    public List<String> getData() {
        return pagesSummary.get();
    }
}

interface ParserFactoryEventListener {

    void eventStart();

    void eventIteration();

    void eventStop();
}