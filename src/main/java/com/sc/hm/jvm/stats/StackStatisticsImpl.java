package com.sc.hm.jvm.stats;

/**
 * 
 * @author Sudiptasish Chanda
 */
public class StackStatisticsImpl implements StackStatistics {
	
	private final int depth;
	private String callStack = null;
	
	public StackStatisticsImpl(StackTraceElement[] ste, int depth) {
		this.depth = depth;
		
		if (ste != null && ste.length > 0) {
			StringBuilder _buff = new StringBuilder(64);			
			// Skip the top 2 classes (Thread, MethodTracer)
			for (int i = 2; (i < 2 + depth) && (i < ste.length); i ++) {
				String className = ste[i].getClassName();
				className = className.indexOf(".") > 0 ? className.substring(className.lastIndexOf(".") + 1) : className;
				_buff.append(i > 2 ? "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" : "");
				_buff.append(className).append(":").append(ste[i].getMethodName());			
			}
			this.callStack = _buff.toString();
		}
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.StackStatistics#getCallStack()
	 */
	@Override
	public String getCallStack() {
		return callStack;
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.stats.StackStatistics#getDepth()
	 */
	@Override
	public int getDepth() {
		return depth;
	}

}
