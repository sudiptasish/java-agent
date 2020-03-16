package com.sc.hm.jvm.agent.transform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sc.hm.jvm.agent.AgentArgMetadata;
import com.sc.hm.jvm.agent.AgentMainUtil;
import com.sc.hm.jvm.agent.exception.AgentException;
import com.sc.hm.jvm.agent.mbean.GlobalConfig;
import com.sc.hm.jvm.agent.util.AgentLogger;
import com.sc.hm.jvm.agent.util._ObjectCreator;
import com.sc.hm.jvm.agent.util._StreamHandler;
import com.sc.hm.jvm.agent.util.AgentLogger.SEVERITY;

/**
 * The default implementation of {@link TransformationHandler}.
 * 
 * If no provider specific handler is specified, then the default handler will be
 * innvoked, which will kick start the instrumentation process.
 * 
 * @author Sudiptasish Chanda
 */
public class DefaultTransformationHandler extends TransformationHandler {
	
	public DefaultTransformationHandler() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.agent.transform.TransformationHandler#prepare()
	 */
	@Override
	public void prepare() throws AgentException {
		prepare(false);
	}

	@Override
	public void prepare(boolean reTransformNeeded) throws AgentException {
		Map<String, List<MethodInstrumentation>> mapping = null;
		
		try {
			String transformerClass = AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_TRANSFORMER);
			RuntimeClassFileTranformer transformer = _ObjectCreator.create(transformerClass);
			
			AgentLogger.log(SEVERITY.DEBUG, String.format(
                "Created classfile transformer [%s]"
                , transformer.getClass().getSimpleName()));
						
			String mappingFile = AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_TRANSFORM_FILE);
			if (mappingFile != null) {
				mapping = loadClassFileMapping(mappingFile);
				transformer.addClassMapping(mapping);
				
				AgentLogger.log(SEVERITY.DEBUG, String.format(
                    "Loaded %d class mapping from file %s"
                    , mapping.size()
                    , mappingFile));
			}
			AgentMainUtil.addClassFileTransformer(transformer);
			
			AgentLogger.log(SEVERITY.DEBUG, String.format(
                "Added classfile transformer [%s]"
                , transformer.getClass().getSimpleName()));	
			
