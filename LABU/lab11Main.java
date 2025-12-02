import java.sql.*;
import java.util.Scanner;

public class lab11Main {
    public static void main(String[] args) {
        System.out.println("=== Всі співробітники ===");
        getAllEmployees();

        System.out.println("\n=== Всі завдання ===");
        getAllTasks();

        System.out.println("\n=== Співробітники відділу IT ===");
        getEmployeesByDepartment("IT");

        System.out.println("\n=== Додавання завдання для Ivan Petrenko (ID 1) ===");
        addTask(1, "Write documentation");
        getTasksForEmployee(1);

        System.out.println("\n=== Видалення співробітника (ID 3) ===");
        deleteEmployee(1002);
        getAllEmployees();
    }

    public static void getAllEmployees() {
        String query = "SELECT e.id, e.name, e.position, d.name as dept_name " +
                "FROM Employee e LEFT JOIN Department d ON e.department_id = d.id";
        try (Connection conn = lab11DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                System.out.printf("ID: %d | Name: %s | Pos: %s | Dept: %s%n",
                        rs.getInt("id"), rs.getString("name"),
                        rs.getString("position"), rs.getString("dept_name"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void getAllTasks() {
        String query = "SELECT * FROM Task";
        try (Connection conn = lab11DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                System.out.printf("Task: %s | EmployeeID: %d%n",
                        rs.getString("description"), rs.getInt("employee_id"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void getEmployeesByDepartment(String deptName) {
        String query = "SELECT e.name FROM Employee e " +
                "JOIN Department d ON e.department_id = d.id WHERE d.name = ?";
        try (Connection conn = lab11DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, deptName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("- " + rs.getString("name"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public static void addTask(int empId, String description) {
        String query = "INSERT INTO Task (description, employee_id) VALUES (?, ?)";
        try (Connection conn = lab11DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, description);
            pstmt.setInt(2, empId);
            pstmt.executeUpdate();
            System.out.println("Завдання успішно додано.");

        } catch (SQLException e) { e.printStackTrace(); }
    }
    public static void getTasksForEmployee(int empId) {
        String query = "SELECT description FROM Task WHERE employee_id = ?";
        try (Connection conn = lab11DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, empId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("Task: " + rs.getString("description"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deleteEmployee(int empId) {
        String query = "DELETE FROM Employee WHERE id = ?";
        try (Connection conn = lab11DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, empId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Співробітника видалено.");
            else System.out.println("Співробітника з таким ID не знайдено.");

        } catch (SQLException e) { e.printStackTrace(); }
    }
}