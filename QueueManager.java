package crawler;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class QueueManager {

    Deque<Map<String, Integer>> deque = new ConcurrentLinkedDeque<>();
    Map<String, Integer> tempURLs = new ConcurrentHashMap<>();
}
