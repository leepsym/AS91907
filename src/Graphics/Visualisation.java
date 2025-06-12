package Graphics;

import Main.Simulation;
import Main.Subject;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Visualisation {
    private static final Window stats = new Window(1500, 1000);
    private static final Window sim = new Window();
    public static ArrayList<Pixel> pixelQueue = new ArrayList<>();

    static BufferedImage offScreenImage;
    static PieChart pie;

    public static Container simContentPane = new Container() {
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(offScreenImage, 0, 0, null);
        }
    };

    public static void render(){
        offScreenImage = new BufferedImage(simContentPane.getWidth(), simContentPane.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) offScreenImage.getGraphics();

        // Prepares grid drawing
        g2.setColor(Color.BLACK);

        // Fills in boxes depending on the majority type of subject there
        for(Pixel pixel : pixelQueue) {
            g2.setColor(pixel.colour);
            g2.fillRect(pixel.x * 10, pixel.y * 10, 10, 10);
        }
    }

    public static Container statsContentPane = new Container(){
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(pie, 0, 0, null);
        }
    };

    public Visualisation() {
        pie = new PieChart();
        sim.setContentPane(simContentPane);
        stats.setContentPane(statsContentPane);
        simContentPane.setPreferredSize(new Dimension(Simulation.size[0] * 10, Simulation.size[1] * 10));
        sim.setSize(sim.getPreferredSize());
    }


    public static void visualiseRound() {
        // Creates a 3d array for storing the number of infected, immune, and normal subjects on each tile
        int[][][] count = new int[Simulation.size[0]][Simulation.size[1]][3];
        int[] totalCount = new int[3];

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

                totalCount[0] += k;
                totalCount[1] += m;
                totalCount[2] += n;

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
        render();
        pie.render(totalCount);
    }

    private void visualiseStats() {

    }

    // Record for easy storage
    private record Pixel(int x, int y, Color colour) {}




    public class PieChart extends BufferedImage {

        final Color[] colours = new Color[]{
            Color.RED,
            Color.GREEN,
            Color.GRAY
        };
        public PieChart() {
            super(100, 100, TYPE_INT_ARGB);
        }

        // http://cs111.wellesley.edu/archive/cs111_fall05/public_html/labs/lab6/arc.html
        public void render(int[] nums) {
            for (int num : nums) {

            }
        }
    }
}