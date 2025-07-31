import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Handles all visualization aspects of the simulation including the spatial grid display,
 * real-time statistics, pie charts, and historical trend graphs.
 * Creates and manages two separate windows: one for the simulation grid and one for statistics.
 */
public class Visualisation {
    // Reference to the simulation being visualized
    final Simulation s;

    // Graphics and rendering components
    ArrayList<Pixel> pixelQueue = new ArrayList<>();  // Queue of pixels to draw each frame
    BufferedImage offScreenImage;                     // Off-screen buffer for smooth rendering
    int[] nums = new int[3];                         // Current counts [infected, susceptible, immune]
    private int start = 0;                           // Starting angle for pie chart drawing

    // Historical data for trend visualization (limited to 50 data points for performance)
    ArrayList<Float> infectedHistory = new ArrayList<>();     // Historical infected population proportions
    ArrayList<Float> uninfectedHistory = new ArrayList<>();   // Historical susceptible population proportions
    ArrayList<Float> immuneHistory = new ArrayList<>();       // Historical immune population proportions

    // GUI windows and formatting
    JFrame statisticsPane;                           // Window displaying charts and statistics
    JFrame simulationPane;                          // Window displaying the spatial simulation grid
    NumberFormat percentRounder = NumberFormat.getNumberInstance();  // Formatter for percentage display

    /**
     * Constructor that initializes the visualization system and creates the GUI windows.
     *
     * @param s Reference to the simulation to visualize
     */
    public Visualisation(Simulation s) {
        this.s = s;

        // Initialize statistics window
        statisticsPane = new JFrame("AS91907 | Statistics");
        statisticsPane.setSize(1050, 370);

        // Initialize simulation display window
        simulationPane = new JFrame("AS91907 | Simulation");
        simulationPane.setTitle("AS91907 | Virus Simulator");
        simulationPane.setLayout(new GridBagLayout());
        simulationPane.setResizable(false);
        statisticsPane.setResizable(false);

        // Set up content panes for both windows
        simulationPane.setContentPane(simContentPane);
        statisticsPane.setContentPane(statsContentPane);

        // Size the simulation window based on grid dimensions (3x scale factor)
        simContentPane.setPreferredSize(new Dimension(s.size[0] * 3, s.size[1] * 3));
        simulationPane.pack();

        // Make both windows visible
        simulationPane.setVisible(true);
        statisticsPane.setVisible(true);

        // Configure percentage formatter to show 2 decimal places
        percentRounder.setMaximumFractionDigits(2);
    }

    /**
     * Custom container for the simulation grid display.
     * Handles painting the off-screen buffer to provide smooth animation.
     */
    public Container simContentPane = new Container() {
        public void paint(Graphics g) {
            super.paint(g);
            // Draw the off-screen rendered image to the screen
            g.drawImage(offScreenImage, 0, 0, null);
        }
    };

    /**
     * Renders the current simulation state to an off-screen buffer.
     * Each grid cell is drawn as a 3x3 pixel block colored by the dominant population type.
     */
    public void render() {
        // Create or resize off-screen buffer if needed
        if (offScreenImage == null || offScreenImage.getWidth() != simContentPane.getWidth() ||
                offScreenImage.getHeight() != simContentPane.getHeight()) {
            offScreenImage = new BufferedImage(simContentPane.getWidth(), simContentPane.getHeight(), BufferedImage.TYPE_INT_RGB);
        }

        Graphics2D g2 = (Graphics2D) offScreenImage.getGraphics();

        // Clear background to black
        g2.setColor(Color.black);
        g2.fillRect(0, 0, s.size[0] * 3, s.size[1] * 3);

        // Draw each pixel from the queue as a 3x3 block
        for (Pixel pixel : pixelQueue) {
            g2.setColor(pixel.colour);
            g2.fillRect(pixel.x * 3, pixel.y * 3, 3, 3);
        }

        g2.dispose();
    }

