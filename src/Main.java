import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.zip.DataFormatException;

/**
 * Main class that provides the GUI interface for configuring and managing virus simulations.
 * This class creates a settings window where users can input simulation parameters,
 * start new simulations, and download results.
 */
public class Main {
    // Main settings window for the application
    private static final JFrame settings = new JFrame("Simulation Settings");

    // List to store all active simulations
    private static final ArrayList<Simulation> simulations = new ArrayList<>();

    // List to store simulation names for display purposes
    private static ArrayList<String> simNames = new ArrayList<>();

    // Dropdown to select which simulation to interact with
    private static JComboBox comboBox;

    // Dropdown to select what action to perform on selected simulation
    private static JComboBox comboBox2;

    // Array of text input fields for simulation parameters
    static JTextField[] textFields;

    // Standard black border for GUI elements
    static Border blackLine = BorderFactory.createLineBorder(Color.black);

    /**
     * Main method that initializes the GUI and sets up all input fields.
     * Creates a 7x2 grid layout with parameter inputs and control buttons.
     */
    public static void main(String[] args) {
        // Initialize array for 13 text input fields
        textFields = new JTextField[13];

        // Set up main window layout and properties
        settings.setLayout(new GridLayout(7, 2));
        settings.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        settings.setSize(350, 400);

        // Create and configure simulation height input field
        textFields[0] = new JTextField();
        textFields[0].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Height"));
        textFields[0].setText("256");
        settings.add(textFields[0]);

        // Create and configure simulation width input field
        textFields[1] = new JTextField();
        textFields[1].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Width"));
        textFields[1].setText("256");
        settings.add(textFields[1]);

        // Create and configure population size input field
        textFields[2] = new JTextField();
        textFields[2].setBorder(BorderFactory.createTitledBorder(blackLine, "Population"));
        textFields[2].setText("65536");
        settings.add(textFields[2]);

        // Create and configure starting infected count input field
        textFields[3] = new JTextField();
        textFields[3].setBorder(BorderFactory.createTitledBorder(blackLine, "Starting Infected"));
        textFields[3].setText("1");
        settings.add(textFields[3]);

        // Create and configure infection probability input field (as percentage)
        textFields[4] = new JTextField();
        textFields[4].setBorder(BorderFactory.createTitledBorder(blackLine, "Chance of infection (%)"));
        textFields[4].setText("75");
        settings.add(textFields[4]);

        // Create and configure how long subjects remain infected
        textFields[5] = new JTextField();
        textFields[5].setBorder(BorderFactory.createTitledBorder(blackLine, "Infection Duration"));
        textFields[5].setText("16");
        settings.add(textFields[5]);

        // Create and configure how long subjects remain immune after recovery
        textFields[6] = new JTextField();
        textFields[6].setBorder(BorderFactory.createTitledBorder(blackLine, "Immunity Duration"));
        textFields[6].setText("48");
        settings.add(textFields[6]);

        // Create and configure maximum simulation runtime (-1 for unlimited)
        textFields[7] = new JTextField();
        textFields[7].setBorder(BorderFactory.createTitledBorder(blackLine, "Maximum Runtime"));
        textFields[7].setText("-1");
        settings.add(textFields[7]);

        // Create and configure delay between simulation frames for visualization
        textFields[8] = new JTextField();
        textFields[8].setBorder(BorderFactory.createTitledBorder(blackLine, "Frame Delay (ms)"));
        textFields[8].setText("0");
        settings.add(textFields[8]);

        // Create and configure simulation name input field
        textFields[9] = new JTextField();
        textFields[9].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Name"));
        textFields[9].setText("Unnamed Simulation");
        settings.add(textFields[9]);

        // Create dropdown for selecting existing simulations
        comboBox = new JComboBox();
        comboBox.setBorder(BorderFactory.createTitledBorder(blackLine, "Select Simulation"));
        settings.add(comboBox);

        // Create dropdown for selecting actions to perform on simulations
        comboBox2 = new JComboBox(new String[]{"Create New", "Stop", "Close", "Download"});
        comboBox2.setBorder(BorderFactory.createTitledBorder(blackLine, "Select Action"));
        settings.add(comboBox2);

        // Create button to execute the selected action
        JButton button = new JButton("Execute Action");
        button.addActionListener(l -> simulationAction());
        settings.add(button);

        // Make the settings window visible
        settings.setVisible(true);
    }

