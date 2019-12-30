import com.github.javafaker.*;
import lombok.*;
import org.junit.jupiter.api.*;

import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginTest {
    @BeforeEach
    void setUp() throws SQLException {
        val faker = new Faker();
        val dataSQL = "INSERT INTO users(login, password) VALUES (?, ?);";
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "249281008"
                );
                val dataStmt = conn.prepareStatement(dataSQL);
        ) {
            dataStmt.setString(1, faker.name().username());
            dataStmt.setString(2, "password");
            dataStmt.executeUpdate();
            dataStmt.setString(1, faker.name().username());
            dataStmt.setString(2, "password");
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
            cardsStmt.setInt(1, 1);
            try (val rs = cardsStmt.executeQuery()) {
                while (rs.next()) {
                    val id = rs.getInt("id");
                    val number = rs.getString("number");
                    val balanceInKopecks = rs.getInt("balance_in_kopecks");
                    System.out.println(id + " " + number + " " + balanceInKopecks);
                }
            }
        }
    }

}
