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
    static int[][] angles = new int[3][2];

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

            g.setColor(Color.red);
            g.fillArc(0, 0, 300, 300, angles[0][0], angles[0][1]);

            g.setColor(Color.green);
            g.fillArc(0, 0, 300, 300, angles[1][0], angles[1][1]);

            g.setColor(Color.darkGray);
            g.fillArc(0, 0, 300, 300, angles[2][0], angles[2][1]);
        }
    };

    public Visualisation() {
        pie = new PieChart();

        sim.setContentPane(simContentPane);
        stats.setContentPane(statsContentPane);


        simContentPane.setPreferredSize(new Dimension(Simulation.size[0] * 10, Simulation.size[1] * 10));
        statsContentPane.setPreferredSize(new Dimension(1500, 1000));
        sim.setSize(sim.getPreferredSize());
        stats.setSize(stats.getPreferredSize());

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
                int k = count[i][j][0]; // Infected
                int m = count[i][j][1]; // Infectable
                int n = count[i][j][2]; // Immune

                totalCount[0] += k;
                totalCount[1] += m;
                totalCount[2] += n;

                if (k > 0) {
                    pixelQueue.add(new Pixel(i, j, Color.red));
                } else if (m > 0) {
                    pixelQueue.add(new Pixel(i, j, Color.GREEN));
                } else if (n > 0){
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

        public void render(int[] nums) {
            int total = nums[0] + nums[1] + nums[2];

            if (total == 0) return; // Avoid division by zero

            // Calculate angles as integers (avoiding truncation issues)
            int angle1 = (int) Math.round((double) nums[0] / total * 360);
            int angle2 = (int) Math.round((double) nums[1] / total * 360);
            int angle3 = 360 - angle1 - angle2; // Ensure total is exactly 360

            // Set starting angles and arc lengths
            angles[0][0] = 0;           // Start at 0 degrees
            angles[0][1] = angle1;      // Arc length for infected (red)

            angles[1][0] = angle1;      // Start where first arc ended
            angles[1][1] = angle2;      // Arc length for susceptible (green)

            angles[2][0] = angle1 + angle2;  // Start where second arc ended
            angles[2][1] = angle3;      // Arc length for immune (gray)
        }
    }
}