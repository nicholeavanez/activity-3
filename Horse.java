import java.util.List;

class Horse {
    private final String name;
    private final String warCry;
    private final Condition condition;
    private final int age;
    private AgeGroup ageGroup;
    private List<Float> speed;
    private float distanceTravelled;

    public Horse(String name, String warCry, Condition condition, int age, List<Float> speed) {
        this.name = name;
        this.warCry = warCry;
        this.condition = condition;
        this.age = age;
		this.speed = speed;
    }

    // getters and setters
    public String getName() {
        return name;
    }

    public String getWarCry() {
        return warCry;
    }

    public Condition getCondition() {
        return condition;
    }

    public int getAge() {
        return age;
    }

    public AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public List<Float> getSpeed() {
        return speed;
    }

    public float getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }
	
	public void setSpeed(List<Float> speed) {
		this.speed = speed;
	}

    public void setDistanceTravelled(float distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    @Override
    public String toString() {
        return String.format(
            "Horse{name='%s', warCry='%s', age=%d, condition=%s, ageGroup=%s, speed=%s}",
            name,
            warCry,
            age,
            condition,
            ageGroup,
			speed
        );
    }
}
