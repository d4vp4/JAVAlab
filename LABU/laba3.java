import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// ================= MODEL =================


enum PassType { WORKDAY, WEEKEND, SEASON }
enum DurationType { HALF_DAY_AM, HALF_DAY_PM, DAY_1, DAY_2, DAY_5, RIDES_10, RIDES_20, RIDES_50, RIDES_100, SEASON }


abstract class SkiPass {
    private static int idCounter = 1;
    protected int id;
    protected boolean blocked;
    protected PassType passType;

    public SkiPass(PassType passType) {
        this.id = idCounter++;
        this.blocked = false;
        this.passType = passType;
    }

    public int getId() { return id; }
    public boolean isBlocked() { return blocked; }
    public void block() { this.blocked = true; }
    public PassType getPassType() { return passType; }


    public abstract boolean canPass(LocalDateTime currentTime);


    public void decrementRide() {}

    @Override
    public String toString() {
        return "ID: " + id + " [" + (blocked ? "BLOCKED" : "ACTIVE") + "] Type: " + passType;
    }
}


class CountSkiPass extends SkiPass {
    private int ridesLeft;

    public CountSkiPass(PassType passType, int rides) {
        super(passType);
        this.ridesLeft = rides;
    }

    @Override
    public boolean canPass(LocalDateTime currentTime) {
        if (blocked) return false;
        DayOfWeek day = currentTime.getDayOfWeek();
        boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
        if (passType == PassType.WORKDAY && isWeekend) return false;
        if (passType == PassType.WEEKEND && !isWeekend) return false;
        return ridesLeft > 0;
    }
    @Override
    public void decrementRide() {
        if (ridesLeft > 0) ridesLeft--;
    }
    @Override
    public String toString() {
        return super.toString() + ", Rides left: " + ridesLeft;
    }
}

class TimeSkiPass extends SkiPass {
    private LocalDateTime validUntil;
    private DurationType durationType;
    public TimeSkiPass(PassType passType, DurationType durationType) {
        super(passType);
        this.durationType = durationType;
        calculateExpiration();
    }

    private void calculateExpiration() {
        LocalDateTime now = LocalDateTime.now();
        switch (durationType) {
            case HALF_DAY_AM: validUntil = now.withHour(13).withMinute(0); break;
            case HALF_DAY_PM: validUntil = now.withHour(17).withMinute(0); break;
            case DAY_1: validUntil = now.plusDays(1); break;
            case DAY_2: validUntil = now.plusDays(2); break;
            case DAY_5: validUntil = now.plusDays(5); break;
            case SEASON: validUntil = now.plusMonths(6); break;
            default: validUntil = now;
        }
    }
    @Override
    public boolean canPass(LocalDateTime currentTime) {
        if (blocked) return false;
        if (currentTime.isAfter(validUntil)) return false;
        DayOfWeek day = currentTime.getDayOfWeek();
        boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
        if (passType == PassType.SEASON) return true;
        if (passType == PassType.WORKDAY && isWeekend) return false;
        if (passType == PassType.WEEKEND && !isWeekend) return false;
        if (durationType == DurationType.HALF_DAY_AM && currentTime.getHour() >= 13) return false;
        if (durationType == DurationType.HALF_DAY_PM && currentTime.getHour() < 13) return false;
        return true;
    }
    @Override
    public String toString() {
        return super.toString() + ", Valid until: " + validUntil;
    }
}
class Registry {
    private Map<Integer, SkiPass> users = new HashMap<>();
    public void registerPass(SkiPass pass) {
        users.put(pass.getId(), pass);
    }
    public SkiPass getPass(int id) {
        return users.get(id);
    }
    public void blockPass(int id) {
        if (users.containsKey(id)) {
            users.get(id).block();
        }
    }
}
class Turnstile {
    private Registry registry;
    private int acceptedCount = 0;
    private int deniedCount = 0;

    public Turnstile(Registry registry) {
        this.registry = registry;
    }
    public boolean checkPass(int id) {
        SkiPass pass = registry.getPass(id);

        LocalDateTime now = LocalDateTime.now();

        if (pass != null && pass.canPass(now)) {
            pass.decrementRide();
            acceptedCount++;
            return true;
        } else {
            deniedCount++;
            return false;
        }
    }

    public String getStatistics() {
        return "Статистика турнікету: Дозволено: " + acceptedCount + ", Відмовлено: " + deniedCount;
    }
}

// ================= VIEW =================
class ConsoleView {
    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printMenu() {
        System.out.println("\n--- SKI RESORT SYSTEM ---");
        System.out.println("1. Купити Ski-Pass (Count based)");
        System.out.println("2. Купити Ski-Pass (Time based / Season)");
        System.out.println("3. Пройти через турнікет");
        System.out.println("4. Заблокувати картку");
        System.out.println("5. Показати статистику");
        System.out.println("6. Вихід");
        System.out.print("Вибір: ");
    }
}

// ================= CONTROLLER =================
public class laba3 {
    private Registry registry;
    private Turnstile turnstile;
    private ConsoleView view;
    private Scanner scanner;

    public laba3() {
        registry = new Registry();
        turnstile = new Turnstile(registry);
        view = new ConsoleView();
        scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            view.printMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": buyCountPass(); break;
                case "2": buyTimePass(); break;
                case "3": useTurnstile(); break;
                case "4": blockCard(); break;
                case "5": view.printMessage(turnstile.getStatistics()); break;
                case "6": running = false; break;
                default: view.printMessage("Невірний вибір.");
            }
        }
    }

    private void buyCountPass() {
        view.printMessage("Тип дня (1 - Робочий, 2 - Вихідний): ");
        PassType type = scanner.nextLine().equals("1") ? PassType.WORKDAY : PassType.WEEKEND;
        view.printMessage("Кількість поїздок (10, 20, 50, 100): ");
        int count = Integer.parseInt(scanner.nextLine());

        SkiPass pass = new CountSkiPass(type, count);
        registry.registerPass(pass);
        view.printMessage("Видано: " + pass);
    }

    private void buyTimePass() {
        view.printMessage("1 - Півдня(ранок), 2 - День, 3 - Сезонний: ");
        String ch = scanner.nextLine();
        SkiPass pass;

        if (ch.equals("3")) {
            pass = new TimeSkiPass(PassType.SEASON, DurationType.SEASON);
        } else {
            view.printMessage("Тип дня (1 - Робочий, 2 - Вихідний): ");
            PassType type = scanner.nextLine().equals("1") ? PassType.WORKDAY : PassType.WEEKEND;
            DurationType dur = ch.equals("1") ? DurationType.HALF_DAY_AM : DurationType.DAY_1;
            pass = new TimeSkiPass(type, dur);
        }

        registry.registerPass(pass);
        view.printMessage("Видано: " + pass);
    }

    private void useTurnstile() {
        view.printMessage("Прикладіть картку (введіть ID): ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            boolean passed = turnstile.checkPass(id);
            if (passed) {
                view.printMessage(">> ПРОХІД ДОЗВОЛЕНО! Приємного катання.");
            } else {
                view.printMessage(">> ПРОХІД ЗАБОРОНЕНО! (Картка не знайдена, прострочена або заблокована)");
            }
        } catch (NumberFormatException e) {
            view.printMessage("ID має бути числом.");
        }
    }

    private void blockCard() {
        view.printMessage("Введіть ID для блокування: ");
        int id = Integer.parseInt(scanner.nextLine());
        registry.blockPass(id);
        view.printMessage("Картку " + id + " заблоковано (якщо вона існувала).");
    }

    public static void main(String[] args) {
        new laba3().start();
    }
}