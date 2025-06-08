package Graphics;

import Main.Simulation;
import Main.Subject;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class Visualisation {
    private final Window window = new Window(1050, 1050);

    public Visualisation() {
        Container contentPane = new Container() {
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;

                g2.setColor(Color.BLACK);

                for (int i = 0; i < 100; i++) {
                    g2.draw(new Line2D.Float(i * 10, 0, i * 10, 1000));
                    g2.draw(new Line2D.Float(0, i * 10, 1000, i * 10));
                }
            }
        };

        window.setContentPane(contentPane);
    }


    public void visualiseRound() {
        ArrayList<Subject> subjects = Simulation.population;

        for (Subject subject : subjects) {
            // Display icon (dot) at its location
        }
    }
}
