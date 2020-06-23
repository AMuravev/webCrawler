package crawler;

import java.io.IOException;
import java.util.*;

public class ParserFactory {

    public static volatile boolean flag = true;
    private final ParserFactoryEventListener parserFactoryEventListener;
    private Thread[] workers = new Worker[1];
    private QueueManager queueManager = null;
    private PagesSummary pagesSummary = null;
    private int timeLimit;
    private int maxDepth;

    ParserFactory(ParserFactoryEventListener parserFactoryEventListener) {
        this.parserFactoryEventListener = parserFactoryEventListener;
    }

    public void start(String url) {
        ParserFactory.flag = true;
        initializeEnvironment();
        queueManager.put(Map.entry(url, 0));
        initializeWorkers();
        startWorkers();
        checkTimeLimit();
        setWorkersShutdownScheduled();
    }

    public void stop() {
        if (ParserFactory.flag) {
            ParserFactory.flag = false;
            queueManager.clearQueue();
            stopWorkers();
            parserFactoryEventListener.eventStop();
        }
    }

    public void initializeEnvironment() {
        queueManager = new QueueManager();
        if (maxDepth != 0) {
            queueManager.setDepth(maxDepth);
        }
        pagesSummary = new PagesSummary();
    }

    public void initializeWorkers() {

        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(new WorkerScheduleManager() {

                @Override
                public Map.Entry<String, Integer> nextTask() throws AllTasksCompeteException {
                    Map.Entry<String, Integer> next;
                    if ((next = queueManager.next()) == null) {
                        throw new AllTasksCompeteException("All task complete");
                    }
                    return next;
                }

                @Override
                public void work(Map.Entry<String, Integer> task) throws IOException {
                    String url = task.getKey();
                    int deep = task.getValue() + 1;

                    String defaultHost = HTMLParser.parseHost(url);
                    String modifyURL = HTMLParser.parseURL(url, defaultHost);

                    String content = HTMLParser.parseContent(modifyURL);
                    String title = HTMLParser.parseTitle(content);

                    if (pagesSummary.put(List.of(modifyURL, title))) {
                        parserFactoryEventListener.eventIteration();
                    }

                    List<String> links = HTMLParser.getLinks(content);

                    for (String link : links) {
                        queueManager.put(Map.entry(link, deep));
                    }
                }

                @Override
                public void taskComplete() {

                }
            });
        }
    }

    public synchronized boolean isRunning() {
        for (Thread worker : workers) {
            if (worker.getState() != Thread.State.WAITING && ParserFactory.flag) {
                return true;
            }
        }
        return false;
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

    public void startWorkers() {
        for (Thread worker : workers) {
            worker.start();
        }
        parserFactoryEventListener.eventStart();
    }

    public void stopWorkers() {
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTimeLimit(int time) {
        timeLimit = time;
    }

    public void setDepth(int depth) {
        maxDepth = depth;
    }

    public void setWorkersCount(int count) {
        workers = new Worker[Math.max(1, count)];
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

    public List<String> getData() {
        return pagesSummary.get();
    }
}

interface ParserFactoryEventListener {

    void eventStart();

    void eventIteration();

    void eventStop();
}