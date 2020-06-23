package crawler;

import java.io.IOException;
import java.util.Map;

public class Worker extends Thread {

    private WorkerScheduleManager workerScheduleManager;

    public Worker(WorkerScheduleManager workerScheduleManager) {
        this.workerScheduleManager = workerScheduleManager;
    }

    @Override
    public void run() {
        nextSchedule();
    }

    private void nextSchedule() {
        try {
            while (ParserFactory.flag) {
                next(workerScheduleManager.nextTask());
            }
        }
        catch (AllTasksCompeteException ignored) {
//            System.out.println(this.getName() + " is finished.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void next(Map.Entry<String, Integer> el) {

        try {
            workerScheduleManager.work(el);

            workerScheduleManager.taskComplete();

        } catch (IOException e) {
//            System.out.println("Cant resolve task: " + el.getKey());
        }
    }
}

interface WorkerScheduleManager {

    Map.Entry<String, Integer> nextTask() throws AllTasksCompeteException;

    void work(Map.Entry<String, Integer> task) throws IOException;

    void taskComplete();
}

class AllTasksCompeteException extends Exception {

    AllTasksCompeteException(String message) {
        super(message);
    }
}
