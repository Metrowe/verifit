package org.verifit;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
class Attendance {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@NotBlank(message = "Username can not be blank")
	private String username;

	private LocalDateTime timestamp;

	public Attendance(String username, LocalDateTime timestamp) {
        this.username = username;
		if (timestamp == null) {
			this.timestamp = LocalDateTime.now();
		} else {
			this.timestamp = timestamp;
		}
    }

	public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
