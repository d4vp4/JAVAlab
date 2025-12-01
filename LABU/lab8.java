import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

// Основний клас завдань
class SumTask extends RecursiveTask<Long> {
    private final int[] array;
    private final int start;
    private final int end;
    private static final int THRESHOLD = 20;
    public SumTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        } else {

            int mid = start + (end - start) / 2;

            SumTask leftTask = new SumTask(array, start, mid);
            SumTask rightTask = new SumTask(array, mid, end);


            leftTask.fork();
            long rightResult = rightTask.compute();
            long leftResult = leftTask.join();

            return leftResult + rightResult;
        }
    }
}

public class lab8 {
    public static void main(String[] args) {
        int size = 1_000_000;
        int[] array = new int[size];
        Random random = new Random();


        long expectedSum = 0;
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(101);
            expectedSum += array[i];
        }
        System.out.println("Масив згенеровано. Розмір: " + size);
        System.out.println("Очікувана сума (циклом): " + expectedSum);
        ForkJoinPool pool = new ForkJoinPool();

        SumTask task = new SumTask(array, 0, size);
        long startTime = System.currentTimeMillis();

        long result = pool.invoke(task);

        long endTime = System.currentTimeMillis();

        System.out.println("Сума через ForkJoin:     " + result);
        System.out.println("Час виконання: " + (endTime - startTime) + " мс");
    }
}
