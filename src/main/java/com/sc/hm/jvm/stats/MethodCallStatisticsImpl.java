package com.sc.hm.jvm.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author Sudiptasish Chanda
 */
public class MethodCallStatisticsImpl implements MethodCallStatistics {
	
	private final String methodName;
	private final AtomicLong invocationCount = new AtomicLong(0L);
	private final AtomicLong maxDuration = new AtomicLong(0L);
	
	public MethodCallStatisticsImpl(String methodName) {
		this.methodName = methodName;
	}
	
	/**
	 * Increment the method invocation counter.
	 */
	public void incrementInvocationCount() {
		invocationCount.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.MethodCallStatistics#getMethodName()
	 */
	@Override
	public String getMethodName() {
		return methodName;
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.MethodCallStatistics#getInvocationCount()
	 */
	@Override
	public long getInvocationCount() {
		return invocationCount.get();
	}

	/**
	 * @param duration the maxDuration to set
	 */
	public void setDuration(long duration) {
		long currDuration = 0L;
		while (duration > (currDuration = maxDuration.get())) {
			maxDuration.compareAndSet(currDuration, duration);
		}
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.MethodCallStatistics#getMaxDuration()
	 */
	@Override
	public long getMaxDuration() {
		return maxDuration.get();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.MethodCallStatistics#getMaxElapsedTime()
	 */
	@Override
	public String getMaxElapsedTime() {
		NumberFormat nFormat = new DecimalFormat("0.00");
		long millis = maxDuration.get();
		
		if (millis < 1000) {
			return millis + " Millis";
		}
		else if (millis >= 1000 && millis < 60 * 1000) {
			return nFormat.format(millis / (1000D)) + " Sec.";
		}
		else if (millis >= 60 * 1000 && millis < 60 * 60 * 1000) {
			return nFormat.format(millis / (60 * 1000D)) + " Min.";
		}
		else if (millis >= 60 * 60 * 1000 && millis < 24 * 60 * 60 * 1000) {
			return nFormat.format(millis / (60 * 60 * 1000D)) + " Hr.";
		}
		else {
			return nFormat.format(millis / (24 * 60 * 60 * 1000D)) + " Day";
		}
	}

    public void reset() {
        invocationCount.set(0L);
        maxDuration.set(0L);
    }
}
