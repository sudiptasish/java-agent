package com.sc.hm.jvm.stats;

/**
 * Interface that represents a CPU statisticss.
 * Often it is important to know the clock time spent by a thread while executing
 * a method or a block of code. The agent provides this capability. The cpu clock
 * time is stored internally and later exposed as a metric.
 * 
 * @author Sudiptasish Chanda
 */
public interface CPUStatistics extends Statistics {
	
	long INVALID_TIME = -999L;
	long RUNNING_TIME = -111L;
	
	int PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();

	/**
	 * Return the percentage of CPU time a thread took
	 * to complete the execution of a method.
	 * 
	 * @return double
	 */
	double getThreadCpuUsage();

	/**
	 * Return the percentage of USER time a thread took
	 * to complete the execution of a method.
	 * 
	 * @return double
	 */
	double getThreadUserUsage();
}
