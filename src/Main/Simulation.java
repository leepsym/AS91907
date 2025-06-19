package Main;

import Graphics.Visualisation;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Simulation {
    static int populationSize = 100000;
    static int startingInfected = 1;
    static int infectChance = 80;
    static int infectDuration = 5;
    static int immunityDuration = 25;
    static int maxRuntime = -1;

    public static int[] size = new int[2];

    public static int round = 0;
    static int infected = 0;
    static int immune = 0;
    static int susceptible = 0;

    public static ArrayList<Infection> infections = new ArrayList<>();
    public static Subject[] population;
    public static ArrayList<Subject>[][] board;

    private static final ArrayList<Subject> infectedCache = new ArrayList<>();
    private static final ArrayList<Subject> susceptibleCache = new ArrayList<>();

    static Visualisation visualisation;

    public static void main(String[] args) {
        size[0] = 100;
        size[1] = 100;

        visualisation = new Visualisation();
        initializeBoard();
        initializePopulation();

        while (infected > 0 && susceptible > 0 && round != maxRuntime) {
            simulateRound();
            round++;

            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException ignored) {}
        }

        System.out.println("Simulation ended at round " + round);
        System.out.println("Final stats - Infected: " + infected + ", Immune: " + immune + ", Susceptible: " + susceptible);
    }

    private static void initializeBoard() {
        board = new ArrayList[size[0]][size[1]];
        for (int i = 0; i < size[0]; i++) {
            for (int j = 0; j < size[1]; j++) {
                board[i][j] = new ArrayList<>();
            }
        }
    }

    private static void initializePopulation() {
        population = new Subject[populationSize];
        susceptible = populationSize;

        for (int i = 0; i < populationSize; i++) {
            population[i] = new Subject();
        }

        for (int i = 0; i < startingInfected; i++) {
            population[i].startInfected();
            susceptible--;
        }
    }

    public static void simulateRound() {
        // Clear the board efficiently
        clearBoard();

        // Update subjects and place them on board
        updateAndPlaceSubjects();

        // Process contacts
        processContacts();

        // Update visualization
        updateVisualization();
    }

    private static void clearBoard() {
        for (int i = 0; i < size[0]; i++) {
            for (int j = 0; j < size[1]; j++) {
                board[i][j].clear();
            }
        }
    }

    private static void updateAndPlaceSubjects() {
        for (Subject current : population) {
            current.move();
            current.handleInfection();
            board[current.location[0]][current.location[1]].add(current);
        }
    }

    private static void processContacts() {
        for (int i = 0; i < size[0]; i++) {
            for (int j = 0; j < size[1]; j++) {
                if (board[i][j].size() > 1) {
                    simulateContact(board[i][j]);
                }
            }
        }
    }

    private static void updateVisualization() {
        Visualisation.pixelQueue.clear();
        Visualisation.visualiseRound();
        Visualisation.simContentPane.repaint();
        Visualisation.statsContentPane.repaint();
    }

    public static void addInfection(int[] location, Subject subject, Subject source) {
        if (subject.infected) return; // Prevent double infection

        Infection infection = new Infection(location.clone(), round, subject, source);
        infections.add(infection);

        subject.infectCount.add(infection);
        if (source != subject) {
            source.infectionCount.add(infection);
        }

        subject.infected = true;
        infected++;

        if (subject.infectable) {
            susceptible--;
        }
    }

    public static void simulateContact(ArrayList<Subject> subjects) {
        infectedCache.clear();
        susceptibleCache.clear();

        for (Subject subject : subjects) {
            if (subject.infected) {
                infectedCache.add(subject);
            } else if (subject.infectable) {
                susceptibleCache.add(subject);
            }
        }

        if (!infectedCache.isEmpty() && !susceptibleCache.isEmpty()) {
            double infectionThreshold = infectChance / 100.0;

            for (Subject subject : susceptibleCache) {
                if (Math.random() < infectionThreshold) {
                    Subject source = infectedCache.get((int) (Math.random() * infectedCache.size()));
                    subject.infect(source);
                }
            }
        }
    }

    // Method to update statistics when subjects change state
    public static void updateStats(Subject subject, String newState) {
        switch (newState) {
            case "recovered":
                infected--;
                immune++;
                break;
            case "susceptible_again":
                immune--;
                susceptible++;
                break;
        }
    }
}