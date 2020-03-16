package com.sc.hm.jvm.trace;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sc.hm.jvm.agent.AgentArgMetadata;
import com.sc.hm.jvm.agent.AgentMainUtil;
import com.sc.hm.jvm.agent.util.AgentLogger;
import com.sc.hm.jvm.agent.util.AgentLogger.SEVERITY;
import com.sc.hm.jvm.stats.CPUStatistics;
import com.sc.hm.jvm.stats.CompositeStatistics;
import com.sc.hm.jvm.stats.MethodCallStatisticsImpl;
import com.sc.hm.jvm.stats.Statistics;
import java.util.HashMap;

/**
 * A simple method tracer.
 * 
 * @author Sudiptasish Chanda
 */
public class MethodTracer extends Tracer {
	
	private final String className;
	private final String methodName;
	
	// Statistics per thread call basis
	private final Map<String, List<Statistics>> statsMapping = new HashMap<>();
	
	// Run state per thread
	private final Map<String, STATE> stateMapping = new HashMap<>();
	
	// Object to keep the aggregated statistics for this method.
	private final MethodCallStatisticsImpl aggregateStats;
	
	private final String[] headers = {"Iteration"
            , "Thread Name"
            , "Execution Start"
            , "Execution End"
            , "Duration (millis)"
            , "Elapsed Time"
            , "CPU Time"
            , "User Time"
            , "Call Stack"};
	
	// Get a hanlde to the thread MXBean
	private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	
	MethodTracer(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
		this.aggregateStats = new MethodCallStatisticsImpl(methodName);
	}

	@Override
	public void start() {
	    if (!isEnabled()) {
	        return;
	    }        
		try {
			acquireWriteLock();
			AgentLogger.log(SEVERITY.DEBUG, String.format("Starting Method Trace for [%s]::[%s]", className, methodName));
			
			boolean cpuStatEnabled = "Y".equalsIgnoreCase(AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_CPU_STAT));
			boolean stackTraceStatEnabled = "Y".equalsIgnoreCase(AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_STACK_STAT));
			
			long id = Thread.currentThread().getId();
			String name = Thread.currentThread().getName();
			
			long cpuTime = cpuStatEnabled ? threadMXBean.getThreadCpuTime(id) : CPUStatistics.INVALID_TIME;
			long userTime = cpuStatEnabled ? threadMXBean.getThreadUserTime(id) : CPUStatistics.INVALID_TIME;
			
			STATE state = stateMapping.get(name);
			Statistics statistics = new CompositeStatistics(
					new Date()
					, System.nanoTime()
					, cpuTime
					, userTime
					, stackTraceStatEnabled ? Thread.currentThread().getStackTrace() : null
					, getDepth());
			
