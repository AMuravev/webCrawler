package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebCrawler extends JFrame {

    private final String LINE_SEPARATOR = System.getProperty("line.separator");

    public WebCrawler() {
        super("Simple Window");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 440);
        setLocationRelativeTo(null);

        initComponents();

        setLayout(null);
        setVisible(true);
    }

    private void initComponents() {

        JLabel labelTitleURL = new JLabel();
        labelTitleURL.setBounds(5, 5, 60, 30);
        labelTitleURL.setText("URL: ");
        add(labelTitleURL);

        JTextField textField = new JTextField();
        textField.setBounds(50, 5, 200, 30);
        textField.setName("UrlTextField");
        add(textField);

        JLabel labelTitleText = new JLabel();
        labelTitleText.setBounds(5, 35, 120, 30);
        labelTitleText.setText("Title: ");
        add(labelTitleText);

        JLabel labelTitleName = new JLabel();
        labelTitleName.setBounds(50, 35, 200, 30);
        labelTitleName.setName("TitleLabel");
        add(labelTitleName);

        JButton button = new JButton("Parse");
        button.setBounds(260, 5, 100, 30);
        button.setName("RunButton");
        add(button);

        String[] columnNames = {"URL", "Title"};
        JTable table = new JTable(new DefaultTableModel(columnNames, 0));
        table.setName("TitlesTable");
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        scrollPane.setBounds(5, 65, 570, 290);
        add(scrollPane);

        JLabel labelExportText = new JLabel();
        labelExportText.setBounds(5, 360, 120, 30);
        labelExportText.setText("Export: ");
        add(labelExportText);

        JTextField textExportField = new JTextField();
        textExportField.setBounds(50, 360, 200, 30);
        textExportField.setName("ExportUrlTextField");
        add(textExportField);

        JButton buttonExport = new JButton("Save");
        buttonExport.setBounds(260, 360, 100, 30);
        buttonExport.setName("ExportButton");
        add(buttonExport);

        button.addActionListener(e -> {

            String url = textField.getText();
            String defaultHost = null;
            String content = null;
            String label = null;
            List<String> links;
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            if (url != null && url.trim().length() > 0) {
                try {
                    content = HTMLParser.parseContent(url);
                    label = HTMLParser.parseTitle(content);

                    labelTitleName.setText(label);

                } catch (IOException ignored) {
                }

                model.setRowCount(0);
                model.addRow(new Object[]{url, label});

                defaultHost = HTMLParser.parseHost(url);
                links = HTMLParser.getLinks(content);

                for (String link : links) {
                    String modifyURL = HTMLParser.parseURL(link, defaultHost);
                    String modifyContent;
                    try {
                        modifyContent = HTMLParser.parseContent(modifyURL);
                    } catch (Exception ioException) {
                        modifyContent = "";
                    }
                    if (!modifyContent.equals("")) {
                        String modifyTitle = HTMLParser.parseTitle(modifyContent);
                        model.addRow(new Object[]{modifyURL, modifyTitle});
                    }
                }
            }
        });

        buttonExport.addActionListener(e -> {
            String path = textExportField.getText();
            List<String> dataRaw = new ArrayList<>();

            if (path != null && path.trim().length() > 0) {

                for (int i = 0; i < table.getRowCount(); i++) {
                    dataRaw.add(table.getValueAt(i, 0).toString());
                    dataRaw.add(table.getValueAt(i, 1).toString());
                }

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