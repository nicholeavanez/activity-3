public class Exercise3 {
	private final static int MINIMUM_HORSES = 1;
	private final static int MINIMUM_HEALTHY_HORSES = 2;
	
    public static void main(String[] args) {
        RacetrackService racetrackService = null;
        boolean insufficientHorses = true;
        do {
            int horses = Utils.getValidIntegerInput("Number of horses: ");

            if (horses <= MINIMUM_HORSES) {
                System.out.println("You need at least 2 horses to race.");
                continue;
            }

            racetrackService = new RacetrackService(horses);
			int healthyHorses = racetrackService.getRaceTrack().getHealthyHorses().size();
			
            if (healthyHorses < MINIMUM_HEALTHY_HORSES) {
                System.out.println("Not enough healthy horses.");
                continue;
            }
			
            insufficientHorses = false;
        } while (insufficientHorses);

        int trackLength = Utils.getValidIntegerInput("Race track length: ");
        racetrackService.startRace(trackLength);
    }
}