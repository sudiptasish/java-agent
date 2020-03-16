package com.sc.hm.jvm.trace;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Queue;

import com.sc.hm.jvm.agent.util._StreamHandler;

/**
 * A writer that writes the statistics in an HTML format.
 * 
 * @author Sudiptasish Chanda
 */
public class HTMLWriter {
	
	private static final String template = "Class: {0}<br/>Method: {1}<br/>Invocation Count: {2}<br/>Max Elapsed Time: {3}";

	/**
	 * Write the statistics to the HTML file.
	 * 
	 * @param file
	 * @param d
	 */
	public static void write(String file, Dumper d) {
	    StringBuilder _buff = new StringBuilder(2048);
	    prepareHeader(_buff);
	    
		for (Iterator<TabularData> itr = d.dataIterator(); itr.hasNext(); ) {
			TabularData tabularData = itr.next();
			writeTableData(_buff, file, tabularData);
			_buff.append("<br/><br/>");
		}
		prepareFooter(_buff);
		writeInternal(file, _buff);
	}
	
	private static void prepareHeader(StringBuilder _buff) {
	    _buff.append("\n").append("<!doctype html>");
        _buff.append("\n").append("<html lang=\"en\">");
        _buff.append("\n").append(" <head>");
        _buff.append("\n").append("  <meta charset=\"UTF-8\">");
        _buff.append("\n").append("  <title>Method Runtime Statistics</title>");
        _buff.append("\n").append(" </head>");
        _buff.append("\n").append(" <body>");        
	}

	private static void prepareFooter(StringBuilder _buff) {
	    _buff.append("\n").append("  </table>");
        _buff.append("\n").append(" </body>");
        _buff.append("\n").append("</html>");
	}
	
	/**
	 * 
	 * @param headers
	 * @param tableData
	 */
	private static void writeTableData(StringBuilder _buff
	        , String file
	        , TabularData tabularData) {
		
	    String prevThread = "";
		String className = tabularData.getMetadata(Dumper.CLASS_NAME);
		String methodName = tabularData.getMetadata(Dumper.METHOD_NAME);
		
		String[] headers = tabularData.getHeaders();
        Queue<String[]> tableData = tabularData.getData();
        
        _buff.append("\n").append("  <table border=\"0\" cellspacing=\"2\" cellpadding=\"3\" style=\"font-family:Arial,sans-serif; font-size:11px\">");
        _buff.append("\n").append("   <tr><td colspan=\"" + headers.length + "\">&nbsp;</td></tr>");
        _buff.append("\n").append("   <tr><td colspan=\"" + headers.length + "\">&nbsp;</td></tr>");
        _buff.append("\n").append("   <tr>");
        _buff.append("\n").append("    <td colspan=\"2\" bgcolor=\"#ffaeae\">").append(MessageFormat.format(template, className, methodName, tabularData.getMetadata(Dumper.INVOCATION_COUNT), tabularData.getMetadata(Dumper.MAX_DURATION))).append("</td>");
        _buff.append("\n").append("    <td colspan=\"" + (headers.length - 2) + "\"></td>");
        _buff.append("\n").append("   </tr>");
        
        _buff.append("\n").append("   <tr bgcolor=\"#cecece\" align=\"center\">");
        for (int i = 0; i < headers.length; i ++) {
            _buff.append("\n").append("    <td>").append(headers[i]).append("</td>");
        }
        _buff.append("\n").append("   </tr>");
		
		for (int k = 0; !tableData.isEmpty(); k ++) {
			String[] row = tableData.remove();
			
			if (prevThread.length() > 0 && !row[1].equals(prevThread)) {
				// New thread encountered. Enter a new blank row.
				_buff.append("\n").append("   <tr bgcolor=\"#eaeaea\"><td colspan=\"" + headers.length + "\">&nbsp;</td></tr>");
				prevThread = row[1];
			}			
			
			String style = k % 2 == 0 ? " bgcolor=\"#ffffa4\"" : " bgcolor=\"#bfffbf\"";
			_buff.append("\n").append("   <tr" + style + " align=\"center\">");
			
			for (int i = 0; i < row.length; i ++) {
				// Last column (call stack) would be left aligned
				String align = i == row.length - 1 ? " align=\"left\"" : "";
				_buff.append("\n").append("    <td" + align + ">").append(row[i]).append("</td>");
			}
			_buff.append("\n").append("   </tr>");			
			prevThread = row[1];
		}
	}
	
	/**
	 * Write the html content to the file.
	 * @param filename
	 * @param _buff
	 */
	private static void writeInternal(String filename, StringBuilder _buff) {
		PrintWriter pw = null;		
		try {
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename), true))));
			pw.print(_buff.toString());
			pw.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (pw != null) {
				_StreamHandler.closeStream(pw);
			}
		}
	}
}
