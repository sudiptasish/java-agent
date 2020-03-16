package com.sc.hm.jvm.stats;

/**
 * Default implementation of {@link CPUStatistics}.
 * 
 * @author Sudiptasish Chanda
 */
public class CPUStatisticsImpl implements CPUStatistics {
	
	private final long executionStart;
	private final long cpuStartTime;
	private final long userStartTime;

	private long executionEnd;
	private long cpuEndTime;
	private long userEndTime;
	
	public CPUStatisticsImpl(long executionStart
			, long cpuStartTime
			, long userStartTime) {
		
		this.executionStart = executionStart;
		this.cpuStartTime = cpuStartTime;
		this.userStartTime = userStartTime;
	}

	/**
	 * @return the executionStart
	 */
	public long getExecutionStart() {
		return executionStart;
	}

	/**
	 * @return the cpuStartTime
	 */
	public long getCpuStartTime() {
		return cpuStartTime;
	}

	/**
	 * @return the userStartTime
	 */
	public long getUserStartTime() {
		return userStartTime;
	}

	/**
	 * @return the executionEnd
	 */
	public long getExecutionEnd() {
		return executionEnd;
	}

	/**
	 * @param executionEnd the executionEnd to set
	 */
	public void setExecutionEnd(long executionEnd) {
		this.executionEnd = executionEnd;
	}

	/**
	 * @return the cpuEndTime
	 */
	public long getCpuEndTime() {
		return cpuEndTime;
	}

	/**
	 * @param cpuEndTime the cpuEndTime to set
	 */
	public void setCpuEndTime(long cpuEndTime) {
		this.cpuEndTime = cpuEndTime;
	}

	/**
	 * @return the userEndTime
	 */
	public long getUserEndTime() {
		return userEndTime;
	}

	/**
	 * @param userEndTime the userEndTime to set
	 */
	public void setUserEndTime(long userEndTime) {
		this.userEndTime = userEndTime;
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.CPUStatistics#getThreadCpuUsage()
	 */
	@Override
	public double getThreadCpuUsage() {
		if (executionEnd == 0) {
			return RUNNING_TIME;
		}
		if (executionEnd > executionStart
				&& cpuStartTime != INVALID_TIME && cpuEndTime != INVALID_TIME) {
            return ((cpuEndTime - cpuStartTime) * 100D) / ((executionEnd - executionStart) * PROCESSOR_COUNT);
        }
		return INVALID_TIME;
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.CPUStatistics#getThreadUserUsage()
	 */
	@Override
	public double getThreadUserUsage() {
		if (executionEnd == 0) {
			return RUNNING_TIME;
		}
		if (executionEnd > executionStart
				&& userStartTime != INVALID_TIME && userEndTime != INVALID_TIME) {
            return ((userEndTime - userStartTime) * 100D) / ((executionEnd - executionStart) * PROCESSOR_COUNT);
        }
		return INVALID_TIME;
	}

}
