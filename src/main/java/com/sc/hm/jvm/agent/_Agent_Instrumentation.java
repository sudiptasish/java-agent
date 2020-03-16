package com.sc.hm.jvm.agent;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

import com.sc.hm.jvm.agent.transform.RuntimeClassFileTranformer;

/**
 * INTERNAL.
 * 
 * @author Sudiptasish Chanda
 */
public final class _Agent_Instrumentation {

	private static final _Agent_Instrumentation _a_inst = new _Agent_Instrumentation();
	
	private boolean initialized = false;
	
	private Instrumentation _inst;
	
	private _Agent_Instrumentation() {}
	
	/**
	 * Return the agent instrumentation.
	 * @return	_Agent_Instrumentation
	 */
	static _Agent_Instrumentation _get() {
		return _a_inst;
	}
	
	/**
	 * Initialize the instrumentation agent wrapper.
	 * @param _inst
	 */
	public void _initialize(Instrumentation _inst) {
		if (_is_initialized()) {
			throw new IllegalStateException("Agent's state is already initialized");
		}
		this._inst = _inst;
		this.initialized = true;
	}

	/**
	 * @return the initialized
	 */
	public boolean _is_initialized() {
		return initialized;
	}
	
	/**
	 * Add a class file transformer
	 * @param ctTransformer
	 */
	public void _add_transformer(RuntimeClassFileTranformer ctTransformer) {
		_inst.addTransformer(ctTransformer, true);
	}
	
	/**
	 * Remove the specific class file transformer
	 * @param ctTransformer
	 */
	public void _remove_transformer(RuntimeClassFileTranformer ctTransformer) {
		_inst.removeTransformer(ctTransformer);
	}
	
	/**
	 * Re transform classes
	 * @param classes
	 * @throws UnmodifiableClassException
	 */
	public void _retransform_classes(Class<?>... classes) throws UnmodifiableClassException {
		_inst.retransformClasses(classes);
	}
	
	/**
	 * Re define the specified classes.
	 * 
	 * @param definitions
	 * @throws ClassNotFoundException
	 * @throws UnmodifiableClassException
	 */
	public void _redefine_classes(ClassDefinition... definitions)
			throws ClassNotFoundException, UnmodifiableClassException {
		
		_inst.redefineClasses(definitions);
	}
	
	/**
	 * Calculate the size of the object supplied.
	 * @param obj
	 * @return	long
	 */
	public long _calculate_size(Object obj) {
		return _inst.getObjectSize(obj);
	}
	
	/**
	 * Check to see if re transformation of classes is supported.
	 * @return	boolean
	 */
	public boolean is_retransformation_supported() {
		return _inst.isRetransformClassesSupported();
	}
	
	/**
	 * Check to see if re definition of classes is supported.
	 * @return	boolean
	 */
	public boolean is_redefine_supported() {
		return _inst.isRedefineClassesSupported();
	}
	
	/**
	 * Return the list of classes loaded by this JVM.
	 * @return	Class[]
	 */
	public Class[] _loaded_classes() {
		return _inst.getAllLoadedClasses();
	}
}
