package Main;

import Graphics.Visualisation;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Simulation {
    // Base Parameters
    static int populationSize = 100000;
    static int startingInfected = 1;
    static int infectChance = 50;
    static int infectDuration = 2;
    static int immunityDuration = 2;
    static int maxRuntime = 1000000;
    public static int[] size = new int[2];

    // Statistics trackers
    static int round = 0;
    static int infected = 0;
    static int immune = 0;

    // Storage
    public static ArrayList<Infection> infections = new ArrayList<>();
    public static ArrayList<Subject> population = new ArrayList<>();
    public static ArrayList<Subject>[][] board;

    // Other
    static Visualisation visualisation;

    public static void main(String[] args) {
        size[0] = 100;
        size[1] = 100;

        visualisation = new Visualisation();
        initializeBoard();

        // Initialize population
        for (int i = 0; i < populationSize; i++) {
            population.add(new Subject());
        }

        // Set initial infected
        for (int i = 0; i < startingInfected; i++) {
            population.get(i).startInfected();
        }

        // Main simulation loop
        while (infected > 0 && infected < populationSize && round < maxRuntime) {
            simulateRound();
            round++;

            try {
                TimeUnit.MILLISECONDS.sleep(1); // Change to user preference
            } catch (InterruptedException ignored) {}
        }
    }

    // Loads all positions into first frame of
    private static void initializeBoard() {
        board = new ArrayList[size[0]][size[1]];
        for (int i = 0; i < size[0]; i++) {
            for (int j = 0; j < size[1]; j++) {
                board[i][j] = new ArrayList<>();
            }
        }
    }

    public static void simulateRound() {
        // Clear the board
        for (int i = 0; i < size[0]; i++) {
            for (int j = 0; j < size[1]; j++) {
                board[i][j].clear();
            }
        }

        // Update subjects and place them on board
        for (Subject current : population) {
            current.move();
            current.handleInfection();
            board[current.location[0]][current.location[1]].add(current);
        }

        // Process contacts
        for (int i = 0; i < size[0]; i++) {
            for (int j = 0; j < size[1]; j++) {
                if (!board[i][j].isEmpty()) {
                    simulateContact(board[i][j]);
                }
            }
        }

        // Update visualization
        Visualisation.pixelQueue.clear(); // Clear previous pixels
        Visualisation.visualiseRound();
        Visualisation.simContentPane.repaint();
    }

    public static void addInfection(int[] location, Subject subject, Subject source) {
        if (subject.infected) return; // Prevent double infection

        Infection infection = new Infection(location.clone(), round, subject, source);
        infections.add(infection);

        subject.infectCount.add(infection);
        if (source != subject) { // Only add to source's infection count if it's not self-infection
            source.infectionCount.add(infection);
        }

        subject.infected = true;
        infected++;
    }

    public static void simulateContact(ArrayList<Subject> subjects) {
        ArrayList<Subject> infectedSubjects = new ArrayList<>();
        ArrayList<Subject> susceptible = new ArrayList<>();

        for (Subject subject : subjects) {
            if (subject.infected) {
                infectedSubjects.add(subject);
            } else if (subject.infectable) {
                susceptible.add(subject);
            }
        }

        if (!infectedSubjects.isEmpty() && !susceptible.isEmpty()) {
            for (Subject subject : susceptible) {
                // Fixed the random chance calculation
                if (Math.random() * 100 < infectChance) {
                    Subject source = infectedSubjects.get((int) (Math.random() * infectedSubjects.size()));
                    subject.infect(source);
                }
            }
        }
    }
}