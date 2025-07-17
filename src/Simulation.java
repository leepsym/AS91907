import java.util.ArrayList;

public class Simulation extends Thread{
    // Base Parameters
    public int populationSize;
    public int startingInfected;
    public int infectChance;
    public int infectDuration;
    public int immunityDuration;
    public int maxRuntime;

    public int[] size = new int[2];

    // Statistics trackers
    public int round = 0;
    public int infected = 0;
    public int immune = 0;

    // Storage
    public ArrayList<Infection> infections = new ArrayList<>();
    public ArrayList<Subject> population = new ArrayList<>();
    public ArrayList<Subject>[][] board;

    // Other
    Visualisation v;

    public Simulation(int sw, int sh,int ps, int si, int ic, int id, int iD, int mr) {
        super();

        size[0] = sw;
        size[1] = sh;
        populationSize = ps;
        startingInfected = si;
        infectChance = ic;
        infectDuration = id;
        immunityDuration = iD;
        maxRuntime = mr;


        v = new Visualisation(this);
        initializeBoard();

        // Initialize population
        for (int i = 0; i < populationSize; i++) {
            population.add(new Subject(this));
        }

        // Set initial infected
        for (int i = 0; i < startingInfected; i++) {
            population.get(i).startInfected();
        }
    }

    // Loads all positions into first frame of
    private void initializeBoard() {
        board = new ArrayList[size[0]][size[1]];
        for (int i = 0; i < size[0]; i++) {
            for (int j = 0; j < size[1]; j++) {
                board[i][j] = new ArrayList<>();
            }
        }
    }

    public void simulateRound() {
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
        v.pixelQueue.clear();
        v.visualiseRound();
        v.simContentPane.repaint();
        v.statsContentPane.repaint();
    }

    public void addInfection(int[] location, Subject subject, Subject source) {
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

    public void simulateContact(ArrayList<Subject> subjects) {
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

    @Override
    public void run() {
        System.out.println(round != maxRuntime);
        while (infected > 0 && infected < populationSize - immune && round != maxRuntime) {
            System.out.println(round);
            simulateRound();
            round++;
        }
    }
}