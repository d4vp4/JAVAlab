import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


class Address {
    private String street;
    private String house;
    private String flat;

    public Address(String street, String house, String flat) {
        this.street = street;
        this.house = house;
        this.flat = flat;
    }

    @Override
    public String toString() {
        return "вул. " + street + ", буд. " + house + ", кв. " + flat;
    }
}


class Student {
    private String lastName;
    private String firstName;
    private LocalDate birthDate;
    private String phone;
    private Address address;

    public Student(String lastName, String firstName, LocalDate birthDate, String phone, Address address) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
    }


    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return String.format("| %-15s | %-15s | %-10s | %-13s | %s",
                lastName, firstName, birthDate.format(formatter), phone, address);
    }
}


public class laba2 {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Student> journal = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== ЖУРНАЛ КУРАТОРА ===");

        boolean running = true;
        while (running) {
            System.out.println("\nМеню:");
            System.out.println("1. Додати запис про студента");
            System.out.println("2. Показати всі записи");
            System.out.println("3. Вихід");
            System.out.print("Ваш вибір: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addStudentEntry();
                    break;
                case "2":
                    displayJournal();
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private static void addStudentEntry() {
        System.out.println("\n--- Додавання нового студента ---");


        String lastName = inputWithValidation("Прізвище", "^[A-Za-zА-Яа-яІіЇїЄє'-]+$");
        String firstName = inputWithValidation("Ім'я", "^[A-Za-zА-Яа-яІіЇїЄє'-]+$");

        LocalDate birthDate = inputDate("Дата народження (рррр-мм-дд)");

        String phone = inputWithValidation("Телефон (наприклад, 0991234567)", "^\\d{10,12}$");

        System.out.println("--- Адреса ---");
        String street = inputWithValidation("Вулиця", "^[A-Za-zА-Яа-яІіЇїЄє0-9\\s.-]+$");
        String house = inputWithValidation("Будинок", "^[0-9A-Za-z/-]+$");
        String flat = inputWithValidation("Квартира", "^[0-9]+$");


        Address address = new Address(street, house, flat);
        Student student = new Student(lastName, firstName, birthDate, phone, address);

        journal.add(student);
        System.out.println(">> Запис успішно додано!");
    }

    private static void displayJournal() {
        if (journal.isEmpty()) {
            System.out.println("\nЖурнал порожній.");
        } else {
            System.out.println("\n--- СПИСОК СТУДЕНТІВ ---");
            for (Student s : journal) {
                System.out.println(s.toString());
            }
        }
    }


    private static String inputWithValidation(String fieldName, String regex) {
        while (true) {
            System.out.print("Введіть " + fieldName + ": ");
            String input = scanner.nextLine().trim();

            if (Pattern.matches(regex, input)) {
                return input;
            } else {
                System.out.println("Помилка! Некоректний формат поля '" + fieldName + "'. Спробуйте ще раз.");
            }
        }
    }


    private static LocalDate inputDate(String fieldName) {
        while (true) {
            System.out.print("Введіть " + fieldName + ": ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input); // Формат за замовчуванням YYYY-MM-DD
            } catch (DateTimeParseException e) {
                System.out.println("Помилка! Формат дати має бути РРРР-ММ-ДД (наприклад, 2005-08-24).");
            }
        }
    }
}