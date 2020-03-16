package com.sc.hm.jvm.agent.transform;

/**
 * Class that keeps the method to be instrumented.
 * 
 * If no line number is specified, then the entire method will be instrumented,
 * otherwise in a given method only the chunk of code in between the two line nnumbers
 * will be instrumented.
 * 
 * @author Sudiptasish Chanda
 */
public class MethodInstrumentation {

	private String methodName;
	
	private int[] lines;
	
	public MethodInstrumentation() {}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the lines
	 */
	public int[] getLines() {
		return lines;
	}

	/**
	 * @param lines the lines to set
	 */
	public void setLines(int[] lines) {
		this.lines = lines;
	}
}
