import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Main {
    private static final JFrame settings = new JFrame("Simulation Settings");

    public static void main(String[] args) {
        Border blackLine = BorderFactory.createLineBorder(Color.black);
        JTextField[] textFields = new JTextField[8];

        settings.setLayout(new GridLayout(5, 2));
        settings.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create individual text fields
        textFields[0] = new JTextField();
        textFields[0].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Height"));
        textFields[0].setText("30");
        settings.add(textFields[0]);

        textFields[1] = new JTextField();
        textFields[1].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Width"));
        textFields[1].setText("30");
        settings.add(textFields[1]);

        textFields[2] = new JTextField();
        textFields[2].setBorder(BorderFactory.createTitledBorder(blackLine, "Population"));
        textFields[2].setText("1000");
        settings.add(textFields[2]);

        textFields[3] = new JTextField();
        textFields[3].setBorder(BorderFactory.createTitledBorder(blackLine, "Starting Infected"));
        textFields[3].setText("1");
        settings.add(textFields[3]);

        textFields[4] = new JTextField();
        textFields[4].setBorder(BorderFactory.createTitledBorder(blackLine, "Chance of infection (%)"));
        textFields[4].setText("90");
        settings.add(textFields[4]);

        textFields[5] = new JTextField();
        textFields[5].setBorder(BorderFactory.createTitledBorder(blackLine, "Infection Duration"));
        textFields[5].setText("10");
        settings.add(textFields[5]);

        textFields[6] = new JTextField();
        textFields[6].setBorder(BorderFactory.createTitledBorder(blackLine, "Immunity Duration"));
        textFields[6].setText("10");
        settings.add(textFields[6]);

        textFields[7] = new JTextField();
        textFields[7].setBorder(BorderFactory.createTitledBorder(blackLine, "Maximum Runtime"));
        textFields[7].setText("200");
        settings.add(textFields[7]);

        JButton button = new JButton("Start");
        button.addActionListener(l -> runSimulation(textFields));
        settings.add(button);

        // Pack and show the window
        settings.pack();
        settings.setVisible(true);
    }

    private static void runSimulation(JTextField[] textFields) {
        boolean pass = true;
        int[] st = new int[8];
        Border blackLine = BorderFactory.createLineBorder(Color.black);

        try {
            st[0] = Integer.parseInt(textFields[0].getText());
            // Reset border to normal if parsing succeeded
            textFields[0].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Height"));
        } catch (NumberFormatException e) {
            textFields[0].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Simulation Height"));
            pass = false;
        }

        try {
            st[1] = Integer.parseInt(textFields[1].getText());
            textFields[1].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Width"));
        } catch (NumberFormatException e) {
            textFields[1].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Simulation Width"));
            pass = false;
        }

        try {
            st[2] = Integer.parseInt(textFields[2].getText());
            textFields[2].setBorder(BorderFactory.createTitledBorder(blackLine, "Population"));
        } catch (NumberFormatException e) {
            textFields[2].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Population"));
            pass = false;
        }

        try {
            st[3] = Integer.parseInt(textFields[3].getText());
            textFields[3].setBorder(BorderFactory.createTitledBorder(blackLine, "Starting Infected"));
        } catch (NumberFormatException e) {
            textFields[3].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Starting Infected"));
            pass = false;
        }

        try {
            st[4] = Integer.parseInt(textFields[4].getText());
            textFields[4].setBorder(BorderFactory.createTitledBorder(blackLine, "Chance of infection (%)"));
        } catch (NumberFormatException e) {
            textFields[4].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Chance of infection (%)"));
            pass = false;
        }

        try {
            st[5] = Integer.parseInt(textFields[5].getText());
            textFields[5].setBorder(BorderFactory.createTitledBorder(blackLine, "Infection Duration"));
        } catch (NumberFormatException e) {
            textFields[5].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Infection Duration"));
            pass = false;
        }

        try {
            st[6] = Integer.parseInt(textFields[6].getText());
            textFields[6].setBorder(BorderFactory.createTitledBorder(blackLine, "Immunity Duration"));
        } catch (NumberFormatException e) {
            textFields[6].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Immunity Duration"));
            pass = false;
        }

        try {
            st[7] = Integer.parseInt(textFields[7].getText());
            textFields[7].setBorder(BorderFactory.createTitledBorder(blackLine, "Maximum Runtime"));
        } catch (NumberFormatException e) {
            textFields[7].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Maximum Runtime"));
            pass = false;
        }

        if (pass) {
            settings.setVisible(false); // Hide settings window
            new Simulation(st[0], st[1], st[2], st[3], st[4], st[5], st[6], st[7]).start();
        } else {
            settings.getContentPane().removeAll();

            for (int i = 0; i < textFields.length; i++) {
                settings.add(textFields[i]);
            }

            JButton button = new JButton("Start");
            button.addActionListener(l -> runSimulation(textFields));
            settings.add(button);

            settings.revalidate();
            settings.repaint();
        }
    }
}