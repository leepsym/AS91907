package Main;

import Graphics.Visualisation;
import Graphics.Window;

import java.util.ArrayList;

public class Simulation {
    // Parameters

    static int populationSize = 30; // Number of subjects in the simulation
    static int startingInfected = 1; // Number of beginning infected subjects
    static int infectChance = 70; // Percent chance of infection upon contact
    static int infectDuration = 10; // Rounds that subject stays infected for
    static int immunityDuration = 10; // Rounds that subject stays immune for
    static int maxRuntime = 200; // Maximum rounds in the simulation
    public static int[] size = new int[2]; // Area that the simulation will run in

    // Statistics
    static int round = 0;
    static int infected = 0;
    static int immune = 0;
    static ArrayList<Infection> infections = new ArrayList<>();
    public static ArrayList<Subject> population = new ArrayList<>();

    // Other
    static Window window = new Window(1250, 750);
    static Visualisation visualisation = new Visualisation();


    public static void main (String[] args) {
        size[0] = 100;
        size[1] = 100;

        for (int i = 0; i < populationSize; i++) {
            population.add(new Subject());
        }

        for (int i = 0; i < startingInfected; i++) {
            population.get(i).startInfected();
        }


        //while (infected < populationSize && round < maxRuntime) {
        //    simulateRound();
        //}
    }

    public static void userParameters() {

    }

    public static void simulateRound() {
        ArrayList<Subject>[][] board = new ArrayList[size[0]][size[1]];

        for (Subject current : population) {
            current.move();
            current.handleInfection();
            board[current.location[0]][current.location[1]].add(current);
        }

        for (int i = 0; i <= size[0]; i++) {
            for (int j = 0; j <= size[1]; j++) {
                simulateContact(board[i][j]);
            }
        }
    }

    public static void addInfection(int[] location, Subject subject, Subject source) {
        Infection infection = new Infection(location, round, subject, source);
        infections.add(infection);

        subject.infectCount.add(infection);
        source.infectionCount.add(infection);

        infected++;
    }

    public static void simulateContact(ArrayList<Subject> subjects) {
        ArrayList<Subject> infected = new ArrayList<>();
        ArrayList<Subject> uninfected = new ArrayList<>();

        for (Subject subject : subjects) {
            if (subject.infected) {
                infected.add(subject);
            } else {
                uninfected.add(subject);
            }
        }

        if (!infected.isEmpty()) {
            for (Subject subject : uninfected) {
                if ((int) (Math.random() * 10) >= infectChance) {
                    subject.infect(infected.get((int) (Math.random() * infected.size())));
                }
            }
        }
    }
}
