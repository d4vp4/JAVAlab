import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class lab10_2 {
    private static final Logger logger = LogManager.getLogger(lab10_2.class);
    private static ResourceBundle bundle;
    private static Locale currentLocale;

    public static void main(String[] args) {

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        currentLocale = new Locale("en", "US");
        updateBundle();

        logger.info(bundle.getString("log.start"));

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    calculate(scanner);
                    break;
                case "2":
                    changeLanguage(scanner);
                    break;
                case "3":
                    System.out.println(bundle.getString("msg.bye"));
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void updateBundle() {
        // шлях "messages"
        bundle = ResourceBundle.getBundle("messages", currentLocale);
    }

    private static void printMenu() {
        System.out.println("\n" + bundle.getString("menu.title"));
        System.out.println(bundle.getString("menu.calc"));
        System.out.println(bundle.getString("menu.lang"));
        System.out.println(bundle.getString("menu.exit"));
        System.out.print("> ");
    }

    private static void calculate(Scanner scanner) {
        try {
            System.out.println(bundle.getString("msg.enter_nums"));
            String inputA = scanner.next();
            String inputB = scanner.next();
            scanner.nextLine();

            int a = Integer.parseInt(inputA);
            int b = Integer.parseInt(inputB);

            int sum = a + b;

            logger.info("Calculation: {} + {} = {}", a, b, sum);
            System.out.println(bundle.getString("msg.result") + sum);

        } catch (NumberFormatException e) {
            logger.error("Input error", e);
            System.out.println(bundle.getString("error.invalid"));
            scanner.nextLine();
        }
    }

    private static void changeLanguage(Scanner scanner) {
        System.out.println("1 - English, 2 - Українська");
        String choice = scanner.nextLine();
        if (choice.equals("2")) {
            currentLocale = new Locale("uk", "UA");
        } else {
            currentLocale = new Locale("en", "US");
        }
        updateBundle();
        logger.info("Language changed to: " + currentLocale);
    }
}