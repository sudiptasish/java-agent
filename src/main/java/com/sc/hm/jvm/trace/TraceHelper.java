package com.sc.hm.jvm.trace;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sc.hm.jvm.agent.AgentArgMetadata;
import com.sc.hm.jvm.agent.AgentMainUtil;
import com.sc.hm.jvm.agent.util.AgentLogger;
import com.sc.hm.jvm.agent.util.AgentLogger.SEVERITY;

/**
 * A helper class to offload some heavy work from the Tracer.
 * 
 * @author Sudiptasish Chanda
 */
public final class TraceHelper {
    
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Start visiting the tracer components to gather statistics.
     */
    public static void doWork() {
        AgentLogger.log(SEVERITY.DEBUG, "Starting to gather method statistics");
        
        Visitor visitor = new TracerVisitor();
        TraceManager.getManager().dump(visitor);
        
        if (visitor.hasResult()) {
            dumpFile(visitor);
        }
        else {
            AgentLogger.log(SEVERITY.ERROR, "No statistics collected by Tracer thread");
        }
    }
    
    /**
     * Dump the statistics to file.
     * @param visitor
     */
    public static void dumpFile(Visitor visitor) {
        String dumpDir = AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_DUMP_DIRECTORY);
        int statsCount = visitor.getDumper().getStatsCount();
        
        AgentLogger.log(
                SEVERITY.DEBUG
                , String.format("Tracer thread collected %d stats"
                        , statsCount));
    
        if (statsCount > 0) {
            String date = df.format(new Date());
            date = date.replace(" ", "_");
            date = date.replaceAll(":", "_");
            String filename = dumpDir + File.separator + AgentMainUtil.getProcessId() + "_trace_" + date + ".html";

            HTMLWriter.write(filename, visitor.getDumper());

            AgentLogger.log(SEVERITY.DEBUG
                    , String.format("Tracer thread dumped %d stats to file %s"
                            , statsCount
                            , filename));
        }
    }

}
