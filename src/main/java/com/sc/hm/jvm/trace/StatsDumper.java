package com.sc.hm.jvm.trace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used extract the composite statistics and store it in tabular format.
 * Later the tabular data will be dumped to the HTML file.
 * 
 * @author Sudiptasish Chanda
 */
public class StatsDumper implements Dumper {
	
	private int statsCount = 0;
	
	private final List<TabularData> list = new ArrayList<>();

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.trace.Dumper#hasStatistics()
	 */
	@Override
	public boolean hasStatistics() {
		return !list.isEmpty();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.trace.Dumper#addTabularData(com.sc.hm.jvm.trace.TabularData)
	 */
	@Override
	public void addTabularData(TabularData data) {
		list.add(data);
		statsCount = data.size();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.trace.Dumper#dataIterator()
	 */
	@Override
	public Iterator<TabularData> dataIterator() {
		return list.iterator();
	}

	/* (non-Javadoc)
	 * @see com.sc.hm.jvm.trace.Dumper#getStatsCount()
	 */
	@Override
	public int getStatsCount() {
		return statsCount;
	}
}
