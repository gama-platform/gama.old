/*******************************************************************************************************
 *
 * msi.gama.util.graph.layout.PrefuseStaticLayoutAbstract.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.layout;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph.GraphUtilsPrefuse;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.operators.fastmaths.FastMath;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.layout.Layout;
import prefuse.activity.Activity;
import prefuse.data.Graph;
import prefuse.render.DefaultRendererFactory;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;

@SuppressWarnings ({ "rawtypes" })
public abstract class PrefuseStaticLayoutAbstract implements IStaticLayout {

	public static final String PREFUSE_GRAPH = "g";

	private int count_measures = 20; // TODO adapt to the size of the graph
	private Map<VisualItem, Double> lastNode2measures = null;

	protected Random random = new Random();

	private final Logger logger = Logger.getLogger(getClass().getName());

	private void resetThermometer(final int nbtuples) {
		lastNode2measures = null;
		count_measures = CmnFastMath.min(FastMath.max(nbtuples / 20, // ideal:
																		// measure
																		// 5% of
																		// nodes
				10 // measure at least 10, even if the network is small !
		), nbtuples - 1 // but don't try to measure more than existing..;
		);
	}

	private Double insertThermometer(final Visualization viz) {

		try {

			if (lastNode2measures == null) {

				final VisualTable tuples = (VisualTable) viz.getVisualGroup(PREFUSE_GRAPH + ".nodes");
				final int nbtuples = tuples.getTupleCount();

				lastNode2measures = new HashMap<VisualItem, Double>(count_measures);

				// let's select n random nodes that will be used for measurement
				while (lastNode2measures.size() < count_measures) {
					final VisualItem i = tuples.getItem(random.nextInt(nbtuples));
					lastNode2measures.put(i, i.getX() + i.getY());
				}

				// no previous reference, the difference (and temperature) is
				// infinite !
				return Double.POSITIVE_INFINITY;

			} else {

				final Map<VisualItem, Double> newMeasures = new HashMap<VisualItem, Double>(count_measures);

				double temperature = 0.0;

				for (final VisualItem i : lastNode2measures.keySet()) {
					final double prev = lastNode2measures.get(i);
					final double novel = i.getX() + i.getY();
					newMeasures.put(i, novel);
					temperature += FastMath.pow(prev - novel, 2);
				}

				lastNode2measures = newMeasures;

				logger.fine("temperature = " + temperature);
				return temperature;
			}

		} catch (final RuntimeException e) {
			return Double.POSITIVE_INFINITY; // in case of error, we just have
												// no measure...
		}
	}

	/**
	 * Takes a prefuse graph and applies a prefuse layout, with a max time for execution; the layout is bounded
	 * according to parameters.
	 * 
	 * @param prefuseGraph
	 * @param prefuseLayout
	 * @param bounds
	 * @param maxtime
	 */
	private void renderLayout(final Graph prefuseGraph, final Layout prefuseLayout, final Rectangle2D bounds,
			final long maxtime) {

		// configure the layout
		prefuseLayout.setGroup(PREFUSE_GRAPH);
		prefuseLayout.setLayoutBounds(bounds);
		prefuseLayout.setMargin(0, 0, 0, 0);
		// prefuseLayout.setDuration(maxtime);
		prefuseLayout.setStepTime(0);

		// create the visualization required to drive a layout
		final Visualization viz = new Visualization();
		viz.addGraph(PREFUSE_GRAPH, prefuseGraph);
		viz.setVisible(PREFUSE_GRAPH, null, true);

		// viz.setInteractive(PREFUSE_GRAPH, null, false); // no interactivity
		// there

		final ActionList actionsLayout = new ActionList(Activity.INFINITY);
		actionsLayout.add(prefuseLayout);

		viz.putAction("layout", actionsLayout);

		viz.setRendererFactory(new DefaultRendererFactory());

		final Display display = new Display(viz);
		display.setSize((int) FastMath.ceil(bounds.getWidth()), (int) FastMath.ceil(bounds.getHeight())); // set
																											// display
																											// size

		// init positions
		Iterator itPrefuseNodes = viz.getVisualGroup(PREFUSE_GRAPH + ".nodes").tuples();
		while (itPrefuseNodes.hasNext()) {
			final VisualItem prefuseNode = (VisualItem) itPrefuseNodes.next();
			final Object pn = prefuseNode.get(GraphUtilsPrefuse.PREFUSE_ATTRIBUTE_GAMA_OBJECT);
			if (pn instanceof IShape) {
				final IShape gamaNode = (IShape) pn;
				prefuseNode.setX(gamaNode.getLocation().getX());
				prefuseNode.setY(gamaNode.getLocation().getY());
			}
		}

		// actually run layout
		viz.run("layout");
		resetThermometer(prefuseGraph.getNodeCount());
		final long timeBegin = System.currentTimeMillis();
		final long sleepTime = 20;
		System.err.println("layout !");
		Double temperature = Double.POSITIVE_INFINITY;
		while (System.currentTimeMillis() - timeBegin < maxtime && temperature > 0.1) {

			// compute temperature
			temperature = insertThermometer(viz);

			try {
				Thread.sleep(sleepTime);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.err.print(".");

		}

		// stop and end layout
		viz.cancel("layout");
		// viz.cancel("layout");
		logger.fine("layout finished in: " + (System.currentTimeMillis() - timeBegin));

		// retrieve the resulting coordinates
		itPrefuseNodes = viz.getVisualGroup(PREFUSE_GRAPH + ".nodes").tuples();

		while (itPrefuseNodes.hasNext()) {
			final VisualItem prefuseNode = (VisualItem) itPrefuseNodes.next();
			final Object pn = prefuseNode.get(GraphUtilsPrefuse.PREFUSE_ATTRIBUTE_GAMA_OBJECT);
			if (pn instanceof IShape) {
				final IShape gamaNode = (IShape) pn;
				final ILocation newloc = new GamaPoint(prefuseNode.getX(), prefuseNode.getY());
				gamaNode.setLocation(newloc);

			}

		}

		// free memory
		viz.removeGroup(PREFUSE_GRAPH);
		prefuseGraph.clear();

	}

	/**
	 * Takes a prefuse graph and applies a prefuse layout, with a max time for execution. Layout will use the space
	 * defined by the world agent found through the gama scope.
	 * 
	 * @param prefuseGraph
	 * @param prefuseLayout
	 * @param scope
	 * @param maxtime
	 */
	private void renderLayout(final Graph prefuseGraph, final Layout prefuseLayout, final IScope scope,
			final long maxtime) {

		final Envelope envelope = scope.getSimulation().getEnvelope();

		final Rectangle bounds =
				new Rectangle((int) Math.floor(envelope.getMinX()), (int) Math.floor(envelope.getMinY()),
						(int) Math.ceil(envelope.getWidth()), (int) Math.ceil(envelope.getHeight()));

		renderLayout(prefuseGraph, prefuseLayout, bounds, maxtime);

	}

	/**
	 * The actual creation of the prefuse layout to be used by the layout process.
	 * 
	 * @param timeout
	 * @param options
	 * @return
	 */
	protected abstract Layout createLayout(final IScope scope, long timeout, Map<String, Object> options);

	/**
	 * Returns a concise name for this layout
	 * 
	 * @return
	 */
	protected abstract String getLayoutName();

	/**
	 * returns the name of options that could be accepted by the layout
	 * 
	 * @return
	 */
	protected abstract Collection<String> getLayoutOptions();

	protected Logger getLayoutLogger() {
		return Logger.getLogger(getLayoutName());

	}

	@Override
	public void doLayoutOneShot(final IScope scope, final GamaGraph<?, ?> graph, final long timeout,
			final Map<String, Object> options) {

		renderLayout(GraphUtilsPrefuse.getPrefuseGraphFromGamaGraphForVisu(scope, graph),
				createLayout(scope, timeout, options), scope, timeout);

		// warn the user of the options that were provided but not used
		final Set<String> uselessOptions = new HashSet<String>(options.keySet());
		uselessOptions.removeAll(getLayoutOptions());
		if (!uselessOptions.isEmpty()) {
			final StringBuffer sb = new StringBuffer();
			sb.append("layout: ").append(getLayoutName());
			sb.append(" ignored some of the options that were provided: ");
			sb.append(uselessOptions);
			sb.append(" (as a reminder, this layout accepts the following options: ");
			sb.append(getLayoutOptions()).append(")");
			GAMA.reportError(scope, GamaRuntimeException.warning(sb.toString(), scope), false);
		}
	}

}
