import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.lang.Float;
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


    public Visualisation(Simulation s) {
        this.s = s;

        JFrame statisticsPane = new JFrame("AS91907 | Statistics");
        statisticsPane.setSize(1250, 500);
        JFrame simulationPane = new JFrame("AS91907 | Simulation");
        simulationPane.setTitle("AS91907 | Virus Simulator");
        simulationPane.setLayout(new GridBagLayout());
        simulationPane.setResizable(false);

        simulationPane.setContentPane(simContentPane);
        statisticsPane.setContentPane(statsContentPane);

        simContentPane.setPreferredSize(new Dimension(s.size[0] * 10, s.size[1] * 10));
        simulationPane.pack();

        simulationPane.setVisible(true);
        statisticsPane.setVisible(true);
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

        g2.setColor(Color.BLACK);

        for(Pixel pixel : pixelQueue) {
            g2.setColor(pixel.colour);
            g2.fillRect(pixel.x * 10, pixel.y * 10, 10, 10);
        }
    }

    public Container statsContentPane = new Container(){
        public void paint(Graphics g) {
            super.paint(g);

            int total = nums[0] + nums[1] + nums[2];

            if (total != 0) {
                fillArc(Math.round((double) nums[0] / total * 360), Color.red, g);
                fillArc(Math.round((double) nums[1] / total * 360), Color.green, g);
                fillArc(0, Color.darkGray, g);

                g.setColor(Color.black);
                g.drawOval(15, 15, 300, 300);
            }


            // Draw pie chart background
            g.setColor(Color.black);
            g.drawOval(0, 0, 300, 300);

            // Draw stacked areas using smooth connecting lines
            if (!infectedGraphValues.isEmpty()) {
                // Calculate cumulative heights for each time point
                int[] xPoints = new int[infectedGraphValues.size()];
                int[] uninfectedTopY = new int[infectedGraphValues.size()];
                int[] immuneTopY = new int[infectedGraphValues.size()];

                for (int i = 0; i < infectedGraphValues.size(); i++) {
                    xPoints[i] = 450 + (i * 10);
                    // Stack from bottom to top: immune (bottom), uninfected (middle), infected (top)
                    immuneTopY[i] = 300 - immuneGraphValues.get(i)[1][0].intValue();
                    uninfectedTopY[i] = immuneTopY[i] - uninfectedGraphValues.get(i)[1][0].intValue();
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


            // Add legend
            g.setColor(Color.BLACK);
            g.drawString("Legend:", 950, 50);

            g.setColor(Color.red);
            g.fillRect(950, 60, 20, 15);
            g.fillRect(310, 80, 20, 15);
            g.setColor(Color.BLACK);
            g.drawString("Infected", 980, 72);

            g.setColor(Color.green);
            g.fillRect(950, 80, 20, 15);
            g.fillRect(310, 100, 20, 15);
            g.setColor(Color.BLACK);
            g.drawString("Susceptible", 980, 92);

            g.setColor(Color.darkGray);
            g.fillRect(950, 100, 20, 15);
            g.fillRect(310, 120, 20, 15);
            g.setColor(Color.BLACK);
            g.drawString("Immune", 980, 112);

            g.drawString("Round: " + s.round, 980, 132);
        }

        private void fillArc(float radian, Color colour, Graphics g) {
            int intRad = (int) radian;
            g.setColor(colour);
            g.fillArc(15, 15, 300, 300, start, intRad);
            start = intRad;
        }
    };

    public void visualiseRound() {
        int[][][] count = new int[s.size[0]][s.size[1]][3];

        for (int i = 0; i < s.size[0]; i++) {
            for (int j = 0; j < s.size[1]; j++) {
                for (Subject subject : s.board[i][j]) {
                    if (subject.infected){
                        count[i][j][0]++;
                        nums[0]++;
                    } else if (subject.infectable) {
                        count[i][j][1]++;
                        nums[1]++;
                    } else {
                        count[i][j][2]++;
                        nums[2]++;
                    }
                }
                int k = count[i][j][0];
                int m = count[i][j][1];
                int n = count[i][j][2];

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
        renderHistogram(nums);
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