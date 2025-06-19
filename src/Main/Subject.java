package Main;

import java.util.ArrayList;

public class Subject {
    public boolean infected = false;
    public boolean infectable = true;

    ArrayList<Infection> infectionCount = new ArrayList<>();
    ArrayList<Infection> infectCount = new ArrayList<>();

    int[] location = new int[2];
    private int recoveryRound = -1;
    private int reinfectionRound = -1;

    public Subject() {
        location[0] = (int) (Math.random() * Simulation.size[0]);
        location[1] = (int) (Math.random() * Simulation.size[1]);
    }

    public void infect(Subject source) {
        if (!infected && infectable) {
            Simulation.addInfection(location, this, source);
        }
    }

    public void startInfected() {
        Simulation.addInfection(location, this, this);
    }

    public void handleInfection() {
        if (infected) {
            if (recoveryRound == -1) {
                recoveryRound = Simulation.round + Simulation.infectDuration;
                reinfectionRound = recoveryRound + Simulation.immunityDuration;
            }

            if (Simulation.round >= recoveryRound) {
                infected = false;
                infectable = false;
                recoveryRound = -1;
                Simulation.updateStats(this, "recovered");
            }
        } else if (!infectable) {
            if (Simulation.round >= reinfectionRound) {
                infectable = true;
                reinfectionRound = -1;
                Simulation.updateStats(this, "susceptible_again");
            }
        }
    }

    public void move() {
        int direction = (int) (Math.random() * 5);

        switch (direction) {
            case 0:
                if (location[0] < Simulation.size[0] - 1) location[0]++;
                break;
            case 1:
                if (location[1] < Simulation.size[1] - 1) location[1]++;
                break;
            case 2:
                if (location[0] > 0) location[0]--;
                break;
            case 3:
                if (location[1] > 0) location[1]--;
                break;
        }
    }
}