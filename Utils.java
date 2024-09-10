import java.util.InputMismatchException;
import java.util.Scanner;

public class Utils {
	private static final Scanner scanner = new Scanner(System.in);

	public static int getValidIntegerInput(String prompt) {
		int value = 0;
        boolean valid = false;

        while (!valid) {
            try {
                System.out.print(prompt);
                value = scanner.nextInt();
                scanner.nextLine();
                if (value > 0) {
                    valid = true;
                } else {
                    System.out.println("Value must be greater than 0.");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.err.println("Invalid input. Please enter a whole number.");
            } catch (Exception e) {
				System.err.println("Unexpected error: " + e.getMessage());
			}
        }
		
        return value;
	}
	
	public static String getValidStringInput(String prompt) {
        System.out.print(prompt);
		return scanner.nextLine();
    }

}