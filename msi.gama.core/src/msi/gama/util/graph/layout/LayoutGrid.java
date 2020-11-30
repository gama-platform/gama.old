package msi.gama.util.graph.layout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.Graphs;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Random;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Queries;
import msi.gaml.types.Types; 

public class LayoutGrid {

	private final IGraph<IShape, IShape> graph;

	private final double coeffSq;

	private final IShape envelopeGeometry;

	public LayoutGrid(final IGraph<IShape, IShape> graph, final IShape envelopeGeometry, final double coeffSq) {
		this.graph = graph;
		this.envelopeGeometry = envelopeGeometry;
		this.coeffSq = coeffSq;
	}

	@SuppressWarnings("null")
	public void applyLayout(final IScope scope) {

		IList<IShape> places = null;
		IMap<IShape, GamaPoint> locs = GamaMapFactory.create();
		do {
			places = Spatial.Transformations.toSquares(scope, envelopeGeometry,
					Maths.round(graph.getVertices().size() * coeffSq), false);
		} while (places.size() < graph.getVertices().size());

		IShape currentV = null;
		int dmax = -1;
		final Map<IShape, Integer> degrees = new IdentityHashMap<>();
		final int nbV = graph.getVertices().size();
		for (final IShape v : graph.getVertices()) {
			final int d = graph.degreeOf(v);
			locs.put(v, v.getLocation().copy(scope).toGamaPoint());
			degrees.put(v, d);
			if (d > dmax) {
				dmax = d;
				currentV = v;
			}
		}
		IShape center = Queries.overlapping(scope, places, envelopeGeometry.getLocation()).firstValue(scope);
		places.remove(center);
		locs.put(currentV, center.getLocation().toGamaPoint());
		final List<IShape> open = new ArrayList<>();
		final List<IShape> remaining = new ArrayList<>();
		remaining.addAll(graph.getVertices());
		remaining.remove(currentV);

		final List<IShape> close = new ArrayList<>();
		close.add(currentV);

		while (close.size() < nbV) {
			IList<IShape> neigh = Graphs.predecessorsOf(scope, graph, currentV);
			neigh.addAll(Graphs.successorsOf(scope, graph, currentV));
			neigh = Random.opShuffle(scope, neigh);

			for (final IShape n : neigh) {
				if (remaining.contains(n)) {
					center = Queries.closest_to(scope, places, locs.get(currentV));
					places.remove(center);
					locs.put(n, center.getLocation().toGamaPoint());
					open.add(n);
					remaining.remove(n);
				}
			}
			if (remaining.isEmpty()) {
				break;
			}
			dmax = -1;
			java.util.Collections.shuffle(open, scope.getRandom().getGenerator());
			for (final IShape v : open) {
				final int d = degrees.get(v);
				if (d >= dmax) {
					dmax = d;
					currentV = v;
				}
			}
			open.remove(currentV);
			close.add(currentV);
			if (open.isEmpty()) {
				IShape nV = null;
				dmax = -1;
				java.util.Collections.shuffle(remaining, scope.getRandom().getGenerator());

				for (final IShape v : remaining) {
					final int d = degrees.get(v);
					if (d > dmax) {
						dmax = d;
						nV = v;
					}
				}
				remaining.remove(nV);
				open.add(nV);
				final Set<IShape> neigh2 = new HashSet<IShape>(Graphs.predecessorsOf(scope, graph, nV));
				neigh2.addAll(Graphs.successorsOf(scope, graph, nV));

				neigh2.removeAll(close);
				neigh2.removeAll(open);
				if (!neigh2.isEmpty()) {
					final IList<GamaPoint> pts = GamaListFactory.create(Types.POINT);
					for (final IShape n : neigh2) {
						pts.add(locs.get(n));
					}
					final GamaPoint targetLoc = (GamaPoint) msi.gaml.operators.Containers.mean(scope, pts);
					center = places.size() > 0 ? Queries.closest_to(scope, places, targetLoc.getLocation())
							: locs.get(nV);
				} else {

					center = places.size() > 0 ? places.anyValue(scope) : locs.get(nV);
				}
				places.remove(center);
				locs.put(nV, center.getLocation().toGamaPoint());

			}

		}

		for (IShape v : locs.keySet()) {
			v.setLocation(locs.get(v));

		}
	}

}
