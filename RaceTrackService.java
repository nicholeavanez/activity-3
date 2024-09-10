import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

class RacetrackService {
    private final Racetrack racetrack;
	private final static float MINIMUM_SPEED_ACCELERATION_VALUE = 5.0f;
	private final static float MAXIMUM_SPEED_ACCELERATION_VALUE = 10.0f;

    public RacetrackService(int totalHorses) {
        HorseService horseService = new HorseService();
        this.racetrack = new Racetrack(horseService.createHorses(totalHorses));
        horseService.assignAgeGroups(racetrack.getHealthyHorses());
    }

    public void startRace(int trackLength) {
		List<Horse> healthyHorses = racetrack.getHealthyHorses();
		List<Horse> rankings = new ArrayList<>();

		if (healthyHorses.isEmpty()) {
			return;
		}
		initiateAndAnnounceRace(healthyHorses, trackLength, rankings);
		displayRaceRankings(rankings);
	}

	private static void initiateAndAnnounceRace(List<Horse> group, int trackLength, List<Horse> rankings) {
		System.out.println("\nStarting race");
		group.parallelStream().forEach(horse -> simulateHorseRace(horse, trackLength, rankings));
	}

	private static void simulateHorseRace(Horse horse, int trackLength, List<Horse> horseRankings) {
		horse.setDistanceTravelled(0);
		Random random = new Random();
		AtomicInteger iteration = new AtomicInteger(1);
		List<Float> speeds = new ArrayList<>(horse.getSpeed());
		DateTimeFormatter dateTimeWith5Miliseconds = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS");

		while (horse.getDistanceTravelled() < trackLength) {
			float speed = speeds.get(Math.min(iteration.get() - 1, speeds.size() - 1));
			horse.setDistanceTravelled(horse.getDistanceTravelled() + speed);
			float remainingDistance = Float.parseFloat(String.format("%.2f", trackLength - horse.getDistanceTravelled()));

			System.out.println("[" + LocalDateTime.now().format(dateTimeWith5Miliseconds)
					+ "] " + horse.getName() + " ran " + speed + "m with " + Math.max(remainingDistance, 0) + "m remaining.");

			if (horse.getDistanceTravelled() >= trackLength) {
				horse.setDistanceTravelled(trackLength);
				synchronized (horseRankings) {
					horseRankings.add(horse);
					System.out.println("\t[" + LocalDateTime.now().format(dateTimeWith5Miliseconds)
							+ "] " + horse.getName() + " finished. " + horse.getWarCry());
				}
				break;
			}

			Predicate<Horse> intermediateCondition = h -> h.getAgeGroup().equals(AgeGroup.INTERMEDIATE) && iteration.get() >= 4;
			Predicate<Horse> advancedCondition = h -> h.getAgeGroup().equals(AgeGroup.ADVANCED) && iteration.get() >= 2;

			Consumer<Float> increaseSpeedAction = currentSpeed -> {
				float truncatedValue = Float.parseFloat(String.format("%.2f", (float) Math.floor((currentSpeed * 1.10f) * 100) / 100)); // +10% to the next speed
				speeds.add(truncatedValue);
			};
			
			Consumer<Float> addRandomSpeedAction = currentSpeed -> {
				float randomSpeedAddition = MINIMUM_SPEED_ACCELERATION_VALUE + (MAXIMUM_SPEED_ACCELERATION_VALUE - MINIMUM_SPEED_ACCELERATION_VALUE) * random.nextFloat(); // Range 5.0-10.0
				float truncatedValue = Float.parseFloat(String.format("%.2f", ((float) Math.floor(randomSpeedAddition * 100) / 100) + currentSpeed));
				speeds.add(truncatedValue);
			};

			if (intermediateCondition.test(horse)) {
				updateHorseSpeed(horse, speeds, increaseSpeedAction);
			} else if (advancedCondition.test(horse)) {
				updateHorseSpeed(horse, speeds, addRandomSpeedAction);
			} else if (!horse.getAgeGroup().equals(AgeGroup.BEGINNER)) {
				speeds.add(speeds.get(speeds.size() - 1));
				horse.setSpeed(speeds);
			}
			
			iteration.getAndIncrement();
		}
	}
	
	private static void updateHorseSpeed(Horse horse, List<Float> speeds, Consumer<Float> action) {
		float currentSpeed = speeds.get(speeds.size() - 1);
		action.accept(currentSpeed);
		horse.setSpeed(speeds);
	}

	private static void displayRaceRankings(List<Horse> rankings) {
		System.out.println("\nRankings:");
		rankings.forEach(horse -> System.out.println(horse.getName() + ": " + horse.getSpeed()));
	}

	public Racetrack getRaceTrack() {
		return this.racetrack;
	}
}
