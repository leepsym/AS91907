import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private static final JFrame settings = new JFrame("Simulation Settings");
    private static final ArrayList<Simulation> simulations = new ArrayList<>();
    private static ArrayList<String> simNames = new ArrayList<>();
    private static JComboBox comboBox;
    private static JComboBox comboBox2;
    static JTextField[] textFields;


    static Border blackLine = BorderFactory.createLineBorder(Color.black);

    public static void main(String[] args) {
        textFields = new JTextField[13];

        settings.setLayout(new GridLayout(7, 2));
        settings.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        settings.setSize(350, 400);

        // Create individual text fields
        textFields[0] = new JTextField();
        textFields[0].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Height"));
        textFields[0].setText("256");
        settings.add(textFields[0]);

        textFields[1] = new JTextField();
        textFields[1].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Width"));
        textFields[1].setText("256");
        settings.add(textFields[1]);

        textFields[2] = new JTextField();
        textFields[2].setBorder(BorderFactory.createTitledBorder(blackLine, "Population"));
        textFields[2].setText("65536");
        settings.add(textFields[2]);

        textFields[3] = new JTextField();
        textFields[3].setBorder(BorderFactory.createTitledBorder(blackLine, "Starting Infected"));
        textFields[3].setText("1");
        settings.add(textFields[3]);

        textFields[4] = new JTextField();
        textFields[4].setBorder(BorderFactory.createTitledBorder(blackLine, "Chance of infection (%)"));
        textFields[4].setText("75");
        settings.add(textFields[4]);

        textFields[5] = new JTextField();
        textFields[5].setBorder(BorderFactory.createTitledBorder(blackLine, "Infection Duration"));
        textFields[5].setText("16");
        settings.add(textFields[5]);

        textFields[6] = new JTextField();
        textFields[6].setBorder(BorderFactory.createTitledBorder(blackLine, "Immunity Duration"));
        textFields[6].setText("48");
        settings.add(textFields[6]);

        textFields[7] = new JTextField();
        textFields[7].setBorder(BorderFactory.createTitledBorder(blackLine, "Maximum Runtime"));
        textFields[7].setText("33554432");
        settings.add(textFields[7]);

        textFields[8] = new JTextField();
        textFields[8].setBorder(BorderFactory.createTitledBorder(blackLine, "Frame Delay (ms)"));
        textFields[8].setText("0");
        settings.add(textFields[8]);

        textFields[9] = new JTextField();
        textFields[9].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Name"));
        textFields[9].setText("Unnamed Simulation");
        settings.add(textFields[9]);


        comboBox = new JComboBox();
        comboBox.setBorder(BorderFactory.createTitledBorder(blackLine, "Select Simulation"));
        settings.add(comboBox);

        comboBox2 = new JComboBox(new String[]{"Create New", "Stop", "Close", "Download"});
        comboBox2.setBorder(BorderFactory.createTitledBorder(blackLine, "Select Action"));
        settings.add(comboBox2);

        JButton button = new JButton("Execute Action");
        button.addActionListener(l -> simulationAction());
        settings.add(button);

        settings.setVisible(true);
    }

    private static void runSimulation() {
        boolean pass = true;
        int[] st = new int[9];
        Border blackLine = BorderFactory.createLineBorder(Color.black);
        String name = null;

        try {
            st[0] = Integer.parseInt(textFields[0].getText());
            textFields[0].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Height"));
        } catch (Exception ignored) {
            textFields[0].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Simulation Height"));
            pass = false;
        }

        try {
            st[1] = Integer.parseInt(textFields[1].getText());
            textFields[1].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Width"));
        } catch (Exception ignored) {
            textFields[1].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Simulation Width"));
            pass = false;
        }

        try {
            st[2] = Integer.parseInt(textFields[2].getText());
            textFields[2].setBorder(BorderFactory.createTitledBorder(blackLine, "Population"));
        } catch (Exception ignored) {
            textFields[2].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Population"));
            pass = false;
        }

        try {
            st[3] = Integer.parseInt(textFields[3].getText());
            textFields[3].setBorder(BorderFactory.createTitledBorder(blackLine, "Starting Infected"));
        } catch (Exception ignored) {
            textFields[3].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Starting Infected"));
            pass = false;
        }

        try {
            st[4] = Integer.parseInt(textFields[4].getText());
            textFields[4].setBorder(BorderFactory.createTitledBorder(blackLine, "Chance of infection (%)"));
        } catch (Exception ignored) {
            textFields[4].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Chance of infection (%)"));
            pass = false;
        }

        try {
            st[5] = Integer.parseInt(textFields[5].getText());
            textFields[5].setBorder(BorderFactory.createTitledBorder(blackLine, "Infection Duration"));
        } catch (Exception ignored) {
            textFields[5].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Infection Duration"));
            pass = false;
        }

        try {
            st[6] = Integer.parseInt(textFields[6].getText());
            textFields[6].setBorder(BorderFactory.createTitledBorder(blackLine, "Immunity Duration"));
        } catch (Exception ignored) {
            textFields[6].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Immunity Duration"));
            pass = false;
        }

        try {
            st[7] = Integer.parseInt(textFields[7].getText());
            textFields[7].setBorder(BorderFactory.createTitledBorder(blackLine, "Maximum Runtime"));
        } catch (Exception ignored) {
            textFields[7].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Maximum Runtime"));
            pass = false;
        }

        try {
            st[8] = Integer.parseInt(textFields[8].getText());
            textFields[8].setBorder(BorderFactory.createTitledBorder(blackLine, "Frame Delay (ms)"));
        } catch (Exception ignored) {
            textFields[8].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Frame Delay (ms)"));
            pass = false;
        }

        try {
            name = textFields[9].getText();
            textFields[9].setBorder(BorderFactory.createTitledBorder(blackLine, "Simulation Name"));
        } catch (Exception ignored) {
            textFields[9].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Simulation Name"));
            pass = false;
        }

        if (pass) {
            Simulation sim = new Simulation(st[0], st[1], st[2], st[3], st[4], st[5], st[6], st[7], st[8], textFields[9].getText());
            simulations.add(sim);
            simNames.add(name);
            comboBox.addItem(name);
            settings.repaint();
            sim.start();
        }
    }

    private static void simulationAction() {
        boolean pass = true;
        Simulation sim = null;
        String act = null;

        try {
            act = (String) comboBox2.getSelectedItem();
        } catch (Exception ignored) {
            comboBox2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Select Action"));
            pass = false;
        }

        if (act == "Create New") {
            runSimulation();
            return;
        }

        try {
            sim = simulations.get(comboBox.getSelectedIndex());
        } catch (Exception ignored) {
            comboBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Select Simulation"));
            pass = false;
        }



        try {
            if (pass) {
                switch (act) {
                    case "Stop" -> sim.run = false;

                    case "Close" -> {
                        sim.run = false;
                        sim.v.statisticsPane.dispose();
                        sim.v.simulationPane.dispose();
                    }

                    case "Download" -> download(sim);

                    default -> {}
                }
            }
        } catch (Exception ignored){}
    }


    public static void download(Simulation sim) {
        JFileChooser chooser = new JFileChooser();
        String ls = System.lineSeparator();
        String act = null;
        Subject subject = null;
        boolean pass = true;

        chooser.setSelectedFile(new File(sim.name + ".csv"));

        JFrame frame = new JFrame("Download");
        frame.setLayout(new GridLayout(1, 3));
        frame.setSize(10, 5);

        JComboBox comboBox3 = new JComboBox(new String[]{"Statistics", "Infections", "Subjects", "Target Subject"});
        JComboBox comboBox4 = new JComboBox();

        for (Subject s : sim.population) {
            comboBox4.addItem(s);
        }

        comboBox3.setBorder(BorderFactory.createTitledBorder(blackLine, "Select Type"));
        comboBox3.setBorder(BorderFactory.createTitledBorder(blackLine, "Select Subject"));

        try {
            act = (String) comboBox3.getSelectedItem();
        } catch (Exception ignored) {
            comboBox2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Select Type"));
            pass = false;
        }

        if (act == "Target Subject") {
            try {
                subject = (Subject) comboBox4.getSelectedItem();
            } catch (Exception ignored) {
                comboBox4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Select Subject"));
                pass = false;
            }
        }

        JButton b = new JButton("Download");
        boolean finalPass = pass;
        String finalAct = act;
        Subject finalSubject = subject;
        b.addActionListener(l -> {
            if (finalPass && chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                FileWriter fw = null;
                try {fw = new FileWriter(chooser.getSelectedFile());} catch (Exception ignored) {}
                switch (finalAct) {
                    case "Statistics" -> {
                        try {
                            fw.write(sim.name + " Statistics" + ls);
                            fw.append("Total Infections, Final Infected, Final Susceptible, Final Immune, Final Round" + ls);
                            fw.append(sim.infections.size() + "," + sim.infected + "," + (sim.populationSize - sim.infected - sim.immune) + "," + sim.immune + "," + sim.round);
                        } catch (Exception ignored) {}
                    }

                    case "Infections" -> {
                        try {
                            fw.write(sim.name + " Infections" + ls);
                            fw.append("ID, X Pos, Y Pos, Round, Subject, Source" + ls);

                            for (Infection i : sim.infections) {
                                fw.append(i + "," + i.location()[0] +","+ i.location()[1] + "," + i.round() + "," + i.subject() + "," + i.source() + ls);
                            }
                        } catch (Exception ignored) {}
                    }

                    case "Subjects" -> {
                        try {
                            fw.write(sim.name + " Subjects" + ls);

                            for (Subject s : sim.population) {
                                fw.append(s + ls);
                            }
                        } catch (Exception ignored) {}
                    }

                    case "Target Subject" -> {
                        try {
                            Subject fs = finalSubject;
                            fw.write(sim.name + " : Subject " + finalSubject + ls);
                            fw.append("X Pos, Y Pos, State, Times Infected, Number Infected, Infections, Infections Given" + ls);
                            fw.append(fs.location[0] + "," + fs.location[1] + "," + (fs.infectable ? "Immune" : (fs.infected ? "Infected" : "Susceptible")) + "," + fs.infectCount.size() + "," + fs.infectionCount.size() + "," + fs.infectCount.getFirst() + "," + fs.infectionCount.getFirst() + ls);

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

                    }
                }

                try {
                    fw.flush();
                    fw.close();
                    frame.dispose();
                } catch (Exception ignored) {}
            }
        });




        frame.add(b);
        frame.add(comboBox3);
        frame.add(comboBox4);

        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);

    }
}