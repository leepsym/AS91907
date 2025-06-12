package Graphics;

import Main.Simulation;
import Main.Subject;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class Visualisation {
    private final Window sim = new Window();
    private final Window stats = new Window(1500, 1000);
    public static ArrayList<Pixel> pixelQueue = new ArrayList<>();

    public static Container simContentPane = new Container() {
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;

            // Prepares grid drawing
            g2.setColor(Color.BLACK);
            int width = getWidth() - 1;
            int height = getHeight() - 1;

            // Draws grid lines on the y-axis
            for (int i = 0; i <= Simulation.size[0]; i++) {
                g2.draw(new Line2D.Float(i * 10, 0, i * 10, height));
            }

            // Draws grid lines on the x-axis
            for (int i = 0; i <= Simulation.size[1]; i++) {
                g2.draw(new Line2D.Float(0, i * 10, width, i * 10));
            }

            // Fills in boxes depending on the majority type of subject there
            for(Pixel pixel : pixelQueue) {
                g2.setColor(pixel.colour);
                g2.fillRect(pixel.x * 10 + 1, pixel.y * 10 + 1, 9, 9);
            }
        }
    };

    public static Container statsContentPane = new Container(){
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;

            // figure out how to draw graphs + charts
        }
    };

    public Visualisation() {
        sim.setContentPane(simContentPane);
        simContentPane.setPreferredSize(new Dimension(Simulation.size[0] * 10 + 1, Simulation.size[1] * 10 + 1));
        sim.setSize(sim.getPreferredSize());
    }


    public static void visualiseRound() {
        // Creates a 3d array for storing the number of infected, immune, and normal subjects on each tile
        int[][][] count = new int[Simulation.size[0]][Simulation.size[1]][3];

        // Counts the number of types of subject on each tile and draws them
        for (int i = 0; i < Simulation.size[0]; i++) {
            for (int j = 0; j < Simulation.size[1]; j++) {
                for (Subject subject : Simulation.board[i][j]) {
                    if (subject.infected){
                        count[i][j][0]++;
                    } else if (subject.infectable) {
                        count[i][j][1]++;
                    } else {
                        count[i][j][2]++;
                    }
                }
                int k = count[i][j][0];
                int m = count[i][j][1];
                int n = count[i][j][2];

                if (k > m) {
                    if (k > n) {
                        pixelQueue.add(new Pixel(i, j, Color.red));
                    }
                } else if (m > n) {
                    pixelQueue.add(new Pixel(i, j, Color.GREEN));
                } else if (n > m){
                    pixelQueue.add(new Pixel(i, j, Color.GRAY));
                }
            }
        }
    }

    private void visualiseStats() {

    }

    // Record for easy storage
    private record Pixel(int x, int y, Color colour) {}

}