    /**
     * Custom container for the statistics display.
     * Handles painting pie charts, trend graphs, legends, and numerical statistics.
     */
    public Container statsContentPane = new Container() {
        public void paint(Graphics g) {
            super.paint(g);

            int total = s.populationSize;

            // Draw pie chart showing current population distribution
            if (total != 0) {
                start = 0; // Reset starting angle for pie chart

                // Draw full circle in blue (immune), then overlay infected and susceptible
                fillArc(360, Color.blue, g);
                fillArc(Math.round((double) nums[0] / total * 360), Color.red, g);      // Infected portion
                fillArc(Math.round((double) nums[1] / total * 360), Color.green, g);   // Susceptible portion

                // Draw border around pie chart
                g.setColor(Color.black);
                g.drawOval(15, 15, 300, 300);
            }

            // Draw stacked area chart showing population trends over time
            drawStackedAreaChart(g);

            // Draw legend and current statistics
            g.setColor(Color.black);
            g.drawString("Legend:", 850, 50);

            // Infected legend entry (red)
            g.setColor(Color.red);
            g.fillRect(850, 60, 20, 20);
            g.setColor(Color.black);
            g.drawRect(850, 60, 20, 20);
            g.drawString("Infected", 880, 75);

            // Susceptible legend entry (green)
            g.setColor(Color.green);
            g.fillRect(850, 85, 20, 20);
            g.setColor(Color.black);
            g.drawRect(850, 85, 20, 20);
            g.drawString("Susceptible", 880, 100);

            // Immune legend entry (blue)
            g.setColor(Color.blue);
            g.fillRect(850, 110, 20, 20);
            g.setColor(Color.black);
            g.drawRect(850, 110, 20, 20);
            g.drawString("Immune", 880, 125);

            // Display current simulation statistics
            g.drawString("Round: " + s.round, 850, 155);
            g.drawString("Infected Count: " + nums[0], 850, 185);
            g.drawString("Susceptible Count: " + nums[1], 850, 200);
            g.drawString("Immune Count: " + nums[2], 850, 215);

            // Calculate and display percentages
            int t = nums[0] + nums[1] + nums[2];
            double[] p = new double[]{
                    100.0 * nums[0] / t,  // Infected percentage
                    100.0 * nums[1] / t,  // Susceptible percentage
                    100.0 * nums[2] / t,  // Immune percentage
            };

            g.drawString("Infected Percent: " + percentRounder.format(p[0]) + "%", 850, 245);
            g.drawString("Susceptible Percent: " + percentRounder.format(p[1]) + "%", 850, 260);
            g.drawString("Immune Percent: " + percentRounder.format(p[2]) + "%", 850, 275);

            // Draw border around trend chart area
            g.drawRect(340, 15, 490, 300);
        }

        /**
         * Helper method to draw a colored arc segment for the pie chart.
         *
         * @param radian Size of the arc in degrees
         * @param colour Color to fill the arc
         * @param g Graphics context to draw on
         */
        private void fillArc(float radian, Color colour, Graphics g) {
            int intRad = (int) radian;
            g.setColor(colour);
            g.fillArc(15, 15, 300, 300, start, intRad);
            start += intRad;  // Update starting position for next arc
        }
    };

    /**
     * Processes the current simulation state and prepares visual elements for rendering.
     * Counts population types at each grid location and determines appropriate colors.
     */
    public void visualiseRound() {
        pixelQueue.clear(); // Clear previous frame's pixel data

        // 3D array to count different subject types at each grid location
        int[][][] count = new int[s.size[0]][s.size[1]][3];
        int[] totalCount = new int[3]; // Global counts [infected, susceptible, immune]

        // Process each grid cell to count and visualize subject populations
        for (int i = 0; i < s.size[0]; i++) {
            for (int j = 0; j < s.size[1]; j++) {
                int infected = 0, susceptible = 0, immune = 0;

                // Count different types of subjects at this location
                for (Subject subject : s.board[i][j]) {
                    if (subject.infected) {
                        infected++;
                    } else if (subject.infectable) {
                        susceptible++;
                    } else {
                        immune++;
                    }
                }

                // Update global counters
                totalCount[0] += infected;
                totalCount[1] += susceptible;
                totalCount[2] += immune;

                // Determine pixel color based on dominant population type at this location
                if (infected > 0) {
                    pixelQueue.add(new Pixel(i, j, Color.red));      // Red for any infected presence
                } else if (susceptible > 0) {
                    pixelQueue.add(new Pixel(i, j, Color.green));    // Green for susceptible only
                } else if (immune > 0) {
                    pixelQueue.add(new Pixel(i, j, Color.blue));     // Blue for immune only
                }
                // Black pixels (background) represent empty locations
            }
        }

        // Render the visual representation and update statistics
        render();
        nums = totalCount;
        renderHistogram(totalCount);
    }

    /**
     * Simple record class to represent a colored pixel for rendering.
     *
     * @param x X coordinate on the grid
     * @param y Y coordinate on the grid
     * @param colour Color to display at this location
     */
    private record Pixel(int x, int y, Color colour) {}

    /**
     * Updates the historical data used for trend visualization.
     * Maintains a rolling window of the last 50 data points for performance.
     *
     * @param nums Array containing current counts [infected, susceptible, immune]
     */
    public void renderHistogram(int[] nums) {
        if (s.populationSize == 0) return;

        float total = (float) s.populationSize;

        // Calculate proportional heights for stacked area chart (0-300 pixel range)
        float infectedHeight = (nums[0] / total) * 300;
        float uninfectedHeight = (nums[1] / total) * 300;
        float immuneHeight = (nums[2] / total) * 300;

        // Maintain rolling window of 99 data points for performance
        if (infectedHistory.size() >= 99) {
            infectedHistory.removeFirst();      // Remove oldest infected data point
            uninfectedHistory.removeFirst();    // Remove oldest susceptible data point
            immuneHistory.removeFirst();        // Remove oldest immune data point
        }

        // Add current values to history
        infectedHistory.add(infectedHeight);
        uninfectedHistory.add(uninfectedHeight);
        immuneHistory.add(immuneHeight);
    }

