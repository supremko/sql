import com.codeborne.selenide.SelenideElement;
import com.github.javafaker.*;
import lombok.*;
import org.junit.jupiter.api.*;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class LoginTest {
    @BeforeAll
    static void addUser() throws SQLException {
        val faker = new Faker();
        val dataSQL = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "249281008"
                );
                val dataStmt = conn.prepareStatement(dataSQL);
        ) {
            dataStmt.setString(1, faker.bothify("##??#???-?##?-##??-####-###??#?#??##"));
            dataStmt.setString(2, "test");
            dataStmt.setString(3, "pass");
            dataStmt.executeUpdate();
        }
    }

    @AfterAll
    static void purification() throws SQLException {
        val deleteAuthCodes = "DELETE from auth_codes";
        val deleteCards = "DELETE from cards;";
        val deleteUsers = "DELETE from users;";
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "249281008"
                );
                val deleteStmt = conn.createStatement();
        ) {
            val countAuthCodes = deleteStmt.executeUpdate(deleteAuthCodes);
            val countCards = deleteStmt.executeUpdate(deleteCards);
            val countUsers = deleteStmt.executeUpdate(deleteUsers);
            System.out.println(countCards + " rows deleted from cards\n" +
                    countUsers + " rows deleted from users\n" +
                    countAuthCodes + " rows deleted from auth_codes");
        }
    }

    @Test
    void validLoginTest() throws SQLException {
        SelenideElement codeField = $("[data-test-id=code] input");
        SelenideElement verifyBtn = $("[data-test-id=action-verify]");
        val usersSQL = "SELECT login, password FROM users WHERE login = ?;";
        val authSQL = "select a.code from auth_codes a, users u where a.user_id=u.id and u.login= ? order by a.created desc limit 1;";
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "249281008"
                );
                val usersStmt = conn.prepareStatement(usersSQL);
                val authStmt = conn.prepareStatement(authSQL);
        ) {
            usersStmt.setString(1, "vasya" );
            authStmt.setString(1, "vasya");
            try (val rs = usersStmt.executeQuery()) {
                while(rs.next()) {
                    val login = rs.getString("login");
                    val password = rs.getString("password");
                    System.out.println(login + " | " + password);
                    open("http://localhost:9999");
                    $("[data-test-id=login] input").setValue(login);
                    $("[data-test-id=password] input").setValue("qwerty123");
                    $("[data-test-id=action-login]").click();
                    codeField.waitUntil(visible,1000);
                    try (val code = authStmt.executeQuery()) {
                        while (code.next()) {
                            val auth_code = code.getString("code");
                            System.out.println(auth_code);
                            codeField.setValue(auth_code);
                        }
                    }
                    verifyBtn.shouldBe(visible).click();
                    $("[data-test-id=dashboard]").waitUntil(visible, 1000);
                }
            }
        }
    }

    @Test
    void invalidLoginTest() {
        open("http://localhost:9999");
        $("[data-test-id=login] input").setValue("test");
        $("[data-test-id=password] input").setValue("invalid");
        $("[data-test-id=action-login]").click();
        $("[data-test-id='error-notification']").waitUntil(visible, 1000);
    }
}
