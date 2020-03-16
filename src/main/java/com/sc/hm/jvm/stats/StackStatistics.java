package com.sc.hm.jvm.stats;

/**
 * Call stack statistics.
 * 
 * @author Sudiptasish Chanda
 */
public interface StackStatistics extends Statistics {

	/**
	 * Return the thread stack.
	 * @return String
	 */
	String getCallStack();
	
	/**
	 * Return the depth of the call statks.
	 * @return int
	 */
	int getDepth();
}
