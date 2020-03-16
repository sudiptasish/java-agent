package com.sc.hm.jvm.agent.transform;

import java.lang.instrument.ClassFileTransformer;
import java.util.List;
import java.util.Map;

/**
 * Interface that represents a classfile transformer.
 * 
 * @author Sudiptasish Chanda
 */
public interface RuntimeClassFileTranformer extends ClassFileTransformer {

	/**
	 * This map contains the class names and their corresponding method name(s)
	 * that are to be transformed by this transformer.
	 *  
	 * @param mapping
	 */
	void addClassMapping(Map<String, List<MethodInstrumentation>> mapping);
}
