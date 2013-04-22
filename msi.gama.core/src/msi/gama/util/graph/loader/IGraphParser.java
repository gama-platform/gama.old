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
