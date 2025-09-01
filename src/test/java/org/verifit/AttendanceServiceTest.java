package org.verifit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private final String testUsername = "user00";
    private final LocalDateTime testTimestamp = LocalDateTime.of(2025, 8, 30, 10, 0);
    private Attendance testAttendance;

    @BeforeEach
    void setUp() {
        testAttendance = new Attendance(testUsername, testTimestamp);
    }

    // Tests for createAttendance method
    @Test
    void createAttendance_onSuccessfulSave_returnsAttendanceRecord() {
        when(attendanceRepository.save(any(Attendance.class)))
            .thenReturn(testAttendance);

        Attendance result = attendanceService.createAttendance(testUsername, testTimestamp);

        assertNotNull(result);
        assertEquals(testAttendance.getUsername(), result.getUsername());
        assertEquals(testAttendance.getTimestamp(), result.getTimestamp());
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void createAttendance_onSaveException_returnsNull() {
        when(attendanceRepository.save(any(Attendance.class)))
            .thenThrow(new RuntimeException("Simulated database error"));

        Attendance result = attendanceService.createAttendance(testUsername, testTimestamp);

        assertNull(result);
    }

    // Tests for getUserStreak method
    @Test
    void getUserStreak_noStreak_returnsZero() {
        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
            LocalDate mockDate = LocalDate.of(2025, 8, 30);
            mockedLocalDate.when(LocalDate::now).thenReturn(mockDate);

            when(attendanceRepository.existsByUsernameAndTimestampBetween(
                    eq(testUsername),
                    eq(LocalDate.of(2025, 8, 24).atTime(LocalTime.MIN)),
                    eq(LocalDate.of(2025, 8, 30).atTime(LocalTime.MAX))))
                .thenReturn(false);

            Integer result = attendanceService.getUserStreak(testUsername);

            assertEquals(0, result);
        }
    }

    @Test
    void getUserStreak_withTwoWeekStreak_returnsTwo() {
        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
            LocalDate mockDate = LocalDate.of(2025, 8, 30);
            mockedLocalDate.when(LocalDate::now).thenReturn(mockDate);

            // Mock a 2 week streak
            when(attendanceRepository.existsByUsernameAndTimestampBetween(
                    eq(testUsername),
                    eq(LocalDate.of(2025, 8, 24).atTime(LocalTime.MIN)),
                    eq(LocalDate.of(2025, 8, 30).atTime(LocalTime.MAX))))
                .thenReturn(true);
            when(attendanceRepository.existsByUsernameAndTimestampBetween(
                    eq(testUsername),
                    eq(LocalDate.of(2025, 8, 17).atTime(LocalTime.MIN)),
                    eq(LocalDate.of(2025, 8, 23).atTime(LocalTime.MAX))))
                .thenReturn(true);
            when(attendanceRepository.existsByUsernameAndTimestampBetween(
                    eq(testUsername),
                    eq(LocalDate.of(2025, 8, 10).atTime(LocalTime.MIN)),
                    eq(LocalDate.of(2025, 8, 16).atTime(LocalTime.MAX))))
                .thenReturn(false);

            Integer result = attendanceService.getUserStreak(testUsername);

            assertEquals(2, result);
        }
    }

    // Tests for getUserEligibleDiscount method
    @Test
    void getUserEligableDiscount_withLessThanThreeWeekStreak_returnsFalse() {
        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
            LocalDate mockDate = LocalDate.of(2025, 8, 30);
            mockedLocalDate.when(LocalDate::now).thenReturn(mockDate);
            
            // Mock a 2 week streak (less than the required 3)
            when(attendanceRepository.existsByUsernameAndTimestampBetween(
                    eq(testUsername),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)))
                .thenReturn(true, true, false);

            Boolean result = attendanceService.getUserEligableDiscount(testUsername);

            assertEquals(false, result);
        }
    }
    
    @Test
    void getUserEligableDiscount_withExactlyThreeWeekStreak_returnsTrue() {
        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
            LocalDate mockDate = LocalDate.of(2025, 9, 1);
            mockedLocalDate.when(LocalDate::now).thenReturn(mockDate);

            // Mock a 3 week streak
            when(attendanceRepository.existsByUsernameAndTimestampBetween(
                    eq(testUsername),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)))
                .thenReturn(true, true, true, false);

            Boolean result = attendanceService.getUserEligableDiscount(testUsername);

            assertEquals(true, result);
        }
    }
}
