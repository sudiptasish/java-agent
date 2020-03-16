package com.sc.hm.jvm.trace;

/**
 * 
 * @author Sudiptasish Chanda
 */
public interface Visitable {

	/**
	 * Accept the visitor.
	 * @param v
	 */
	void accept(Visitor v);
}
