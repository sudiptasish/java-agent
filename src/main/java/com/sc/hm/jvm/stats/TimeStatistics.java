package com.sc.hm.jvm.stats;

import java.util.Date;

/**
 * Interface that represents a time statistics.
 * 
 * Often it is important to know the average execution time of a method or a 
 * block of code. This metric help expose the statistics of the same.
 * 
 * @author Sudiptasish Chanda
 */
public interface TimeStatistics {
	
	/**
	 * Time when this method invocation was started.
	 * @return Date
	 */
	Date getStartTime();
	
	/**
	 * Time when this method invocation was ended.
	 * @return Date
	 */
	Date getEndTime();

	/**
	 * Return the time (in milliseconds) the specific method
	 * took to complete.
	 *  
	 * @return long
	 */
	long getDuration();
	
	/**
	 * Get the elapsed time in human readable format
	 * @return String      Could be in Sec., Min., Hr., etc
	 */
	String getElapsedTime();
}
