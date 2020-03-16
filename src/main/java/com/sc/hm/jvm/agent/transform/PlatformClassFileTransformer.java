package com.sc.hm.jvm.agent.transform;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sc.hm.jvm.agent.mbean.GlobalConfig;
import com.sc.hm.jvm.agent.transform.provider.Provider;
import com.sc.hm.jvm.agent.util.AgentLogger;
import com.sc.hm.jvm.agent.util.AgentLogger.SEVERITY;

/**
 * Platform provided custom classfile transformer.
 * 
 * There will be a single instance of this transformer class inside a JVM. Once a
 * (re)transformation is triggered, it's {@link transform} method will be innvoked
 * by the underlying instrumentation API, it will then call the provider specific
 * {@code  Transformer} in order to transform the class.
 * 
 * @author Sudiptasish Chanda
 */
public class PlatformClassFileTransformer implements RuntimeClassFileTranformer {
	
	private final Map<String, List<MethodInstrumentation>> mapping = new HashMap<>();
	
	public PlatformClassFileTransformer() {}

	@Override
	public void addClassMapping(Map<String, List<MethodInstrumentation>> mapping) {
		this.mapping.putAll(mapping);
	}

	@Override
	public byte[] transform(ClassLoader loader
			, String className
			, Class<?> classBeingRedefined
			, ProtectionDomain protectionDomain
			, byte[] classfileBuffer) throws IllegalClassFormatException {
		
		if (mapping.containsKey(className)) {
		    if (GlobalConfig.AGENT_UNLOADED) {
		        AgentLogger.log(SEVERITY.DEBUG, String.format(
                        "Undo Transformation Request received for class [%s]. Loader [%s]"
                        , className
                        , loader));
		        
		        return Provider.getDefault()
                        .getTransformer().flushAndGetOriginalClassBytes(loader, className);
		    }
		    else {
		        AgentLogger.log(SEVERITY.DEBUG, String.format(
		                "Transformation Request received for class [%s]. Loader [%s]"
		                , className
		                , loader));
		        
	            List<MethodInstrumentation> methodInsts = mapping.get(className);
	            
	            return Provider.getDefault()
	                    .getTransformer().transform(
	                            className
	                            , classfileBuffer
	                            , methodInsts.toArray(new MethodInstrumentation[methodInsts.size()])
	                            , loader);
		    }
		}
		return null;
	}

}