    /**
     * Validates input parameters and creates a new simulation.
     * Checks all text fields for valid integer inputs and highlights invalid fields in red.
     * If all inputs are valid, creates and starts a new simulation.
     */
    private static void runSimulation() {
        boolean pass = true; // Flag to track if all validations pass
        int[] st = new int[9]; // Array to store parsed integer values
        Border blackLine = BorderFactory.createLineBorder(Color.black);
        String name = null;

        // Validate simulation height input
        try {
            if (Integer.parseInt(textFields[0].getText()) <= 0) throw new Exception("negative");
            st[0] = Integer.parseInt(textFields[0].getText());
            textFields[0].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Height"));
        } catch (Exception ignored) {
            textFields[0].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Simulation Height"));
            pass = false;
        }

        // Validate simulation width input
        try {
            if (Integer.parseInt(textFields[1].getText()) <= 0) throw new Exception("negative");
            st[1] = Integer.parseInt(textFields[1].getText());
            textFields[1].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Width"));
        } catch (Exception ignored) {
            textFields[1].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Simulation Width"));
            pass = false;
        }

        // Validate population size input
        try {
            if (Integer.parseInt(textFields[2].getText()) <= 0) throw new Exception("negative");
            st[2] = Integer.parseInt(textFields[2].getText());
            textFields[2].setBorder(BorderFactory.createTitledBorder(blackLine, "Population"));
        } catch (Exception ignored) {
            textFields[2].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Population"));
            pass = false;
        }

        // Validate starting infected count input
        try {
            if (Integer.parseInt(textFields[3].getText()) <= 0) throw new Exception("negative");
            st[3] = Integer.parseInt(textFields[3].getText());
            textFields[3].setBorder(BorderFactory.createTitledBorder(blackLine, "Starting Infected"));
        } catch (Exception ignored) {
            textFields[3].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Starting Infected"));
            pass = false;
        }

        // Validate infection chance percentage input
        try {
            if (Integer.parseInt(textFields[4].getText()) <= 0) throw new Exception("negative");
            st[4] = Integer.parseInt(textFields[4].getText());
            textFields[4].setBorder(BorderFactory.createTitledBorder(blackLine, "Chance of infection (%)"));
        } catch (Exception ignored) {
            textFields[4].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Chance of infection (%)"));
            pass = false;
        }

        // Validate infection duration input
        try {
            if (Integer.parseInt(textFields[5].getText()) <= 0) throw new Exception("negative");
            st[5] = Integer.parseInt(textFields[5].getText());
            textFields[5].setBorder(BorderFactory.createTitledBorder(blackLine, "Infection Duration"));
        } catch (Exception ignored) {
            textFields[5].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Infection Duration"));
            pass = false;
        }

        // Validate immunity duration input
        try {
            if (Integer.parseInt(textFields[6].getText()) <= 0) throw new Exception("negative");
            st[6] = Integer.parseInt(textFields[6].getText());
            textFields[6].setBorder(BorderFactory.createTitledBorder(blackLine, "Immunity Duration"));
        } catch (Exception ignored) {
            textFields[6].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Immunity Duration"));
            pass = false;
        }

        // Validate maximum runtime input
        try {
            st[7] = Integer.parseInt(textFields[7].getText());
            textFields[7].setBorder(BorderFactory.createTitledBorder(blackLine, "Maximum Runtime"));
        } catch (Exception ignored) {
            textFields[7].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Maximum Runtime"));
            pass = false;
        }

        // Validate frame delay input
        try {
            if (Integer.parseInt(textFields[8].getText()) < 0) throw new Exception("negative");
            st[8] = Integer.parseInt(textFields[8].getText());
            textFields[8].setBorder(BorderFactory.createTitledBorder(blackLine, "Frame Delay (ms)"));
        } catch (Exception ignored) {
            textFields[8].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Frame Delay (ms)"));
            pass = false;
        }

        // Validate simulation name input
        try {
            name = textFields[9].getText();
            textFields[9].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Name"));
        } catch (Exception ignored) {
            textFields[9].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Simulation Name"));
            pass = false;
        }

        // If all validations passed, create and start the simulation
        if (pass) {
            Simulation sim = new Simulation(st[0], st[1], st[2], st[3], st[4], st[5], st[6], st[7], st[8], textFields[9].getText());
            simulations.add(sim);
            simNames.add(name);
            comboBox.addItem(name); // Add to dropdown for future selection
            settings.repaint(); // Refresh the GUI
            sim.start(); // Start the simulation thread
        }
    }

    /**
     * Handles actions performed on existing simulations.
     * Processes the selected action (Create New, Stop, Close, Download) and applies it
     * to the selected simulation from the dropdown.
     */
    private static void simulationAction() {
        boolean pass = true;
        Simulation sim = null;
        String act = null;

        // Get the selected action from dropdown
        try {
            act = (String) comboBox2.getSelectedItem();
        } catch (Exception ignored) {
            comboBox2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Select Action"));
            pass = false;
        }

        // If "Create New" is selected, start a new simulation
        if (act == "Create New") {
            runSimulation();
            return;
        }

        // Get the selected simulation from dropdown
        try {
            sim = simulations.get(comboBox.getSelectedIndex());
        } catch (Exception ignored) {
            comboBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Select Simulation"));
            pass = false;
        }

        // Execute the selected action on the simulation
        try {
            if (pass) {
                switch (act) {
                    case "Stop" -> sim.run = false; // Stop simulation execution

                    case "Close" -> { // Stop and close all windows
                        sim.run = false;
                        sim.v.statisticsPane.dispose();
                        sim.v.simulationPane.dispose();
                    }

                    case "Download" -> download(sim); // Open download dialog

                    default -> {}
                }
            }
        } catch (Exception ignored){}
    }

