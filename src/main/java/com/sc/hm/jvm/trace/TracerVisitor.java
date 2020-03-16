package com.sc.hm.jvm.trace;

/**
 * 
 * @author Sudiptasish Chanda
 */
public class TracerVisitor implements Visitor {
	
	private final Dumper d;	
	
	public TracerVisitor() {
		d = new StatsDumper();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.trace.Visitor#visit(com.sc.hm.jvm.trace.Visitable)
	 */
	@Override
	public void visit(Visitable v) {
		if (v instanceof Tracer) {
			((Tracer)v).drainTo(d);
		}
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.trace.Visitor#getDumper()
	 */
	@Override
	public Dumper getDumper() {
		return d;
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.trace.Visitor#hasResult()
	 */
	@Override
	public boolean hasResult() {
		return d.hasStatistics();
	}
}
