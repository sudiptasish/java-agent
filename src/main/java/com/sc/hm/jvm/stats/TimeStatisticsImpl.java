package com.sc.hm.jvm.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

/**
 * 
 * @author Sudiptasish Chanda
 */
public class TimeStatisticsImpl implements TimeStatistics {

	private final Date startTime;
	private Date endTime;
	
	public TimeStatisticsImpl(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.trace.TimeStatistics#getDuration()
	 */
	@Override
	public long getDuration() {
		return endTime != null
				? endTime.getTime() - startTime.getTime()
						: new Date().getTime() - startTime.getTime();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.trace.TimeStatistics#getElapsedTime()
	 */
	@Override
	public String getElapsedTime() {
		NumberFormat nFormat = new DecimalFormat("0.00");
		long millis = getDuration();
		
		boolean running = endTime == null;
		
		if (millis == 0L) {
			return running ? "&nbsp;(Running)" : "";
		}
		else if (millis < 1000) {
			return millis + " Millis" + (running ? "&nbsp;(Running)" : "");
		}
		else if (millis >= 1000 && millis < 60 * 1000) {
			return nFormat.format(millis / (1000D)) + " Sec." + (running ? "&nbsp;(Running)" : "");
		}
		else if (millis >= 60 * 1000 && millis < 60 * 60 * 1000) {
			return nFormat.format(millis / (60 * 1000D)) + " Min." + (running ? "&nbsp;(Running)" : "");
		}
		else if (millis >= 60 * 60 * 1000 && millis < 24 * 60 * 60 * 1000) {
			return nFormat.format(millis / (60 * 60 * 1000D)) + " Hr." + (running ? "&nbsp;(Running)" : "");
		}
		else {
			return nFormat.format(millis / (24 * 60 * 60 * 1000D)) + " Day" + (running ? "&nbsp;(Running)" : "");
		}
	}

}
