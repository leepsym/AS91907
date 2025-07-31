public class Infection{
    int[] location;
    int round;
    Subject subject;
    Subject source;
    public Infection(int[] location, int round, Subject subject, Subject source){
        this.location = location;
        this.round = round;
        this.subject = subject;
        this.source = source;
    }
}