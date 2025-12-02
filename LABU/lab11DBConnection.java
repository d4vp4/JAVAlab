import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class lab11DBConnection {
    public static Connection getConnection() {
        try (InputStream input = lab11DBConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Вибачте, файл database.properties не знайдено");
                return null;
            }

            prop.load(input);

            return DriverManager.getConnection(
                    prop.getProperty("db.url"),
                    prop.getProperty("db.user"),
                    prop.getProperty("db.password")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
