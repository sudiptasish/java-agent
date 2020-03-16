package com.sc.hm.jvm.agent.util;

import java.util.Enumeration;
import java.util.Properties;

import com.sc.hm.jvm.agent.AgentArgMetadata;
import com.sc.hm.jvm.agent.exception.AgentException;

/**
 * A simple validator class to validate various agent configurations to ensure 
 * they are compliant.
 * 
 * @author Sudiptasish Chanda
 */
public final class Validator {
	
	/**
	 * Verify the list of arguments.
     * 
	 * @param props
	 * @return
	 */
	public static boolean verifyArgument(Properties props) throws AgentException {
		AgentArgMetadata[] metadataSet = AgentArgMetadata.values();
		
		// First, check for any missing mandatory parameter.
		for (AgentArgMetadata metadata : metadataSet) {
			if (metadata.isMandatory() && !props.containsKey(metadata.getName())) {
				throw new AgentException(String.format("Missing mandatory parameter [%s]", metadata.getName()));
			}
		}
		// Now, check for any unknown parameter.
		for (Enumeration enm = props.propertyNames(); enm.hasMoreElements(); ) {
			String key = (String)enm.nextElement();
			String value = props.getProperty(key);
			boolean found = false;
			
			for (AgentArgMetadata metadata : metadataSet) {
				if (metadata.getName().equals(key)) {
					if (metadata.getDataType() == Integer.class) {
						try {
							Integer.parseInt(value);
						}
						catch (NumberFormatException e) {
							throw new AgentException(
									String.format("Invalid data type. Expected %s, Found %s"
											, metadata.getDataType().getSimpleName()
											, value));
						}
					}
					else if (metadata.getDataType() == Boolean.class) {
						if (!(value.equals("true") || value.equals("false"))) {
							throw new AgentException(
									String.format("Invalid data type. Expected %s, Found %s"
											, metadata.getDataType().getSimpleName()
											, value));
						}
					}
					found = true;
					break;
				}
			}
			if (!found) {
				throw new AgentException(String.format("Wrong argument [%s] specified", key));
			}
		}
		return true;
	}
}
