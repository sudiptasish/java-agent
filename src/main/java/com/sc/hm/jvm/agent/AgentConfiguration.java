package com.sc.hm.jvm.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.sc.hm.jvm.agent.args.AgentArg;
import com.sc.hm.jvm.agent.util.AgentLogger;
import com.sc.hm.jvm.agent.util.AgentLogger.SEVERITY;

/**
 * Singleton class to stored various configurations to fast forward instrumentation.
 * 
 * @author Sudiptasish Chanda
 */
public class AgentConfiguration {

	private static final AgentConfiguration CONFIG = new AgentConfiguration();
	
	private final Map<String, AgentArg> configMapping = new HashMap<>();
	
	private AgentConfiguration() {}
	
	/**
	 * Return the singleton agent configuration
	 * @return	AgentConfiguration
	 */
	static AgentConfiguration getAgentConfig() {
		return CONFIG;
	}
	
	/**
	 * Override the existing property value for this key with the new value.
	 * @param key
	 * @param newValue
	 */
	public void setConfigProperty(String key, String newValue) {
	    AgentArg arg = configMapping.get(key);
        if (arg != null) {
            arg.setArgValue(newValue);
        }
        else {
            throw new IllegalArgumentException("No such property found for " + key);
        }
	}
	
	/**
	 * Return the configuration property for this key.
	 * If the value is provided by the user, then return the value.
	 * Otherwise return the default value as defined by this agent framework.
	 * 
	 * @param key
	 * @return
	 */
	public String getConfigProperty(String key) {
		AgentArg arg = configMapping.get(key);
		if (arg != null) {
			String value = arg.getArgValue();
			return value != null ? value : arg.getDefaultValue();
		}
		return null;
	}
	
	/**
	 * Add the new configuration property (key & value).
	 * @param key
	 * @param value
	 */
	public void addConfigProperty(String key, AgentArg value) {
		configMapping.put(key, value);
	}
	
	/**
	 * Add all the agent config properties
	 * @param props
	 */
	void addAllConfigProperties(Properties props) {
		AgentArgMetadata[] metadataSet = AgentArgMetadata.values();
		for (AgentArgMetadata metadata : metadataSet) {
			String value = props.getProperty(metadata.getName());
			configMapping.put(metadata.getName()
					, new AgentArg(metadata.getName()
							, value
							, metadata.getDefaultValue()));
		}
	}
	
	/**
	 * Print the current configuration to the console.
	 */
	public void print() {
		StringBuilder _buff = new StringBuilder(20);			
		for (Map.Entry<String, AgentArg> me : configMapping.entrySet()) {
			AgentArg value = me.getValue();				
			_buff.append("\n").append(value);
		}
		AgentLogger.log(SEVERITY.DEBUG, "Agent Configuration:" + _buff.toString());
	}
}
