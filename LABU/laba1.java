import java.util.Scanner;

public class laba1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введіть число n: ");
        if (scanner.hasNextInt()) {
            int n = scanner.nextInt();

            int resultNumber = 0;
            int maxOnesCount = -1;


            for (int i = 2; i <= n; i++) {


                if (isPrime(i)) {

                    int currentOnes = countBinaryOnes(i);


                    if (currentOnes > maxOnesCount) {
                        maxOnesCount = currentOnes;
                        resultNumber = i;
                    }
                }
            }

            if (maxOnesCount != -1) {
                System.out.println("Знайдене просте число: " + resultNumber);
                System.out.println("Його двійкова форма: " + Integer.toBinaryString(resultNumber));
                System.out.println("Кількість одиниць: " + maxOnesCount);
            } else {
                System.out.println("Простих чисел не знайдено.");
            }

        } else {
            System.out.println("Будь ласка, введіть ціле число.");
        }

        scanner.close();
    }


    public static boolean isPrime(int num) {
        if (num < 2) return false;

        for (int i = 2; i * i <= num; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }


    public static int countBinaryOnes(int num) {

        String binaryString = Integer.toBinaryString(num);

        int count = 0;

        for (char c : binaryString.toCharArray()) {
            if (c == '1') {
                count++;
            }
        }
        return count;
    }
}