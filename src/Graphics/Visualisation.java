package Graphics;

import Main.Simulation;
import Main.Subject;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.Float;
import java.lang.Math;
import java.util.ArrayList;

public class Visualisation {
    private static final Window stats = new Window(1500, 1000);
    private static final Window sim = new Window();
    public static ArrayList<Pixel> pixelQueue = new ArrayList<>();

    static BufferedImage offScreenImage;
    static int[][] angles = new int[3][2];
    static ArrayList<Float[][]> uninfectedGraphValues;
    static ArrayList<Float[][]> immuneGraphValues;
    static ArrayList<Float[][]> infectedGraphValues;

    static float infectedHeight;
    static float uninfectedHeight;
    static float immuneHeight;

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

            // Draw infected slice (red)
            g.setColor(Color.red);
            g.fillArc(0, 0, 300, 300, angles[0][0], angles[0][1]);

            // Draw uninfected slice (green)
            g.setColor(Color.green);
            g.fillArc(0, 0, 300, 300, angles[1][0], angles[1][1]);

            // Draw immune slice (grey)
            g.setColor(Color.darkGray);
            g.fillArc(0, 0, 300, 300, angles[2][0], angles[2][1]);

            // Draw pie chart background
            g.setColor(Color.black);
            g.drawOval(0, 0, 300, 300);

            // Draw stacked areas using smooth connecting lines
            if (infectedGraphValues.size() > 0) {
                // Calculate cumulative heights for each time point
                int[] xPoints = new int[infectedGraphValues.size()];
                int[] infectedTopY = new int[infectedGraphValues.size()];
                int[] uninfectedTopY = new int[infectedGraphValues.size()];
                int[] immuneTopY = new int[infectedGraphValues.size()];

                for (int i = 0; i < infectedGraphValues.size(); i++) {
                    xPoints[i] = 450 + (i * 10);
                    // Stack from bottom to top: immune (bottom), uninfected (middle), infected (top)
                    immuneTopY[i] = 300 - immuneGraphValues.get(i)[1][0].intValue();
                    uninfectedTopY[i] = immuneTopY[i] - uninfectedGraphValues.get(i)[1][0].intValue();
                    infectedTopY[i] = 0; // Infected always goes to the top
                }

                // Draw immune area (bottom layer - grey)
                g.setColor(Color.darkGray);
                int[] immuneXPoints = new int[immuneGraphValues.size() * 2 + 2];
                int[] immuneYPoints = new int[immuneGraphValues.size() * 2 + 2];

                // First point at bottom left
                immuneXPoints[0] = 450;
                immuneYPoints[0] = 300;

                // Draw top edge
                for (int i = 0; i < immuneGraphValues.size(); i++) {
                    immuneXPoints[i + 1] = xPoints[i];
                    immuneYPoints[i + 1] = immuneTopY[i];
                }

                // Close to bottom right
                immuneXPoints[immuneGraphValues.size() + 1] = 450 + (infectedGraphValues.size() - 1) * 10;
                immuneYPoints[immuneGraphValues.size() + 1] = 300;

                // Draw bottom edge back to start
                for (int i = 0; i < immuneGraphValues.size(); i++) {
                    immuneXPoints[immuneGraphValues.size() + 2 + i] = 450 + (immuneGraphValues.size() - 1 - i) * 10;
                    immuneYPoints[immuneGraphValues.size() + 2 + i] = 300;
                }

                g.fillPolygon(immuneXPoints, immuneYPoints, immuneGraphValues.size() * 2 + 2);

                // Draw uninfected area (middle layer - green)
                g.setColor(Color.green);
                int[] uninfectedXPoints = new int[uninfectedGraphValues.size() * 2 + 2];
                int[] uninfectedYPoints = new int[uninfectedGraphValues.size() * 2 + 2];

                // Start at bottom left of this layer
                uninfectedXPoints[0] = 450;
                uninfectedYPoints[0] = immuneTopY[0];

                // Draw top edge
                for (int i = 0; i < uninfectedGraphValues.size(); i++) {
                    uninfectedXPoints[i + 1] = xPoints[i];
                    uninfectedYPoints[i + 1] = uninfectedTopY[i];
                }

                // Close to bottom right of this layer
                uninfectedXPoints[uninfectedGraphValues.size() + 1] = 450 + (uninfectedGraphValues.size() - 1) * 10;
                uninfectedYPoints[uninfectedGraphValues.size() + 1] = immuneTopY[immuneGraphValues.size() - 1];

                // Draw bottom edge back
                for (int i = 0; i < uninfectedGraphValues.size(); i++) {
                    uninfectedXPoints[uninfectedGraphValues.size() + 2 + i] = 450 + (uninfectedGraphValues.size() - 1 - i) * 10;
                    uninfectedYPoints[uninfectedGraphValues.size() + 2 + i] = immuneTopY[uninfectedGraphValues.size() - 1 - i];
                }

                g.fillPolygon(uninfectedXPoints, uninfectedYPoints, uninfectedGraphValues.size() * 2 + 2);

                // Draw infected area (top layer - red) - fills to top of chart
                g.setColor(Color.red);
                int[] infectedXPoints = new int[infectedGraphValues.size() * 2 + 2];
                int[] infectedYPoints = new int[infectedGraphValues.size() * 2 + 2];

                // Start at top left
                infectedXPoints[0] = 450;
                infectedYPoints[0] = 0;

                // Draw across top
                for (int i = 0; i < infectedGraphValues.size(); i++) {
                    infectedXPoints[i + 1] = xPoints[i];
                    infectedYPoints[i + 1] = 0;
                }

                // Close to top right
                infectedXPoints[infectedGraphValues.size() + 1] = 450 + (infectedGraphValues.size() - 1) * 10;
                infectedYPoints[infectedGraphValues.size() + 1] = 0;

                // Draw bottom edge (top of uninfected layer)
                for (int i = 0; i < infectedGraphValues.size(); i++) {
                    infectedXPoints[infectedGraphValues.size() + 2 + i] = 450 + (infectedGraphValues.size() - 1 - i) * 10;
                    infectedYPoints[infectedGraphValues.size() + 2 + i] = uninfectedTopY[infectedGraphValues.size() - 1 - i];
                }

                g.fillPolygon(infectedXPoints, infectedYPoints, infectedGraphValues.size() * 2 + 2);
            }

