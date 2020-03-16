package com.sc.hm.jvm.agent.transform.provider;

import java.util.ArrayList;
import java.util.List;

import com.sc.hm.jvm.agent.AgentArgMetadata;
import com.sc.hm.jvm.agent.AgentMainUtil;
import com.sc.hm.jvm.agent.exception.AgentException;
import com.sc.hm.jvm.agent.util.AgentLogger;
import com.sc.hm.jvm.agent.util._ObjectCreator;
import com.sc.hm.jvm.agent.util.AgentLogger.SEVERITY;

/**
 * 
 * @author Sudiptasish Chanda
 */
public class TransformationProvider extends Provider {
	
	private final List<Transformer> transformers = new ArrayList<>();

	TransformationProvider() {}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.agent.transform.provider.Provider#getTransformer()
	 */
	@Override
	public Transformer getTransformer() {
		if (!transformers.isEmpty()) {
			return transformers.get(0);
		}
		String transformerClass = AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_TRANSFORMER_PROVIDER);
		try {
			Transformer transformer = _ObjectCreator.create(transformerClass);
			transformers.add(transformer);
			AgentLogger.log(SEVERITY.DEBUG, String.format("Initialized Transformer [%s]", transformerClass));
			
			return transformer;
		}
		catch (Exception e) {
			throw new AgentException(e);
		}
	}
}
