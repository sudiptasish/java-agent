package com.sc.hm.jvm.stats;

import java.util.Date;

/**
 * If all three kind of monitorinng is enabled, then these are exposed as a 
 * composite metric.
 * 
 * @author Sudiptasish Chanda
 */
public class CompositeStatistics implements TimeStatistics, CPUStatistics, StackStatistics {
	
	private final TimeStatisticsImpl timeStats;
	private final CPUStatisticsImpl cpuStat;
	private final StackStatisticsImpl stackStats;
	
	public CompositeStatistics(Date startTime
			, long executionStart
			, long cpuStartTime
			, long userStartTime
			, StackTraceElement[] ste
			, int depth) {
		
		timeStats = new TimeStatisticsImpl(startTime);
		cpuStat = new CPUStatisticsImpl(executionStart, cpuStartTime, userStartTime);
		stackStats = new StackStatisticsImpl(ste, depth);
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		timeStats.setEndTime(endTime);
	}

	/**
	 * @param executionEnd the executionEnd to set
	 */
	public void setExecutionEnd(long executionEnd) {
		cpuStat.setExecutionEnd(executionEnd);
	}

	/**
	 * @param cpuEndTime the cpuEndTime to set
	 */
	public void setCpuEndTime(long cpuEndTime) {
		cpuStat.setCpuEndTime(cpuEndTime);
	}

	/**
	 * @param userEndTime the userEndTime to set
	 */
	public void setUserEndTime(long userEndTime) {
		cpuStat.setUserEndTime(userEndTime);
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.TimeStatistics#getStartTime()
	 */
	@Override
	public Date getStartTime() {
		return timeStats.getStartTime();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.TimeStatistics#getEndTime()
	 */
	@Override
	public Date getEndTime() {
		return timeStats.getEndTime();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.TimeStatistics#getDuration()
	 */
	@Override
	public long getDuration() {
		return timeStats.getDuration();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.TimeStatistics#getElapsedTime()
	 */
	@Override
	public String getElapsedTime() {
		return timeStats.getElapsedTime();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.CPUStatistics#getThreadCpuUsage()
	 */
	@Override
	public double getThreadCpuUsage() {
		return cpuStat.getThreadCpuUsage();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.CPUStatistics#getThreadUserUsage()
	 */
	@Override
	public double getThreadUserUsage() {
		return cpuStat.getThreadUserUsage();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.StackStatistics#getCallStack()
	 */
	@Override
	public String getCallStack() {
		return stackStats.getCallStack();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.StackStatistics#getDepth()
	 */
	@Override
	public int getDepth() {
		return stackStats.getDepth();
	}
}
