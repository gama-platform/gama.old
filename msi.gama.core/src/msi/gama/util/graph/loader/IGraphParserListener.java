/*********************************************************************************************
 * 
 *
 * 'IGraphParserListener.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util.graph.loader;


public interface IGraphParserListener {

	
	public void startOfParsing();
	
	/**
	 * Notifies the listener of the end of graph loading.
	 */
	public void endOfParsing();
	
	public void detectedNode(String nodeId);
	
	public void detectedEdge(String edgeId, String nodeIdFrom, String nodeIdTo);
	
	public void detectedNodeAttribute(String nodeId, String attributeName, Object value);
	
	public void detectedEdgeAttribute(String edgeId, String attributeName, Object value);
	
	
}
