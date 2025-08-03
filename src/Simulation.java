import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Main simulation engine that runs the virus spread simulation.
 * This class extends Thread to run the simulation in a separate thread,
 * managing the population, tracking infections, and updating the visualization.
 */
public class Simulation extends Thread{
    // Simulation configuration parameters
    public int populationSize;        // Total number of subjects in simulation
    public int startingInfected;      // Number of subjects infected at start
    public int infectChance;          // Probability of infection on contact (0-100)
    public int infectDuration;        // How many rounds subjects stay infected
    public int immunityDuration;      // How many rounds subjects stay immune after recovery
    public int maxRuntime;            // Maximum number of rounds to run (-1 for unlimited)

    // Simulation grid dimensions [width, height]
    public int[] size = new int[2];

    // Simulation identification and timing
    public String name;               // Name of this simulation
    public int frameDelay;            // Milliseconds to wait between rounds for visualization

    // Real-time statistics tracking
    public int round = 0;             // Current simulation round number
    public int infected = 0;          // Current number of infected subjects
    public int immune = 0;            // Current number of immune subjects

    // Data storage for analysis and export
    public ArrayList<Infection> infections = new ArrayList<>();  // All infection events
    public ArrayList<Subject> population = new ArrayList<>();    // All subjects in simulation
    public ArrayList<Subject>[][] board;                         // 2D grid for spatial tracking

    // Simulation control and visualization
    final Visualisation v;            // Handles graphical display
    public Boolean run = true;        // Controls simulation execution

    // Performance optimization objects - reused each round to avoid garbage collection
    private final ArrayList<Subject> infectedCache = new ArrayList<>();     // Temporary list for infected subjects
    private final ArrayList<Subject> susceptibleCache = new ArrayList<>();  // Temporary list for susceptible subjects
    private final ThreadLocalRandom random = ThreadLocalRandom.current();   // Fast random number generator

    /**
     * Constructor that initializes a new simulation with specified parameters.
     *
     * @param sw Simulation width (grid columns)
     * @param sh Simulation height (grid rows)
     * @param ps Population size (total subjects)
     * @param si Starting infected count
     * @param ic Infection chance percentage (0-100)
     * @param id Infection duration in rounds
     * @param iD Immunity duration in rounds
     * @param mr Maximum runtime in rounds (-1 for unlimited)
     * @param fd Frame delay in milliseconds between rounds
     * @param n Name of this simulation
     */
    public Simulation(int sw, int sh,int ps, int si, int ic, int id, int iD, int mr, int fd, String n) {
        super();

        // Store simulation parameters
        size[0] = sw;
        size[1] = sh;
        populationSize = ps;
        startingInfected = si;
        infectChance = ic;
        infectDuration = id;
        immunityDuration = iD;
        maxRuntime = mr;
        frameDelay = fd;
        name = n;

        // Initialize visualization system
        v = new Visualisation(this);

        // Set up the spatial grid for tracking subject positions
        initializeBoard();

        // Create the population with specified size
        population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            population.add(new Subject(this));
        }

        // Infect the specified number of initial subjects
        for (int i = 0; i < startingInfected; i++) {
            population.get(i).startInfected();
        }
    }

    /**
     * Initializes the 2D spatial grid used for tracking subject positions.
     * Each cell in the grid contains a list of subjects currently at that location.
     */
    private void initializeBoard() {
        board = new ArrayList[size[0]][size[1]];
        for (int i = 0; i < size[0]; i++) {
            for (int j = 0; j < size[1]; j++) {
                board[i][j] = new ArrayList<>();
            }
        }
    }

    /**
     * Executes one round of the simulation.
     * This involves: clearing the board, moving subjects, handling infections,
     * placing subjects on the board, simulating contacts, and updating visualization.
     */
    public void simulateRound() {
        // Clear all subjects from the spatial grid (more efficient than creating new lists)
        for (int i = 0; i < size[0]; i++) {
            for (int j = 0; j < size[1]; j++) {
                board[i][j].clear();
            }
        }

        // Update each subject's state and position, then place them on the board
        for (Subject current : population) {
            current.move();                                           // Move subject to new position
            current.handleInfection();                               // Update infection/immunity status
            board[current.location[0]][current.location[1]].add(current);  // Place on spatial grid
        }

        // Check each grid cell for potential infections when multiple subjects occupy same space
        for (int i = 0; i < size[0]; i++) {
            for (int j = 0; j < size[1]; j++) {
                ArrayList<Subject> cell = board[i][j];
                if (cell.size() > 1) { // Only process cells with multiple subjects
                    simulateContact(cell);
                }
            }
        }

        // Update the visual representation and refresh display
        v.visualiseRound();
        v.simContentPane.repaint();
        v.statsContentPane.repaint();
    }

    /**
     * Records a new infection event and updates all relevant data structures.
     *
     * @param location The [x, y] coordinates where infection occurred
     * @param subject The subject who became infected
     * @param source The subject who transmitted the infection
     */
    public void addInfection(int[] location, Subject subject, Subject source) {
        if (subject.infected) return; // Prevent double-infection

        // Create infection record with cloned location to prevent reference issues
        Infection infection = new Infection(location.clone(), round, subject, source);
        infections.add(infection);

        // Update subject infection histories
        subject.infectCount.add(infection);                    // Track infections received by subject
        if (source != subject) source.infectionCount.add(infection); // Track infections given by source

        // Update subject status and simulation statistics
        subject.infected = true;
        infected++;
    }

    /**
     * Simulates potential virus transmission when multiple subjects occupy the same location.
     * Uses optimized caching to avoid repeated list allocations and improve performance.
     *
     * @param subjects List of all subjects at the current location
     */
    public void simulateContact(ArrayList<Subject> subjects) {
        // Clear and reuse cached lists to avoid garbage collection overhead
        infectedCache.clear();
        susceptibleCache.clear();

        // Categorize subjects in a single pass for efficiency
        for (Subject subject : subjects) {
            if (subject.infected) {
                infectedCache.add(subject);
            } else if (subject.infectable) {  // Susceptible (not immune)
                susceptibleCache.add(subject);
            }
        }

        // Skip processing if no potential for transmission
        if (infectedCache.isEmpty() || susceptibleCache.isEmpty()) {
            return;
        }

        // Pre-calculate values for performance optimization
        int infectedSize = infectedCache.size();
        int infectThreshold = (int) Math.round((1 - Math.pow(1 - ((double) infectChance / 100), infectedSize)) * 100);

        // Test each susceptible subject for potential infection
        for (Subject subject : susceptibleCache) {
            if (random.nextInt(100) < infectThreshold) {
                // Select random infected subject as source of transmission
                Subject source = infectedCache.get(random.nextInt(infectedSize));
                subject.infect(source);
            }
        }
    }

    /**
     * Main simulation loop that runs in a separate thread.
     * Continues until one of the stopping conditions is met:
     * - No more infected subjects (epidemic ends naturally)
     * - All subjects are infected or immune (no more susceptible)
     * - Maximum runtime reached
     * - Simulation manually stopped
     */
    @Override
    public void run() {
        while (infected > 0 &&                           // Still have infected subjects
                infected < populationSize - immune &&     // Still have susceptible subjects
                round != maxRuntime &&                     // Haven't reached time limit
                run) {                                     // Simulation not manually stopped

            round++;           // Increment round counter
            simulateRound();   // Execute one simulation round

            // Add delay for visualization if specified
            if (frameDelay > 0) {
                try {
                    Thread.sleep(frameDelay);
                } catch (Exception ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}