package com.sc.hm.jvm.stats;

/**
 * Interface that represents a method call statisticss.
 * 
 * @author Sudiptasish Chanda
 */
public interface MethodCallStatistics extends AggregatedStatistics {

	/**
	 * Return the underlying method name.
	 * @return String
	 */
	String getMethodName();
	
	/**
	 * Return the number of times the method was invoked.,
	 * @return long
	 */
	long getInvocationCount();
	
	/**
	 * Return the max time (in milliseconds) took by any thread
	 * to execute this method.
	 * @return long
	 */
	long getMaxDuration();
	
	/**
	 * Return the max time in human readable format took by any thread
	 * to execute this method.
	 * 
	 * @return String     (In Sec, Min, Hr, etc)
	 */
	String getMaxElapsedTime();
}
