package Graphics;

import Main.Simulation;
import Main.Subject;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Visualisation {
    private static final Window stats = new Window(1500, 1000);
    private static final Window sim = new Window();
    public static ArrayList<Pixel> pixelQueue = new ArrayList<>();

    static BufferedImage offScreenImage;
    static int[][] angles = new int[3][2];
    static ArrayList<float[][]> uninfectedGraphValues;
    static ArrayList<float[][]> immuneGraphValues;
    static ArrayList<float[][]> infectedGraphValues;

    private static final int GRID_SIZE = 10;
    private static final int MAX_GRAPH_POINTS = 50;

    public static Container simContentPane = new Container() {
        public void paint(Graphics g) {
            super.paint(g);
            if (offScreenImage != null) {
                g.drawImage(offScreenImage, 0, 0, null);
            }
        }
    };

    public static void render(){
        if (simContentPane.getWidth() <= 0 || simContentPane.getHeight() <= 0) return;

        offScreenImage = new BufferedImage(simContentPane.getWidth(), simContentPane.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) offScreenImage.getGraphics();

        g2.setColor(Color.BLACK);

        for(Pixel pixel : pixelQueue) {
            g2.setColor(pixel.colour);
            g2.fillRect(pixel.x * GRID_SIZE, pixel.y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
        }

        g2.dispose();
    }

    public static Container statsContentPane = new Container(){
        public void paint(Graphics g) {
            super.paint(g);

            g.setColor(Color.WHITE);
            g.fillOval(0, 0, 300, 300);

            g.setColor(Color.red);
            g.fillArc(0, 0, 300, 300, angles[0][0], angles[0][1]);

            g.setColor(Color.green);
            g.fillArc(0, 0, 300, 300, angles[1][0], angles[1][1]);

            g.setColor(Color.darkGray);
            g.fillArc(0, 0, 300, 300, angles[2][0], angles[2][1]);

            if (infectedGraphValues.size() > 1) {
                drawStackedGraph(g);
            }

            drawLegend(g);
        }
    };

    private static void drawStackedGraph(Graphics g) {
        int[] xPoints = new int[infectedGraphValues.size()];
        int[] infectedTopY = new int[infectedGraphValues.size()];
        int[] uninfectedTopY = new int[infectedGraphValues.size()];
        int[] immuneTopY = new int[infectedGraphValues.size()];

        for (int i = 0; i < infectedGraphValues.size(); i++) {
            xPoints[i] = 400 + (i * 10);
            immuneTopY[i] = 300 - (int) immuneGraphValues.get(i)[1][0];
            uninfectedTopY[i] = immuneTopY[i] - (int) uninfectedGraphValues.get(i)[1][0];
            infectedTopY[i] = 0;
        }

        drawArea(g, Color.darkGray, xPoints, immuneTopY, 300);
        drawArea(g, Color.green, xPoints, uninfectedTopY, immuneTopY);
        drawArea(g, Color.red, xPoints, infectedTopY, uninfectedTopY);
    }

    private static void drawArea(Graphics g, Color color, int[] xPoints, int[] topY, int bottomValue) {
        g.setColor(color);
        int size = xPoints.length;
        int[] polyX = new int[size * 2];
        int[] polyY = new int[size * 2];

        for (int i = 0; i < size; i++) {
            polyX[i] = xPoints[i];
            polyY[i] = topY[i];
        }

        for (int i = 0; i < size; i++) {
            polyX[size + i] = xPoints[size - 1 - i];
            if (bottomValue instanceof Integer) {
                polyY[size + i] = (Integer) bottomValue;
            } else {
                int[] bottomArray = new int[]{bottomValue};
                polyY[size + i] = bottomArray[size - 1 - i];
            }
        }

        g.fillPolygon(polyX, polyY, size * 2);
    }

    private static void drawArea(Graphics g, Color color, int[] xPoints, int[] topY, int[] bottomY) {
        g.setColor(color);
        int size = xPoints.length;
        int[] polyX = new int[size * 2];
        int[] polyY = new int[size * 2];

        for (int i = 0; i < size; i++) {
            polyX[i] = xPoints[i];
            polyY[i] = topY[i];
        }

        for (int i = 0; i < size; i++) {
            polyX[size + i] = xPoints[size - 1 - i];
            polyY[size + i] = bottomY[size - 1 - i];
        }

        g.fillPolygon(polyX, polyY, size * 2);
    }

    private static void drawLegend(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("Legend:", 950, 50);

        g.setColor(Color.red);
        g.fillRect(950, 60, 20, 15);
        g.setColor(Color.BLACK);
        g.drawString("Infected", 980, 72);

        g.setColor(Color.green);
        g.fillRect(950, 80, 20, 15);
        g.setColor(Color.BLACK);
        g.drawString("Susceptible", 980, 92);

        g.setColor(Color.darkGray);
        g.fillRect(950, 100, 20, 15);
        g.setColor(Color.BLACK);
        g.drawString("Immune", 980, 112);
    }

    public Visualisation() {
        uninfectedGraphValues = new ArrayList<>();
        immuneGraphValues = new ArrayList<>();
        infectedGraphValues = new ArrayList<>();

        sim.setContentPane(simContentPane);
        stats.setContentPane(statsContentPane);

        simContentPane.setPreferredSize(new Dimension(Simulation.size[0] * GRID_SIZE, Simulation.size[1] * GRID_SIZE));
        statsContentPane.setPreferredSize(new Dimension(1500, 1000));
        sim.setSize(sim.getPreferredSize());
        stats.setSize(stats.getPreferredSize());
    }

    public static void visualiseRound() {
        int[][][] count = new int[Simulation.size[0]][Simulation.size[1]][3];
        int[] totalCount = new int[3];

        for (int i = 0; i < Simulation.size[0]; i++) {
            for (int j = 0; j < Simulation.size[1]; j++) {
                ArrayList<Subject> subjects = Simulation.board[i][j];
                if (subjects.isEmpty()) continue;

                for (Subject subject : subjects) {
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

                if (count[i][j][0] > 0) {
                    pixelQueue.add(new Pixel(i, j, Color.red));
                } else if (count[i][j][1] > 0) {
                    pixelQueue.add(new Pixel(i, j, Color.green));
                } else if (count[i][j][2] > 0){
                    pixelQueue.add(new Pixel(i, j, Color.darkGray));
                }
            }
        }

        render();
        renderPie(totalCount);
        renderHistogram(totalCount);
    }

    private record Pixel(int x, int y, Color colour) {}

    public static void renderPie(int[] nums) {
        int total = nums[0] + nums[1] + nums[2];
        if (total == 0) return;

        int angle1 = (int) Math.round((double) nums[0] / total * 360);
        int angle2 = (int) Math.round((double) nums[1] / total * 360);
        int angle3 = 360 - angle1 - angle2;

        angles[0][0] = 0;
        angles[0][1] = angle1;
        angles[1][0] = angle1;
        angles[1][1] = angle2;
        angles[2][0] = angle1 + angle2;
        angles[2][1] = angle3;
    }

    public static void renderHistogram(int[] nums) {
        int total = nums[0] + nums[1] + nums[2];
        if (total == 0) return;

        float infectedHeight = ((float) nums[0] / total) * 300;
        float uninfectedHeight = ((float) nums[1] / total) * 300;
        float immuneHeight = ((float) nums[2] / total) * 300;

        float[][] infected = {{0f, 0f}, {infectedHeight, 0f}};
        float[][] uninfected = {{0f, 0f}, {uninfectedHeight, 0f}};
        float[][] immune = {{0f, 0f}, {immuneHeight, 0f}};

        if (infectedGraphValues.size() >= MAX_GRAPH_POINTS) {
            infectedGraphValues.remove(0);
            uninfectedGraphValues.remove(0);
            immuneGraphValues.remove(0);
        }

        infectedGraphValues.add(infected);
        uninfectedGraphValues.add(uninfected);
        immuneGraphValues.add(immune);
    }
}