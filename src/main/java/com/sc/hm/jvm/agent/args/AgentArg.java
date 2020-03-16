package com.sc.hm.jvm.agent.args;

/**
 * 
 * @author Sudiptasish Chanda
 */
public final class AgentArg {

	private String argName;
	
	private String argValue;
	
	private String defaultValue;
	
	public AgentArg() {}

	public AgentArg(String argName, String argValue, String defaultValue) {
		this.argName = argName;
		this.argValue = argValue;
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the argName
	 */
	public String getArgName() {
		return argName;
	}

	/**
	 * @param argName the argName to set
	 */
	public void setArgName(String argName) {
		this.argName = argName;
	}

	/**
	 * @return the argValue
	 */
	public String getArgValue() {
		return argValue;
	}

	/**
	 * @param argValue the argValue to set
	 */
	public void setArgValue(String argValue) {
		this.argValue = argValue;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Name: %s, Value: %s, Default: %s", argName, argValue, defaultValue);
	}
}