    /**
     * Draws a stacked area chart showing population trends over time.
     * Creates three colored layers representing immune (bottom), susceptible (middle), and infected (top).
     *
     * @param g Graphics context to draw the chart on
     */
    private void drawStackedAreaChart(Graphics g) {
        if (infectedHistory.isEmpty()) return;

        int size = infectedHistory.size();
        int[] xPoints = new int[size];

        // Calculate x coordinates for each data point (5 pixels apart)
        for (int i = 0; i < size; i++) {
            xPoints[i] = 340 + (i * 5);
        }

        int baseY = 315; // Bottom of chart area (300 + 15 offset)
        int topY = 15;   // Top of chart area

        // Draw immune area (bottom layer of stack)
        int[] immuneTopY = calculateTopY(immuneHistory, baseY);
        drawArea(g, xPoints, immuneTopY, baseY, Color.blue);

        // Draw susceptible area (middle layer of stack)
        int[] uninfectedTopY = calculateTopY(uninfectedHistory, immuneTopY);
        drawArea(g, xPoints, uninfectedTopY, immuneTopY, Color.green);

        // Draw infected area (top layer of stack) - fills remaining space to chart top
        int[] chartTop = new int[size];
        for (int i = 0; i < size; i++) {
            chartTop[i] = topY;
        }
        drawArea(g, xPoints, chartTop, uninfectedTopY, Color.red);
    }

    /**
     * Calculates the top Y coordinates for an area based on historical data and a base Y level.
     *
     * @param history List of historical height values
     * @param baseY Base Y coordinate to subtract heights from
     * @return Array of Y coordinates representing the top edge of the area
     */
    private int[] calculateTopY(ArrayList<Float> history, int baseY) {
        int[] topY = new int[history.size()];
        for (int i = 0; i < history.size(); i++) {
            topY[i] = baseY - history.get(i).intValue();
        }
        return topY;
    }

    /**
     * Calculates the top Y coordinates for an area based on historical data and varying base Y levels.
     *
     * @param history List of historical height values
     * @param baseYArray Array of base Y coordinates to subtract heights from
     * @return Array of Y coordinates representing the top edge of the area
     */
    private int[] calculateTopY(ArrayList<Float> history, int[] baseYArray) {
        int[] topY = new int[history.size()];
        for (int i = 0; i < history.size(); i++) {
            topY[i] = baseYArray[i] - history.get(i).intValue();
        }
        return topY;
    }

    /**
     * Draws a filled area between a top edge and a constant bottom Y level.
     *
     * @param g Graphics context to draw on
     * @param xPoints X coordinates for the area
     * @param topY Y coordinates for the top edge of the area
     * @param bottomY Constant Y coordinate for the bottom edge
     * @param color Fill color for the area
     */
    private void drawArea(Graphics g, int[] xPoints, int[] topY, int bottomY, Color color) {
        int size = xPoints.length;
        int[] areaX = new int[size * 2];  // Doubled for top and bottom edges
        int[] areaY = new int[size * 2];

        // Define top edge of area (left to right)
        System.arraycopy(xPoints, 0, areaX, 0, size);
        System.arraycopy(topY, 0, areaY, 0, size);

        // Define bottom edge of area (right to left to close the polygon)
        for (int i = 0; i < size; i++) {
            areaX[size + i] = xPoints[size - 1 - i];  // Reverse order for polygon closure
            areaY[size + i] = bottomY;                // Constant bottom level
        }

        // Fill the polygon area
        g.setColor(color);
        g.fillPolygon(areaX, areaY, size * 2);
    }

    /**
     * Draws a filled area between a top edge and a varying bottom edge.
     *
     * @param g Graphics context to draw on
     * @param xPoints X coordinates for the area
     * @param topY Y coordinates for the top edge of the area
     * @param bottomYArray Y coordinates for the bottom edge of the area
     * @param color Fill color for the area
     */
    private void drawArea(Graphics g, int[] xPoints, int[] topY, int[] bottomYArray, Color color) {
        int size = xPoints.length;
        int[] areaX = new int[size * 2];  // Doubled for top and bottom edges
        int[] areaY = new int[size * 2];

        // Define top edge of area (left to right)
        System.arraycopy(xPoints, 0, areaX, 0, size);
        System.arraycopy(topY, 0, areaY, 0, size);

        // Define bottom edge of area (right to left to close the polygon)
        for (int i = 0; i < size; i++) {
            areaX[size + i] = xPoints[size - 1 - i];        // Reverse order for polygon closure
            areaY[size + i] = bottomYArray[size - 1 - i];   // Variable bottom level
        }

        // Fill the polygon area
        g.setColor(color);
        g.fillPolygon(areaX, areaY, size * 2);
    }
}