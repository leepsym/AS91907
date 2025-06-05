package Graphics;

import Main.Simulation;
import Main.Subject;

import java.awt.*;
import java.util.ArrayList;

public class Visualisation {
    private Window window = new Window(750, 750);
    public Visualisation() {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                window.label(":3", window.constraints(i, j, 1, 1), window.border(Color.BLACK, 1));
            }
        }
    }


    public void visualiseRound() {
        ArrayList<Subject> subjects = Simulation.population;

        for (Subject subject : subjects) {
            // Display icon (dot) at its location
        }
    }
}
