/* $Header: AgentMBeanHandler.java Jan 28, 2017 schanda  Exp $ */

/* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved. */

/*
   DESCRIPTION
    <short description of component this file declares/defines>

   PRIVATE CLASSES
    <list of private classes defined - with one-line descriptions>

   NOTES
    <other useful comments, qualifications, etc.>

   MODIFIED    (MM/DD/YY)
    schanda     Jan 28, 2017 - Creation
 */

/**
 * @version $Header: AgentMBeanHandler.java Jan 28, 2017 schanda  Exp $
 * @author  schanda
 * @since   release specific (what release of product did this appear in)
 */

package com.sc.hm.jvm.agent.mbean;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

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
