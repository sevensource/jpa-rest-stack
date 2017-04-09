package org.sevensource.support.rest.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;

public class ApiErrorDTO {
	private long timestamp;
	private int status;
	private String error;
	private String exception;
	private String message;
	
	private String path;
	
	
	public ApiErrorDTO(Exception ex, HttpStatus status) {
		this.timestamp = Instant.now().toEpochMilli();
		this.exception = ex.getClass().getName();
		this.status = status.value();
		this.error = status.getReasonPhrase();
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
	public void setMessage(String message) {
		this.message = message;
	}
}