			if (reTransformNeeded) {
				AgentLogger.log(SEVERITY.DEBUG, "Agent loaded from remote location."
                    + " Triggered re-transformation");
				triggerRetransform(mapping);
			}
		}
		catch (Exception e) {
			throw new AgentException(e);
		}
	}

	/**
	 * Trigger a retransformation of classes as specified in the map.
	 * @param mapping
	 */
	private void triggerRetransform(Map<String, List<MethodInstrumentation>> mapping) {
		if (mapping != null) {
			Class<?>[] loadedClasses = AgentMainUtil.getLoadedClass();
			AgentLogger.log(SEVERITY.DEBUG, String.format("Loaded class count: [%d]", loadedClasses.length));
			
			List<Class<?>> classes = new ArrayList<>(mapping.size());
			for (int i = 0; i < loadedClasses.length; i ++) {
				String loadedClass = loadedClasses[i].getName().replaceAll("\\.", "/");
				if (mapping.containsKey(loadedClass)) {
					AgentLogger.log(SEVERITY.DEBUG, "Found the class [{0}] to be instrumented", loadedClasses[i]);
					classes.add(loadedClasses[i]);
				}
			}
			if (classes.size() > 0) {
				try {
				    Class<?>[] _arr = classes.toArray(new Class<?>[classes.size()]);
				    GlobalConfig.CLASS_ARRAY = _arr;
					AgentMainUtil.retransformClasses(_arr);
				}
				catch (UnmodifiableClassException e) {
					AgentLogger.log(SEVERITY.ERROR, "Can not retransform class");
				}
			}
		}
		else {
			// Instrument ALL classes (Not Supported).
		}
	}

	/**
	 * Load the class file vs method mapping (that needs to be instrumented).
	 * 
	 * Default file would have the mapping in the following format:
	 * <class_name_1>-<method_11>
	 * <class_name_2>-<method_21>
	 * <class_name_2>-{:<from_line_#>:<to_line_#>}
	 * <class_name_3>-<method_31>
	 * ........
	 * ........
	 * 
     * This is one crucial part of transformation handler's job. It has to read the
     * configuration to extract the class name and method name(s) that need to be
     * instrumented. It keeps the metadata about instrumentation in the memory,
     * only to be used by specific {@link Transformer} at a later stage.
     * 
	 * @param 	mappingFile
	 * @return	Map
	 */
	protected Map<String, List<MethodInstrumentation>> loadClassFileMapping(String mappingFile) throws AgentException {
		Map<String, List<MethodInstrumentation>> map = new HashMap<>();
		
		BufferedReader br = null;
		InputStream iStream = null;
		
		Pattern pattern = Pattern.compile(getMappingRegX());
		
		Matcher matcher = null;
		
		try {
			iStream = _StreamHandler.parseInputStream(mappingFile);
			br = new BufferedReader(new InputStreamReader(iStream));
			
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if (line.startsWith("#") || line.trim().length() == 0) {
					continue;
				}
				matcher = pattern.matcher(line.trim());
				if (!matcher.matches()) {
					throw new AgentException(String.format("Incorrect class mapping [%s]", line));
				}
				String[] _arr = line.split("-");
				String className = _arr[0].trim();
				MethodInstrumentation _m_inst = handleMethod(_arr[1].trim());
				
				String canonicalClass = className.replaceAll("\\.", "/");
				List<MethodInstrumentation> methodList = map.get(canonicalClass);
				if (methodList == null) {
					methodList = new ArrayList<>();
					map.put(canonicalClass, methodList);
				}
				methodList.add(_m_inst);
			}
		}
		catch (IOException e) {
			throw new AgentException(e);
		}
		finally {
			_StreamHandler.closeStream(br);
		}
		return map;
	}

	/**
	 * @param 	_methods_desc
	 * @return	MethodInstrumentation
	 */
	private MethodInstrumentation handleMethod(String _methods_desc) {
		MethodInstrumentation m_inst = new MethodInstrumentation();
		
        // com.sc.hm.jvm.main.JVMMonitor-{execute(5:24)}
        // _methods_desc = {execute(5:24)}
        
		int c_idx = _methods_desc.indexOf("(");
		String method = c_idx > 0
				? _methods_desc.substring(1, c_idx)
						: _methods_desc.substring(1, _methods_desc.length() - 1);
				
		String lines = c_idx > 0
				? _methods_desc.substring(c_idx, _methods_desc.length() - 1)
						: "";
		
		if (lines.trim().length() > 0) {
			Pattern p = Pattern.compile("(\\([0-9]{1,}:[0-9]{1,}\\))");
			Matcher m = p.matcher(lines.trim());
			
			while (m.find()) {
				String lineGroup = m.group();
				String[] line_arr = lineGroup.substring(1, lineGroup.length() - 1).split(":");
				
				int[] _lines = new int[line_arr.length];				
				for (int j = 0; j < line_arr.length; j ++) {
					_lines[j] = Integer.parseInt(line_arr[j].trim());
					if (j % 2 == 1 && _lines[j] < _lines[j - 1]) {
						throw new AgentException("Wrong ordering of start and end line# for element " + _methods_desc);
					}
				}
				// BCI will occur at the line# as specified
				m_inst.setMethodName(method);
				m_inst.setLines(_lines);
			}
			
		}
		else {
			// BCI will occur at the very beginning and end of this method
			m_inst.setMethodName(method);
			
		}
		return m_inst;
	}

	/**
	 * @param _methods
	 */
	protected void trim(String[] _methods) {
		for (int i = 0; i < _methods.length; i ++) {
			_methods[i] = _methods[i].trim();
		}
	}
}
