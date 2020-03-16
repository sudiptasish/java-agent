package com.sc.hm.jvm.agent.mbean;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

/**
 * 
 * @author Sudiptasish Chanda
 */
public class AgentMBeanHandler {
    
    private final String AGENT_DOMAIN = "com.sc.hm";

    public AgentMBeanHandler() {}
    
    /**
     * Register the Agent MXBean
     * 
     * @throws MalformedObjectNameException 
     * @throws NotCompliantMBeanException 
     * @throws MBeanRegistrationException 
     * @throws InstanceAlreadyExistsException 
     */
    public void registerMBean() throws MalformedObjectNameException
                                    , InstanceAlreadyExistsException
                                    , MBeanRegistrationException
                                    , NotCompliantMBeanException {
        
        // Get handle to the platform mbean server.
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName(AGENT_DOMAIN + ":type=agent,name=SCAgent");
        
        mbeanServer.registerMBean(new SCAgentMXBeanImpl(), name);
    }
    
    /**
     * Unregister the Agent MXBean.
     * 
     * @throws MalformedObjectNameException 
     * @throws InstanceNotFoundException 
     * @throws MBeanRegistrationException 
     */
    public void unregisterMBean() throws MalformedObjectNameException
                                    , MBeanRegistrationException
                                    , InstanceNotFoundException {
        
        // Get handle to the platform mbean server.
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName(AGENT_DOMAIN + ":type=agent,name=SCAgent");
        
        mbeanServer.unregisterMBean(name);
    }
}
