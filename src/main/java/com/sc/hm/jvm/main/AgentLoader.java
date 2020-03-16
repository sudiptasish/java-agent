package com.sc.hm.jvm.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.sc.hm.jvm.agent.AgentArgMetadata;
import com.sc.hm.jvm.agent.AgentMainUtil;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * Class to load the java agent to an already running VM.
 * 
 * If the java process is already running, then this class will help push the 
 * tiny agent to the remote process and exit.
 * 
 * It accept the following arguments:
 * 
 * -a   : Complete path of the java-agent.jar.
 *        Note that, use has to specify the canonical path, relative path will not work.
 * 
 * -p   : Process Id of the java application, that is running in the same host.
 * 
 * -m   : Fully qualified name of the class map.
 *        This file contains the java class name and the method name (along with
 *        the line numbers, optional) that are required to be instrumented.
 * 
 *        Each line of the file will have entry in below format:
 *        com.sc.hm.srvc.prc.EmployeeService-{createEmployee(107:222)}
 *        com.sc.hm.core.alg.PersistentDiskStore-{persist}
 * 
 * -d   : Indicates whether the collected metrics will be dumped (default: Y)
 * 
 * -i   : Specify the interval (in seconds) the metrics would be dumped [default: 900]
 * 
 * -o   : Specify the dump directory, where the metrics (HTML) file will be generated.
 *        The directory location may exist, if not it will be created. Ensure the
 *        current process has appropriate privilege to create the directory.
 * 
 * -v   : On|Off the verbose [default: Y]
 * 
 * -l   : Specify the log directory where the agent log will be generated.
 *        This option will work, only if verbose is set to Y.
 * 
 * -c   : Specify the directory where the instrumented class(s) will be dumped [default: dump directory]
 * 
 * -s   : Specify if javaassist library is needed to be loaded by remote vm [default: N]
 * 
 * -j   : Path to javaassist library (required if -s option is specified).
 * 
 * @author Sudiptasish Chanda
 */
public class AgentLoader {
    
    public static void main(String[] args) {
        if ((args.length == 1 && args[0].equals("--help")) || args.length < 4) {
			usage();
			System.exit(-1);
		}
		try {
		    String agentPath = "";
		    String processId = "";
            StringBuilder _buff = new StringBuilder();
		    
		    for (int i = 0; i < args.length; i ++) {
		        if (args[i].equals("-a")) {
		            agentPath = args[++ i];
		        }
		        else if (args[i].equals("-p")) {
		            processId = args[++ i];
                }
		        else {
		            AgentArgMetadata metadata = AgentArgMetadata.from(args[i]);
		            _buff.append(metadata.getName()).append("=").append(args[++ i]).append(",");
		        }
		    }
		    if (_buff.length() > 3) {
		        _buff.delete(_buff.length() - 1, _buff.length());
		    }
		    if (agentPath == null || agentPath.trim().length() == 0 || !new File(agentPath.trim()).exists()) {
		        throw new IllegalArgumentException("Agent path cannot be empty.");
		    }
		    if (processId == null || processId.trim().length() == 0) {
		        throw new IllegalArgumentException("Must specify the remote process id.");
		    }
		    boolean enableClassloading = "Y".equalsIgnoreCase(AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_ASSIST_CLOADER));
	        if (enableClassloading) {
	            String lib = AgentMainUtil.getConfigProperty(AgentArgMetadata.JAVA_ASSIST_LIB);
	            if (lib == null) {
	                throw new IllegalArgumentException("No path to java assist library is specified");
	            }
	        }
			attach(processId, agentPath, _buff.toString());
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
    
    /**
     * Attach to the remote process as identified by this processId and load the agent.
     * If attach is not supported by the underlying OS implementation, then an
     * exception will be thrown and instrumentation will be disabled.
     * 
     * @param processId
     * @param agentLibrary
     * @param option
     * 
     * @throws AttachNotSupportedException
     * @throws IOException
     * @throws AgentLoadException
     * @throws AgentInitializationException
     */
    private static void attach(String processId, String agentLibrary, String option)
            throws AttachNotSupportedException
            , IOException
            , AgentLoadException
            , AgentInitializationException {
        
        System.out.println(String.format("Uploading agent [%s] to remote process [%s]", agentLibrary, processId));
        VirtualMachine vm = VirtualMachine.attach(processId);   // process id        
        vm.loadAgent(agentLibrary, option);  // Path to agent library
        vm.detach();
        
        System.out.println(String.format("Detached from remote process [%s]", processId));
    }

	/**
	 * Print the usage.
	 */
	private static void usage() {
	    StringBuilder _buff = new StringBuilder(200);
        _buff.append("Usage: java -jar target/java-agent-<version>.jar [options....]");
		
		_buff.append("\n\nOptions:").append("\n");
		_buff.append("\n").append(" 1.").append("\t").append("-a").append("    ").append("Path to java agent library");
        _buff.append("\n").append(" 2.").append("\t").append("-p").append("    ").append("Remote JVM Process Id");
        _buff.append("\n").append(" 3.").append("\t").append("-m").append("    ").append("Path to classmap.dat");
		_buff.append("\n").append(" 4.").append("\t").append("-d").append("    ").append("Specify if dumping of metrics is enabled [default: Y]");
		_buff.append("\n").append(" 5.").append("\t").append("-i").append("    ").append("Specify the interval (in seconds) the metrics would be dumped [default: 900]");
		_buff.append("\n").append(" 6.").append("\t").append("-o").append("    ").append("Specify the dump directory, where the metrics (HTML) file will be generated");
		_buff.append("\n").append(" 7.").append("\t").append("-v").append("    ").append("On|Off the verbose [default: Y]");
		_buff.append("\n").append(" 8.").append("\t").append("-l").append("    ").append("Specify the log directory where the agent log will be generated (only if verbose is set to Y).");
		_buff.append("\n").append(" 9.").append("\t").append("-c").append("    ").append("Specify the directory where the instrumented class(s) will be dumped [default: dump directory]");
		_buff.append("\n").append(" 10.").append("\t").append("-s").append("    ").append("Specify if javaassist library is needed to be loaded by remote vm [default: N]");
		_buff.append("\n").append(" 11.").append("\t").append("-j").append("    ").append("Path to javaassist library (required if -s option is specified)");
        
		System.out.println(_buff.toString());
		System.out.println("\n");
        
		try {
    		System.out.println("Available java Processes:\n");
            List<VirtualMachineDescriptor> vmList = VirtualMachine.list();
    		for (VirtualMachineDescriptor vmDesc : vmList) {
    			System.out.println(vmDesc.id() + " " + vmDesc.displayName());
    		}
            System.out.println("\n");
		}
		catch (NoClassDefFoundError e) {
		    // Do Nothing
		}
	}
}
