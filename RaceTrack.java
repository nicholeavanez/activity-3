import java.util.List;
import java.util.stream.Collectors;

class Racetrack {
    private final List<Horse> horses;
    private int length;

    public Racetrack(List<Horse> horses) {
        this.horses = horses;
    }

    public List<Horse> getHealthyHorses() {
        return horses.stream()
			.filter(horse -> horse.getCondition() == Condition.HEALTHY)
			.collect(Collectors.toList());
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
