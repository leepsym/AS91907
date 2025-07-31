import java.util.ArrayList;

public class Subject {
    final Simulation s;
    public boolean infected = false;
    public boolean infectable = true;

    ArrayList<Infection> infectionCount = new ArrayList<>();
    ArrayList<Infection> infectCount = new ArrayList<>();

    int[] location = new int[2];

    public Subject(Simulation s) {
        this.s = s;
        location[0] = (int) (Math.random() * s.size[0]);
        location[1] = (int) (Math.random() * s.size[1]);
    }

    public void infect(Subject source) {if (!infected && infectable) s.addInfection(location, this, source);}

    public void startInfected() {s.addInfection(location, this, this);}

    public void handleInfection() {
        if (infected && !infectCount.isEmpty()) {
            if (s.round >= infectCount.getLast().round + s.infectDuration) {
                infected = false;
                s.infected--;
                infectable = false;
                s.immune++;
            }
        } else if (!infectable && !infectCount.isEmpty()) {
            if (s.round >= infectCount.getLast().round + s.infectDuration + s.immunityDuration) {
                infectable = true;
                s.immune--;
            }
        }
    }

    public void move() {
        int direction = (int) (Math.random() * 5);

        switch (direction) {
            case 0: // Move right
                if (location[0] < s.size[0] - 1) {
                    location[0]++;
                }
                break;
            case 1: // Move down
                if (location[1] < s.size[1] - 1) {
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
            case 4: // Dont move
                break;
        }
    }
}