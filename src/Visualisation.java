import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.lang.Float;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Visualisation {
    final Simulation s;

    ArrayList<Pixel> pixelQueue = new ArrayList<>();

    BufferedImage offScreenImage;
    int[] nums = new int[3];
    private int start = 0;
    ArrayList<Float[][]> uninfectedGraphValues = new ArrayList<>();
    ArrayList<Float[][]> immuneGraphValues = new ArrayList<>();
    ArrayList<Float[][]> infectedGraphValues = new ArrayList<>();

    float infectedHeight;
    float uninfectedHeight;
    float immuneHeight;

    JFrame statisticsPane;
    JFrame simulationPane;
    NumberFormat percentRounder = NumberFormat.getNumberInstance();

    public Visualisation(Simulation s) {

        this.s = s;

        statisticsPane = new JFrame("AS91907 | Statistics");
        statisticsPane.setSize(1050, 370);
        simulationPane = new JFrame("AS91907 | Simulation");
        simulationPane.setTitle("AS91907 | Virus Simulator");
        simulationPane.setLayout(new GridBagLayout());
        simulationPane.setResizable(false);
        statisticsPane.setResizable(false);

        simulationPane.setContentPane(simContentPane);
        statisticsPane.setContentPane(statsContentPane);

        simContentPane.setPreferredSize(new Dimension(s.size[0] * 3, s.size[1] * 3));
        simulationPane.pack();

        simulationPane.setVisible(true);
        statisticsPane.setVisible(true);

        percentRounder.setMaximumFractionDigits(2);
    }

    public Container simContentPane = new Container() {
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(offScreenImage, 0, 0, null);
        }
    };

    public void render(){
        offScreenImage = new BufferedImage(simContentPane.getWidth(), simContentPane.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) offScreenImage.getGraphics();



        g2.setColor(Color.black);
        g2.fillRect(0, 0, s.size[0] * 3, s.size[1] * 3);

        for(Pixel pixel : pixelQueue) {
            g2.setColor(pixel.colour);
            g2.fillRect(pixel.x * 3, pixel.y * 3, 3, 3);
        }
    }

    public Container statsContentPane = new Container(){
        public void paint(Graphics g) {
            super.paint(g);

            int total = nums[0] + nums[1] + nums[2];

            if (total != 0) {
                fillArc(360, Color.blue, g);
                fillArc(Math.round((double) nums[0] / total * 360), Color.red, g);
                fillArc(Math.round((double) nums[1] / total * 360), Color.green, g);

                g.setColor(Color.black);
                g.drawOval(15, 15, 300, 300);
            }

            // Draw stacked areas using smooth connecting lines
            if (infectedGraphValues.size() > 0) {
                // Calculate cumulative heights for each time point
                int[] xPoints = new int[infectedGraphValues.size()];
                int[] infectedTopY = new int[infectedGraphValues.size()];
                int[] uninfectedTopY = new int[infectedGraphValues.size()];
                int[] immuneTopY = new int[infectedGraphValues.size()];

                for (int i = 0; i < infectedGraphValues.size(); i++) {
                    xPoints[i] = 340 + (i * 10);
                    // Stack from bottom to top: immune (bottom), uninfected (middle), infected (top)
                    immuneTopY[i] = 300 - immuneGraphValues.get(i)[1][0].intValue();
                    uninfectedTopY[i] = immuneTopY[i] - uninfectedGraphValues.get(i)[1][0].intValue();
                    infectedTopY[i] = 0; // Infected always goes to the top
                }

                // Draw immune area (bottom layer - grey)
                g.setColor(Color.blue);
                int[] immuneXPoints = new int[immuneGraphValues.size() * 2 + 2];
                int[] immuneYPoints = new int[immuneGraphValues.size() * 2 + 2];

                // First point at bottom left
                immuneXPoints[0] = 340;
                immuneYPoints[0] = 300;

                // Draw top edge
                for (int i = 0; i < immuneGraphValues.size(); i++) {
                    immuneXPoints[i + 1] = xPoints[i];
                    immuneYPoints[i + 1] = immuneTopY[i];
                }

                // Close to bottom right
                immuneXPoints[immuneGraphValues.size() + 1] = 340 + (infectedGraphValues.size() - 1) * 10;
                immuneYPoints[immuneGraphValues.size() + 1] = 300;

                // Draw bottom edge back to start
                for (int i = 0; i < immuneGraphValues.size(); i++) {
                    immuneXPoints[immuneGraphValues.size() + 2 + i] = 340 + (immuneGraphValues.size() - 1 - i) * 10;
                    immuneYPoints[immuneGraphValues.size() + 2 + i] = 300;
                }

                for (int i = 0; i < immuneYPoints.length; i++) {
                    immuneYPoints[i] += 15;
                }

                g.fillPolygon(immuneXPoints, immuneYPoints, immuneGraphValues.size() * 2 + 2);

                // Draw uninfected area (middle layer - green)
                g.setColor(Color.green);
                int[] uninfectedXPoints = new int[uninfectedGraphValues.size() * 2 + 2];
                int[] uninfectedYPoints = new int[uninfectedGraphValues.size() * 2 + 2];

                // Start at bottom left of this layer
                uninfectedXPoints[0] = 340;
                uninfectedYPoints[0] = immuneTopY[0];

                // Draw top edge
                for (int i = 0; i < uninfectedGraphValues.size(); i++) {
                    uninfectedXPoints[i + 1] = xPoints[i];
                    uninfectedYPoints[i + 1] = uninfectedTopY[i];
                }

                // Close to bottom right of this layer
                uninfectedXPoints[uninfectedGraphValues.size() + 1] = 340 + (uninfectedGraphValues.size() - 1) * 10;
                uninfectedYPoints[uninfectedGraphValues.size() + 1] = immuneTopY[immuneGraphValues.size() - 1];

                // Draw bottom edge back
                for (int i = 0; i < uninfectedGraphValues.size(); i++) {
                    uninfectedXPoints[uninfectedGraphValues.size() + 2 + i] = 340 + (uninfectedGraphValues.size() - 1 - i) * 10;
                    uninfectedYPoints[uninfectedGraphValues.size() + 2 + i] = immuneTopY[uninfectedGraphValues.size() - 1 - i];
                }

                for (int i = 0; i < uninfectedYPoints.length; i++) {
                    uninfectedYPoints[i] += 15;
                }

                g.fillPolygon(uninfectedXPoints, uninfectedYPoints, uninfectedGraphValues.size() * 2 + 2);

                // Draw infected area (top layer - red) - fills to top of chart
                g.setColor(Color.red);
                int[] infectedXPoints = new int[infectedGraphValues.size() * 2 + 2];
                int[] infectedYPoints = new int[infectedGraphValues.size() * 2 + 2];

                // Start at top left
                infectedXPoints[0] = 340;
                infectedYPoints[0] = 0;

                // Draw across top
                for (int i = 0; i < infectedGraphValues.size(); i++) {
                    infectedXPoints[i + 1] = xPoints[i];
                    infectedYPoints[i + 1] = 0;
                }

                // Close to top right
                infectedXPoints[infectedGraphValues.size() + 1] = 340 + (infectedGraphValues.size() - 1) * 10;
                infectedYPoints[infectedGraphValues.size() + 1] = 0;

                // Draw bottom edge (top of uninfected layer)
                for (int i = 0; i < infectedGraphValues.size(); i++) {
                    infectedXPoints[infectedGraphValues.size() + 2 + i] = 340 + (infectedGraphValues.size() - 1 - i) * 10;
                    infectedYPoints[infectedGraphValues.size() + 2 + i] = uninfectedTopY[infectedGraphValues.size() - 1 - i];
                }

                for (int i = 0; i < infectedYPoints.length; i++) {
                    infectedYPoints[i] += 15;
                }

                g.fillPolygon(infectedXPoints, infectedYPoints, infectedGraphValues.size() * 2 + 2);
            }


            // Add legend
            g.setColor(Color.black);
            g.drawString("Legend:", 850, 50);

            g.setColor(Color.red);
            g.fillRect(850, 60, 20, 20);
            g.setColor(Color.black);
            g.drawRect(850, 60, 20, 20);
            g.drawString("Infected", 880, 75);

            g.setColor(Color.green);
            g.fillRect(850, 85, 20, 20);
            g.setColor(Color.black);
            g.drawRect(850, 85, 20, 20);
            g.drawString("Susceptible", 880, 100);

            g.setColor(Color.blue);
            g.fillRect(850, 110, 20, 20);
            g.setColor(Color.black);
            g.drawRect(850, 110, 20, 20);
            g.drawString("Immune", 880, 125);

            g.drawString("Round: " + s.round, 850, 155);
            g.drawString("Infected Count: " + nums[0], 850, 185);
            g.drawString("Susceptible Count: " + nums[1], 850, 200);
            g.drawString("Immune Count: " + nums[2], 850, 215);

            int t = nums[0] + nums[1] + nums[2];
            double[] p = new double[]{
                100.0 * nums[0] / t,
                100.0 * nums[1] / t,
                100.0 * nums[2] / t,
            };

            g.drawString("Infected Percent: " + percentRounder.format(p[0]) + "%", 850, 245);
            g.drawString("Susceptible Percent: " + percentRounder.format(p[1]) + "%", 850, 260);
            g.drawString("Immune Percent: " + percentRounder.format(p[2]) + "%", 850, 275);

            g.drawRect(340, 15, 490, 300);
        }

        private void fillArc(float radian, Color colour, Graphics g) {
            int intRad = (int) radian;
            g.setColor(colour);
            g.fillArc(15, 15, 300, 300, start, intRad);
            start = intRad;
        }
    };
    public void visualiseRound() {
        // Creates a 3d array for storing the number of infected, immune, and normal subjects on each tile
        int[][][] count = new int[s.size[0]][s.size[1]][3];
        int[] totalCount = new int[3]; // [infected, uninfected, immune]

        // Counts the number of types of subject on each tile and draws them
        for (int i = 0; i < s.size[0]; i++) {
            for (int j = 0; j < s.size[1]; j++) {
                for (Subject subject : s.board[i][j]) {
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
                    pixelQueue.add(new Pixel(i, j, Color.blue));
                }
            }
        }
        render();
        nums = totalCount;
        renderHistogram(totalCount);
    }

    private record Pixel(int x, int y, Color colour) {}

    public void renderHistogram(int[] nums) {
        int total = nums[0] + nums[1] + nums[2];
        if (total == 0) return;

        infectedHeight = ((float) nums[0] / total) * 300;
        uninfectedHeight = ((float) nums[1] / total) * 300;
        immuneHeight = ((float) nums[2] / total) * 300;

        Float[][] infected = new Float[2][2];
        Float[][] uninfected = new Float[2][2];
        Float[][] immune = new Float[2][2];


        infected[0][0] = 0f;
        infected[0][1] = 0f;
        infected[1][0] = infectedHeight;

        uninfected[0][0] = 0f;
        uninfected[0][1] = 0f;
        uninfected[1][0] = uninfectedHeight;

        immune[0][0] = 0f;
        immune[0][1] = 0f;
        immune[1][0] = immuneHeight;


        if (infectedGraphValues.size() >= 50) {
            infectedGraphValues.removeFirst();
            uninfectedGraphValues.removeFirst();
            immuneGraphValues.removeFirst();
        }

        infectedGraphValues.add(infected);
        uninfectedGraphValues.add(uninfected);
        immuneGraphValues.add(immune);
    }
}