/* $Header: StatsDumper.java Sep 22, 2015 schanda  Exp $ */

/* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved. */

/*
   DESCRIPTION
    <short description of component this file declares/defines>

   PRIVATE CLASSES
    <list of private classes defined - with one-line descriptions>

   NOTES
    <other useful comments, qualifications, etc.>

   MODIFIED    (MM/DD/YY)
    schanda     Sep 22, 2015 - Creation
 */

/**
 * @version $Header: StatsDumper.java Sep 22, 2015 schanda  Exp $
 * @author  schanda
 * @since   release specific (what release of product did this appear in)
 */

package com.sc.hm.jvm.trace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
