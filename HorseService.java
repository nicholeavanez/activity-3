import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalDouble;
import java.util.Random;
import java.time.Instant;
import java.util.stream.Collectors;

class HorseService {
	private final static float MINIMUM_SPEED_VALUE = 1.0f;
	private final static float MAXIMUM_SPEED_VALUE = 10.0f;

    public List<Horse> createHorses(int totalHorses) {
		Random random = new Random(Instant.now().toEpochMilli());
        List<Horse> horses = new ArrayList<>();
		
		// for testing and validation
		for (int currentHorse = 0; currentHorse < totalHorses; currentHorse++) {
            String name = "Horse " + (currentHorse + 1);
            String warCry = "War cry of horse " + (currentHorse + 1);
            int age = random.nextInt(51) + 1; //age restriction: 1-50
            Condition condition = random.nextDouble() < 0.5 ? Condition.HEALTHY : Condition.UNHEALTHY; //90% chance to create a healthy horse
			float generatedValue = 1.0f + (10.0f - 0.1f) * random.nextFloat();
			float truncatedValue = (float) Math.floor(generatedValue * 100) / 100;
			List<Float> speeds = List.of(truncatedValue); //random speed between 1.0-10.0

            horses.add(new Horse(name, warCry, condition, age, speeds));
		}
		
        /*for (int currentHorse = 0; currentHorse < totalHorses; currentHorse++) {

			System.out.println("Details for horse " + (currentHorse + 1));
            String name = Utils.getValidStringInput("Name: ");
            String warCry = Utils.getValidStringInput("War cry: ");
            int age = Utils.getValidIntegerInput("Age: ");
            Condition condition = random.nextBoolean() ? Condition.HEALTHY : Condition.UNHEALTHY;
            float generatedValue = MINIMUM_SPEED_VALUE + (MAXIMUM_SPEED_VALUE - 0.1f) * random.nextFloat();
            float truncatedValue = (float) Math.floor(generatedValue * 100) / 100;
            List<Float> speeds = List.of(truncatedValue); //random speed between 1.0-10.0

            horses.add(new Horse(name, warCry, condition, age, speeds));
        }*/
		
		horses.forEach(System.out::println);
		System.out.println();
		
        return horses;
    }

    public void assignAgeGroups(List<Horse> healthyHorses) {
        if (healthyHorses.isEmpty()) {
            return;
        }

        OptionalDouble averageAge = healthyHorses.stream()
            .mapToInt(Horse::getAge)
            .average();

        Map<Integer, Long> ageCounts = healthyHorses.stream()
			.collect(Collectors.groupingBy(Horse::getAge, Collectors.counting()));

		Optional<Long> maxCount = ageCounts.values().stream()
			.max(Long::compareTo);

		List<Integer> agesWithMaxCount = maxCount.map(count -> ageCounts.entrySet().stream()
				.filter(entry -> entry.getValue().equals(count))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList()))
			.orElse(Collections.emptyList());

		OptionalInt modeAge = agesWithMaxCount.size() == 1
			? OptionalInt.of(agesWithMaxCount.get(0))
			: OptionalInt.empty();

        Map<Predicate<Horse>, Consumer<Horse>> conditionActionMap = getPredicateConsumerMap(modeAge, averageAge);

        healthyHorses.forEach(horse -> conditionActionMap.entrySet().stream()
            .filter(entry -> entry.getKey().test(horse))
            .findFirst()
            .ifPresent(entry -> entry.getValue().accept(horse)));
		
		testAssignedAgeGroups(averageAge, modeAge, healthyHorses);
    }

    private static Map<Predicate<Horse>, Consumer<Horse>> getPredicateConsumerMap(OptionalInt modeAge, OptionalDouble averageAge) {
		Predicate<Horse> beginnerCondition = horse -> horse.getAge() != modeAge.orElse(0) && horse.getAge() < (int) averageAge.getAsDouble();
        Predicate<Horse> intermediateCondition = horse -> horse.getAge() != modeAge.orElse(0) && horse.getAge() > (int) averageAge.getAsDouble();
        Predicate<Horse> advancedCondition = horse -> horse.getAge() == modeAge.orElse(0) || horse.getAge() == (int) averageAge.getAsDouble();

		Consumer<Horse> assignBeginner = horse -> horse.setAgeGroup(AgeGroup.BEGINNER);
        Consumer<Horse> assignIntermediate = horse -> horse.setAgeGroup(AgeGroup.INTERMEDIATE);
		Consumer<Horse> assignAdvanced = horse -> horse.setAgeGroup(AgeGroup.ADVANCED);

        return Map.of(
            beginnerCondition, assignBeginner,
			intermediateCondition, assignIntermediate,
			advancedCondition, assignAdvanced
        );
    }

    private static void testAssignedAgeGroups(OptionalDouble averageAge, OptionalInt modeAge, List<Horse> healthyHorses){
		averageAge.ifPresentOrElse(
            average -> System.out.printf("Computed average age: %d\n", (int) average),
            () -> System.out.println("No average value for age computed")
        );

        modeAge.ifPresentOrElse(
            mode -> System.out.println("Computed mode age: " + mode),
            () -> System.out.println("No mode value for age computed")
        );
		
		String eligibleHorseNames = healthyHorses.stream()
            .map(Horse::getName)
            .collect(Collectors.joining(", "));

        System.out.println(healthyHorses.size() + " Qualified horses: " + eligibleHorseNames);
        healthyHorses.forEach(System.out::println);
        System.out.println();
	}
}