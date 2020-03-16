package com.sc.hm.jvm.main;

import java.lang.instrument.Instrumentation;

import com.sc.hm.jvm.agent.AgentStartup;
import com.sc.hm.jvm.agent.util._ObjectCreator;
import java.net.URLClassLoader;

/**
 * The java agent can be attached to a VM either during JVM startup, or after the startup.
 * 
 * To attach the agent during bootstrap, one has to specify, the following option
 * in the startup script:
 * -javaagent:/path/to/agent.jar=argumentstring
 * 
 * Often it is not desired to stop a VM. In that case the java agent has to be loaded
 * on the fly. This is done with the help of {@link AgentLoader}. {@link AgentLoader}
 * has the ability to connect to an already running java process, and push the tiny
 * agent to that process.
 * 
 * JavaAgentMain has to primary methods:
 * 1. premain   - This method will be invoked during bootstrap.
 * 2. agentmain - This method will be invoked after an agent is pushed to a running process.
 * 
 * Once the agent is loaded into the process JVM, it will start the instrumentation.
 * It uses javassist for byte code instrumentation. If the remote process does not
 * have the javassist library (jar) in it's classpath, then it's classpath will be
 * modified (via the {@link URLClassLoader}) to include the library path, thus enabling
 * it perform the instrumentation.
 * 
 * @author Sudiptasish Chanda
 */
public class JavaAgentMain {
	
	private static final String STARTUP_CLASS = "com.sc.hm.jvm.agent.AgentStartup";
	
	private static AgentStartup startup;
	
	/**
	 * This api will be called the agent before any 'main' method
	 * to get a handle to the instrumentation.
	 * 
	 * To pass arguments to a Java agent, append them after the equals sign:
	 * java -javaagent:/path/to/agent.jar=argumentstring ...
	 * It is advisable to pass key=value pair so that agent main can parse it
	 * appropriately.
	 * 
	 * The accepted structure of the argument string is as follow:
	 * handler=agent.handler.class.name, transformer=class.file.transformer.class.name, <file=/path/to/class_method_mapping>
	 * 
	 * If no agent handler or custom class file transformer name is specified,
	 * then the default handler and default class file transformer will be used.
	 * 
	 * @param	_arg
	 * @param 	_inst
	 */
	public static void premain(String _arg, Instrumentation _inst) {
		try {
			startup = _ObjectCreator.create(STARTUP_CLASS);
			startup.startup(_arg, _inst, false);
			
			System.out.println("Successfully Initialized Agent Instrumentation");
		}
		catch (Exception e) {
			System.err.println("Failed to Initialize Agent Instrumentation");
			e.printStackTrace();
		}
	}
	
	/**
	 * This API will be called while this java agent is attached to a remote process.
	 * @param _arg
	 * @param _inst
	 */
	public static void agentmain(String _arg, Instrumentation _inst) {
		try {
			startup = _ObjectCreator.create(STARTUP_CLASS);
			startup.startup(_arg, _inst, true);
			
			System.out.println("Successfully Loaded and Initialized Agent Instrumentation");
		}
		catch (Exception e) {
			System.err.println("Failed to Load/Initialize Agent Instrumentation");
			e.printStackTrace();
		}
	}
}
