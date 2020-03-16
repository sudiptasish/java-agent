package com.sc.hm.jvm.agent.transform.provider;

import com.sc.hm.jvm.agent.transform.InstrumentedClasses;
import com.sc.hm.jvm.agent.transform.MethodInstrumentation;

/**
 * Represent a transformer that helps trannsform a class.
 * 
 * Different provider can have their own transformer, but ensure they override the
 * {@link transform} method.
 * 
 * @author Sudiptasish Chanda
 */
public abstract class Transformer {
	
	private final InstrumentedClasses instrumentSet;
	
	protected Transformer() {
		instrumentSet = new InstrumentedClasses();
	}

	/**
	 * Transfor the class bytes by BCI.
	 * 
	 * @param className
	 * @param _buff
	 * @param methodInsts
	 * @param loader
	 * @return
	 */
	public abstract byte[] transform(String className
			, byte[] _buff
			, MethodInstrumentation[] methodInsts
			, ClassLoader loader);
	
	/**
	 * Check the instrumented set to see whether this class is already instrumented.
	 * 
	 * @param cLoader
	 * @param className
	 * @return
	 */
	public boolean alreadyInstrumented(ClassLoader cLoader, String className) {
		return instrumentSet.isInstrumented(cLoader, className);
	}
	
	/**
	 * Add the instrumemted class to the existing list.
	 * 
	 * @param cLoader
	 * @param className
	 * @param original
	 */
	public void addInstrumentedClass(ClassLoader cLoader, String className, byte[] original) {
		instrumentSet.addInstrumentedClass(cLoader, className, original);
	}
	
	/**
	 * Return the original class file bytes.
	 * @param cLoader
	 * @param className
	 * @return byte[]
	 */
	public byte[] flushAndGetOriginalClassBytes(ClassLoader cLoader, String className) {
	    byte[] _buff = instrumentSet.getOriginalClassBytes(cLoader, className);
	    instrumentSet.removeInstrumentedClass(cLoader, className);
	    
	    return _buff;
	}
}
