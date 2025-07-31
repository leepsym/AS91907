import java.util.ArrayList;

/**
 * Represents an individual subject (person/entity) in the virus simulation.
 * Each subject has a position, infection status, and maintains a history of
 * infections received and transmitted.
 */
public class Subject {
    // Reference to the simulation this subject belongs to
    final Simulation s;

    // Current health status flags
    public boolean infected = false;    // True if currently infected with virus
    public boolean infectable = true;   // True if susceptible to infection (not immune)

    // Infection history tracking for analysis and export
    ArrayList<Infection> infectionCount = new ArrayList<>();  // Infections this subject has given to others
    ArrayList<Infection> infectCount = new ArrayList<>();     // Infections this subject has received

    // Current position on the simulation grid [x, y]
    int[] location = new int[2];

    /**
     * Constructor that creates a new subject at a random location within the simulation bounds.
     *
     * @param s Reference to the simulation this subject belongs to
     */
    public Subject(Simulation s) {
        this.s = s;
        // Place subject at random coordinates within simulation boundaries
        location[0] = (int) (Math.random() * s.size[0]);
        location[1] = (int) (Math.random() * s.size[1]);
    }

    /**
     * Attempts to infect this subject with the virus from a source subject.
     * Only succeeds if the subject is currently susceptible (not infected and not immune).
     *
     * @param source The subject who is transmitting the infection
     */
    public void infect(Subject source) {
        if (!infected && infectable) {
            s.addInfection(location, this, source);
        }
    }

    /**
     * Infects this subject as a starting case (source is self).
     * Used to initialize the first infected subjects at simulation start.
     */
    public void startInfected() {
        s.addInfection(location, this, this);
    }

    /**
     * Updates the subject's infection and immunity status based on simulation timing.
     * Handles the progression from infected -> immune -> susceptible states
     * according to the simulation's duration parameters.
     */
    public void handleInfection() {
        // If currently infected, check if infection period has ended
        if (infected && !infectCount.isEmpty()) {
            // Calculate if enough rounds have passed since most recent infection
            if (s.round >= infectCount.getLast().round + s.infectDuration) {
                infected = false;     // No longer infected
                s.infected--;         // Decrease simulation's infected count
                infectable = false;   // Now immune (temporarily not susceptible)
                s.immune++;           // Increase simulation's immune count
            }
        }
        // If currently immune, check if immunity period has ended
        else if (!infectable && !infectCount.isEmpty()) {
            // Calculate if enough rounds have passed since recovery + immunity period
            if (s.round >= infectCount.getLast().round + s.infectDuration + s.immunityDuration) {
                infectable = true;    // Immunity has worn off, now susceptible again
                s.immune--;           // Decrease simulation's immune count
            }
        }
    }

    /**
     * Moves the subject to an adjacent location or keeps them in place.
     * Uses random movement with boundary checking to prevent subjects
     * from moving outside the simulation grid.
     */
    public void move() {
        // Generate random direction (0-4 for right, down, left, up, stay)
        int direction = (int) (Math.random() * 5);

        switch (direction) {
            case 0: // Move right
                if (location[0] < s.size[0] - 1) {
                    location[0]++;  // Move right if not at right boundary
                } else {
                    location[0]--;  // Bounce back if at boundary
                }
                break;

            case 1: // Move down
                if (location[1] < s.size[1] - 1) {
                    location[1]++;  // Move down if not at bottom boundary
                } else {
                    location[1]--;  // Bounce back if at boundary
                }
                break;

            case 2: // Move left
                if (location[0] > 0) {
                    location[0]--;  // Move left if not at left boundary
                } else {
                    location[0]++;  // Bounce back if at boundary
                }
                break;

            case 3: // Move up
                if (location[1] > 0) {
                    location[1]--;  // Move up if not at top boundary
                } else {
                    location[1]++;  // Bounce back if at boundary
                }
                break;

            case 4: // Don't move (stay in place)
                break;
        }
    }
}