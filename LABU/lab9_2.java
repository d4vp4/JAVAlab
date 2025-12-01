import java.util.concurrent.atomic.AtomicInteger;

// Клас Кільцевий Буфер
class RingBuffer {
    private final String[] buffer;
    private int head = 0; // Індекс для читання
    private int tail = 0; // Індекс для запису
    private int count = 0; // Кількість елементів
    private final int capacity;

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new String[capacity];
    }

    // Метод додавання (Producer)
    public synchronized void put(String item) {
        while (count == capacity) {
            try {
                wait(); // Чекаємо, поки з'явиться місце
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        buffer[tail] = item;
        tail = (tail + 1) % capacity; // Кільцевий зсув
        count++;
        notifyAll(); // Повідомляємо, що з'явилися дані
    }

    // Метод читання (Consumer)
    public synchronized String take() {
        while (count == 0) {
            try {
                wait(); // Чекаємо, поки з'являться дані
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        String item = buffer[head];
        head = (head + 1) % capacity; // Кільцевий зсув
        count--;
        notifyAll(); // Повідомляємо, що з'явилося місце
        return item;
    }
}

public class lab9_2 {
    public static void main(String[] args) {
        // Створюємо два буфери
        RingBuffer buffer1 = new RingBuffer(10);
        RingBuffer buffer2 = new RingBuffer(10);

        // --- 1. П'ять потоків-генераторів ---
        for (int i = 1; i <= 5; i++) {
            int threadNum = i;
            Thread generator = new Thread(() -> {
                while (true) {
                    String msg = "Потік №" + threadNum + " згенерував повідомлення";
                    buffer1.put(msg);
                    try { Thread.sleep(50); } catch (InterruptedException e) {}
                }
            });
            generator.setDaemon(true); // Робимо демоном
            generator.start();
        }

        // --- 2. Два потоки-перекладачі ---
        for (int i = 1; i <= 2; i++) {
            int threadNum = i;
            Thread transferor = new Thread(() -> {
                while (true) {
                    String msgFromBuf1 = buffer1.take();
                    String newMsg = "Потік №" + threadNum + " переклав повідомлення [" + msgFromBuf1 + "]";
                    buffer2.put(newMsg);
                }
            });
            transferor.setDaemon(true); // Робимо демоном
            transferor.start();
        }

        // --- 3. Основний потік читає 100 повідомлень ---
        System.out.println("--- Початок читання ---");
        for (int i = 0; i < 100; i++) {
            String finalMsg = buffer2.take();
            System.out.println((i + 1) + ". " + finalMsg);
        }
        System.out.println("--- Прочитано 100 повідомлень. Кінець програми ---");
        // Оскільки інші потоки - демони, програма завершиться тут автоматично
    }
}