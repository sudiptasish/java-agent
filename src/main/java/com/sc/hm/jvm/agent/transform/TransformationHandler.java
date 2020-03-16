package com.sc.hm.jvm.agent.transform;

import com.sc.hm.jvm.agent.exception.AgentException;

/**
 * Transformation handler.
 * It defines the methods to bootstrap the provider specific transformer.
 * 
 * @author Sudiptasish Chanda
 */
public abstract class TransformationHandler {
	
	private static final String MAPPING_REG_EX = "[a-z_A-Z0-9\\.]{1,}-\\{[a-z_A-Z0-9$]{1,}[\\([0-9]{1,}[:]{1}[0-9]{0,}\\)]{0,}\\}";
    
    // com.sc.hm.jvm.main.JVMMonitor-{execute(5:24)}

	protected TransformationHandler() {}
	
	/**
	 * Parse the args and prepare the transformer if needed.
	 * It calls prepare(false).
	 */
	public abstract void prepare() throws AgentException;
	
	/**
	 * Parse the args and prepare the transformer if needed.
	 * The additional flag reTransformNeeded determines if an on-demand 
	 * retransformation is required or not.
	 * 
	 * @param reTransformNeeded
	 * @throws AgentException
	 */
	public abstract void prepare(boolean reTransformNeeded) throws AgentException;
	
	/**
	 * Return the first kind of regular expression.
	 * @return
	 */
	protected String getMappingRegX() {
		return MAPPING_REG_EX;
	}
}
