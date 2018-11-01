package org.sevensource.support.rest.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;

public class ApiErrorDTO {
	private final long timestamp = Instant.now().toEpochMilli();
	private final int status;
	private final String error;
	private String exception;
	private String message;

	private String path;


	public ApiErrorDTO(Exception ex, HttpStatus status) {
		this.status = status.value();
		this.error = status.getReasonPhrase();
		this.exception = ex.getClass().getName();
		this.message = ex.getMessage();
	}

	public long getTimestamp() {
		return timestamp;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getStatus() {
		return status;
	}
	public String getError() {
		return error;
	}
	public String getException() {
		return exception;
	}
	public String getMessage() {
		return message;
	}
}
