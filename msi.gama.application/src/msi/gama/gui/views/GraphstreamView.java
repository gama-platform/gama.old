package msi.gama.gui.views;

import java.awt.Frame;
import java.util.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.outputs.*;
import msi.gama.util.graph.*;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.*;
import org.graphstream.ui.swingViewer.Viewer.CloseFramePolicy;
import org.graphstream.ui.swingViewer.util.FpsCounter;

/**
 * Registers as a graph listeners, then is aware of changes.
 * 
 * TODO stop when hidden
 * 
 * 
 * 
 * @author Samuel Thiriot
 * 
 */
public class GraphstreamView extends GamaViewPart implements IViewWithZoom {

	public static final String ID = GuiUtils.GRAPHSTREAM_VIEW_ID;

	private static Logger logger = Logger.getLogger(GraphstreamView.class);
	Composite myComposite = null;

	// AWT stuff

	Frame awtFrame;

	// graphstream objects
	Graph graphstreamGraph;
	Viewer graphstreamViewer;
	View graphstreamView;
	SpringBox graphstreamLayout;
	GraphRenderer graphstreamRender;

	FpsCounter graphstreamFpsCounter;

	// gama objects
	IGraph gamaGraph;
	GraphEventQueue gamaGraphEventQueue;
	/**
	 * Maps GAMA objects (vertices and edges) to String key that
	 * are used for graphstream structures.
	 */
	Map<Object, String> vertex2key;
	Map<Object, String> edge2key;

	// internal objects
	boolean isSynchronized = false;

	ThreadProcessEvents threadAsyncEvents = null;

	/**
	 * This thread processes events as soon as possible.
	 * Used when the view is not synchronized
	 * 
	 * @author Samuel Thiriot
	 */
	class ThreadProcessEvents extends Thread {

		boolean isKilled = false;
		boolean isRunning = false;

		// private final Logger logger = Logger.getLogger(ThreadProcessEvents.class);

		public ThreadProcessEvents() {
			setName("graphevents4graphstream");
			setPriority(NORM_PRIORITY);
			setDaemon(true);
			isRunning = false;
			isKilled = false;
			// for debug
			// logger.setLevel(Level.DEBUG);
		}

		/**
		 * Ends this thread. Will no more
		 * be usable any more.
		 */
		public void kill() {
			isKilled = true;
			this.interrupt();
		}

		public void startEventProcessing() {
			isRunning = true;
			if ( logger.isDebugEnabled() ) {
				logger.debug("starting processing");
			}

			this.interrupt();
		}

		public void stopEventProcessing() {
			if ( logger.isDebugEnabled() ) {
				logger.debug("stopping processing");
			}

			isRunning = false;
		}

		@Override
		public void run() {

			// if ( logger.isTraceEnabled() ) {
			// logger.trace("starting event processing thread");
			// }

			while (!isKilled) {

				// if ( logger.isTraceEnabled() ) {
				// logger.trace("main event loop");
				// }

				if ( isRunning ) {
					// process events quicky
					while (isRunning) {

						// if (logger.isTraceEnabled())
						// logger.trace("processing events");

						if ( !gamaGraphEventQueue.queue.isEmpty() ) {
							processPendingEventsSynchro();
						}

						try {
							// if (logger.isTraceEnabled())
							// logger.trace("short sleep");
							sleep(200);

						} catch (InterruptedException e) {}
					}
				} else {
					// if ( logger.isTraceEnabled() ) {
					// logger.trace("asleep for a while");
					// }

					// do not process events
					// sleep
					// will be waken later by an interrupt
					try {
						sleep(1000000);
					} catch (InterruptedException e) {}
				}
			}

			// if ( logger.isTraceEnabled() ) {
			// logger.trace("thread died");
			// }

		} // end run

	}

	public GraphstreamView() {
		gamaGraphEventQueue = new GraphEventQueue();

		// for debug : logger.setLevel(Level.DEBUG);

		setSynchronized(false);

	}

