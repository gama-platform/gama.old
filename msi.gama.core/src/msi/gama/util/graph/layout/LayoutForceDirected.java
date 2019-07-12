package msi.gama.util.graph.layout;

import java.util.IdentityHashMap;
import java.util.Map;

import org.jgrapht.Graph;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gaml.operators.Containers;
import msi.gaml.operators.Points;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.types.Types;

public class LayoutForceDirected {

	private final Graph<IShape, IShape> graph;
	private final boolean equi;
	private final double criterion;
	private final double coolingRate;
	private final int maxit;
	private final double coeffForce;
	IShape bounds;

	private int iteration = 0;

	private double area;
	private double k;
	private double t;

	private boolean equilibriumReached = false;
	private final Map<IShape, GamaPoint> disp;
	private final Map<IShape, GamaPoint> loc;

	/**
	 * Creates a new Simulation.
	 *
	 * @param graph
	 * @param p
	 * @throws ParseException
	 */
	public LayoutForceDirected(final Graph<IShape, IShape> graph, final IShape bounds, final double coeffForce,
			final double coolingRate, final int maxit, final boolean isEquilibriumCriterion, final double criterion) {
		this.graph = graph;
		this.bounds = bounds;
		this.equi = isEquilibriumCriterion;
		this.criterion = criterion;
		this.coolingRate = coolingRate;
		this.maxit = maxit;
		this.coeffForce = coeffForce;
		this.disp = new IdentityHashMap<>();
		this.loc = new IdentityHashMap<>();

	}

	/**
	 * Starts the simulation.
	 *
	 * @return number of iterations used until criterion is met
	 */
	public int startSimulation(final IScope scope) {

		iteration = 0;
		equilibriumReached = false;

		area = Math.min(bounds.getWidth() * bounds.getWidth(), bounds.getHeight() * bounds.getHeight());
		k = coeffForce * Math.sqrt(area / graph.vertexSet().size());
		t = bounds.getWidth() / 10;

		for (final IShape v : graph.vertexSet()) {
			disp.put(v, new GamaPoint());
			loc.put(v, v.getCentroid().copy(scope));
		}

		if (equi) {
			// simulate until mechanical equilibrium
			while (!equilibriumReached && iteration < maxit) {
				simulateStep(scope);
			}
		} else {
			// simulate maxit-steps
			for (int i = 0; i < maxit; i++) {
				simulateStep(scope);
			}
		}
		for (final IShape v : graph.vertexSet()) {
			v.setLocation(loc.get(v));
		}
		return iteration;
	}

	/**
	 * Simulates a single step.
	 */
	private void simulateStep(final IScope scope) {
		final double toleranceCenter = Math.sqrt(area) / 10.0;
		final double distanceMinCenter = Math.sqrt(area) / 3.0;
		// calculate repulsive forces (from every vertex to every other)
		for (final IShape v : graph.vertexSet()) {
			// reset displacement vector for new calculation
			final GamaPoint vDisp = disp.get(v);
			vDisp.setLocation(0, 0, 0);
			for (final IShape u : graph.vertexSet()) {
				if (!v.equals(u)) {
					// normalized difference position vector of v and u
					GamaPoint deltaPos = Points.subtract(loc.get(v), loc.get(u)).toGamaPoint();
					final double length = Points.norm(scope, deltaPos);

					if (length != 0) {
						deltaPos = Points.multiply(deltaPos, forceRepulsive(length, k) / length).toGamaPoint();
					}

					vDisp.add(deltaPos);

				}
			}
		}

		// calculate attractive forces (only between neighbors)
		for (final IShape e : graph.edgeSet()) {
			final IShape u = graph.getEdgeSource(e);
			final IShape v = graph.getEdgeTarget(e);
			// normalized difference position vector of v and u
			GamaPoint deltaPos = Points.subtract(loc.get(v), loc.get(u)).toGamaPoint();
			final double length = Points.norm(scope, deltaPos);

			if (length != 0) {
				deltaPos = Points.multiply(deltaPos, forceAttractive(length, k) / length).toGamaPoint();
			}

			disp.get(v).minus(deltaPos);
			disp.get(u).add(deltaPos);

		}

		// assume equilibrium
		equilibriumReached = true;

		for (final IShape v : graph.vertexSet()) {

			GamaPoint d = new GamaPoint(disp.get(v));
			final double length = Points.norm(scope, d);

			// no equilibrium if one vertex has too high net force
			if (length > criterion) {
				equilibriumReached = false;
			}
			// limit maximum displacement by temperature t
			if (length != 0) {
				d = Points.multiply(d, Math.min(length, t) / length).toGamaPoint();
			}
			final GamaPoint l = loc.get(v);
			l.add(d);
			if (!bounds.intersects(l)) {
				loc.put(v, Punctal._closest_point_to(l, bounds).toGamaPoint());
			}

		}
		final GamaPoint center = (GamaPoint) Containers.mean(scope, GamaListFactory.wrap(Types.POINT, loc.values()));
		if (center.distance3D(bounds.getCentroid()) > toleranceCenter) {
			final GamaPoint d = Points.subtract(bounds.getCentroid(), center).toGamaPoint();
			d.multiplyBy(0.5);
			for (final IShape v : graph.vertexSet()) {
				final GamaPoint l = loc.get(v);
				l.add(d);
				if (!bounds.intersects(l)) {
					loc.put(v, Punctal._closest_point_to(l, bounds).toGamaPoint());
				}
			}
		}
		double maxDist = graph.vertexSet().stream().mapToDouble(v -> v.euclidianDistanceTo(center)).max().getAsDouble();
		if (maxDist < distanceMinCenter) {
			maxDist = distanceMinCenter - maxDist;
			for (final IShape v : graph.vertexSet()) {
				final GamaPoint l = loc.get(v);
				final GamaPoint d = Points.subtract(l, center).toGamaPoint();
				final double len = d.norm();
				if (len > 0) {
					d.multiplyBy(maxDist / d.norm());
				}
				l.add(d);
				if (!bounds.intersects(l)) {
					loc.put(v, Punctal._closest_point_to(l, bounds).toGamaPoint());
				}
			}
		}

		t = Math.max(t * (1 - coolingRate), 1);

		iteration++;
	}

	/**
	 * Calculates the amount of the attractive force between vertices using the expression entered by the user.
	 *
	 * @param d
	 *            the distance between the two vertices
	 * @param k
	 * @return amount of force
	 */
	private double forceAttractive(final double d, final double k) {
		return k == 0 ? 1 : d * d / k;
	}

	/**
	 * Calculates the amount of the repulsive force between vertices using the expression entered by the user.
	 *
	 * @param d
	 *            the distance between the two vertices
	 * @param k
	 * @return amount of force
	 */
	private double forceRepulsive(final double d, final double k) {
		return d == 0 ? 1 : k * k / d;
	}

}
