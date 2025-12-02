import java.lang.reflect.Field;
import java.util.Scanner;

public class lab10_1 {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        String s1 = "Hello World";
        System.out.println("Введіть рядок для s2 (динамічний):");
        String s2 = new String(scanner.nextLine());

        System.out.println("\n--- До зміни ---");
        System.out.println("s1 (літерал): " + s1);
        System.out.println("s2 (динамічний): " + s2);
        System.out.println("\nВведіть текст для заміни:");
        String replacement = scanner.nextLine();
        modifyString(s1, replacement);
        modifyString(s2, replacement);

        System.out.println("\n--- Після зміни ---");
        System.out.println("s1: " + s1);
        System.out.println("s2: " + s2);

        System.out.println("Нова змінна s3 = \"Hello World\": " + "Hello World");
    }

    private static void modifyString(String target, String newContent) throws Exception {
        // Отримуємо доступ до приватного поля 'value'
        Field valueField = String.class.getDeclaredField("value");
        valueField.setAccessible(true); // Вимикаємо перевірку доступу

        Object valueArray = valueField.get(target);

        // Обробка залежно від версії Java (byte[] для Java 9+, char[] для старих)
        if (valueArray instanceof byte[]) {
            valueField.set(target, newContent.getBytes());
        } else if (valueArray instanceof char[]) {
            valueField.set(target, newContent.toCharArray());
        }
    }
}