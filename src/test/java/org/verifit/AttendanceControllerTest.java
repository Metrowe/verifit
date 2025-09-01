package org.verifit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AttendanceControllerTest {

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private AttendanceController attendanceController;

    private final String testUsername = "user00";
    private Attendance testAttendanceRecord;
    private final LocalDateTime testTimestamp = LocalDateTime.of(2025, 8, 30, 10, 0);

    @BeforeEach
    void setUp() {
        testAttendanceRecord = new Attendance(testUsername, testTimestamp);
    }

    // Tests for addAttendance endpoint (POST)
    @Test
    void addAttendance_withTimestamp_returnsCreatedStatus() {
        when(attendanceService.createAttendance(eq(testUsername), eq(testTimestamp)))
            .thenReturn(testAttendanceRecord);

        ResponseEntity<Attendance> response = attendanceController.addAttendance(testUsername, testTimestamp);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testAttendanceRecord, response.getBody());
    }

    @Test
    void addAttendance_withoutOptionalTimestamp_ReturnsCreatedStatus() {
        when(attendanceService.createAttendance(eq(testUsername), any(LocalDateTime.class)))
            .thenReturn(testAttendanceRecord);

        ResponseEntity<Attendance> response = attendanceController.addAttendance(testUsername, null);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testAttendanceRecord, response.getBody());
    }

    @Test
    void addAttendance_onDatabaseFailure_returnsInternalServerError() {
        // Mock the service call to return null
        when(attendanceService.createAttendance(eq(testUsername), any(LocalDateTime.class)))
            .thenReturn(null);

        ResponseEntity<Attendance> response = attendanceController.addAttendance(testUsername, testTimestamp);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Tests for getUserEligableDiscount endpoint (GET)
    @Test
    void getUserEligableDiscount_whenEligible_returnsTrueAndOkStatus() {
        when(attendanceService.getUserEligableDiscount(testUsername))
            .thenReturn(true);

        ResponseEntity<Boolean> response = attendanceController.getUserEligableDiscount(testUsername);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }

    @Test
    void getUserEligableDiscount_whenNotEligible_returnsFalseAndOkStatus() {
        when(attendanceService.getUserEligableDiscount(testUsername))
            .thenReturn(false);

        ResponseEntity<Boolean> response = attendanceController.getUserEligableDiscount(testUsername);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody());
    }

    // Tests for getUserStreak endpoint (GET)
    @Test
    void getUserStreak_withPositiveStreak_returnsCorrectCountAndOkStatus() {
        int expectedStreakCount = 5;
        when(attendanceService.getUserStreak(testUsername))
            .thenReturn(expectedStreakCount);

        ResponseEntity<Integer> response = attendanceController.getUserStreak(testUsername);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedStreakCount, response.getBody());
    }

    @Test
    void getUserStreak_withNoStreak_returnsZeroAndOkStatus() {
        int expectedStreakCount = 0;
        when(attendanceService.getUserStreak(testUsername))
            .thenReturn(expectedStreakCount);

        ResponseEntity<Integer> response = attendanceController.getUserStreak(testUsername);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedStreakCount, response.getBody());
    }
}
