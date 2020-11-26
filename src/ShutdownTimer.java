import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ShutdownTimer extends JFrame {

    private JButton applyButton = new JButton("Timer starten");
    private JButton cancelButton = new JButton("Herunterfahren abbrechen");
    private JSpinner minuteSpinner = new JSpinner();
    private ButtonGroup stateGroup = new ButtonGroup();
    private JRadioButton radioShutdown = new JRadioButton();
    private JRadioButton radioRestart = new JRadioButton();
    private JCheckBox notificationsCheckbox = new JCheckBox("Benachrichtigungen ausblenden");
    private JCheckBox forceShutdownCheckbox = new JCheckBox("Herunterfahren erzwingen");
    private JComboBox timeUnitComboBox = new JComboBox();

    public ShutdownTimer(String name) {
        super(name);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        // Create and set up the window
        ShutdownTimer frame = new ShutdownTimer("Shutdown Timer");
        frame.setPreferredSize(new Dimension(400, 280));
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
        GridLayout paramLayout = new GridLayout(3, 1);
        paramPanel.setLayout(paramLayout);

        // Control panel for buttons
        final JPanel controlPanel = new JPanel();
        GridLayout controlLayout = new GridLayout(1, 2);
        controlLayout.setHgap(10);
        controlPanel.setLayout(controlLayout);

        // Create combobox with time units
        String timeUnits[] = {"Sekunden", "Minuten", "Stunden"};
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(timeUnits);
        timeUnitComboBox.setLightWeightPopupEnabled (false);
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

        // Add checkboxes
        paramPanel.add(new Label("Zusätzliche Parameter:"));
        notificationsCheckbox.setSelected(true);
        paramPanel.add(notificationsCheckbox);
        forceShutdownCheckbox.setSelected(true);
        paramPanel.add(forceShutdownCheckbox);
        // Add apply and cancel buttons
        controlPanel.add(applyButton);
        controlPanel.add(cancelButton);

        // Process the apply buttons functionality and start the timer
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Process p = null;
                try {
                    p = Runtime.getRuntime().exec(
                            "cmd /c shutdown " + getSelectedOptions());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Process the cancel buttons functionality and stop the shutdown
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Process p = null;
                try {
                    p = Runtime.getRuntime().exec(
                            "cmd /c shutdown /a");

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JSeparator separator1 = new JSeparator();
        separator1.setPreferredSize(new Dimension(1,1));

        JSeparator separator2 = new JSeparator();
        separator2.setPreferredSize(new Dimension(1,1));

        JSeparator separator3 = new JSeparator();
        separator3.setPreferredSize(new Dimension(1,1));

        pane.add(inputPanel);
        pane.add(actionPanel);
        pane.add(separator2);
        pane.add(paramPanel);
        pane.add(separator3);
        pane.add(controlPanel);
    }

    private String getSelectedOptions() {
        StringBuffer sb = new StringBuffer();

        if (radioShutdown.isSelected()) {
            sb.append("/s");
        } else {
            sb.append("/r");
        }

        if (forceShutdownCheckbox.isSelected()) {
            sb.append(" /f");
        }

        if (notificationsCheckbox.isSelected()) {
            sb.append(" /p");
        }

        int timeFactor = 1;
        switch (timeUnitComboBox.getSelectedIndex()) {
            case 0:
                timeFactor = 1;
                break;
            case 1:
                timeFactor = 60;
                break;
            case 3:
                timeFactor = 360;
                break;
            default:
                timeFactor = 60;

        }

        int inputInSeconds = (int) minuteSpinner.getValue() * timeFactor;
        sb.append(" /t " + inputInSeconds);

        return sb.toString();
    }
}
