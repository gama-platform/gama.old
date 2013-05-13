package msi.gama.util;

import java.util.HashMap;
import java.util.Map;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Receives warnings as Strings (hopefully, many times the same). Stores them.
 * Then, we asked for, reports all these warnings to GAMA by grouping them. 
 * Result is like "errorA (10 times)" instead of displaying the same thing
 * 10 times. 
 * 
 * Remember to use quiet generic messages. Else they will never be the same,
 * and will not be grouped together. The comparison is based on the default
 * equals of the String class.
 * 
 * @author Samuel Thiriot
 *
 */
public class PostponedWarningList {

	private Map<String,Integer> warning2count = new HashMap<String, Integer>();

	public static boolean writeSystemOut = false;
	
	public void clear() {
		warning2count.clear();
	}
	
	public void addWarning(String msg) {
		Integer count = warning2count.get(msg);
		if (count == null)
			count = 1;
		else
			count = count + 1;
		warning2count.put(msg, count);
		
		
	}
	
	
	/**
	 * Raise GAma exceptions and transmists them with GAMA.reportError.
	 * If several warnings were detected, the "header" will be displayed first.
	 * 
	 * @param header
	 */
	public void publishAsGAMAWarning(String header) {
		
		if (warning2count.isEmpty())
			return; // quick exit
		
		// raise errors
		if (header != null && !header.isEmpty() && warning2count.size() > 1)
			GAMA.reportError(GamaRuntimeException.error(header));
		for (String msg : warning2count.keySet()) {
			Integer times = warning2count.get(msg);
			StringBuffer sb = new StringBuffer();
			sb.append(msg).append(" (").append(times);
			
			if (times == 1) 
				sb.append(" time)");
			else
				sb.append(" times)");
			
			if (writeSystemOut)
				System.err.println(sb.toString());
			
			GAMA.reportError(GamaRuntimeException.error(sb.toString()));
		}
	
	}
}
