package com.sc.hm.jvm.trace;

import java.util.concurrent.atomic.AtomicInteger;

import com.sc.hm.jvm.agent.AgentArgMetadata;
import com.sc.hm.jvm.agent.AgentMainUtil;

/**
 * A tracer object is responsible for tracing a flow.
 * Every class and method pair that is instrumented will automatically get a tracer
 * assigned, which will help generating the statistics.
 * 
 * @author Sudiptasish Chanda
 */
public abstract class Tracer implements Visitable {
	
	public static enum STATE {RUNNING, COMPLETE};
	
	private static final int IDLE = 0;
	private static final int READING = 1;
	private static final int WRITING = 2;
	
	private final AtomicInteger lock = new AtomicInteger(IDLE);
		
	private static final TraceManager manager = TraceManager.getManager();
	
	private int depth = 4;
	
	protected Tracer() {}
	
	/**
	 * Return a method tracer instance.
	 * This API will first check with the trace manager if a tracer object exists
	 * for this method defined in the caller class. If such an instance exists,
	 * return the same, else create a new one.
	 *  
	 * @return	Tracer
	 */
	public static Tracer methodTracer() {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String key = ste[2].getClassName() + "~" + ste[2].getMethodName();
		
		Tracer tracer = manager.getTracer(key);
		if (tracer == null) {
		    synchronized(Tracer.class) {
    			tracer = new MethodTracer(ste[2].getClassName(), ste[2].getMethodName());
    			tracer.setDepth();
    			manager.addTracer(key, tracer);
		    }
		}
		return tracer;
	}
	
	/**
	 * Acquire the write lock on this tracer.
	 */
	protected void acquireWriteLock() {
		while (!lock.compareAndSet(IDLE, WRITING)) {
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				return;
			}
		}
	}
	
	/**
	 * Release the write lock on this tracer.
	 */
	protected void releaseWriteLock() {
		while (!lock.compareAndSet(WRITING, IDLE)) {
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				return;
			}
		}
	}
	
	/**
	 * Acquire the read lock on this tracer.
	 */
	protected void acquireReadLock() {
		while (!lock.compareAndSet(IDLE, READING)) {
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				return;
			}
		}
	}
	
	/**
	 * Release the read lock on this tracer.
	 */
	protected void releaseReadLock() {
		while (!lock.compareAndSet(READING, IDLE)) {
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				return;
			}
		}
	}

	/**
	 * Start a tracing.
	 */
	public abstract void start();
	
	/**
	 * End tracing.
	 */
	public abstract void end();
	
	/**
	 * Clear the statistics.
	 */
	public abstract void clearStats();
	
	/**
	 * Drain the current statistics
	 * @param d
	 */
	public abstract void drainTo(Dumper d);
	
	/**
	 * Set the depth of the call stack.
	 */
	public void setDepth() {
		String depthString = AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_STACK_DEPTH);
		if (depthString != null) {
			try {
				depth = Integer.parseInt(depthString);
			}
			catch (NumberFormatException e) {
				// Do Nothing
			}
		}
	}

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}
}
