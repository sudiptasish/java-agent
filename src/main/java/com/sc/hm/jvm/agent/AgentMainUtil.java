package com.sc.hm.jvm.agent;

import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;

import com.sc.hm.jvm.agent.transform.RuntimeClassFileTranformer;

/**
 * 
 * @author Sudiptasish Chanda
 */
public class AgentMainUtil {

	private static final _Agent_Instrumentation _agent_inst = _Agent_Instrumentation._get();
	
	private static final AgentConfiguration agentConfig = AgentConfiguration.getAgentConfig();
	
	/**
	 * Return the configuration property.
	 * @param key
	 * @return
	 */
	public static String getConfigProperty(String key) {
		return agentConfig.getConfigProperty(key);
	}
	
	/**
	 * Return the configuration property.
	 * It internally calls getConfigProperty(AgentArgsMetadata.getName()).
	 * 
	 * @param argMD
	 * @return
	 */
	public static String getConfigProperty(AgentArgMetadata argMD) {
		return getConfigProperty(argMD.getName());
	}
	
	/**
	 * Set the new config property.
	 * @param key
	 * @param value
	 */
	public static void setConfigProperty(String key, String value) {
	    agentConfig.setConfigProperty(key, value);
	}
	
	/**
	 * Print The agent configuration.
	 */
	public static void printConfig() {
		agentConfig.print();
	}
	
	/**
	 * Add the class file transformer to the instrumentation agent.
	 * @param ctTransformer
	 */
	public static void addClassFileTransformer(RuntimeClassFileTranformer ctTransformer) {
		_agent_inst._add_transformer(ctTransformer);
	}
	
	/**
	 * Retransform the specific classes.
	 * It may throw UnmodifiableClassException if any of the specified
	 * classes can not be modified.
	 * 
	 * @param classes
	 * @throws UnmodifiableClassException
	 */
	public static void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {
		_agent_inst._retransform_classes(classes);
	}
	
	/**
	 * Return the list of classes loaded by this JVM.
	 * @return
	 */
	public static Class<?>[] getLoadedClass() {
		return _agent_inst._loaded_classes();
	}
	
	/**
	 * Return the process id.
	 * @return	String
	 */
	public static String getProcessId() {
		return ManagementFactory.getRuntimeMXBean().getName();
	}
}
