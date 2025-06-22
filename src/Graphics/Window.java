package Graphics;

import Main.Simulation;

import javax.swing.*;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

public class Window extends JFrame {
    private static String input;
    private static boolean check;
    private final Container content = getContentPane();
    public Window(int width, int height) {
        this();
        setSize(width, height);
        setTitle("AS91907 | Virus Simulator Statistics");
    }

    public Window(){
        setTitle("AS91907 | Virus Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new GridBagLayout());
        setResizable(false);

        setVisible(true);
    }
    public void buttonQuery(String question, Option[] options) {
        reset();
        label(question, constraints(0,0,options.length,1));
        for (int i = 0; i < options.length; i++) {
            Option o = options[i];
            JButton b = new JButton(o.text());
            b.addActionListener(o.action());
            add(b, constraints(i, 1, 1, 1));
        }
        revalidate();
    }

    public void textFieldQuery(String question, ActionListener action) {
        reset();
        label(question, constraints(0,0,1,1));
        JTextField textField = textField("", constraints(0,1,1,1));
        textField.addActionListener(l -> {
            input = textField.getText();
            action.actionPerformed(null);
        });
        revalidate();
    }

    // Resets the pane to a blank slate
    public void reset() {
        content.removeAll();
    }

    public static GridBagConstraints constraints(int x, int y, int w, int h) {
        return new GridBagConstraints(
                x,y,w,h,
                1,1, GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0, 0, 0, 0),0,0);
    }

    public void label(String text, GridBagConstraints constraints) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, constraints);
    }

    public JTextField textField(String text, GridBagConstraints constraints) {
        JTextField textField = new JTextField(text);
        add(textField, constraints);
        return textField;
    }

    public void userParameters() {
        int val = 0;
        while (val != 0) val = check("How many cells wide should the simulation be? (enter a whole, positive number) (Leave blank for default)"); Simulation.size[0] = val; val = 0;
        while (val != 0) val = check("How many cells high should the simulation be? (enter a whole, positive number) (Leave blank for default)"); Simulation.size[1] = val; val = 0;
        while (val != 0) val = check("What should the population of the simulation be? (enter a whole, positive number) (leave blank for default)"); Simulation.populationSize = val; val = 0;
        while (val != 0) val = check("How many subjects should start infected? (enter a whole, positive number) (leave blank for default)"); Simulation.startingInfected = val; val = 0;
        while (val != 0) val = check("What should the chance of transmission be as a percentage? (enter a whole, positive number) (leave blank for default)"); Simulation.infectChance = val; val = 0;
        while (val != 0) val = check("How long should subjects stay infected for? (enter a whole, positive number) (leave blank for default)"); Simulation.infectDuration = val; val = 0;
        while (val != 0) val = check("How long should subjects stay immune for? (enter a whole, positive number) (leave blank for default)"); Simulation.immunityDuration = val; val = 0;
        while (val != 0) val = check("How long should the simulation run for at a maximum? (enter -1 for infinite runtime) (enter a whole, positive number) (leave blank for default)"); Simulation.maxRuntime = val;
    }

    private int check(String query) {
        textFieldQuery(query, l -> check = intCheck(input));
        if (check) {
            return Integer.parseInt(input);
        } else {
            check(query);
            return 0;
        }
    }
    
    private boolean intCheck(String str) {
        try {
            return (Integer.parseInt(str) > 0);
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}