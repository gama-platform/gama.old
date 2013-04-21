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
