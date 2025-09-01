package org.verifit;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/attendance")
@RestController
@Tag(name = "Verifit attendance API", description = "Endpoints for adding gym attendance records and  retrieving user streak data")
public class AttendanceController {
	private final AttendanceService attendanceService;

	@Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

	/**
     * Endpoint to add a new attendance record for a specified username.
     *
     * @param username The username of the person.
	 * @param timestamp The timestamp of the attendance.
     * @return A ResponseEntity with the newly created attendance.
     */
	@Operation(summary = "Adds a new attendance record for a username with an optional timestamp")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Attendance> addAttendance(
		@RequestParam String username, 
		@RequestParam(required = false) LocalDateTime timestamp
	) {
		Attendance attendance = attendanceService.createAttendance(username, timestamp);
		if (attendance == null) {
			return ResponseEntity.internalServerError().build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(attendance);
	}

	/**
	 * Endpoint to get a persons discount eligibility.
	 * 
	 * @param username The username of the person.
	 * @return A ResponseEntity with the persons discount eligibility.
	 */
	@Operation(summary = "Gets a usernames discount eligibility (if they have attended at least once each week for the last 3 weeks)")
	@GetMapping(path = "/{username}/discount")
	public ResponseEntity<Boolean> getUserEligableDiscount(@PathVariable String username) {
		Boolean discountEligibility = attendanceService.getUserEligableDiscount(username);
		return ResponseEntity.status(HttpStatus.OK).body(discountEligibility);
	}

	/**
	 * Endpoint to get a persons streak count.
	 * 
	 * @param username The username of the person.
	 * @return A ResponseEntity with the persons streak count.
	 */
	@Operation(summary = "Gets a usernames streak count (number of weeks in a row they have attended at least once)")
	@GetMapping(path = "/{username}/streak")
	public ResponseEntity<Integer> getUserStreak(@PathVariable String username) {
		Integer streakCount = attendanceService.getUserStreak(username);
		return ResponseEntity.status(HttpStatus.OK).body(streakCount);
	}
	
}
