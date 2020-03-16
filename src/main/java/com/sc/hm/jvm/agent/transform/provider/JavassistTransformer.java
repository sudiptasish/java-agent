package com.sc.hm.jvm.agent.transform.provider;

import java.io.File;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.LoaderClassPath;

import com.sc.hm.jvm.agent.AgentArgMetadata;
import com.sc.hm.jvm.agent.AgentMainUtil;
import com.sc.hm.jvm.agent.mbean.GlobalConfig;
import com.sc.hm.jvm.agent.transform.MethodInstrumentation;
import com.sc.hm.jvm.agent.util.AgentLogger;
import com.sc.hm.jvm.agent.util._StreamHandler;
import com.sc.hm.jvm.agent.util.AgentLogger.SEVERITY;

/**
 * Tranformer class that uses javassist library for bytecode instrumentation.
 * 
 * Javassist is a Java library providing a means to manipulate the Java bytecode
 * of an application. In this sense Javassist provides the support for structural
 * reflection, i.e. the ability to change the implementation of a class at run time.
 * Bytecode manipulation is performed at load-time through a provided class loader.
 * 
 * @author Sudiptasish Chanda
 */
public final class JavassistTransformer extends Transformer {
	
	@Override
	public byte[] transform(String className
			, byte[] _buff
			, MethodInstrumentation[] methodInsts
			, ClassLoader loader) {
	
		byte[] _instrumented_class = null;
		
		// See if the class is already instrumented
		if (GlobalConfig.AGENT_UNLOADED || !alreadyInstrumented(loader, className)) {
			_instrumented_class = internalTransform(className, _buff, methodInsts, loader);
			
			addInstrumentedClass(loader, className, _buff);
			AgentLogger.log(SEVERITY.DEBUG
					, String.format("Instrumentation done for Class [%s] loaded by Classloader [%s]." +
							" Old Size (byte): %d. New Size (byte): %d"
							, className
							, loader
							, _buff.length
							, _instrumented_class.length));
		}
		else {
			AgentLogger.log(SEVERITY.DEBUG
					, String.format("Class [%s] loaded by Classloader [%s] is already instrumented"
							, className
							, loader));
		}
		return _instrumented_class;
	}

	/**
	 * @param className
	 * @param _buff
	 * @return
	 */
	private byte[] internalTransform(String className
			, byte[] _buff
			, MethodInstrumentation[] methodInst
			, ClassLoader loader) {
		
		CtClass cl = null;
		
		try {
			ClassPool pool = ClassPool.getDefault();
			ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
			//pool.insertClassPath(new LoaderClassPath(ctxClassLoader));
			pool.insertClassPath(new LoaderClassPath(loader));
			
			cl = pool.makeClass(new java.io.ByteArrayInputStream(_buff));
			CtBehavior[] methods = cl.getDeclaredBehaviors();
			for (int i = 0; i < methods.length; i++) {
		        if (!methods[i].isEmpty()) {
		        	int index = toBeInstrumented(methodInst, methods[i]);
		        	if (index != -1) {
		        		instrumentMethod(methods[i], methodInst[index]);
		        	}
		        }
			}			
			_buff = cl.toBytecode();

			String dumpDir = AgentMainUtil.getConfigProperty(AgentArgMetadata.BYTECODE_DUMP_DIR);
			if (dumpDir != null && dumpDir.trim().length() > 0) {
				dumpModifiedClass(className, _buff, dumpDir);
			}
	    }
	    catch (Throwable e) {
	    	e.printStackTrace();
	    }
	    finally {
	    	if (cl != null) {
	    		cl.detach();
	    	}
	    }
	    return _buff;
	}

	/**
	 * 
	 * @param 	ctBehavior
	 * @param 	mi
	 * @throws 	CannotCompileException 
	 */
	private void instrumentMethod(CtBehavior ctBehavior, MethodInstrumentation mi) throws CannotCompileException {
		int[] lines = mi.getLines();
		if (lines != null) {
			int[] orgLines = new int[lines.length];
			
			for (int i = 0; i < lines.length; i ++) {
				orgLines[i] = ctBehavior.insertAt(lines[i], false, "com.sc.hm.jvm.trace.Tracer.methodTracer().start();");
			}
			// Now start inserting the pair of lines
			for (int i = 0; i < orgLines.length; i ++) {
				if (i % 2 == 0) {
					if (orgLines[i] == orgLines[i + 1]) {
						AgentLogger.log(SEVERITY.ERROR
								, String.format("Instrumentation at line# %d won't be possible for method [%s]"
										, lines[i]
										, ctBehavior.getLongName()));
						continue;
					}
					ctBehavior.insertAt(orgLines[i], "com.sc.hm.jvm.trace.Tracer.methodTracer().start();");
					AgentLogger.log(SEVERITY.DEBUG
							, String.format("BCI occured at line# %d (beginning) of method [%s]"
									, orgLines[i]
									, ctBehavior.getLongName()));
				}
				else {
					if (orgLines[i] == orgLines[i - 1]) {
						AgentLogger.log(SEVERITY.ERROR
								, String.format("Instrumentation at line# %d won't be possible for method [%s]"
										, lines[i]
										, ctBehavior.getLongName()));
						continue;
					}
					ctBehavior.insertAt(orgLines[i], "com.sc.hm.jvm.trace.Tracer.methodTracer().end();");
					AgentLogger.log(SEVERITY.DEBUG
							, String.format("BCI occured at line# %d (end) of method [%s]"
									, orgLines[i]
									, ctBehavior.getLongName()));
				}
			}
		}
		else {
			ctBehavior.insertBefore("com.sc.hm.jvm.trace.Tracer.methodTracer().start();");
			ctBehavior.insertAfter("com.sc.hm.jvm.trace.Tracer.methodTracer().end();");
			
			AgentLogger.log(SEVERITY.DEBUG
					, String.format("BCI occured at the beginning and end of method [%s]"
							, ctBehavior.getLongName()));
		}
	}

	private int toBeInstrumented(MethodInstrumentation[] methodInst, CtBehavior ctBehavior) {
		int i = 0;
		for (; i < methodInst.length; i ++) {
			if (ctBehavior.getName().equals(methodInst[i].getMethodName())) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Dump the modified class file to the current directory.
	 * @param className
	 * @param _buff
	 * @param dumpDir
	 */
	private void dumpModifiedClass(String className, byte[] _buff, String dumpDir) {
		String filename = dumpDir
				+ File.separator
				+ (className.indexOf("/") >= 0
					? className.substring(className.lastIndexOf("/") + 1)
					: className)
				+ ".class";
		
		_StreamHandler.write(filename, _buff);
		
		AgentLogger.log(SEVERITY.DEBUG
				, String.format("Dumped Instrumented Class [%s] to [%s]"
						, className
						, filename));
	}
}
