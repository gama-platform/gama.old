/*********************************************************************************************
 * 
 *
 * 'QueryUtilities.java', in plugin 'msi.gama.hpc', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.hpc.evaluator;

import java.io.IOException;
import java.util.ArrayList;

import org.basex.core.Context;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.item.Item;
import org.basex.query.iter.Iter;

public class QueryUtilities {

	static Context context = new Context();
	
	public static ArrayList query(String strQuery) throws QueryException, IOException {
		// ------------------------------------------------------------------------
	    // Iterate through all query results
	    //System.out.println("\n* Convert each result to its Java representation:");
	    return iterate(strQuery);
	}
	
	/**
	   * This method uses the {@link QueryProcessor} to evaluate a query.
	   * The results are iterated one by one and converted to their Java
	   * representation, using {{@link Item#toJava()}. This variant is especially
	   * efficient if large result sets are expected.
	   * @param query query to be evaluated
	   * @throws QueryException if an error occurs while evaluating the query
	   * @throws IOException if an error occurs while serializing the results
	   */
	  static ArrayList iterate(final String query) throws QueryException, IOException {
		  ArrayList result = new ArrayList();
	    // ------------------------------------------------------------------------
	    // Create a query processor
	    QueryProcessor proc = new QueryProcessor(query, context);

	    // ------------------------------------------------------------------------
	    // Store the pointer to the result in an iterator:
	    Iter iter = proc.iter();

	    // ------------------------------------------------------------------------
	    // Iterate through all items and serialize
	    for(Item item; (item = iter.next()) != null;) {
	      //System.out.println(item.toJava());
	      result.add(item.toJava());
	    }

	    // ------------------------------------------------------------------------
	    // Close the query processor
	    proc.close();
	    return result;
	  }
	  
	/**
	 * @param args
	 * @throws IOException 
	 * @throws QueryException 
	 */

}
