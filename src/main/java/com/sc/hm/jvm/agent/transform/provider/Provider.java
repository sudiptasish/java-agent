package com.sc.hm.jvm.agent.transform.provider;

/**
 * Abstract Provider class which encapsulates the default provider.
 * 
 * @author Sudiptasish Chanda
 */
public abstract class Provider {

	private static final Provider DEFAULT = new TransformationProvider();
	
	protected Provider() {}
	
	/**
	 * Return transformation provider
	 * @return Provider
	 */
	public static Provider getDefault() {
		return DEFAULT;
	}
	
	/**
	 * Return the platform specific transformation Transformer
	 * @return	Transformer
	 */
	public abstract Transformer getTransformer();
}
