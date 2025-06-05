import java.util.ArrayList;

public class Subject {
    boolean infected = false; // If the subject is infected
    boolean infectable = true; // If the subject is immune or not

    ArrayList<Infection> infectionCount; // Times this subject has infected another subject
    ArrayList<Infection> infectCount; // Times this subject has been infected

    int[] location = new int[2]; // Where the subject is located

    public void infect(Subject source) {
        if (!infected && infectable) Simulation.addInfection(location, this, source); Simulation.infected++;
    }

    public void handleInfection() {
        if (infected) {
            if (infectCount.getLast().round() + Simulation.infectDuration > Simulation.round) {
                infected = false;
                Simulation.infected--;
                infectable = false;
                Simulation.immune++;
            }
        } else if (!infectable) {
            if (infectCount.getLast().round() + Simulation.infectDuration + Simulation.immunityDuration > Simulation.round)    infectable = true; Simulation.immune--;
        }
    }

    public void move() {
        int direction = (int) (Math.random() * 4);

        switch (direction) {
            case 0:
                if (!(location[0] > Simulation.size[0])) {
                    location[0]++;
                } else {
                    location[0]--;
                }
            case 1:
                if (!(location[1] > Simulation.size[1])) {
                    location[1]++;
                } else {
                    location[1]--;
                }
            case 2:
                if (!(location[0] < -Simulation.size[0])) {
                    location[0]++;
                } else {
                    location[0]--;
                }
            case 3:
                if (!(location[0] < -Simulation.size[0])) {
                    location[0]++;
                } else {
                    location[0]--;
                }
        }
    }
}
