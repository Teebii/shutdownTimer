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
        // Set up the content pane
        frame.addComponentsToPane(frame.getContentPane());
        // Display the window
        frame.pack();
        frame.setVisible(true);
    }

    public void addComponentsToPane(final Container pane) {
        final JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 2));
        JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(2, 3));

        // Add spinner with model for minutes
        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
        minuteSpinner.setModel(spinnerModel);
        inputPanel.add(minuteSpinner);
        inputPanel.add(new Label("Minuten"));

        // Add radio buttons for states (shutdown or restart)
        radioShutdown.setText("Herunterfahren");
        radioRestart.setText("Neu starten");
        stateGroup.add(radioShutdown);
        stateGroup.add(radioRestart);
        radioShutdown.setSelected(true);
        inputPanel.add(radioShutdown);
        inputPanel.add(radioRestart);

        // Add checkboxes
        notificationsCheckbox.setSelected(true);
        inputPanel.add(notificationsCheckbox);
        inputPanel.add(new Label(""));
        forceShutdownCheckbox.setSelected(true);
        inputPanel.add(forceShutdownCheckbox);
        inputPanel.add(new Label(""));

        // Add apply and cancel buttons
        controls.add(applyButton);
        controls.add(cancelButton);

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
        pane.add(inputPanel, BorderLayout.NORTH);
        pane.add(controls, BorderLayout.SOUTH);
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

        int minutes = (int) minuteSpinner.getValue() * 60;
        sb.append(" /t " + minutes);

        return sb.toString();
    }
}
