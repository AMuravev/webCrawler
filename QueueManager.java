package crawler;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class QueueManager {

    Deque<Map.Entry<String, Integer>> deque = new ConcurrentLinkedDeque<>();
    Map<String, Integer> tempURLs = new ConcurrentHashMap<>();
    private int maxDepth = Integer.MAX_VALUE;

    public void setDepth(int depth) {
        maxDepth = depth;
    }

    public synchronized Map.Entry<String, Integer> next() {
        while (deque.peekFirst() == null) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        notifyAll();
        return deque.pollFirst();
    }

    public synchronized void put(Map.Entry<String, Integer> el) {
        String url = el.getKey();
        int deep = el.getValue();
        if (!tempURLs.containsKey(url) && deep < maxDepth) {
            tempURLs.put(url, deep);
            if (deque.peekFirst() != null && deep <= deque.peekFirst().getValue()) {
                deque.addFirst(el);
            } else {
                deque.addLast(el);
            }
            notifyAll();
        }
    }
}