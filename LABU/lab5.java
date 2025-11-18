import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class SecretOutputStream extends FilterOutputStream {
    private final int key;

    public SecretOutputStream(OutputStream out, int key) {
        super(out);
        this.key = key;
    }

    @Override
    public void write(int b) throws IOException {
        super.write(b + key);
    }
}

class SecretInputStream extends FilterInputStream {
    private final int key;

    public SecretInputStream(InputStream in, int key) {
        super(in);
        this.key = key;
    }

    @Override
    public int read() throws IOException {
        int b = super.read();
        if (b == -1) return -1;
        return (b - key);
    }
}

class FileManager {
    public String findLineWithMaxWords(String filePath) throws IOException {
        String maxLine = "";
        int maxCount = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.trim().split("\\s+");
                if (words.length > maxCount) {
                    maxCount = words.length;
                    maxLine = line;
                }
            }
        }
        return maxLine + " (Кількість слів: " + maxCount + ")";
    }

    public void encryptFile(String srcPath, String destPath, int key) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(srcPath));
             SecretOutputStream out = new SecretOutputStream(new FileOutputStream(destPath), key)) {
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
        }
    }

    public void decryptFile(String srcPath, String destPath, int key) throws IOException {
        try (SecretInputStream in = new SecretInputStream(new FileInputStream(srcPath), key);
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destPath))) {
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
        }
    }

    public void saveObject(Object obj, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
        }
    }

    public Object loadObject(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return ois.readObject();
        }
    }
}

class UrlAnalyzer {
    public Map<String, Integer> analyzeTags(String urlString) throws IOException {
        Map<String, Integer> tagCounts = new HashMap<>();
        URL url = new URL(urlString);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            Pattern pattern = Pattern.compile("<([a-zA-Z][a-zA-Z0-9]*)\\b[^>]*>");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String tag = matcher.group(1).toLowerCase();
                    tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
                }
            }
        }
        return tagCounts;
    }

    public void printSortedByTag(Map<String, Integer> tags) {
        System.out.println("\n--- Теги лексикографічно (A-Z) ---");
        tags.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }

    public void printSortedByFrequency(Map<String, Integer> tags) {
        System.out.println("\n--- Теги за частотою (зростання) ---");
        tags.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }
}

public class lab5 {
    private static final Scanner scanner = new Scanner(System.in);
    private static final FileManager fileManager = new FileManager();
    private static final UrlAnalyzer urlAnalyzer = new UrlAnalyzer();

    public static void main(String[] args) {
        System.out.println("=== ЛАБОРАТОРНА РОБОТА №5: I/O Streams & Networking ===");

        while (true) {
            System.out.println("\nМЕНЮ:");
            System.out.println("1. Знайти рядок з макс. кількістю слів у файлі");
            System.out.println("2. Зашифрувати файл");
            System.out.println("3. Дешифрувати файл");
            System.out.println("4. Аналіз тегів URL (+ збереження результату)");
            System.out.println("5. Завантажити збережений результат аналізу");
            System.out.println("0. Вихід");
            System.out.print("Ваш вибір: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Введіть шлях до файлу: ");
                        String path = scanner.nextLine();
                        try {
                            System.out.println("Результат: " + fileManager.findLineWithMaxWords(path));
                        } catch (FileNotFoundException e) {
                            System.out.println("ПОМИЛКА: Файл не знайдено. Перевірте шлях і спробуйте ще раз.");
                        }
                        break;
                    case "2":
                        processEncryption(true);
                        break;
                    case "3":
                        processEncryption(false);
                        break;
                    case "4":
                        processUrlAnalysis();
                        break;
                    case "5":
                        processLoadResult();
                        break;
                    case "0":
                        System.out.println("До побачення!");
                        return;
                    default:
                        System.out.println("Невірний вибір. Спробуйте 1-5.");
                }
            } catch (Exception e) {
                System.out.println("КРИТИЧНА ПОМИЛКА: " + e.getMessage());
            }
        }
    }

    private static int readIntSafe() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Помилка! Введіть ціле число, а не текст: ");
            }
        }
    }

    private static void processEncryption(boolean encrypt) {
        System.out.print("Введіть вхідний файл: ");
        String src = scanner.nextLine();
        System.out.print("Введіть вихідний файл: ");
        String dest = scanner.nextLine();

        System.out.print("Введіть числовий ключ (ціле число): ");
        int key = readIntSafe();

        try {
            if (encrypt) {
                fileManager.encryptFile(src, dest, key);
                System.out.println("Файл успішно зашифровано!");
            } else {
                fileManager.decryptFile(src, dest, key);
                System.out.println("Файл успішно дешифровано!");
            }
        } catch (IOException e) {
            System.out.println("Помилка при роботі з файлом: " + e.getMessage());
        }
    }

    private static void processUrlAnalysis() {
        System.out.print("Введіть URL (наприклад, https://google.com): ");
        String url = scanner.nextLine();

        try {
            System.out.println("Завантаження та аналіз...");
            Map<String, Integer> results = urlAnalyzer.analyzeTags(url);

            urlAnalyzer.printSortedByTag(results);
            urlAnalyzer.printSortedByFrequency(results);

            System.out.print("\nЗберегти результат у файл? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.print("Введіть назву файлу для збереження: ");
                String savePath = scanner.nextLine();
                fileManager.saveObject(results, savePath);
                System.out.println("Об'єкт серіалізовано та збережено.");
            }
        } catch (IOException e) {
            System.out.println("Помилка з'єднання або запису: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void processLoadResult() {
        System.out.print("Введіть назву файлу з даними: ");
        String path = scanner.nextLine();

        try {
            Map<String, Integer> loadedData = (Map<String, Integer>) fileManager.loadObject(path);
            System.out.println("Завантажено даних: " + loadedData.size() + " тегів.");
            urlAnalyzer.printSortedByFrequency(loadedData);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не знайдено.");
        } catch (IOException e) {
            System.out.println("Помилка читання файлу.");
        } catch (ClassNotFoundException e) {
            System.out.println("Помилка формату даних (клас не знайдено).");
        }
    }
}