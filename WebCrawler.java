package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.List;

public class WebCrawler extends JFrame {

    private final String LINE_SEPARATOR = System.getProperty("line.separator");

    public WebCrawler() {
        super("Simple Window");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocationRelativeTo(null);

        initComponents();

        setLayout(null);
        setVisible(true);
    }

    private void initComponents() {

        JTextField textField = new JTextField();
        textField.setBounds(5, 5, 120, 30);
        textField.setName("UrlTextField");
        add(textField);

        JLabel labelTitleText = new JLabel();
        labelTitleText.setBounds(5, 35, 120, 30);
        labelTitleText.setText("Title: ");
        add(labelTitleText);

        JLabel labelTitleName = new JLabel();
        labelTitleName.setBounds(50, 35, 120, 30);
        labelTitleName.setName("TitleLabel");
        add(labelTitleName);

        JButton button = new JButton("Parse");
        button.setBounds(130, 5, 100, 30);
        button.setName("RunButton");
        add(button);

        String[] columnNames = {"URL", "Title"};
        JTable table = new JTable(new DefaultTableModel(columnNames, 0));
        table.setName("TitlesTable");
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        scrollPane.setBounds(5, 65, 300, 250);
        add(scrollPane);

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

    }
}