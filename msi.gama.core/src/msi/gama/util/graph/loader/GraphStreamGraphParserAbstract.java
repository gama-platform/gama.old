/*********************************************************************************************
 *
 * 'GraphStreamGraphParserAbstract.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph.loader;

import java.io.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
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

		private final IGraphParserListener listener;

		// private PostponedWarningList warnings = new PostponedWarningList();

		public GraphStreamGamaGraphSink(final IGraphParserListener listener) {
			this.listener = listener;
		}

		@Override
		public void edgeAdded(final String sourceId, final long timeId, final String edgeId, final String fromNodeId,
			final String toNodeId, final boolean directed) {

			listener.detectedEdge(edgeId, fromNodeId, toNodeId);
		}

		@Override
		public void nodeAdded(final String sourceId, final long timeId, final String nodeId) {

			listener.detectedNode(nodeId);
		}

		@Override
		public void edgeAttributeAdded(final String sourceId, final long timeId, final String edgeId,
			final String attribute, final Object value) {
			listener.detectedEdgeAttribute(edgeId, attribute, value);
		}

		@Override
		public void graphAttributeAdded(final String sourceId, final long timeId, final String attribute,
			final Object value) {
			// warnings.addWarning("an information was ignored during the loading of the graph: graph attribute '"+attribute+"'='"+value+"'");
		}

		@Override
		public void nodeAttributeAdded(final String sourceId, final long timeId, final String nodeId,
			final String attribute, final Object value) {
			listener.detectedNodeAttribute(nodeId, attribute, value);
		}

		@Override
		public void edgeAttributeChanged(final String sourceId, final long timeId, final String edgeId,
			final String attribute, final Object oldValue, final Object newValue) {
			// warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): "
			// +
			// "the attribute '"+attribute+"' of an edge changed.");
		}

		@Override
		public void edgeAttributeRemoved(final String sourceId, final long timeId, final String edgeId,
			final String attribute) {
			// warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): "
			// +
			// "the attribute '"+attribute+"' of an edge was removed");
		}

		@Override
		public void graphAttributeChanged(final String sourceId, final long timeId, final String attribute,
			final Object oldValue, final Object newValue) {
			// warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): "
			// +
			// "the attribute '"+attribute+"' of the graph changed.");
		}

		@Override
		public void graphAttributeRemoved(final String sourceId, final long timeId, final String attribute) {
			// warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): "
			// +
			// "the attribute '"+attribute+"' of the graph was removed");
		}

		@Override
		public void nodeAttributeChanged(final String sourceId, final long timeId, final String nodeId,
			final String attribute, final Object oldValue, final Object newValue) {
			// warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): "
			// +
			// "the attribute '"+attribute+"' of a node changed.");
		}

		@Override
		public void nodeAttributeRemoved(final String sourceId, final long timeId, final String nodeId,
			final String attribute) {
			/*
			 * warnings.addWarning(
			 * "an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): " +
			 * "the attribute '"+attribute+"' of a node was removed");
			 */
		}

		@Override
		public void edgeRemoved(final String sourceId, final long timeId, final String edgeId) {
			// warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): "
			// +
			// "an edge should have been removed");
		}

		@Override
		public void graphCleared(final String sourceId, final long timeId) {
			// warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): "
			// +
			// "the graph should have been cleaned");
		}

		@Override
		public void nodeRemoved(final String sourceId, final long timeId, final String nodeId) {
			// warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): "
			// +
			// "a node should have been removed");
		}

		@Override
		public void stepBegins(final String sourceId, final long timeId, final double step) {
			// warnings.addWarning("an event was ignored during the loading of the graph (the dynamic part of graphs is ignored): "
			// +
			// "new step detected.");
		}

		public void endParsing() {
			listener.endOfParsing();
			// warnings.publishAsGAMAWarning("during the parsing of the graph, warnings have been detected:");
		}

	}

	/**
	 * To be overriden by subclasses, in order to provide the FileSource
	 * with the relevant parameters setting.
	 * @return
	 */
	protected abstract FileSource getFileSource();

	@Override
	public void parseFile(final IGraphParserListener listener, final String filename) {

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
			throw GamaRuntimeException.error("Unable to load file from " + filename + " (" + e.getLocalizedMessage() +
				")");
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
			throw GamaRuntimeException.error("Error while parsing a graph from " + filename + " (" +
				e.getLocalizedMessage() + ")");
		} catch (Exception e) {
			throw GamaRuntimeException.error("Error while parsing a graph from " + filename + " (" +
				e.getLocalizedMessage() + ")");
		} catch (Throwable e) {
			throw GamaRuntimeException.error("Error while parsing a graph from " + filename + " (" +
				e.getLocalizedMessage() + ")");
		} finally {

			// end of parsing, warn everybody
			ourSink.endParsing();
		}

		// that's all folks :-)

	}

}
