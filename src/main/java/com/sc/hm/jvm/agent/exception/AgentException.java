package com.sc.hm.jvm.agent.exception;

/**
 * 
 * @author Sudiptasish Chanda
 */
public class AgentException extends RuntimeException {

	public AgentException() {
		super();
	}
	
	public AgentException(String message) {
		super(message);
	}
	
	public AgentException(Throwable t) {
		super(t);
	}
	
	public AgentException(String message, Throwable t) {
		super(message, t);
	}
}
