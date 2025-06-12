package Main;

import java.util.ArrayList;

public class Subject {
    public boolean infected = false;
    public boolean infectable = true;

    ArrayList<Infection> infectionCount = new ArrayList<>();
    ArrayList<Infection> infectCount = new ArrayList<>();

    int[] location = new int[2];

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
        if (infected && !infectCount.isEmpty()) {
            // Check if infection duration has passed
            if (Simulation.round >= infectCount.getLast().round() + Simulation.infectDuration) {
                infected = false;
                Simulation.infected--;
                infectable = false;
                Simulation.immune++;
            }
        } else if (!infectable && !infectCount.isEmpty()) {
            // Check if immunity duration has passed
            if (Simulation.round >= infectCount.getLast().round() + Simulation.infectDuration + Simulation.immunityDuration) {
                infectable = true;
                Simulation.immune--;
            }
        }
    }

    public void move() {
        int direction = (int) (Math.random() * 4);

        switch (direction) {
            case 0: // Move right
                if (location[0] < Simulation.size[0] - 1) {
                    location[0]++;
                }
                break;
            case 1: // Move down
                if (location[1] < Simulation.size[1] - 1) {
                    location[1]++;
                }
                break;
            case 2: // Move left
                if (location[0] > 0) {
                    location[0]--;
                }
                break;
            case 3: // Move up
                if (location[1] > 0) {
                    location[1]--;
                }
                break;
        }
    }
}