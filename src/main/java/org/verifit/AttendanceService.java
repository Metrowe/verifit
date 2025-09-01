package org.verifit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendanceService {

	private final AttendanceRepository attendanceRepository;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;

	}
	
	public Attendance createAttendance(String username, LocalDateTime timestamp) {
		Attendance attendance = new Attendance(username, timestamp);
		try {
			attendanceRepository.save(attendance);
			return attendance;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	Integer getUserStreak(String username) {
		return getStreakCount(username, -1);
	}

	Boolean getUserEligableDiscount(String username) {
		return getStreakCount(username, 3) >= 3;
	}


	// Takes username and returns streak count
	// If limit is above 0, returns streak count up to limit
	// If limit is 0 or negative, returns max streak count
	private Integer getStreakCount(String username, Integer limit) {
		Boolean streakExists = true;
		Integer streakCount = 0;
		LocalDate current = LocalDate.now();

		while (streakExists && (limit <= 0 || streakCount < limit)) {
			if (attendanceRepository.existsByUsernameAndTimestampBetween(username, current.plusDays(-6).atTime(LocalTime.MIN), current.atTime(LocalTime.MAX))) {
				streakCount++;
			} else {
				streakExists = false;
			}
			current = current.plusDays(-7);
		}
		return streakCount;
	}
}
