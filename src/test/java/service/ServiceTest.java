package service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import validation.GradeValidator;
import validation.HomeworkValidator;
import validation.StudentValidator;
import validation.ValidationException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {
    static Service service;
    static File studentFile;
    static File homeworkFile;
    static File gradeFile;

    static String defaultText = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<Entities>\n</Entities>";

    private static File initializeFile(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();

        FileWriter writer = new FileWriter(fileName);
        writer.write(defaultText);
        writer.close();

        return file;
    }

    @BeforeAll
    public static void createXMLFiles() {
        String studentFileName = "students_test.xml";
        String homeworkFileName = "homework_test.xml";
        String gradeFileName = "grade_test.xml";

        try {
            studentFile = initializeFile(studentFileName);
            homeworkFile = initializeFile(homeworkFileName);
            gradeFile = initializeFile(gradeFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        service = new Service(
                new StudentXMLRepository(new StudentValidator(), studentFileName),
                new HomeworkXMLRepository(new HomeworkValidator(), homeworkFileName),
                new GradeXMLRepository(new GradeValidator(), gradeFileName));
    }

    @AfterAll
    public static void deleteXMLFiles() {
        studentFile.delete();
        homeworkFile.delete();
        gradeFile.delete();
    }

    @Test
    public void studentShouldBeCreated() {
        assertEquals(service.saveStudent("1", "Name1", 111), 1);
    }

    @Test
    public void duplicateStudentShouldNotBeCreated() {
        assertAll(
                () -> assertEquals(service.saveStudent("2", "Duplicate", 111), 1),
                () -> assertEquals(service.saveStudent("2", "Duplicate", 111), 0)
        );
    }

    @Test
    public void homeworkWithNegativeTimespanShouldThrowException() {
        assertThrows(ValidationException.class, () ->
                service.saveHomework("1", "Homework 1", 2, 10));
    }

    @Test
    public void homeworkShouldBeDeleted() {
        assertAll(
                () -> assertEquals(service.saveHomework("1",
                        "Homework to be deleted", 5, 2), 1),
                () -> assertEquals(service.deleteHomework("1"), 1)
        );
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.000001, 10.00001, 189.12, Double.MAX_VALUE})
    public void gradeWithInvalidValueShouldThrowException(double valGrade) {
        service.saveStudent("3", "Graded Student", 555);
        service.saveHomework("3", "Graded Homework", 5, 2);
        assertThrows(ValidationException.class, () ->
                service.saveGrade("3", "3", valGrade, 5, "No comment"));
    }
}
