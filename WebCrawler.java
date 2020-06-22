package crawler;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class WebCrawler extends JFrame {

    public WebCrawler() {
        super("Web Crawler");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 330);
        setLocationRelativeTo(null);

        initComponents();

        setLayout(null);
        setVisible(true);
    }

    private void initComponents() {

        final int DEFAULT_HEIGHT = 30;
        final int DEFAULT_LABEL_WIDTH = 120;

        final int X_FIRST_COL = 10;
        final int X_SECOND_COL = 130;
        final int X_THIRD_COL = 470;

        //start row
        JLabel labelTitleURL = new JLabel("Start URL:");
        labelTitleURL.setBounds(X_FIRST_COL, 5, DEFAULT_LABEL_WIDTH, DEFAULT_HEIGHT);
        add(labelTitleURL);

        JTextField urlTextField = new JTextField();
        urlTextField.setBounds(X_SECOND_COL, 5, 320, DEFAULT_HEIGHT);
        urlTextField.setName("UrlTextField");
        add(urlTextField);

        JToggleButton runButton = new JToggleButton("Run");
        runButton.setBounds(X_THIRD_COL, 5, 100, DEFAULT_HEIGHT);
        runButton.setName("RunButton");
        add(runButton);

        //start row
        JLabel labelWorkers = new JLabel("Workers:");
        labelWorkers.setBounds(X_FIRST_COL, 45, DEFAULT_LABEL_WIDTH, DEFAULT_HEIGHT);
        add(labelWorkers);

        JTextField workersTextField = new JTextField();
        workersTextField.setBounds(X_SECOND_COL, 45, 440, DEFAULT_HEIGHT);
        workersTextField.setName("WorkersTextField");
        add(workersTextField);

        //start row
        JLabel depthLabel = new JLabel("Maximum depth:");
        depthLabel.setBounds(X_FIRST_COL, 85, DEFAULT_LABEL_WIDTH, DEFAULT_HEIGHT);
        add(depthLabel);

        JTextField depthTextField = new JTextField();
        depthTextField.setBounds(X_SECOND_COL, 85, 320, DEFAULT_HEIGHT);
        depthTextField.setName("DepthTextField");
        add(depthTextField);

        JCheckBox depthCheckBox = new JCheckBox("Enabled");
        depthCheckBox.setBounds(X_THIRD_COL, 85, 100, DEFAULT_HEIGHT);
        depthCheckBox.setName("DepthCheckBox");
        add(depthCheckBox);

        //start row
        JLabel timeLabel = new JLabel("Time limit:");
        timeLabel.setBounds(X_FIRST_COL, 125, DEFAULT_LABEL_WIDTH, DEFAULT_HEIGHT);
        add(timeLabel);

        JTextField timeTextField = new JTextField();
        timeTextField.setBounds(X_SECOND_COL, 125, 220, DEFAULT_HEIGHT);
        add(timeTextField);

        JLabel timeSecondLabel = new JLabel("seconds");
        timeSecondLabel.setBounds(400, 125, DEFAULT_LABEL_WIDTH, DEFAULT_HEIGHT);
        add(timeSecondLabel);

        JCheckBox timeCheckBox = new JCheckBox("Enabled");
        timeCheckBox.setBounds(X_THIRD_COL, 125, 100, DEFAULT_HEIGHT);
        add(timeCheckBox);

        //start row
        JLabel timeElapsedLabel = new JLabel("Elapsed time:");
        timeElapsedLabel.setBounds(X_FIRST_COL, 165, DEFAULT_LABEL_WIDTH, DEFAULT_HEIGHT);
        add(timeElapsedLabel);

        ClockLabel clockLabel = new ClockLabel();
        clockLabel.setBounds(X_SECOND_COL, 165, DEFAULT_LABEL_WIDTH, DEFAULT_HEIGHT);
        add(clockLabel);

        //start row
        JLabel pageLabel = new JLabel("Parsed pages:");
        pageLabel.setBounds(X_FIRST_COL, 205, DEFAULT_LABEL_WIDTH, DEFAULT_HEIGHT);
        add(pageLabel);

        JLabel parsedLabel = new JLabel("0");
        parsedLabel.setBounds(X_SECOND_COL, 205, 220, DEFAULT_HEIGHT);
        parsedLabel.setName("ParsedLabel");
        add(parsedLabel);

        //start row
        JLabel exportLabel = new JLabel("Export:");
        exportLabel.setBounds(X_FIRST_COL, 245, DEFAULT_LABEL_WIDTH, DEFAULT_HEIGHT);
        add(exportLabel);

        JTextField exportUrlTextField = new JTextField();
        exportUrlTextField.setBounds(X_SECOND_COL, 245, 320, DEFAULT_HEIGHT);
        exportUrlTextField.setName("ExportUrlTextField");
        add(exportUrlTextField);

        JButton exportButton = new JButton("Save");
        exportButton.setBounds(X_THIRD_COL, 245, 100, DEFAULT_HEIGHT);
        exportButton.setName("ExportButton");
        add(exportButton);

        Counter labelCounter = new Counter() {
            private int counter = 0;
            private JLabel item = parsedLabel;

            @Override
            public void increment() {
                item.setText(String.valueOf(counter++));
            }
        };

        ParserFactory parserFactory = new ParserFactory(new ParserFactoryEventListener() {
            @Override
            public void eventStart() {
                runButton.setText("Stop");
                runButton.setSelected(true);
                clockLabel.start();
            }

            @Override
            public synchronized void eventIteration() {
                labelCounter.increment();
            }

            @Override
            public void eventStop() {
                clockLabel.stop();
                runButton.setText("Run");
                runButton.setSelected(false);
                System.out.println("Work stopped");
            }
        });

        runButton.addActionListener(e -> {
            String url = urlTextField.getText();
            String workers = workersTextField.getText();
            String depth = depthTextField.getText();
            String timeLimit = timeTextField.getText();

            if (workers.matches("^\\d+$")) {
                parserFactory.setWorkers(Integer.parseInt(workers));
            }

            if (depthCheckBox.isSelected() && depth.matches("^\\d+$")) {
                parserFactory.setDepth(Integer.parseInt(depth));
            }

            if (timeCheckBox.isSelected() && timeLimit.matches("^\\d+$")) {
                parserFactory.setTimeLimit(Integer.parseInt(timeLimit));
            }

            if (url != null && url.trim().length() > 0) {
                if (runButton.isSelected()) {
                    parserFactory.start(url);
                } else {
                    parserFactory.stop();
                }
            }

        });

        exportButton.addActionListener(e -> {
            String path = exportUrlTextField.getText();
            List<String> dataRaw = parserFactory.getData();

            if (path != null && path.trim().length() > 0) {
                export(path, dataRaw);
            }
        });
    }

    private void export(String path, List<String> data) {
        try {
            FileManager.exportLogs(path, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

@FunctionalInterface
interface Counter {
    void increment();
}