package com.sc.hm.jvm.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sc.hm.jvm.agent.exception.AgentException;
import com.sc.hm.jvm.agent.mbean.AgentMBeanHandler;
import com.sc.hm.jvm.agent.transform.TransformationHandler;
import com.sc.hm.jvm.agent.util.AgentLogger;
import com.sc.hm.jvm.agent.util.AgentLogger.SEVERITY;
import com.sc.hm.jvm.agent.util.Validator;
import com.sc.hm.jvm.agent.util._ObjectCreator;
import com.sc.hm.jvm.trace.TraceThread;

/**
 * Agent startup class.
 * 
 * This class performs the following tasks (in order):
 * 1. Parse the command line argument and validate them with the agent metadata.
 * 2. Initialize the agent logger (if enabled).
 * 3. Add the class loader hook, only if the provider library is not already loaded
 *    by the current JVM.
 * 4. Initialize the transformation layer.
 * 5. Register the MBeans for monitoring instrumentation.
 * 6. Finally starts the agent logger.
 * 
 * Once the {@link start()) method returns, the required class file(s) are already
 * instrumented to trace and dump the statistics.
 * 
 * @author Sudiptasish Chanda
 */
public class AgentStartup {
	
	private final ExecutorService exeService = Executors.newFixedThreadPool(1);
	
	private final AgentMBeanHandler mbeanHandler = new AgentMBeanHandler();

	public AgentStartup() {}
	
	/**
	 * Entry point for initialization.
     * 
	 * @param _arg
	 * @param _inst
     * @param reTransformReqd
	 */
	public void startup(String _arg, Instrumentation _inst, boolean reTransformReqd) throws AgentException {
		if (_inst == null) {
			throw new AgentException("Java Instrumentation object is null");
		}
		_Agent_Instrumentation _agent_inst = _Agent_Instrumentation._get();
		_agent_inst._initialize(_inst);
		
		initialize(_arg, reTransformReqd);
		
		AgentMainUtil.printConfig();
		
		startTracer();
	}

	/**
	 * @param _arg
	 */
	private void initialize(String _arg, boolean reTransformReqd) throws AgentException {
		try {
			Properties props = parseArgs(_arg);
			if (Validator.verifyArgument(props)) {
				// Populate agent configuration object
				AgentConfiguration.getAgentConfig().addAllConfigProperties(props);
			}
			// Initialize Logger
			AgentLogger.initializeLogger();
			
			// Add the classloader hook.
			// If javaassist is not in the current classloader's classpath, then add it.
			addClassLoaderHook();
			
			// Initialize the transformation handler
			String handlerClass = AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_HANDLER);
			if (handlerClass == null) {
				throw new AgentException("Can not initialize agent. No handler found");
			}
			TransformationHandler handler = _ObjectCreator.create(handlerClass);
			handler.prepare(reTransformReqd);
			
			// Register the agent MBean.
			mbeanHandler.registerMBean();
			AgentLogger.log(SEVERITY.DEBUG, "AgentMBean is registered successfully");
		}
		catch (AgentException e) {
			throw e;
		}
		catch (Throwable t) {
			throw new AgentException(t);
		}
	}

	/**
     * Add the javaassist library to the current classloader path.
	 * @throws IOException 
     */
    private void addClassLoaderHook() throws IOException {
        boolean enableClassloading = "Y".equalsIgnoreCase(AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_ASSIST_CLOADER));
        if (!enableClassloading) {
            return;
        }
        String lib = AgentMainUtil.getConfigProperty(AgentArgMetadata.JAVA_ASSIST_LIB);
        File file = new File(lib);
        if (!file.exists()) {
            AgentLogger.log(SEVERITY.ERROR, "Invalid path " + lib + " specified for java assist lib.");
            return;
        }
        URL libUrl = file.toURI().toURL();
        addURL(libUrl);
        
        AgentLogger.log(SEVERITY.DEBUG, "Successfully added classpath hook for " + lib);
    }
    
    /**
     * Adds the content pointed by the URL to the classpath.
     * 
     * @param url the URL pointing to the content to be added
     * @throws IOException
     */
    public static void addURL(URL url) throws IOException {
        URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        Class<?> sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] {url}); 
        }
        catch (Exception t) {
            throw new IOException("Error, could not add URL to system classloader");
        }
    }

    /**
	 * Start the method tracer thread.
	 */
	private void startTracer() {
		String dumpEnabled = AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_TRACE_DUMP);
		
		if ("Y".equalsIgnoreCase(dumpEnabled)) {
			Integer dumpInterval = Integer.parseInt(AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_DUMP_INTERVAL));
						
			TraceThread tThread = new TraceThread(dumpInterval);			
			exeService.execute(tThread);
			
			AgentLogger.log(SEVERITY.DEBUG, "Tracer thread started successfully");
		}		
	}
	
	/**
	 * Parse the input agent argument and prepare the attribute map.
	 * @param 	_arg
	 */
	private Properties parseArgs(String _arg) {
		Properties props = new Properties();
		if (_arg != null) {
			String[] _arr = _arg.split(",");
			for (String element : _arr) {
				String[] _pairs = element.trim().split("=");
				if (_pairs.length == 2) {
					props.setProperty(_pairs[0].trim(), _pairs[1].trim());
				}
			}			
		}
		return props;
	}
}
