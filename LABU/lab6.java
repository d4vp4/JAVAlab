import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


class Translator {
    private Map<String, String> dictionary;

    public Translator() {
        dictionary = new HashMap<>();
    }
    public void addWordPair(String englishWord, String ukrainianWord) {
        dictionary.put(englishWord.toLowerCase().trim(), ukrainianWord.toLowerCase().trim());
    }
    public String translatePhrase(String phrase) {
        StringBuilder result = new StringBuilder();
        String[] words = phrase.split(" ");

        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z]", "");
            String searchKey = cleanWord.toLowerCase();

            if (dictionary.containsKey(searchKey)) {
                result.append(dictionary.get(searchKey)).append(" ");
            } else {
                result.append(word).append("* ");
            }
        }
        return result.toString().trim();
    }

    public void showDictionary() {
        if (dictionary.isEmpty()) {
            System.out.println("Словник порожній!");
            return;
        }
        System.out.println("\n=== Вміст словника ===");
        System.out.printf("%-15s | %-15s%n", "English", "Ukrainian");
        System.out.println("--------------------------------");

        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            System.out.printf("%-15s | %-15s%n", entry.getKey(), entry.getValue());
        }
        System.out.println("--------------------------------");
    }
}

// Головний клас
public class lab6 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        Translator myTranslator = new Translator();
        myTranslator.addWordPair("hello", "привіт");
        myTranslator.addWordPair("world", "світ");
        myTranslator.addWordPair("java", "джава");
        myTranslator.addWordPair("student", "студент");
        myTranslator.addWordPair("code", "код");

        while (true) {
            System.out.println("\n--- МЕНЮ ---");
            System.out.println("1. Перекласти фразу (ENG -> UKR)");
            System.out.println("2. Додати нове слово");
            System.out.println("3. Показати весь словник");
            System.out.println("4. Вихід");
            System.out.print("Ваш вибір: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("Введіть фразу англійською:");
                    String input = scanner.nextLine();
                    System.out.println("Переклад: " + myTranslator.translatePhrase(input));
                    break;
                case "2":
                    System.out.print("Введіть слово англійською: ");
                    String eng = scanner.nextLine();
                    System.out.print("Введіть переклад українською: ");
                    String ukr = scanner.nextLine();
                    myTranslator.addWordPair(eng, ukr);
                    System.out.println("Успішно додано!");
                    break;
                case "3":
                    myTranslator.showDictionary();
                    break;
                case "4":
                    System.out.println("Роботу завершено.");
                    return;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }
}