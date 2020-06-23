package crawler;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class ClockLabel extends JLabel {

    private Timer timer;
    private LocalDateTime startTime;
    private final Duration duration = Duration.ofMinutes(0);

    public ClockLabel() {
        super(String.format("%02dh %02dm %02ds", 0, 0, 0));
    }

    protected void start() {

        startTime = LocalDateTime.now();

        timer = new Timer(500, e -> {
            LocalDateTime now = LocalDateTime.now();
            Duration runningTime = Duration.between(now, startTime);
            Duration timeLeft = duration.minus(runningTime);
            if (timeLeft.isZero() || timeLeft.isNegative()) {
                timeLeft = Duration.ZERO;
            }

            setText(format(timeLeft));
        });
        timer.start();
    }

    protected String format(Duration duration) {
        long hours = duration.toHours();
        long mins = duration.minusHours(hours).toMinutes();
        long seconds = duration.minusMinutes(mins).toMillis() / 1000;
        return String.format("%02dh %02dm %02ds", hours, mins, seconds);
    }

    protected void stop() {
        timer.stop();
    }
}
