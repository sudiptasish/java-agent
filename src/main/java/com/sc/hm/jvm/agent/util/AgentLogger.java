/* $Header: AgentLogger.java Sep 26, 2015 schanda  Exp $ */

/* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved. */

/*
   DESCRIPTION
    <short description of component this file declares/defines>

   PRIVATE CLASSES
    <list of private classes defined - with one-line descriptions>

   NOTES
    <other useful comments, qualifications, etc.>

   MODIFIED    (MM/DD/YY)
    schanda     Sep 26, 2015 - Creation
 */

/**
 * @version $Header: AgentLogger.java Sep 26, 2015 schanda  Exp $
 * @author  schanda
 * @since   release specific (what release of product did this appear in)
 */

package com.sc.hm.jvm.agent.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.sc.hm.jvm.agent.AgentArgMetadata;
import com.sc.hm.jvm.agent.AgentMainUtil;

public final class AgentLogger {
	
	private static final String template = "{0} [{1}] [{2}] {3}";	// Timestamp Severity Thread Message
	
	// Log file size before it is rolled over.
	private static final int logSize = 10 * 1024 * 1024;	// 10 MB
	
	private static final String filename = "agent-profile.log";
	
	// Log directory where the files will be written
	private static String logDirectory = null;
	
	// Maximum number of archived log files
	private static final int maxFiles = 20;
	
	public static enum SEVERITY {DEBUG, ERROR};
	
	private static boolean initialized = false;
	
	private static boolean verbose = false;
	
	private static LogWriter writer;
	
	/**
	 * Initialize agent logger.
	 */
	public static void initializeLogger() {
		verbose = "Y".equalsIgnoreCase(AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_VERBOSE));
		
		if (verbose) {
			// Check for log directory.
			String logDir = AgentMainUtil.getConfigProperty(AgentArgMetadata.AGENT_LOG_DIR);
			if (logDir != null && logDir.trim().length() > 0) {
				File dir = new File(logDir);
				if (dir.exists()) {
					logDirectory = logDir;
					try {
						writer = new LogWriter(new FileOutputStream(new File(dir, filename)));
					}
					catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				else {
					System.err.println(String.format("Log directory [%s] does not exist. Logging will redirected to console.", logDir));
					writer = new LogWriter(System.out);
				}
			}
			else {
				System.err.println("No Log directory specified. Logging will redirected to console.");
				writer = new LogWriter(System.out);
			}
		}
		initialized = true;
	}
	
	/**
	 * @return the initialized
	 */
	public static boolean isInitialized() {
		return initialized;
	}

	/**
	 * @return the verbose
	 */
	public static boolean isVerbose() {
		return verbose;
	}

	/**
	 * Log the message with the specified severity.
	 * 
	 * @param severity
	 * @param message
	 */
	public static void log(SEVERITY severity, String message) {
		if (isVerbose()) {
			writer.write(severity, message);
		}
	}
	
	/**
	 * Log the message with the specified severity and message arguments.
	 * 
	 * @param severity
	 * @param message
	 * @param args
	 */
	public static void log(SEVERITY severity, String message, Object... args) {
		if (isVerbose()) {
			writer.write(severity, message, args);
		}
	}
	
	// Internal log writer class.
	private static class LogWriter {
		
		private BufferedWriter writer = null;
		
		private Lock writeLock = new ReentrantLock();
		
		LogWriter(OutputStream oStream) {
			this.writer = new BufferedWriter(new OutputStreamWriter(oStream));
		}
		
		/**
		 * Write the message string to the output stream
		 * @param message 
		 */
		void write(SEVERITY severity, String message) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String logMessage = 
					MessageFormat.format(
							template
							, df.format(new Date())
							, severity
							, Thread.currentThread().getName(), message);
			
			writeInternal(logMessage);
		}
		
		/**
		 * Write the message string to the output stream
		 * @param message 
		 */
		void write(SEVERITY severity, String message, Object... args) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String logMessage = 
					MessageFormat.format(
							template
							, df.format(new Date())
							, severity
							, Thread.currentThread().getName()
							, MessageFormat.format(message, args));
			
			writeInternal(logMessage);			
		}
		
		private void writeInternal(String logMessage) {
			try {
				writeLock.lock();
				checkFileSize();
				ensureOpen();
				writer.write(logMessage);
				writer.write("\n");
				writer.flush();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				writeLock.unlock();
			}
		}

		/**
		 * 
		 */
		private void checkFileSize() {
			if (AgentLogger.logDirectory != null) {
				// file logging is enabled.
				File file = new File(AgentLogger.logDirectory, AgentLogger.filename);
				if (file.length() >= AgentLogger.logSize) {
					File dir = new File(AgentLogger.logDirectory);
					
					try {
						// Close the existing stream
						writer.close();
						
						// Now check the number of files
						List<Path> files = getFiles(dir.toPath());
						if (files.size() == maxFiles) {
							// Delete the first (oldest) element.
							files.get(0).toFile().delete();
						}						
						file.renameTo(new File(logDirectory
								, AgentLogger.filename
									+ "."
									+ new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())));
						
						this.writer = new BufferedWriter(
								new OutputStreamWriter(
										new FileOutputStream(
												new File(AgentLogger.logDirectory, AgentLogger.filename))));
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	    private static List<Path> getFiles(Path logDir) throws IOException {
	    	final PathMatcher typeMatcher = FileSystems.getDefault().getPathMatcher("glob:" + AgentLogger.filename + ".*");
			
			List<Path> files = new ArrayList<>();
	        try (DirectoryStream<Path> dirStream
	                = Files.newDirectoryStream(logDir, new DirectoryStream.Filter<Path>() {
	                    @Override
	                    public boolean accept(Path entry) throws IOException {
	                        return typeMatcher.matches(entry.getFileName());
	                    }
	                }
	            )) {
	            for(Path p : dirStream) {
	                files.add(p);
	            }
	        }
	        Collections.sort(files, new Comparator<Path>() {
				@Override
				public int compare(Path o1, Path o2) {
					return Long.compare(o1.toFile().lastModified(), o2.toFile().lastModified());
				}
			});        
	        return files;
	    }

		/**
		 * Check if the underlying stream is still open
		 */
		private void ensureOpen() throws IOException {
	        if (writeLock == null) {
	            throw new IOException("Stream closed");
	        }
		}
	}
}
