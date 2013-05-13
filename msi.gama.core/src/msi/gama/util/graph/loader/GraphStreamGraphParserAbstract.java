package msi.gama.util.graph.loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.PostponedWarningList;

import org.graphstream.stream.SinkAdapter;
import org.graphstream.stream.file.FileSource;

/**
 * Basis for the graph parser based on graphstream.
 * GraphStream is based on filesource (which defines the format),
 * and sinks (that receive data). Here a sink is implemented which 
 * transmits events to a gama GraphParserListener, and raises
 * warnings (in a grouped way) when pieces of data are ignored.
 * 
 * @author Samuel Thiriot
 */
public abstract class GraphStreamGraphParserAbstract implements IGraphParser {

	/**
	 * Receives events from a graphstream loader
	 * and transmits them to our listener
	 * 
	 */
	public static class GraphStreamGamaGraphSink extends SinkAdapter {

		private IGraphParserListener listener;
		private PostponedWarningList warnings = new PostponedWarningList();
		
		public GraphStreamGamaGraphSink(IGraphParserListener listener) {
			this.listener = listener;
		}

		@Override
		public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId,
			String toNodeId, boolean directed) {
			
			listener.detectedEdge(edgeId, fromNodeId, toNodeId);
		}

		@Override
		public void nodeAdded(String sourceId, long timeId, String nodeId) {

			listener.detectedNode(nodeId);
		}

		@Override
		public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
			listener.detectedEdgeAttribute(edgeId, attribute, value);
		}

		@Override
		public void graphAttributeAdded(String sourceId, long timeId,
				String attribute, Object value) {
			warnings.addWarning("an information was ignored during the loading of the graph: graph attribute '"+attribute+"'='"+value+"'");
		}

		@Override
		public void nodeAttributeAdded(String sourceId, long timeId,
				String nodeId, String attribute, Object value) {
			listener.detectedNodeAttribute(nodeId, attribute, value);
		}

		@Override
		public void edgeAttributeChanged(String sourceId, long timeId,
				String edgeId, String attribute, Object oldValue,
				Object newValue) {
			warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of an edge changed.");
		}

		@Override
		public void edgeAttributeRemoved(String sourceId, long timeId,
				String edgeId, String attribute) {
			warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of an edge was removed");
		}

		@Override
		public void graphAttributeChanged(String sourceId, long timeId,
				String attribute, Object oldValue, Object newValue) {
			warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of the graph changed.");
		}

		@Override
		public void graphAttributeRemoved(String sourceId, long timeId,
				String attribute) {
			warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of the graph was removed");
		}

		@Override
		public void nodeAttributeChanged(String sourceId, long timeId,
				String nodeId, String attribute, Object oldValue,
				Object newValue) {
			warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of a node changed.");
		}

		@Override
		public void nodeAttributeRemoved(String sourceId, long timeId,
				String nodeId, String attribute) {
			warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the attribute '"+attribute+"' of a node was removed");
		}

		@Override
		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"an edge should have been removed");
		}

		@Override
		public void graphCleared(String sourceId, long timeId) {
			warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"the graph should have been cleaned");
		}

		@Override
		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"a node should have been removed");
		}

		@Override
		public void stepBegins(String sourceId, long timeId, double step) {
			warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
					"new step detected.");
		}
		
		public void endParsing() {
			listener.endOfParsing();
			warnings.publishAsGAMAWarning("during the parsing of the graph, warnings have been detected:");
		}
		
		

	}
	
	/**
	 * To be overriden by subclasses, in order to provide the FileSource
	 * with the relevant parameters setting.
	 * @return
	 */
	protected abstract FileSource getFileSource();
	
	@Override
	public void parseFile(IGraphParserListener listener,
			String filename) {
		
		
		// init our sink which will process events from the filesource
		GraphStreamGamaGraphSink ourSink = new GraphStreamGamaGraphSink(listener);
		
		// the graphstream reader
		FileSource fileSource = getFileSource();
		
		// that we listen to
		fileSource.addSink(ourSink);

		// attempt to open the file
		InputStream is;
		try {
			is = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			throw GamaRuntimeException.error("Unable to load file from " + filename +
				" (" + e.getLocalizedMessage() + ")");
		}
		
		// actually load the graph
		listener.startOfParsing();
		try {
			fileSource.begin(is);
			while (fileSource.nextEvents()) {
				// nothing to do
			}
			fileSource.end();
		} catch (IOException e) {
			throw GamaRuntimeException.error("Error while parsing a graph from " +
				filename+ " (" + e.getLocalizedMessage() + ")");
		} catch (Exception e) {
			throw GamaRuntimeException.error("Error while parsing a graph from " +
					filename+ " (" + e.getLocalizedMessage() + ")");
		} catch(Throwable e) {
			throw GamaRuntimeException.error("Error while parsing a graph from " +
					filename+ " (" + e.getLocalizedMessage() + ")");
		} finally {
		
			// end of parsing, warn everybody
			ourSink.endParsing();
		}
		
		// that's all folks :-)
		
	}

	
}
