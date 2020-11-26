import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShutdownTimer extends JFrame {

    private final JButton applyButton = new JButton("Timer starten");
    private final JButton cancelButton = new JButton("Herunterfahren abbrechen");
    private final JButton helpButton = new JButton("?");
    private final JSpinner minuteSpinner = new JSpinner();
    private final ButtonGroup stateGroup = new ButtonGroup();
    private final JRadioButton radioShutdown = new JRadioButton();
    private final JRadioButton radioRestart = new JRadioButton();
    private final JCheckBox notificationsCheckbox = new JCheckBox("Benachrichtigungen ausblenden");
    private final JCheckBox forceShutdownCheckbox = new JCheckBox("Herunterfahren erzwingen");
    private final JComboBox<String> timeUnitComboBox = new JComboBox<>();
    private final String parameterPlaceholder = "Eigene Parameter";
    private final JTextField parameterTextField = new JTextField(parameterPlaceholder);

    public ShutdownTimer(String name) {
        super(name);
        setResizable(false);
        setLocation(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(ShutdownTimer::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Create and set up the window
        ShutdownTimer frame = new ShutdownTimer("Shutdown Timer");
        frame.setPreferredSize(new Dimension(400, 250));
        // Set content pane Layout
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        // Set up the content pane
        frame.addComponentsToPane(frame.getContentPane());
        // Display the window
        frame.pack();
        frame.setVisible(true);
    }

    public void addComponentsToPane(final Container pane) {
        // Input Panel for time inputs
        final JPanel inputPanel = new JPanel();
        GridLayout inputLayout = new GridLayout(1, 2);
        inputLayout.setHgap(10);
        inputPanel.setLayout(inputLayout);

        // Action panel for actions
        final JPanel actionPanel = new JPanel();
        GridLayout actionLayout = new GridLayout(2, 2);
        actionPanel.setLayout(actionLayout);

        // Param panel for additional parameters
        final JPanel paramPanel = new JPanel();
        GridBagLayout paramLayout = new GridBagLayout();
        paramPanel.setLayout(paramLayout);

        // Control panel for buttons
        final JPanel controlPanel = new JPanel();
        GridLayout controlLayout = new GridLayout(1, 2);
        controlLayout.setHgap(10);
        controlPanel.setLayout(controlLayout);

        // Create combobox with time units
        String[] timeUnits = {"Sekunden", "Minuten", "Stunden"};
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>(timeUnits);
        timeUnitComboBox.setLightWeightPopupEnabled(false);
        timeUnitComboBox.setModel(comboBoxModel);
        timeUnitComboBox.setSelectedIndex(1);

        // Add spinner with model for minutes
        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
        minuteSpinner.setModel(spinnerModel);
        inputPanel.add(minuteSpinner);
        inputPanel.add(timeUnitComboBox);

        // Add radio buttons for states (shutdown or restart)
        actionPanel.add(new Label("Aktion:"));
        actionPanel.add(new Label(""));
        radioShutdown.setText("Herunterfahren");
        radioRestart.setText("Neu starten");
        stateGroup.add(radioShutdown);
        stateGroup.add(radioRestart);
        radioShutdown.setSelected(true);
        actionPanel.add(radioShutdown);
        actionPanel.add(radioRestart);

        // Add checkboxes to GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 100;
        gbc.fill = GridBagConstraints.BOTH;
        paramPanel.add(new Label("Zusätzliche Parameter:"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 10;
        notificationsCheckbox.setSelected(true);
        paramPanel.add(notificationsCheckbox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 10;
        forceShutdownCheckbox.setSelected(true);
        paramPanel.add(forceShutdownCheckbox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 9;
        parameterTextField.setForeground(Color.GRAY);
        paramPanel.add(parameterTextField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;
        paramPanel.add(helpButton, gbc);

        // Listener for parameter text field to show placeholder if empty
        parameterTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (parameterTextField.getText().equals(parameterPlaceholder)) {
                    parameterTextField.setText("");
                    parameterTextField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (parameterTextField.getText().isEmpty()) {
                    parameterTextField.setForeground(Color.GRAY);
                    parameterTextField.setText(parameterPlaceholder);
                }
            }
        });
        parameterTextField.requestFocus();

        // Add apply and cancel buttons
        controlPanel.add(applyButton);
        controlPanel.add(cancelButton);

        // Process the apply buttons functionality and start the timer
        applyButton.addActionListener(e -> {
            try {
                Runtime.getRuntime().exec("cmd /c shutdown " + getSelectedOptions());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Process the cancel buttons functionality and stop the shutdown
        cancelButton.addActionListener(e -> {
            try {
                Runtime.getRuntime().exec("cmd /c shutdown -a");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Process the help buttons functionality and show shutdown help
        helpButton.addActionListener(e -> {
            try {
                final Process p = Runtime.getRuntime().exec("cmd /c shutdown -?");
                final ProcessResultReader stdout = new ProcessResultReader(p.getInputStream(), "STDOUT");
                stdout.start();
                p.waitFor();
                JTextArea jTextArea = new JTextArea(stdout.toString());
                jTextArea.setLineWrap(true);
                jTextArea.setWrapStyleWord(true);
                jTextArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(jTextArea);
                scrollPane.setPreferredSize(new Dimension(500, 500));
                JOptionPane.showMessageDialog(null, scrollPane);
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        });

        JSeparator separator1 = new JSeparator();
        separator1.setPreferredSize(new Dimension(1, 1));

        JSeparator separator2 = new JSeparator();
        separator2.setPreferredSize(new Dimension(1, 1));

        JSeparator separator3 = new JSeparator();
        separator3.setPreferredSize(new Dimension(1, 1));

        pane.add(inputPanel);
        pane.add(actionPanel);
        pane.add(separator2);
        pane.add(paramPanel);
        pane.add(separator3);
        pane.add(controlPanel);
    }

    private String getSelectedOptions() {
        StringBuilder sb = new StringBuilder();

        if (radioShutdown.isSelected()) {
            sb.append("-s");
        } else {
            sb.append("-r");
        }

        if (forceShutdownCheckbox.isSelected()) {
            sb.append(" -f");
        }

        if (notificationsCheckbox.isSelected()) {
            sb.append(" -c \" \"");
        }

        if (!parameterTextField.getText().equals(parameterPlaceholder)) {
            sb.append(" ").append(parameterTextField.getText());
        }

        int timeFactor = 1;
        switch (timeUnitComboBox.getSelectedIndex()) {
            case 0:
                timeFactor = 1;
                break;
            case 1:
                timeFactor = 60;
                break;
            case 2:
                timeFactor = 3600;
                break;
        }

        int inputInSeconds = (int) minuteSpinner.getValue() * timeFactor;
        sb.append(" -t ").append(inputInSeconds);

        return sb.toString();
    }

    private static class ProcessResultReader extends Thread {
        final InputStream is;
        final String type;
        final StringBuilder sb;

        ProcessResultReader(final InputStream is, String type) {
            this.is = is;
            this.type = type;
            this.sb = new StringBuilder();
        }

        public void run() {
            try {
                final InputStreamReader isr = new InputStreamReader(is, "IBM850");
                final BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    this.sb.append(line).append("\n");
                }
            } catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }

        @Override
        public String toString() {
            return this.sb.toString();
        }
    }
}
