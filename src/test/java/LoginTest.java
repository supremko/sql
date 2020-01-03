import com.github.javafaker.*;
import lombok.*;
import org.junit.jupiter.api.*;

import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginTest {
    @BeforeEach
    void setUp() throws SQLException {
        val faker = new Faker();
        val dataSQL = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "249281008"
                );
                val dataStmt = conn.prepareStatement(dataSQL);
        ) {
            dataStmt.setString(1, faker.bothify("##??#???-?##?-##??-####-###??#?#??##"));
            dataStmt.setString(2, faker.name().username());
            dataStmt.setString(3, "password");
            dataStmt.executeUpdate();
            dataStmt.setString(1, faker.bothify("##??#???-?##?-##??-####-###??#?#??##"));
            dataStmt.setString(2, faker.name().username());
            dataStmt.setString(3, "password");
            dataStmt.executeUpdate();
        }
    }

    @Test
    void stubTest() throws SQLException {
        val countSQL = "SELECT COUNT(*) FROM users;";
        val cardsSQL = "SELECT id, number, balance_in_kopecks FROM cards WHERE user_id = ?;";
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "249281008"
                );
                val countStmt = conn.createStatement();
                val cardsStmt = conn.prepareStatement(cardsSQL);
        ) {
            try (val rs = countStmt.executeQuery(countSQL)) {
                if (rs.next()) {
                    val count = rs.getInt(1);
                    System.out.println(count);
                }
            }
            cardsStmt.setString(1, "ffa708a6-e90c-4cdf-9581-f5af9fc1b396" );
            try (val rs = cardsStmt.executeQuery()) {
                while (rs.next()) {
                    val id = rs.getString("id");
                    val number = rs.getString("number");
                    val balanceInKopecks = rs.getInt("balance_in_kopecks");
                    System.out.println(id + " | " + number + " | " + balanceInKopecks);
                }
            }
        }
    }

}
