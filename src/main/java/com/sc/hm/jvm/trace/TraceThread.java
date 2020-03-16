package com.sc.hm.jvm.trace;

import com.sc.hm.jvm.agent.AgentArgMetadata;
import com.sc.hm.jvm.agent.AgentMainUtil;
import com.sc.hm.jvm.agent.mbean.GlobalConfig;

/**
 * 
 * @author Sudiptasish Chanda
 */
public class TraceThread implements Runnable {
	
	private final int dumpIntervalSecond;
	
	private boolean running = true;
	
	public TraceThread(int dumpIntervalSecond) {
		this.dumpIntervalSecond = dumpIntervalSecond;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	@Override
	public void run() {
		while (isRunning() && !Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(dumpIntervalSecond * 1000);
				// Check if agent is already unloaded, if so, do not collect any metrics.
				boolean isEnabled = "Y".equals(AgentMainUtil.getConfigProperty(AgentArgMetadata.ENABLE_COLLECTION));
				
				if (isEnabled && !GlobalConfig.AGENT_UNLOADED) {
				    TraceHelper.doWork();
				}
			}
			catch (InterruptedException e) {
				return;
			}
		}
	}
	
	/**
	 * Stop this trace thread
	 */
	public void stop() {
		this.running = false;
	}
}
