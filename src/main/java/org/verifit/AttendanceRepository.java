package org.verifit;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	/**
     * Checks for the existence of at least one record for a given username
     * within a specified date range.
     *
     * @param username The username to search for.
     * @param startDate The beginning of the date range (inclusive).
     * @param endDate The end of the date range (inclusive).
     * @return true if at least one record exists, otherwise false.
     */
    boolean existsByUsernameAndTimestampBetween(String username, LocalDateTime startDate, LocalDateTime endDate);
}
