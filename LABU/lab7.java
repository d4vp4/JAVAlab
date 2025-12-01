import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.IntStream;

public class lab7 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть число n: ");
        int n = scanner.nextInt();

        // Основний ланцюжок Stream API
        Integer result = IntStream.rangeClosed(2, n)
                .filter(lab7::isPrime)
                .boxed()
                .max(Comparator.comparingLong(lab7::countZerosInBinary))
                .orElse(null);

        if (result != null) {
            System.out.println("Результат: " + result);
            System.out.println("У двійковій системі: " + Integer.toBinaryString(result));
            System.out.println("Кількість нулів: " + countZerosInBinary(result));
        } else {
            System.out.println("Простих чисел не знайдено.");
        }
    }


    public static boolean isPrime(int number) {
        if (number <= 1) return false;

        return IntStream.rangeClosed(2, (int) Math.sqrt(number))
                .noneMatch(divisor -> number % divisor == 0);
    }


    public static long countZerosInBinary(int number) {
        String binaryString = Integer.toBinaryString(number);
        return binaryString.chars()
                .filter(ch -> ch == '0')
                .count();
    }
}
