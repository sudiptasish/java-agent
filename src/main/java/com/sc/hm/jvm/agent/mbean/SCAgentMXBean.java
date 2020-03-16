package com.sc.hm.jvm.agent.mbean;

/**
 * Java agent MBean.
 * 
 * @author Sudiptasish Chanda
 */
public interface SCAgentMXBean {

    /**
     * Unload the java agent.
     * In theory it will remove the extra byte code from the instrumented classes.
     */
    void unloadAgent();
    
    /**
     * Reload the java agent.
     * This will trigger a re-transformation of the classes.
     */
    void reloadAgent();
    
    /**
     * Stop the collection of runtime metrics.
     */
    void stopCollection();
    
    /**
     * Resume the collection of metrics.
     */
    void resumeCollection();
}
