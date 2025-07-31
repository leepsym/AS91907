/**
 * Represents a single infection event in the simulation.
 * This class stores all relevant data about when and where an infection occurred.
 */
public class Infection {
    // The x,y coordinates where this infection took place
    int[] location;

    // The simulation round when this infection occurred
    int round;

    // The subject who became infected
    Subject subject;

    // The subject who caused this infection (source of transmission)
    Subject source;

    /**
     * Constructor to create a new infection record.
     *
     * @param location Array containing [x, y] coordinates of infection
     * @param round The simulation round when infection occurred
     * @param subject The subject who became infected
     * @param source The subject who transmitted the infection
     */
    public Infection(int[] location, int round, Subject subject, Subject source) {
        this.location = location;
        this.round = round;
        this.subject = subject;
        this.source = source;
    }
}