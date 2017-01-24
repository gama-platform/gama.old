/*********************************************************************************************
 *
 * 'IGraphParserListener.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph.loader;

import msi.gama.runtime.IScope;

public interface IGraphParserListener {

	public void startOfParsing();

	/**
	 * Notifies the listener of the end of graph loading.
	 */
	public void endOfParsing(IScope scope);

	public void detectedNode(final IScope scope, String nodeId);

	public void detectedEdge(final IScope scope, String edgeId, String nodeIdFrom, String nodeIdTo);

	public void detectedNodeAttribute(final IScope scope, String nodeId, String attributeName, Object value);

	public void detectedEdgeAttribute(final IScope scope, String edgeId, String attributeName, Object value);

}
