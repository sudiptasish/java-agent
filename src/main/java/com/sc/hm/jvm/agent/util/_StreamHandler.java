package com.sc.hm.jvm.agent.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Utility class for handling stream.
 * This has the API to read data from and write to underlying filesystem.
 * 
 * @author Sudiptasish Chanda
 */
public final class _StreamHandler {

	/**
	 * Return the Input stream associated with the file.
     * The caller must take the responsibility to explicitly close the stream after
     * ut's usage.
	 * 
	 * @param 	filename		file name
	 * @return	InputStream		Associated Stream
     * @throws  FileNotFoundException
	 */
	public static InputStream parseInputStream(final String filename) throws FileNotFoundException {
		// File input stream for the file to be read
		InputStream i_stream = null;
		
		File file = new File(filename);
		if (file.exists()) {
			i_stream = new FileInputStream(file);
		}
		else {
			i_stream = _StreamHandler.class.getClassLoader().getResourceAsStream(filename);
			if (i_stream == null) {
				URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
				String urlFile = url.getFile();
				urlFile = urlFile.replaceAll("%20", " ");
				
				i_stream = new FileInputStream(new File(urlFile));
			}
		}
		return i_stream;
	}
	
	/**
	 * Write the content to the file as specified by the filename.
     * 
	 * @param filename
	 * @param _content
	 */
	public static void write(String filename, byte[] _content) {
		OutputStream oStream = null;
		
		try {
			oStream = new FileOutputStream(new File(filename));
			oStream.write(_content);
			oStream.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			closeStream(oStream);
		}
	}
	
	/**
	 * Close the stream.
	 * @param c
	 */
	public static void closeStream(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				// Do Nothing
			}
		}
	}
}