            int total = (int) (infectedHeight + immuneHeight + uninfectedHeight) / 100;

            // Add legend
            g.setColor(Color.BLACK);
            g.drawString("Legend:", 950, 50);

            g.setColor(Color.red);
            g.fillRect(950, 60, 20, 15);
            g.fillRect(310, 80, 20, 15);
            g.setColor(Color.BLACK);
            g.drawString("Infected", 980, 72);
            g.drawString((int) (infectedHeight / total + 0.5) + "%", 340, 90);

            g.setColor(Color.green);
            g.fillRect(950, 80, 20, 15);
            g.fillRect(310, 100, 20, 15);
            g.setColor(Color.BLACK);
            g.drawString("Susceptible", 980, 92);
            g.drawString((int) (uninfectedHeight / total + 0.5) + "%", 340, 110);

            g.setColor(Color.darkGray);
            g.fillRect(950, 100, 20, 15);
            g.fillRect(310, 120, 20, 15);
            g.setColor(Color.BLACK);
            g.drawString("Immune", 980, 112);
            g.drawString((int) (immuneHeight / total + 0.5) + "%", 340, 130);

            g.drawString("Round: " + Simulation.round, 980, 132);
        }
    };

    public Visualisation() {
        uninfectedGraphValues = new ArrayList<>();
        immuneGraphValues = new ArrayList<>();
        infectedGraphValues = new ArrayList<>();

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
        int[] totalCount = new int[3]; // [infected, uninfected, immune]

        // Counts the number of types of subject on each tile and draws them
        for (int i = 0; i < Simulation.size[0]; i++) {
            for (int j = 0; j < Simulation.size[1]; j++) {
                for (Subject subject : Simulation.board[i][j]) {
                    if (subject.infected){
                        count[i][j][0]++;
                        totalCount[0]++;
                    } else if (subject.infectable) {
                        count[i][j][1]++;
                        totalCount[1]++;
                    } else {
                        count[i][j][2]++;
                        totalCount[2]++;
                    }
                }
                int k = count[i][j][0]; // Infected
                int m = count[i][j][1]; // Infectable
                int n = count[i][j][2]; // Immune

                if (k > 0) {
                    pixelQueue.add(new Pixel(i, j, Color.red));
                } else if (m > 0) {
                    pixelQueue.add(new Pixel(i, j, Color.green));
                } else if (n > 0){
                    pixelQueue.add(new Pixel(i, j, Color.darkGray));
                }
            }
        }
        render();
        renderPie(totalCount);
        renderHistogram(totalCount);
    }

    // Record for easy storage
    private record Pixel(int x, int y, Color colour) {}

    public static void renderPie(int[] nums) {
        int total = nums[0] + nums[1] + nums[2];

        if (total == 0) return; // Avoid division by zero

        // Calculate angles as integers (avoiding truncation issues)
        int angle1 = (int) Math.round((double) nums[0] / total * 360); // Infected
        int angle2 = (int) Math.round((double) nums[1] / total * 360); // Uninfected
        int angle3 = 360 - angle1 - angle2; // Immune - ensure total is exactly 360

        // Set starting angles and arc lengths for infected (red)
        angles[0][0] = 0;
        angles[0][1] = angle1;

        // Set starting angles and arc lengths for uninfected (green)
        angles[1][0] = angle1;
        angles[1][1] = angle2;

        // Set starting angles and arc lengths for immune (grey)
        angles[2][0] = angle1 + angle2;
        angles[2][1] = angle3;
    }

    public static void renderHistogram(int[] nums) {
        int total = nums[0] + nums[1] + nums[2];
        if (total == 0) return;

        // Calculate percentages for 100% stacked chart (height proportional to percentage)
        infectedHeight = ((float) nums[0] / total) * 300; // Scale to chart height
        uninfectedHeight = ((float) nums[1] / total) * 300;
        immuneHeight = ((float) nums[2] / total) * 300;

        Float[][] infected = new Float[2][2];
        Float[][] uninfected = new Float[2][2];
        Float[][] immune = new Float[2][2];

        // Store just the height for each category (not position)
        infected[0][0] = 0f; // Not used for height calculation
        infected[0][1] = 0f; // Not used for height calculation
        infected[1][0] = infectedHeight; // Height of infected area

        uninfected[0][0] = 0f; // Not used for height calculation
        uninfected[0][1] = 0f; // Not used for height calculation
        uninfected[1][0] = uninfectedHeight; // Height of uninfected area

        immune[0][0] = 0f; // Not used for height calculation
        immune[0][1] = 0f; // Not used for height calculation
        immune[1][0] = immuneHeight; // Height of immune area

        // Limit the number of data points to keep within chart bounds
        if (infectedGraphValues.size() >= 50) { // 50 * 10 = 500 pixels width
            infectedGraphValues.remove(0);
            uninfectedGraphValues.remove(0);
            immuneGraphValues.remove(0);
        }

        infectedGraphValues.add(infected);
        uninfectedGraphValues.add(uninfected);
        immuneGraphValues.add(immune);
    }
}