package service;

import domain.Homework;
import domain.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ServiceMockTest {
    static Service service;

//    @BeforeEach
//    public void initMocks() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Mock
//    static StudentXMLRepository studentRepositoryMock;
//    @Mock
//    static HomeworkXMLRepository homeworkRepositoryMock;
//    @Mock
//    static GradeXMLRepository gradeRepositoryMock;

    static StudentXMLRepository studentRepositoryMock = Mockito.mock(StudentXMLRepository.class);
    static HomeworkXMLRepository homeworkRepositoryMock = Mockito.mock(HomeworkXMLRepository.class);
    static GradeXMLRepository gradeRepositoryMock = Mockito.mock(GradeXMLRepository.class);

    @BeforeAll
    public static void createService() {
        service = new Service(studentRepositoryMock, homeworkRepositoryMock, gradeRepositoryMock);
    }

    @Test
    public void studentShouldBeCreated() {
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(null);
        assertEquals(service.saveStudent("1", "Name1", 111), 1);
        verify(studentRepositoryMock).save(any(Student.class));
        reset(studentRepositoryMock);
    }

    @Test
    public void duplicateStudentShouldNotBeCreated() {
        when(studentRepositoryMock.save(any(Student.class)))
                .thenReturn(null).thenReturn(Mockito.mock(Student.class));
        assertAll(
                () -> assertEquals(service.saveStudent("2", "Duplicate", 111), 1),
                () -> assertEquals(service.saveStudent("2", "Duplicate", 111), 0)
        );
        verify(studentRepositoryMock, times(2)).save(any(Student.class));
        reset(studentRepositoryMock);
    }

    @Test
    public void homeworkWithNegativeTimespanShouldThrowException() {
        when(homeworkRepositoryMock.save(any(Homework.class))).thenThrow(ValidationException.class);
        assertThrows(ValidationException.class, () ->
                service.saveHomework("1", "Homework 1", 2, 10));
        verify(homeworkRepositoryMock).save(any(Homework.class));
        reset(homeworkRepositoryMock);
    }
}