			if (state == null) {
				// New Entry
				List<Statistics> stats = new ArrayList<>();
				stats.add(statistics);
				
				statsMapping.put(name, stats);
				stateMapping.put(name, STATE.RUNNING);
			}
			else {
				List<Statistics> stats = statsMapping.get(name);
				if (state == STATE.RUNNING) {
					// Last run was not successful (could be due to some error).
					// Therefore, replace this entry with the new one.
					//stats.add(stats.size() - 1, statistics);
                    stats.set(stats.size() - 1, statistics);
				}
				else {
					stats.add(statistics);
					stateMapping.put(name, STATE.RUNNING);
				}
			}
			aggregateStats.incrementInvocationCount();
		}
		finally {
			releaseWriteLock();
		}
	}

	@Override
	public void end() {
	    if (!isEnabled()) {
            return;
        }        
        try {
			acquireWriteLock();
			AgentLogger.log(SEVERITY.DEBUG, String.format("Finishing Method Trace for [%s]::[%s]", className, methodName));
			
			boolean cpuStatEnabled = "Y".equalsIgnoreCase(AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_CPU_STAT));			
			long id = Thread.currentThread().getId();
			String name = Thread.currentThread().getName();
			
			long cpuTime = cpuStatEnabled ? threadMXBean.getThreadCpuTime(id) : CPUStatistics.INVALID_TIME;
			long userTime = cpuStatEnabled ? threadMXBean.getThreadUserTime(id) : CPUStatistics.INVALID_TIME;
			
			List<Statistics> stats = statsMapping.get(name);
			
			CompositeStatistics statistics = (CompositeStatistics)stats.get(stats.size() - 1);
			statistics.setEndTime(new Date());
			statistics.setExecutionEnd(System.nanoTime());
			statistics.setCpuEndTime(cpuTime);
			statistics.setUserEndTime(userTime);
			
			stateMapping.put(name, STATE.COMPLETE);
			
			aggregateStats.setDuration(statistics.getDuration());
		}
		finally {
			releaseWriteLock();
		}
	}
	
	/**
	 * Check if metric collection is enabled.
	 * @return boolean
	 */
	private boolean isEnabled() {
	    return "Y".equalsIgnoreCase(AgentMainUtil.getConfigProperty(AgentArgMetadata.ENABLE_COLLECTION));
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.trace.Visitable#accept(com.sc.hm.jvm.trace.Visitor)
	 */
	@Override
	public void accept(Visitor v) {
		try {
			acquireReadLock();
			v.visit(this);
		}
		finally {
			releaseReadLock();
		}
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.trace.Tracer#clearStats()
	 */
	@Override
	public void clearStats() {
		try {
			acquireWriteLock();
			clearInternal();
			
			AgentLogger.log(SEVERITY.DEBUG, String.format("Cleared Statistics for [%s]::[%s]", className, methodName));
		}
		finally {
			releaseWriteLock();
		}
	}
	
	private void clearInternal() {
		for (Iterator<Map.Entry<String, STATE>> itr = stateMapping.entrySet().iterator(); itr.hasNext(); ) {
			Map.Entry<String, STATE> me = itr.next();
			if (me.getValue() == STATE.COMPLETE) {
				statsMapping.remove(me.getKey());
				itr.remove();
			}
			else {
				List<Statistics> stats = statsMapping.get(me.getKey());
				// Remove all but last record.
				List<Statistics> newStats = new ArrayList<>();
				newStats.add(stats.get(stats.size() - 1));
				statsMapping.put(me.getKey(), newStats);
			}
		}
        aggregateStats.reset();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.trace.Tracer#drainTo(com.sc.hm.jvm.trace.Dumper)
	 */
	@Override
	public void drainTo(Dumper d) {
		gatherStats(d);
		clearInternal();
	}

	/**
	 * Dump the regular statistics
	 * @param d
	 */
	private void gatherStats(Dumper d) {
	    Properties metadata = new Properties();
	    
	    metadata.setProperty(Dumper.CLASS_NAME, className.indexOf(".") > 0 ? className.substring(className.lastIndexOf(".") + 1) : className);
	    metadata.setProperty(Dumper.METHOD_NAME, methodName);
	    metadata.setProperty(Dumper.INVOCATION_COUNT, String.valueOf(aggregateStats.getInvocationCount()));
	    metadata.setProperty(Dumper.MAX_DURATION, String.valueOf(aggregateStats.getMaxDuration()));
	    
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		NumberFormat nFormat = new DecimalFormat("0.00");
		
		boolean cpuStatEnabled = "Y".equalsIgnoreCase(AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_CPU_STAT));
		boolean stackTraceStatEnabled = "Y".equalsIgnoreCase(AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_STACK_STAT));
		
		TabularData tabularData = new TabularData();
		tabularData.addMetadata(metadata);
		tabularData.setHeaders(headers);
		
		for (Iterator<Map.Entry<String, List<Statistics>>> itr = statsMapping.entrySet().iterator(); itr.hasNext(); ) {
			Map.Entry<String, List<Statistics>> me = itr.next();
			String threadName = me.getKey();
			List<Statistics> stats = me.getValue();
			
			for (int i = 0; i < stats.size(); i ++) {
				CompositeStatistics statistics = (CompositeStatistics)stats.get(i);
				String[] arr = new String[] {
						String.valueOf(i + 1)
						, threadName
						, df.format(statistics.getStartTime())
						, df.format(statistics.getEndTime() != null ? statistics.getEndTime() : new Date())
						, String.valueOf(statistics.getDuration())
						, statistics.getElapsedTime()
						, !cpuStatEnabled
							? "Disabled"
								: CPUStatistics.RUNNING_TIME == statistics.getThreadCpuUsage()
									? "Running" : (nFormat.format(statistics.getThreadCpuUsage()) + " %")
						, !cpuStatEnabled
							? "Disabled"
								: CPUStatistics.RUNNING_TIME == statistics.getThreadCpuUsage()
									? "Running" : (nFormat.format(statistics.getThreadUserUsage()) + " %")
						, stackTraceStatEnabled ? statistics.getCallStack() : "Disabled"
					};
				tabularData.addData(arr);
			}
		}
		d.addTabularData(tabularData);		
	}
}
