package msi.gama.networks.ui;

import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.views.GamaViewPart;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.util.graph.GraphEvent;
import msi.gama.util.graph.GraphEventQueue;
import msi.gama.util.graph.IGraph;
import msi.gama.util.graph._Edge;
import msi.gama.util.graph._Vertex;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.springbox.SpringBox;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.GraphRenderer;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.util.FpsCounter;

public class GraphstreamView extends GamaViewPart {

	


	public static final String ID = GuiUtils.GRAPHSTREAM_VIEW_ID;

	Composite myComposite = null;
	
	Frame awtFrame;

	Graph graphstreamGraph;
	Viewer graphstreamViewer;
	View graphstreamView ;
	SpringBox graphstreamLayout ;
	
	IGraph gamaGraph;
	GraphEventQueue gamaGraphEventQueue;
	
	FpsCounter graphstreamFpsCounter ;
	
	Map<_Vertex,String> vertex2key;
	Map<_Edge,String> edge2key;
	
	public GraphstreamView() {
		gamaGraphEventQueue = new GraphEventQueue();
		
		
	}

	@Override
	protected Integer[] getToolbarActionsId() {
		// TODO
		return new Integer[] {
			//PAUSE, REFRESH, SYNCHRONIZE, SEPARATOR, 
			// TODO SNAPSHOT,
			//SEPARATOR, ZOOM_IN, ZOOM_OUT, ZOOM_FIT 
			};
		
	}
	
	

	@Override
	public void ownCreatePartControl(Composite parent) {
		
		// my SWT container
		myComposite  = new Composite(parent, SWT.EMBEDDED);
		myComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		myComposite.setLayout(new FillLayout());
		
		// create the SWT AWT bridge
		awtFrame = SWT_AWT.new_Frame(myComposite);
		if (awtFrame == null) {
			throw new RuntimeException("unable to intialize the SWT to AWT bridge");
		}
		
		graphstreamGraph = new SingleGraph("I can see dead pixels");
		
		graphstreamViewer = new Viewer(graphstreamGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		GraphRenderer renderer = Viewer.newGraphRenderer();
		
		graphstreamLayout = new SpringBox();
		graphstreamLayout.setStabilizationLimit(0);
		//graphstreamLayout.shake();
		graphstreamViewer.enableAutoLayout(graphstreamLayout);
		//graphstreamViewer.
		
		graphstreamGraph.addAttribute("ui.quality");
		graphstreamGraph.addAttribute("ui.antialias");
	
		//graphstreamView = graphstreamViewer.addDefaultView(true);
		graphstreamView = new DefaultView(graphstreamViewer, "view2", Viewer.newGraphRenderer());
		graphstreamViewer.addView(graphstreamView);
		
		//graphstreamRenderer.open(graphstreamViewer.getGraphicGraph(), frame);
		awtFrame.add(graphstreamView);
		
		//graphstreamView.setVisible(true);
		awtFrame.setVisible(true);

		
		graphstreamFpsCounter = new FpsCounter();
		graphstreamFpsCounter.beginFrame();
		
		
	
		
	}
	
	
	
	public void initNetwork() {
	
		new Thread() {
			public void run() {
				WattsStrogatzGenerator generator = new WattsStrogatzGenerator(2000, 4, 0.07);
				
				generator.addSink(graphstreamGraph);
				
				generator.begin();
				generator.nextEvents();
			
				System.out.println("generation started");

				while (generator.nextEvents()) {
					//generator.addSink(sink)
					System.out.print(".");
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					graphstreamFpsCounter.endFrame();
					System.err.println(graphstreamFpsCounter.getAverageFramesPerSecond());
					graphstreamFpsCounter.beginFrame();
				}
				generator.end();
				System.out.println("ended");
				
			}
		}.start();
		
		
	}
	/*
	protected String getOrCreateIdForVertex(_Vertex v) {
		
	}
	
	protected String getOrCreateIdForEdge(_Edge n) {
		
	}*/
	
	@Override
	public void update(IDisplayOutput output) {
		
		if (gamaGraph == null) {
			GraphstreamOutput fso = (GraphstreamOutput)getOutput();
			gamaGraph = fso.getGraph();
			if (gamaGraph != null) {
				gamaGraph.addListener(gamaGraphEventQueue);
				vertex2key = new HashMap<_Vertex, String>();
				edge2key = new HashMap<_Edge, String>();
				
				
			}
			System.err.println("view: "+gamaGraph);
					
			for (Object v: gamaGraph.getVertices()) {
				graphstreamGraph.addNode(v.toString());	// TODO avoid toString
			}
			for (Object e: gamaGraph.getEdges()) {
				
				try {
				graphstreamGraph.addEdge(
					e.toString(),
					gamaGraph.getEdgeSource(e).toString(),
					gamaGraph.getEdgeTarget(e).toString()
					);
				} catch (EdgeRejectedException e2) {
					System.err.println("ALLLERRRT ! perdu lien");
				}
				
			}
		}
		
		if (gamaGraph == null) {
			System.err.println("WARNING ignored update");
			return;
		}
		
		// process events
		for (GraphEvent event : gamaGraphEventQueue.queue) {
			System.err.println(event);
			try {
			switch (event.eventType) {
				case EDGE_ADDED: 
					graphstreamGraph.addEdge(
						event.edge.toString(),
						event.edge.getSource().toString(), 
						event.edge.getTarget().toString()
						);
					break;
				case EDGE_REMOVED: 
					graphstreamGraph.removeEdge(event.edge.toString());
					break;
				case VERTEX_ADDED: 
					graphstreamGraph.addNode(event.vertex.toString());
					break;
				case VERTEX_REMOVED: 
					graphstreamGraph.removeNode(event.vertex.toString());
					break;
						
			}
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		
	}

	

}
