package com.sc.hm.jvm.agent.mbean;

import java.lang.instrument.UnmodifiableClassException;

import com.sc.hm.jvm.agent.AgentArgMetadata;
import com.sc.hm.jvm.agent.AgentMainUtil;
import com.sc.hm.jvm.agent.util.AgentLogger;
import com.sc.hm.jvm.agent.util.AgentLogger.SEVERITY;
import com.sc.hm.jvm.trace.TraceHelper;

/**
 * 
 * @author Sudiptasish Chanda
 */
public class SCAgentMXBeanImpl implements SCAgentMXBean {

    /* (non-Javadoc)
     * @see com.sc.hm.jvm.agent.mbean.SCAgentMXBean#unloadAgent()
     */
    @Override
    public void unloadAgent() {
        if (GlobalConfig.AGENT_UNLOADED) {
            throw new IllegalArgumentException("Agent is already unloaded");
        }
        try {
            GlobalConfig.AGENT_UNLOADED = true;
            if (GlobalConfig.CLASS_ARRAY != null) {
                AgentMainUtil.retransformClasses(GlobalConfig.CLASS_ARRAY);
            }
            // Dump any pending metrics.
            TraceHelper.doWork();
            
            AgentLogger.log(SEVERITY.DEBUG, "Agent unload is successful");
        }
        catch (UnmodifiableClassException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.sc.hm.jvm.agent.mbean.SCAgentMXBean#reloadAgent()
     */
    @Override
    public void reloadAgent() {
        if (!GlobalConfig.AGENT_UNLOADED) {
            throw new IllegalArgumentException("Agent is already loaded");
        }
        try {
            GlobalConfig.AGENT_UNLOADED = false;
            if (GlobalConfig.CLASS_ARRAY != null) {
                AgentMainUtil.retransformClasses(GlobalConfig.CLASS_ARRAY);
            }
            AgentLogger.log(SEVERITY.DEBUG, "Agent reloaded successfully");
        }
        catch (UnmodifiableClassException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.sc.hm.jvm.agent.mbean.SCAgentMXBean#stopCollection()
     */
    @Override
    public void stopCollection() {
        AgentMainUtil.setConfigProperty(AgentArgMetadata.ENABLE_COLLECTION.getName(), "N");
    }

    /* (non-Javadoc)
     * @see com.sc.hm.jvm.agent.mbean.SCAgentMXBean#resumeCollection()
     */
    @Override
    public void resumeCollection() {
        AgentMainUtil.setConfigProperty(AgentArgMetadata.ENABLE_COLLECTION.getName(), "Y");
    }
}
