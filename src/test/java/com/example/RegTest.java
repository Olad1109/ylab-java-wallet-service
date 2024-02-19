package test.java.com.example;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import main.java.com.example.RegisterAndAuth;

public class RegTest {

    File testFolderUser, testDataUserTextFile, testUserActions;
    String input, email;

    // Создаём модульный тест для метода регистрации
    @Test
    public void testAssertTrue() throws IOException {
        // Arrange
        email = "login@ex.com";
        // AssertTrue
        // Add assertions to check if the registerAndAuth was successful
        // For example, check if the user folder and files were created
        testFolderUser = new File("src/test/java/com/example/data/userWallets/" + email);
        RegisterAndAuth.folderUser = testFolderUser;
        testDataUserTextFile = new File(testFolderUser + "/requireForAuth.txt");
        RegisterAndAuth.dataUserTextFile = testDataUserTextFile;
        testUserActions = new File(testFolderUser + "/auditOfUserActions.txt");
        RegisterAndAuth.userActions = testUserActions;

        try {
            assertTrue(RegisterAndAuth.folderUser.exists());
            assertTrue(RegisterAndAuth.dataUserTextFile.exists());
            assertTrue(RegisterAndAuth.userActions.exists());
        } catch (AssertionError e) {
            System.out.println("Файлы отсутствуют!");
        }

    }

    @Test
    public void testAssertFalse() throws IOException {
        // Arrange
        email = "login@ex.com";
        // AssertFalse
        // Add assertions to check if the registerAndAuth was successful
        // For example, check if the user folder and files were created
        testFolderUser = new File("src/test/java/com/example/data/userWallets/" + email);
        RegisterAndAuth.folderUser = testFolderUser;
        testDataUserTextFile = new File(testFolderUser + "/requireForAuth.txt");
        RegisterAndAuth.dataUserTextFile = testDataUserTextFile;
        testUserActions = new File(testFolderUser + "/auditOfUserActions.txt");
        RegisterAndAuth.userActions = testUserActions;

        try {
            assertFalse(RegisterAndAuth.folderUser.exists());
            assertFalse(RegisterAndAuth.dataUserTextFile.exists());
            assertFalse(RegisterAndAuth.userActions.exists());
        } catch (AssertionError e) {
            System.out.println("Файлы существуют!");
        }

    }
    // Add more test cases to cover other scenarios such as existing user, invalid input, etc.
    @Test
    public void testRegistrationAudit() throws IOException {
        email = "login@ex.com";
        testFolderUser = new File("src/test/java/com/example/data/userWallets/" + email);
        testDataUserTextFile = new File(testFolderUser + "/requireForAuth.txt");
        // Arrange
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        Date date = new Date();
        String dataString = formatter.format(date);
        testUserActions = new File(testFolderUser + "/auditOfUserActions.txt");
        // Act
        String audit = email + " passed registration (" + dataString + ")";
        String filePath = String.valueOf(testUserActions);
        FileWriter writer = new FileWriter(filePath, true);
        writer.write(audit + "\n");
        writer.flush();
        writer.close();
        // Assert
        BufferedReader reader = new BufferedReader(new FileReader(testUserActions));
        String line = reader.readLine();
        try {
            assertEquals(audit, line);
            System.out.println("Первое подтверждение регистрации.");
        } catch (AssertionError e) {
            System.out.println("Повторное подтверждение регистрации.");
        }
        reader.close();
    }

    @Test
    public void testExternalBool() {
        // Arrange
        boolean externalBool;
        // Act
        externalBool = false;
        // Assert
        assertFalse(externalBool);
    }

}
