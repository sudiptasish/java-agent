package com.sc.hm.jvm.trace;

/**
 * 
 * @author Sudiptasish Chanda
 */
public interface Visitor {

	/**
	 * Visit the visitable
	 * @param v
	 */
	void visit(Visitable v);
	
	/**
	 * Return the dumper object
	 * @return
	 */
	Dumper getDumper();
	
	/**
	 * Check to see if this visitor has any result
	 * @return	boolean
	 */
	boolean hasResult();
}
