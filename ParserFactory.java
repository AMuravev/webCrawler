package crawler;

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
    private Counter pageCounter;

    ParserFactory(Counter counter) {
        this.queueManager = new QueueManager();
        this.pagesSummary = new PagesSummary();
        this.pageCounter = counter;
    }

    public void parseURL(String url) {
        queueManager.put(Map.entry(url, 0));
        start();
        checkTimeLimit();
        setWorkersShutdownScheduled();
    }

    public boolean isRunning() {
        for (Thread worker : workers) {
            return worker.getState() != Thread.State.WAITING;
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
            workers[i] = new Worker(queueManager, pagesSummary, pageCounter);
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
                }
            }
        }, 1000, 1000);
    }

    public void start() {
        for (Thread worker : workers) {
            worker.start();
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        ParserFactory.flag = false;
        queueManager.clearQueue();
        System.out.println("Work stopped");
    }

    public List<String> getData() {
        return pagesSummary.get();
    }
}