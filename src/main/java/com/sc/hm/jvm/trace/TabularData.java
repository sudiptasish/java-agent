package com.sc.hm.jvm.trace;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

/**
 * 
 * @author Sudiptasish Chanda
 */
public class TabularData {

    private Properties metadata = new Properties();
    
	private String[] headers = new String[0];
	private Queue<String[]> data = new LinkedList<>();
	
	public TabularData() {}

	public TabularData(String[] headers, Queue<String[]> data) {
		this.headers = headers;
		this.data = data;
	}

	/**
	 * @return the headers
	 */
	public String[] getHeaders() {
		return headers;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	/**
	 * @return the data
	 */
	public Queue<String[]> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void addData(String[] data) {
		this.data.add(data);
	}
	
	public int size() {
		return data.size();
	}
	
	/**
     * Add the metadata about the data that is going to be dumped.
     * @param props
     */
    public void addMetadata(Properties props) {
        metadata.putAll(props);
    }
    
    /**
     * Return the metadata value for this key.
     * @param key
     * @return String
     */
    public String getMetadata(String key) {
        return metadata.getProperty(key);
    }
    
    
}
