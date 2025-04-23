import burp.api.montoya.MontoyaApi;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class HeaderJarUserInterface {

    private JPanel ui;
    private JTextPane textPane;
    private JComboBox<Integer> comboBox;
    private HeaderJarHttpHandler handler;
    private int selectedProfile;
    private final Timer debounceTimer;

    private JScrollPane scrollPane;
    private JLabel lblNewLabel;
    private JButton btnNewButton;
    private JPanel panel;

    public HeaderJarUserInterface(MontoyaApi api) {
        ui = new JPanel();
        ui.setLayout(null);

        // Scrollable Text Pane
        scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 67, 1215, 770);
        ui.add(scrollPane);

        textPane = new JTextPane();
        scrollPane.setViewportView(textPane);
        textPane.setFont(new Font("Courier New", Font.PLAIN, 16));
        textPane.setForeground(Color.BLACK);
        textPane.setBackground(new Color(192, 192, 192));

        // Top control panel
        panel = new JPanel();
        panel.setLayout(null);
        ui.add(panel);

        lblNewLabel = new JLabel("Choose Profile:");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        panel.add(lblNewLabel);

        comboBox = new JComboBox<>();
        panel.add(comboBox);
        for (int i = 0; i < 10; i++) {
            comboBox.addItem(i);
        }

        comboBox.addActionListener(e -> {
            if (handler != null && comboBox.getSelectedItem() != null) {
                selectedProfile = (Integer) comboBox.getSelectedItem();
                setHeaderForUi(handler.getRawString()[selectedProfile]);
            }
        });

        btnNewButton = new JButton("SAVE");
        panel.add(btnNewButton);
        btnNewButton.addActionListener(e -> {
            if (handler != null) {
                updateHeader();
                handler.parseProfilesHeaders();
            }
        });

        textPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { resetDebounceTimer(); }
            @Override
            public void removeUpdate(DocumentEvent e) { resetDebounceTimer(); }
            @Override
            public void changedUpdate(DocumentEvent e) { resetDebounceTimer(); }

            private void resetDebounceTimer() {
                debounceTimer.restart();
            }
        });

        selectedProfile = 0;

        debounceTimer = new Timer(500, e -> {
            if (handler != null) {
                updateHeader();
                handler.parseProfilesHeaders();
            }
        });
        debounceTimer.setRepeats(false);

        // Responsive layout
        ui.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustComponents();
            }
        });
    }

    private void adjustComponents() {
        int width = ui.getWidth();
        int height = ui.getHeight();

        int margin = 20;
        int componentHeight = 30;

        // Panel size at top
        int panelHeight = 50;
        panel.setBounds(0, 10, width, panelHeight);

        // Element widths
        int labelWidth = 130;
        int comboWidth = 80;
        int buttonWidth = 100;

        // Total width for centering
        int totalWidth = labelWidth + margin + comboWidth + margin + buttonWidth;
        int startX = (width - totalWidth) / 2;
        int y = (panelHeight - componentHeight) / 2;

        lblNewLabel.setBounds(startX, y, labelWidth, componentHeight);
        comboBox.setBounds(startX + labelWidth + margin, y, comboWidth, componentHeight);
        btnNewButton.setBounds(startX + labelWidth + margin + comboWidth + margin, y, buttonWidth, componentHeight);

        // TextPane and scroll pane responsive size
        int scrollWidth = (int) (width * 0.95);
        int scrollHeight = (int) (height * 0.85);
        int scrollX = (width - scrollWidth) / 2;
        int scrollY = panel.getY() + panelHeight + 10;

        scrollPane.setBounds(scrollX, scrollY, scrollWidth, scrollHeight);
    }


    public JPanel getUI() {
        return this.ui;
    }

    public void setHeaderForUi(String header) {
        this.textPane.setText(header);
    }

    public void setHTTPHandler(HeaderJarHttpHandler handler) {
        this.handler = handler;
    }

    private void updateHeader() {
        if (handler != null) {
            String[] rawStrings = handler.getRawString();
            if (rawStrings != null && selectedProfile >= 0 && selectedProfile < rawStrings.length) {
                rawStrings[selectedProfile] = textPane.getText();
            }
        }
    }

    public int getSelectedProfile() {
        return selectedProfile;
    }

    public void setSelectedProfile(int selectedProfile) {
        this.selectedProfile = selectedProfile;
    }
}
