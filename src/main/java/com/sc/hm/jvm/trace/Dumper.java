package com.sc.hm.jvm.trace;

import java.util.Iterator;

/**
 * Interface to represents a dumper.
 * A dumper is responsible for periodically dumping the statistically data to 
 * some underlying file.
 * 
 * @author Sudiptasish Chanda
 */
public interface Dumper {
    
    String CLASS_NAME = "0";
    String METHOD_NAME = "1";
    String INVOCATION_COUNT = "3";
    String MAX_DURATION = "4";
    
    /**
     * Add the tabular data to this dumper.
     * This data will be displayed on the HTML page.
     * @param data
     */
	void addTabularData(TabularData data);
	
	/**
	 * Check to see if any statistics has been generated.
	 * @return	boolean
	 */
	boolean hasStatistics();
	
	/**
	 * Return the number of stats entry ccollected
	 * @return
	 */
	int getStatsCount();
	
	/**
	 * Return an iterator over the list of tabular data
	 * @return Iterator
	 */
	Iterator<TabularData> dataIterator();
}
