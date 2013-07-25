package org.gephi.streaming.client.exception;

public class GephiStreamingException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public GephiStreamingException() {
		super();
	}
	
	public GephiStreamingException(String message) {
		super(message);
	}
	
	public GephiStreamingException(Throwable t) {
		super(t);
	}
	
	public GephiStreamingException(String message, Throwable t) {
		super(message, t);
	}

}
