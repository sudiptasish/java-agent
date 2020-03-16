/* $Header: SCAgentMXBean.java Jan 28, 2017 schanda  Exp $ */

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
 * @version $Header: SCAgentMXBean.java Jan 28, 2017 schanda  Exp $
 * @author  schanda
 * @since   release specific (what release of product did this appear in)
 */

package com.sc.hm.jvm.agent.mbean;

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
