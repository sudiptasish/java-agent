package com.sc.hm.jvm.trace;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A tracer manager, that stores the mapping between an instrumented class-method
 * pair and their associated {@link Tracer}.
 * 
 * @author Sudiptasish Chanda
 */
public class TraceManager {

	private static final TraceManager instance = new TraceManager();
	
	// This map contains the Tracer for a specific class and a method.
	// A typical key would be <class_name>~<method_name>
	private final ConcurrentMap<String, Tracer> traceMapping = new ConcurrentHashMap<>();
	
	private TraceManager() {}
	
	/**
	 * Rteurn the singleton trace manager.
	 * @return TraceManager
	 */
	public static TraceManager getManager() {
		return instance;
	}
	
	/**
	 * Return the Tracer object against this key.
	 * @param 	key
	 * @return	Tracer
	 */
	public Tracer getTracer(String key) {
		return traceMapping.get(key);
	}
	
	/**
	 * Add the Tracer.
	 * @param key
	 * @param tracer
	 */
	public void addTracer(String key, Tracer tracer) {
		traceMapping.put(key, tracer);
	}
	
	/**
	 * Dump the trace information to a log file/console.
	 */
	public void dump(Visitor visitor) {
		for (Map.Entry<String, Tracer> me : traceMapping.entrySet()) {
			Tracer tracer = me.getValue();
			tracer.accept(visitor);
		}
	}
}
