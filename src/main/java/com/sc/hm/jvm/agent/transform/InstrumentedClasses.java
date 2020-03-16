package com.sc.hm.jvm.agent.transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class acts as a repository to hold the classes that have just been instrumented.
 * Along with that, it also caches the old version (bytecode) of the respective
 * classes.
 * 
 * @author Sudiptasish Chanda
 */
public final class InstrumentedClasses {

	private final IdentityHashMap<ClassLoader, Set<String>> class_mapping = new IdentityHashMap<>();
	private final IdentityHashMap<ClassLoader, Map<String, byte[]>> byte_mapping = new IdentityHashMap<>();
    
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	public InstrumentedClasses() {}
	
	/**
	 * Check if this clas as identified by className loaded by this classloader
	 * is already instrumented.
	 * 
	 * @param cLoader       The classloader responsible for loading this class
	 * @param className     The classname to be innstrumented.
	 * @return	boolean
	 */
	public boolean isInstrumented(ClassLoader cLoader, String className) {
		try {
			lock.readLock().lock();
			if (class_mapping.containsKey(cLoader)) {
				Set<String> instrumentedSet = class_mapping.get(cLoader);
				return instrumentedSet.contains(className);
			}
			return false;
		}
		finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Add the class as loaded by this classloader to instrumented set.
     * It will keep a backup of the old version of the class.
	 * 
	 * @param cLoader       The classloader responsible for loading this class
	 * @param className     The classname to be innstrumented.
	 * @param original      The original version (bytecode) of the class.
	 */
	public void addInstrumentedClass(ClassLoader cLoader, String className, byte[] original) {
		try {
			lock.writeLock().lock();
			Set<String> instrumentedSet = class_mapping.get(cLoader);
			
			if (instrumentedSet == null) {
			    instrumentedSet = new HashSet<String>();
			    class_mapping.put(cLoader, instrumentedSet);
			}
			instrumentedSet.add(className);
			
			Map<String, byte[]> originalClasses = byte_mapping.get(cLoader);
			if (originalClasses == null) {
			    originalClasses = new HashMap<>();
			    byte_mapping.put(cLoader, originalClasses);
			}
			originalClasses.put(className, original); 
		}
		finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Remove the instrumented class entries.
     * 
	 * @param cLoader       The classloader responsible for loading this class
	 * @param className     The classname to be innstrumented.
	 */
	public void removeInstrumentedClass(ClassLoader cLoader, String className) {
	    try {
            lock.writeLock().lock();
            Set<String> instrumentedSet = class_mapping.get(cLoader);
            if (instrumentedSet != null) {
                instrumentedSet.remove(className);
            }
            Map<String, byte[]> originalClasses = byte_mapping.get(cLoader);
            if (originalClasses != null) {
                originalClasses.remove(className);
            }
	    }
	    finally {
	        lock.writeLock().unlock();
	    }
	}
	
	/**
	 * Return the original class file bytes (prior instrumentation).
	 * It will remove the original class bytes from the in-memory storage.
	 * 
	 * @param cLoader       The classloader responsible for loading this class
	 * @param className     The classname to be innstrumented.
     * 
	 * @return byte[]
	 */
	public byte[] getOriginalClassBytes(ClassLoader cLoader, String className) {
	    try {
	        lock.readLock().lock();
	        
    	    Map<String, byte[]> originalClasses = byte_mapping.get(cLoader);
    	    if (originalClasses != null) {
    	        return originalClasses.get(className);
    	    }
    	    return null;
	    }
	    finally {
	        lock.readLock().unlock();
	    }
	}
}