	@Override
	public Integer[] getToolbarActionsId() {
		// TODO
		return new Integer[] { PAUSE, REFRESH, SYNC,
			// SEPARATOR,
			// SEPARATOR,
			// TODO implement SNAPSHOT,
			SEP, ZOOM_IN, ZOOM_OUT, ZOOM_FIT };

	}

	@Override
	public void ownCreatePartControl(final Composite parent) {

		// my SWT container
		myComposite = new Composite(parent, SWT.EMBEDDED);
		myComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		myComposite.setLayout(new FillLayout());

		// create the SWT AWT bridge
		awtFrame = SWT_AWT.new_Frame(myComposite);
		if ( awtFrame == null ) { throw new RuntimeException("unable to intialize the SWT to AWT bridge"); }

		graphstreamGraph = new SingleGraph("I can see dead pixels");

		graphstreamViewer = new Viewer(graphstreamGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		graphstreamViewer.setCloseFramePolicy(CloseFramePolicy.CLOSE_VIEWER);

		graphstreamLayout = new SpringBox();
		graphstreamLayout.setStabilizationLimit(0);
		// graphstreamLayout.shake();
		graphstreamViewer.enableAutoLayout(graphstreamLayout);
		// graphstreamViewer.

		// graphstreamView = graphstreamViewer.addDefaultView(true);
		graphstreamRender = Viewer.newGraphRenderer();
		graphstreamView = new DefaultView(graphstreamViewer, "view2", graphstreamRender);

		graphstreamViewer.addView(graphstreamView);

		// graphstreamRenderer.open(graphstreamViewer.getGraphicGraph(), frame);
		awtFrame.add(graphstreamView);

		// graphstreamView.setVisible(true);
		awtFrame.setVisible(true);

		graphstreamFpsCounter = new FpsCounter();
		graphstreamFpsCounter.beginFrame();

	}

	/*
	 * 
	 * public void initNetwork() {
	 * 
	 * new Thread() {
	 * public void run() {
	 * WattsStrogatzGenerator generator = new WattsStrogatzGenerator(2000, 4, 0.07);
	 * 
	 * generator.addSink(graphstreamGraph);
	 * 
	 * generator.begin();
	 * generator.nextEvents();
	 * 
	 * System.out.println("generation started");
	 * 
	 * while (generator.nextEvents()) {
	 * //generator.addSink(sink)
	 * System.out.print(".");
	 * try {
	 * Thread.sleep(20);
	 * } catch (InterruptedException e) {
	 * // TODO Auto-generated catch block
	 * e.printStackTrace();
	 * }
	 * graphstreamFpsCounter.endFrame();
	 * System.err.println(graphstreamFpsCounter.getAverageFramesPerSecond());
	 * graphstreamFpsCounter.beginFrame();
	 * }
	 * generator.end();
	 * System.out.println("ended");
	 * 
	 * }
	 * }.start();
	 * 
	 * 
	 * }
	 */
	protected String getOrCreateIdForVertex(final Object v) {
		String id = vertex2key.get(v);
		if ( id == null ) {
			id = new Integer(vertex2key.size()).toString();
			vertex2key.put(v, id);
		}
		return id;
	}

	protected String getOrCreateIdForEdge(final Object n) {
		String id = edge2key.get(n);
		if ( id == null ) {
			id = new Integer(edge2key.size()).toString();
			edge2key.put(n, id);
		}
		return id;
	}

	protected String getIdForVertex(final Object v) {
		return vertex2key.get(v);
	}

	protected String getIdForEdge(final Object e) {
		return edge2key.get(e);
	}

	protected void processEvent(final GraphEvent event) {

		switch (event.eventType) {
			case EDGE_ADDED:
				graphstreamGraph.addEdge(getOrCreateIdForEdge(event.edge),
					getOrCreateIdForVertex(gamaGraph.getEdgeSource(event.edge)),
					getOrCreateIdForVertex(gamaGraph.getEdgeTarget(event.edge)));
				break;
			case EDGE_REMOVED: {
				String id = getIdForEdge(event.edge);
				if ( id != null ) {
					edge2key.remove(id);
					try {
						graphstreamGraph.removeEdge(id);
					} catch (ElementNotFoundException e) {
						// ignored: when nodes were removed, edges were automatically removed from
						// the graphstream graph
					}
				} else {
					logger.warn("unable to find edge " + event.vertex + ", the display may be broken");
				}
			}
				break;
			case VERTEX_ADDED:
				graphstreamGraph.addNode(getOrCreateIdForVertex(event.vertex));
				break;
			case VERTEX_REMOVED: {
				String id = getIdForVertex(event.vertex);
				if ( id != null ) {
					vertex2key.remove(id);
					try {
						graphstreamGraph.removeNode(id);
					} catch (ElementNotFoundException e) {
						e.getStackTrace();
					}
				} else {
					logger.warn("unable to find vertex " + event.vertex + ", the display may be broken");
				}
			}
				break;

		}

	}

	protected void updateFromExistingNetwork() {

		if ( gamaGraph == null ) { return; }

		if ( logger.isDebugEnabled() ) {
			logger.debug("updating from gama network");
		}

		for ( Object v : gamaGraph.getVertices() ) {
			graphstreamGraph.addNode(getOrCreateIdForVertex(v));
		}
		for ( Object e : gamaGraph.getEdges() ) {

			try {
				graphstreamGraph.addEdge(getOrCreateIdForEdge(e), getOrCreateIdForVertex(gamaGraph.getEdgeSource(e)),
					getOrCreateIdForVertex(gamaGraph.getEdgeTarget(e)));
			} catch (EdgeRejectedException e2) {
				logger.warn("unable to display an edge " + e);
			}

		}
	}

	protected void processPendingEventsSynchro() {

		// process events
		if ( !gamaGraphEventQueue.isEmpty() ) {

			if ( logger.isDebugEnabled() ) {
				logger.debug("processing queued events (" + gamaGraphEventQueue.size() + ")");
			}

			synchronized (gamaGraphEventQueue.queue) {
				for ( GraphEvent event : gamaGraphEventQueue.queue ) {

					// if ( logger.isTraceEnabled() ) {
					// logger.trace("processing event " + event);
					// }
					try {
						processEvent(event);
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
				gamaGraphEventQueue.queue.clear();
			}
			graphstreamLayout.shake();
		}

	}

	protected void useNetwork(final IGraph novelGraph) {
		gamaGraph = novelGraph;

		if ( gamaGraph != null ) {
			if ( logger.isDebugEnabled() ) {
				logger.debug("gama graph found, initializing internal structures");
			}
			gamaGraph.addListener(gamaGraphEventQueue);
			vertex2key = new HashMap<Object, String>();
			edge2key = new HashMap<Object, String>();

		}

	}

	/**
	 * removes all the previous references to the older network
	 */
	protected void clearNetwork() {

		if ( logger.isDebugEnabled() ) {
			logger.debug("clearing old graph");
		}

		gamaGraph.removeListener(gamaGraphEventQueue);
		vertex2key.clear();
		edge2key.clear();
		graphstreamGraph.clear();

	}

	@Override
	public void update(final IDisplayOutput output) {

		GraphstreamOutput myOutput = (GraphstreamOutput) output;

		// retrieve the graph to display from the output
		IGraph novelGraph = myOutput.getGraph();

		// maybe we do not have a network ?
		if ( gamaGraph == null ) {

			if ( logger.isDebugEnabled() ) {
				logger.debug("gama graph not available, attempting to retrieve the graph passed as parameter...");
			}
			gamaGraph = novelGraph;

			useNetwork(novelGraph);
			updateFromExistingNetwork();

		} else if ( gamaGraph != novelGraph ) {

			if ( logger.isDebugEnabled() ) {
				logger.debug("gama graph changed, using the novel one...");
			}

			clearNetwork();
			useNetwork(novelGraph);
			updateFromExistingNetwork();

		}

		if ( gamaGraph == null ) {
			logger.warn("no graph to display, skipping update");
			return;
		}

		if ( myOutput.getLowQuality() ) {
			graphstreamGraph.removeAttribute("ui.quality");
			graphstreamGraph.removeAttribute("ui.antialias");
		} else {
			graphstreamGraph.addAttribute("ui.quality");
			graphstreamGraph.addAttribute("ui.antialias");
		}

		// if the view is synchronized, it is time to process events !
		if ( isSynchronized ) {
			processPendingEventsSynchro();
		}

	}

	@Override
	public void zoomToFit() {
		graphstreamView.getCamera().setAutoFitView(true);
		graphstreamLayout.shake();
	}

	@Override
	public void zoomIn() {

		graphstreamView.getCamera().setAutoFitView(false);
		graphstreamView.getCamera().setViewPercent(graphstreamView.getCamera().getViewPercent() * 0.9);

	}

	@Override
	public void zoomOut() {

		graphstreamView.getCamera().setAutoFitView(false);
		graphstreamView.getCamera().setViewPercent(graphstreamView.getCamera().getViewPercent() * 1.1);

	}

	@Override
	public void snapshot() {
		// TODO implement !!!

	}

	@Override
	public void setSynchronized(final boolean synchro) {

		this.isSynchronized = synchro;

		if ( !isSynchronized ) {
			if ( threadAsyncEvents == null ) {
				threadAsyncEvents = new ThreadProcessEvents();
				threadAsyncEvents.start();
			}
			threadAsyncEvents.startEventProcessing();
		} else {
			if ( threadAsyncEvents != null ) {
				threadAsyncEvents.stopEventProcessing();
			}
		}

	}

	@Override
	public void dispose() {
		if ( threadAsyncEvents != null ) {
			threadAsyncEvents.kill();
		}
		super.dispose();
	}

	/**
	 * This method does nothing for Graphstream display
	 */
	@Override
	public void toggleView() {
		System.out.println("toggle view is only available for Opengl Display");
	}

	/**
	 * This method does nothing for Graphstream display
	 */
	// @Override
	// public void togglePicking() {
	// System.out.println("toggle picking is only available for Opengl Display");
	// }

	/**
	 * This method does nothing for Graphstream display
	 */
	@Override
	public void toggleArcball() {
		System.out.println("arcball view is only available for Opengl Display");
	}

	/**
	 * This method does nothing for Graphstream display
	 */
	@Override
	public void toggleInertia() {
		System.out.println("inertia mode is only available for Opengl Display");
	}

	/**
	 * This method does nothing for Graphstream display
	 */
	@Override
	public void toggleSelectRectangle() {
		System.out.println("select rectangle tool is only available for Opengl Display");
	}

	/**
	 * This method does nothing for Graphstream display
	 */
	@Override
	public void toggleTriangulation() {
		System.out.println("triangulation tool is only available for Opengl Display");
	}

	/**
	 * This method does nothing for Graphstream display
	 */
	@Override
	public void toggleSplitLayer() {
		System.out.println("toggleSplitLayer tool is only available for Opengl Display");

	}

	/**
	 * This method does nothing for Graphstream display
	 */
	@Override
	public void toggleRotation() {
		System.out.println("toggleRotation tool is only available for Opengl Display");

	}

	/**
	 * This method does nothing for Graphstream display
	 */
	@Override
	public void addShapeFile() {
		// TODO Auto-generated method stub

	}

	@Override
	public void newZoomLevel(final double zoomLevel) {}

	@Override
	public void toggleCamera() {
		// TODO Auto-generated method stub
		System.out.println("toggleCamera tool is only available for Opengl Display");
	}

}
