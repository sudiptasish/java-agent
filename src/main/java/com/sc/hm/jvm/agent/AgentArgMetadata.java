package com.sc.hm.jvm.agent;

/**
 * Enum that contains the various configuration options along with the default
 * values, required for the java agent.
 * 
 * @author Sudiptasish Chanda
 */
public enum AgentArgMetadata {

	// Specify the custom transformation handler.
	// If no handler is specified, then the default transformation handler will be used.
	AGENT_HANDLER ("handler"
			, String.class
			, false
			, "com.sc.hm.jvm.agent.transform.DefaultTransformationHandler"),
	
	// Specify the class file transformer.
	// If no transformer class is specified, then the platform specific transfomrer will be used.
	AGENT_TRANSFORMER ("transformer"
			, String.class
			, false
			, "com.sc.hm.jvm.agent.transform.PlatformClassFileTransformer"),
			
	// Specify the transformation provider.
	// If no transformer class is specified, then the default javassist transformer library will be used.
	AGENT_TRANSFORMER_PROVIDER ("provider"
			, String.class
			, false
			, "com.sc.hm.jvm.agent.transform.provider.JavassistTransformer"),
			
	// Specify if statistics collection and dump is enabled.
	AGENT_TRACE_DUMP ("dump"
			, String.class
			, false
			, "Y"),
			
	// Specify the interval (in seconds), in which the tracer thread will dump the
	// statistics/logs into the file.
	AGENT_DUMP_INTERVAL ("interval"
			, Integer.class
			, false
			, "900"),
			
	// Specify if time based statistics is enabled.
	AGENT_TIME_STAT ("t_stat"
			, String.class
			, false
			, "Y"),
			
	// Specify if CPU statistics is enabled.
	AGENT_CPU_STAT ("c_stat"
			, String.class
			, false
			, "Y"),
			
	// Specify if method aggregated statistics is enabled.
	AGENT_METHOD_STAT ("m_stat"
			, String.class
			, false
			, "Y"),
			
	// Specify if method call stack statistics is enabled.
	AGENT_STACK_STAT ("s_stat"
			, String.class
			, false
			, "Y"),
			
	// Specify the depth of the stack trace.
	// Default value is 6.
	AGENT_STACK_DEPTH ("depth"
			, Integer.class
			, false
			, "6"),
	
	// Fully qualified path of the file which has the class/method details
	// that are to be instrumented.
	AGENT_TRANSFORM_FILE ("t_file"
			, String.class
			, true
			, null),
	
	// Specify the directory where the dump file be created.
	// if no dump directory is specified then the dump file will be created under current user directory. 
	// File name will be in the following format:
	// <process id>_trace_<timestamp>.html
	AGENT_DUMP_DIRECTORY ("b_dump"
			, String.class
			, false
			, System.getProperty("user.dir")),
	
	// Specify the directory where the instrumented class file will be dumped.
	// If no directory is specified, then instrumented class file won't be written.
	// This is used to see wherther byte code is successfully injected into the class file.
	BYTECODE_DUMP_DIR ("c_dump"
			, String.class
			, false
			, null),
			
	// If set to true, then the logs will be printed.
	// If no log directory is specified then output will be printed on the console.
	AGENT_VERBOSE ("verbose"
			, String.class
			, false
			, "Y"),
	
	// Specify the log file name for this agent.
	// If set, then all logs will be redirected to this file.
	AGENT_LOG_DIR ("logdir"
			, String.class
			, false
			, null),
		    
    // Specify whether javaassist classloader should be enabled.
    AGENT_ASSIST_CLOADER ("enable_classloading"
            , String.class
            , false
            , "N"),
            
    // Complete path to javaassist library.
    JAVA_ASSIST_LIB ("java_assist_lib"
            , String.class
            , false
            , null),
    
    // Specify whether metric collection will be enabled..
    ENABLE_COLLECTION ("enable_collection"
            , String.class
            , false
            , "Y");
	
	private final String name;
	private final Class<?> dataType;
	private final boolean mandatory;
	private final String defaultValue;
	
	public static AgentArgMetadata from(String option) {
	    if (option.equals("-m")) {
	        return AgentArgMetadata.AGENT_TRANSFORM_FILE;
	    }
	    else if (option.equals("-d")) {
	        return AgentArgMetadata.AGENT_TRACE_DUMP;
	    }
	    else if (option.equals("-i")) {
	        return AgentArgMetadata.AGENT_DUMP_INTERVAL;
	    }
	    else if (option.equals("-o")) {
	        return AgentArgMetadata.AGENT_DUMP_DIRECTORY;
	    }
	    else if (option.equals("-v")) {
	        return AgentArgMetadata.AGENT_VERBOSE;
	    }
	    else if (option.equals("-l")) {
	        return AgentArgMetadata.AGENT_LOG_DIR;
	    }
        else if (option.equals("-c")) {
            return AgentArgMetadata.BYTECODE_DUMP_DIR;
        }
        else if (option.equals("-s")) {
            return AgentArgMetadata.AGENT_ASSIST_CLOADER;
        }
        else if (option.equals("-j")) {
            return AgentArgMetadata.JAVA_ASSIST_LIB;
        }
	    throw new IllegalArgumentException("Invalid option " + option + " specified");
	}
	
	AgentArgMetadata(String name, Class<?> dataType, boolean mandatory, String defaultValue) {
		this.name = name;
		this.dataType = dataType;
		this.mandatory = mandatory;
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the dataType
	 */
	public Class<?> getDataType() {
		return dataType;
	}

	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
}