    /**
     * Creates a download dialog for exporting simulation data.
     * Allows users to export statistics, infection data, subject data, or specific subject information
     * in CSV format to a file of their choice.
     *
     * @param sim The simulation to export data from
     */
    public static void download(Simulation sim) {
        JFileChooser chooser = new JFileChooser();
        String ls = System.lineSeparator(); // System-specific line separator

        // Set default filename to simulation name with .csv extension
        chooser.setSelectedFile(new File(sim.name + ".csv"));

        // Create download dialog window
        JFrame frame = new JFrame("Download");
        frame.setLayout(new GridLayout(1, 3));
        frame.setSize(10, 5);

        // Dropdown for selecting data type to export
        JComboBox comboBox3 = new JComboBox(new String[]{"Statistics", "Infections", "Subjects", "Target Subject"});

        // Dropdown for selecting specific subject (used when "Target Subject" is selected)
        JComboBox comboBox4 = new JComboBox();

        // Populate subject dropdown with all subjects from the simulation
        for (Subject s : sim.population) {
            comboBox4.addItem(s);
        }

        comboBox3.setBorder(BorderFactory.createTitledBorder(blackLine, "Select Type"));
        comboBox3.setBorder(BorderFactory.createTitledBorder(blackLine, "Select Subject"));

        // Create download button with action handler
        JButton b = new JButton("Download");
        b.addActionListener(l -> {

            String act = null;
            Subject subject = null;
            boolean pass = true;

            // Get selected data type
            try {
                if ((int) comboBox3.getSelectedItem() <= 0) throw new Exception("negative");
                act = (String) comboBox3.getSelectedItem();
            } catch (Exception ignored) {
                comboBox2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Select Type"));
                pass = false;
            }

            // If target subject is selected, get the specific subject
            if (act == "Target Subject") {
                try {
                    if ((int) comboBox4.getSelectedItem() <= 0) throw new Exception("negative");
                    subject = (Subject) comboBox4.getSelectedItem();
                } catch (Exception ignored) {
                    comboBox4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Select Subject"));
                    pass = false;
                }
            }

            // If validation passed and user selects a file, proceed with export
            if (pass && chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                FileWriter fw = null;
                try {fw = new FileWriter(chooser.getSelectedFile());} catch (Exception ignored) {}

                // Export data based on selected type
                switch (act) {
                    case "Statistics" -> { // Export overall simulation statistics
                        try {
                            fw.write(sim.name + " Statistics" + ls);
                            fw.append("Total Infections, Final Infected, Final Susceptible, Final Immune, Final Round" + ls);
                            fw.append(sim.infections.size() + "," + sim.infected + "," + (sim.populationSize - sim.infected - sim.immune) + "," + sim.immune + "," + sim.round);
                        } catch (Exception ignored) {}
                    }

                    case "Infections" -> { // Export all infection events
                        try {
                            fw.write(sim.name + " Infections" + ls);
                            fw.append("ID, X Pos, Y Pos, Round, Subject, Source" + ls);

                            for (Infection i : sim.infections) {
                                fw.append(i + "," + i.location[0] +","+ i.location[1] + "," + i.round + "," + i.subject + "," + i.source + ls);
                            }
                        } catch (Exception ignored) {}
                    }

                    case "Subjects" -> { // Export all subject information
                        try {
                            fw.write(sim.name + " Subjects" + ls);

                            for (Subject s : sim.population) {
                                fw.append(s + ls);
                            }
                        } catch (Exception ignored) {}
                    }

                    case "Target Subject" -> { // Export detailed information for specific subject
                        try {
                            Subject fs = subject;
                            fw.write(sim.name + " : Subject " + subject + ls);
                            fw.append("X Pos, Y Pos, State, Times Infected, Number Infected, Infections, Infections Given" + ls);
                            fw.append(fs.location[0] + "," + fs.location[1] + "," + (fs.infectable ? "Immune" : (fs.infected ? "Infected" : "Susceptible")) + "," + fs.infectCount.size() + "," + fs.infectionCount.size() + "," + fs.infectCount.getFirst() + "," + fs.infectionCount.getFirst() + ls);

                            // Export additional infection history if available
                            if (fs.infectCount.size() >= fs.infectionCount.size()) {
                                for (int i = 1; i < fs.infectCount.size(); i++) {
                                    fw.append(",,,,," + fs.infectCount.get(i) + "," + fs.infectionCount.get(i) + ls);
                                }
                            } else {
                                for (int i = 1; i < fs.infectionCount.size(); i++) {
                                    fw.append(",,,,," + fs.infectCount.get(i) + "," + fs.infectionCount.get(i) + ls);
                                }
                            }
                        } catch (Exception ignored) {}
                    }

                    case "Target Infection" -> {
                        // Reserved for future implementation
                    }
                }

                // Clean up file writer and close dialog
                try {
                    fw.flush();
                    fw.close();
                    frame.dispose();
                } catch (Exception ignored) {}
            }
        });

        // Add components to dialog and display
        frame.add(b);
        frame.add(comboBox3);
        frame.add(comboBox4);

        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
}