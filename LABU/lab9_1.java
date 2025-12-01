import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Клас Рахунок
class Account {
    private final int id;
    private int balance;
    // Статичний лічильник для унікальних ID
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    public Account(int initialBalance) {
        this.id = idGenerator.getAndIncrement();
        this.balance = initialBalance;
    }

    public int getId() { return id; }
    public int getBalance() { return balance; }

    public void withdraw(int amount) { balance -= amount; }
    public void deposit(int amount) { balance += amount; }
}

// Клас Банк з безпечним методом переказу
class Bank {
    public void transfer(Account from, Account to, int amount) {
        // Логіка уникнення Deadlock: завжди блокуємо менший ID першим
        Account firstLock = from.getId() < to.getId() ? from : to;
        Account secondLock = from.getId() < to.getId() ? to : from;

        synchronized (firstLock) {
            synchronized (secondLock) {
                if (from.getBalance() >= amount) {
                    from.withdraw(amount);
                    to.deposit(amount);
                }
            }
        }
    }

    public long getTotalBalance(List<Account> accounts) {
        long total = 0;
        for (Account acc : accounts) {
            total += acc.getBalance();
        }
        return total;
    }
}

public class lab9_1 {
    public static void main(String[] args) throws InterruptedException {
        Bank bank = new Bank();
        List<Account> accounts = new ArrayList<>();
        int numberOfAccounts = 100;

        //  Створюємо рахунки
        for (int i = 0; i < numberOfAccounts; i++) {
            accounts.add(new Account(1000));
        }

        // Рахуємо гроші ДО
        long totalBefore = bank.getTotalBalance(accounts);
        System.out.println("Грошей у банку ДО: " + totalBefore);

        //  Запускаємо тисячі потоків
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Random random = new Random();
        int numberOfTransactions = 10_000;

        for (int i = 0; i < numberOfTransactions; i++) {
            executor.submit(() -> {
                Account from = accounts.get(random.nextInt(numberOfAccounts));
                Account to = accounts.get(random.nextInt(numberOfAccounts));
                int amount = random.nextInt(1000); // Випадкова сума

                if (from != to) {
                    bank.transfer(from, to, amount);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        long totalAfter = bank.getTotalBalance(accounts);
        System.out.println("Грошей у банку ПІСЛЯ: " + totalAfter);

        if (totalBefore == totalAfter) {
            System.out.println("Успіх! Баланс зійшовся.");
        } else {
            System.err.println("Помилка! Гроші зникли або з'явилися.");
        }
    }
}