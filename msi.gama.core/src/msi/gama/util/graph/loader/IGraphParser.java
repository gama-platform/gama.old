/*********************************************************************************************
 * 
 *
 * 'IGraphParser.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util.graph.loader;


/**
 * A graph parser is in charge of parsing a graph stored
 * in a file, and to transmit the corresponding events
 * to a graph parser listener.
 * 
 * @author Samuel Thiriot
 * 
 */
public interface IGraphParser {

	public void parseFile(IGraphParserListener listener, String filename);

}
