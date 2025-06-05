import java.util.ArrayList;

public class Simulation {
    static int simulationXSize;
    static int simulationYSize;
    static int populationSize;
    static int startingInfected;
    static int infectChance;
    static int infectDuration;
    static int immunityDuration;
    static int maxRuntime;
    static int[] size;

    // Statistics
    static int round;
    static int infected = startingInfected;
    static int immune = 0;

    static ArrayList<Infection> infections = new ArrayList<>();
    static ArrayList<Subject> population = new ArrayList<>();


    public static void main (String[] args) {


        while (infected < populationSize && round < maxRuntime) {
            simulateRound();
        }
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
