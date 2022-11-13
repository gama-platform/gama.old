/*******************************************************************************************************
 *
 * PedestrianNetwork.java, in miat.gaml.extensions.pedestrian, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package miat.gaml.extensions.operator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import miat.gaml.extensions.skills.PedestrianRoadSkill;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Creation;
import msi.gaml.operators.Spatial.Operators;
import msi.gaml.operators.Spatial.Transformations;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class PedestrianNetwork.
 */
public class PedestrianNetwork {

	/**
	 * Generate network.
	 *
	 * @param scope the scope
	 * @param obst the obst
	 * @param bounds the bounds
	 * @param regular_network the regular network
	 * @param openArea the open area
	 * @param randomDist the random dist
	 * @param valDistForOpenArea the val dist for open area
	 * @param valDensityOpenArea the val density open area
	 * @param cleanNetwork the clean network
	 * @param toleranceClip the tolerance clip
	 * @param toleranceTriang the tolerance triang
	 * @param minDistPath the min dist path
	 * @param simplicationDistance the simplication distance
	 * @param sizeSquare the size square
	 * @return the i list
	 */
	@SuppressWarnings ("unchecked")
	public static IList<IShape> generateNetwork(final IScope scope, final IList<IContainer<?, ? extends IShape>> obst,
			final IContainer<?, ? extends IShape> bounds, final IContainer<?, ? extends IShape> regular_network,
			final Boolean openArea, final boolean randomDist, final double valDistForOpenArea,
			final double valDensityOpenArea, final boolean cleanNetwork, final double toleranceClip,
			final double toleranceTriang, final double minDistPath, final double simplicationDistance,
			final double sizeSquare) {
		DEBUG.ON();
		DEBUG.OUT("Start generating pedestrian network");

		double t = System.currentTimeMillis();

		boolean walking_area = true;
		IShape walking_shape = scope.getSimulation().getGeometry().copy(scope);
		if (bounds == null || bounds.isEmpty(scope)) {
			walking_area = false;
		} else {
			walking_shape = Spatial.Operators.union(scope, (IContainer<?, IShape>) bounds).copy(scope);
		}
		IShape area = walking_shape.copy(scope);
		double t1 = System.currentTimeMillis();
		DEBUG.OUT("Processing walking area: " + (t1 - t));

		IList<IShape> decomp =
				sizeSquare > 0.0 ? Spatial.Transformations.toSquares(scope, area, sizeSquare, true) : null;

		double t1a = System.currentTimeMillis();
		DEBUG.OUT("|==> Processing squarification : " + (t1a - t1) / 1000);

		for (IContainer<?, ? extends IShape> shp : obst) {
			for (IShape obs : shp.iterable(scope)) {
				if (decomp == null) {
					area = Operators.minus(scope, area, obs);
				} else {
					IList<? extends IShape> geoms = Spatial.Queries.overlapping(scope, decomp, obs);
					for (IShape a : geoms) {
						IShape b = Operators.minus(scope, a, obs);
						if (b != null) {
							decomp.remove(a);
							decomp.add(b);
						}

					}
				}
			}
		}
		if (decomp != null) { area = Spatial.Operators.union(scope, decomp); }

		double t1b = System.currentTimeMillis();
		DEBUG.OUT("|==> Remove obstacle from area : " + (t1b - t1a) / 1000);

		if (area == null)
			throw GamaRuntimeException.error("Get a null area when computing the background geometry", scope);
		area = keepMainGeom(area);

		double t2 = System.currentTimeMillis();
		DEBUG.OUT("|==> Keep main component : " + (t2 - t1b) / 1000);

		if (openArea) { area = managementOpenArea(scope, area, randomDist, valDistForOpenArea, valDensityOpenArea); }

		double t3 = System.currentTimeMillis();
		DEBUG.OUT("Processing open area: " + (t3 - t2));

		double valTolClip = toleranceClip;
		double valTolTri = toleranceTriang;

		IList<IShape> lines = Transformations.skeletonize(scope, area, valTolClip, valTolTri, false);

		double t4 = System.currentTimeMillis();
		DEBUG.OUT("Skeletonization : " + (t4 - t3));

		double valFiltering = minDistPath;
		if (valFiltering > 0.0) {
			final IShape areaTmp = area.getExteriorRing(scope);
			lines.removeIf(l -> areaTmp.euclidianDistanceTo(l) < minDistPath);
		}

		double t5 = System.currentTimeMillis();
		DEBUG.OUT("Clean skeletonized network : " + (t5 - t4));

		// If there is a walking area AND a regular network, combine pedestrian and regular network
		if (walking_area && regular_network != null && !regular_network.isEmpty(scope))
			return collapseNetwork(scope, walking_shape, obst, -1.0, lines,
					(IList<IShape>) regular_network.listValue(scope, Types.GEOMETRY, false));

		double t6 = System.currentTimeMillis();
		DEBUG.OUT("Processing bi network : " + (t6 - t5));

		IShape unionL = Operators.union(scope, lines);
		lines = Transformations.clean(scope, (IList<IShape>) unionL.getGeometries(), 0.0, true, cleanNetwork);

		double t7 = System.currentTimeMillis();
		DEBUG.OUT("Clean final network : " + (t7 - t6));

		IList<IShape> segments = GamaListFactory.create();

		for (IShape g : lines) {
			if (simplicationDistance > 0) { g = Transformations.simplification(scope, g, simplicationDistance); }
			if (g.getPoints().size() == 2) {
				segments.add(g);
			} else if (g.getPoints().size() > 2) {
				for (int i = 0; i < g.getPoints().size() - 1; i++) {
					IList<IShape> coords = GamaListFactory.create();
					coords.add(g.getPoints().get(i));
					coords.add(g.getPoints().get(i + 1));
					IShape line = Creation.line(scope, coords);
					segments.add(line);
				}
			}
		}
		double t8 = System.currentTimeMillis();

		DEBUG.OUT("To segments : " + (t8 - t7));
		return segments;
	}

	/**
	 * Keep main geom.
	 *
	 * @param inGeom the in geom
	 * @return the i shape
	 */
	public static IShape keepMainGeom(final IShape inGeom) {
		IShape result = inGeom;
		if (inGeom.getGeometries().size() > 1) {
			double maxArea = 0;
			IShape g = null;
			for (IShape s : inGeom.getGeometries()) {
				if (s.getArea() > maxArea) {
					maxArea = s.getArea();
					g = s;
				}
			}
			result = g;
		}
		return result;
	}

	/**
	 * Management open area.
	 *
	 * @param scope the scope
	 * @param area the area
	 * @param randomDist the random dist
	 * @param valDistForOpenArea the val dist for open area
	 * @param valDensityOpenArea the val density open area
	 * @return the i shape
	 */
	/*
	 * Add small obstacle inside open area to increase the number of node for pedestrian movement
	 */
	public static IShape managementOpenArea(final IScope scope, IShape area, final boolean randomDist,
			final double valDistForOpenArea, final double valDensityOpenArea) {

		IShape areaTmp = Spatial.Transformations.reduced_by(scope, area, valDistForOpenArea);
		if (areaTmp != null) {
			List<GamaPoint> pts = GamaListFactory.create(Types.GEOMETRY);
			for (IShape g : areaTmp.getGeometries()) {
				if (g == null || g.getArea() == 0) { continue; }
				long nbPoints = Math.round(g.getArea() * valDensityOpenArea);
				if (nbPoints == 0) { continue; }
				if (randomDist) {
					for (int i = 0; i < nbPoints; i++) { pts.add(Spatial.Punctal.any_location_in(scope, g)); }
				} else {
					double dimension = Math.sqrt(g.getArea() / nbPoints);
					List<IShape> squares = Spatial.Transformations.toSquares(scope, g, dimension);
					for (IShape sq : squares) { pts.add(sq.getCentroid()); }
				}
			}
			for (GamaPoint pt : pts) {
				area = Operators.minus(scope, area, Spatial.Transformations.enlarged_by(scope, pt, 0.01, 5));
			}
		}
		return area;
	}

	// ------------------------------------------ //
	// COMBINE REGULAR CORRIDOR WITH 2D CORRIDORS //
	// ------------------------------------------ //

	/**
	 * Collapse network.
	 *
	 * @param scope the scope
	 * @param pedestrianArea the pedestrian area
	 * @param obst the obst
	 * @param buffer the buffer
	 * @param pedestrianNetwork the pedestrian network
	 * @param regularNetwork the regular network
	 * @return the i list
	 */
	/*
	 * Method that collapse a regular network (usually road network) where pedestrian will move using moving skill on
	 * network (see PEDESTRAIN_ROAD_STATUS) and pedestrian virtual network (computed using Delaunay's triangulation and
	 * skeletonization) where agent will move using SFM or other continuous movement model
	 */
	@SuppressWarnings ("unchecked")
	public static IList<IShape> collapseNetwork(final IScope scope, final IShape pedestrianArea,
			final IList<IContainer<?, ? extends IShape>> obst, final double buffer,
			final IList<IShape> pedestrianNetwork, final IList<IShape> regularNetwork) {

		// Output
		IList<IShape> currentLines = GamaListFactory.create();

		DEBUG.OUT("Clean regular and pedestrian network");

		IList<IShape> rNetwork = Transformations.clean(scope,
				(IList<IShape>) Operators.union(scope, regularNetwork).getGeometries(), 0.0, true, false);
		IList<IShape> pNetwork = Transformations.clean(scope,
				(IList<IShape>) Operators.union(scope, pedestrianNetwork).getGeometries(), 0.0, true, true);

		DEBUG.OUT("Remove edges within pedestrian area from regular network");

		IList<IShape> toConnect = GamaListFactory.create();
		for (IShape e : rNetwork) {
			// Road sections not within pedestrian area
			if (!pedestrianArea.covers(e)) {
				// Road sections that do not cross pedestrian area
				if (!pedestrianArea.intersects(e)) {
					e.setAttribute(PedestrianRoadSkill.PEDESTRIAN_ROAD_STATUS, PedestrianRoadSkill.SIMPLE_STATUS);
					currentLines.add(e);
					// For road sections that cross walking area, cut it with open area and find connecting point
				} else {
					IShape s = Operators.minus(scope, e, pedestrianArea);
					s.setAttribute(PedestrianRoadSkill.PEDESTRIAN_ROAD_STATUS, PedestrianRoadSkill.SIMPLE_STATUS);
					currentLines.add(s);

					IShape fs = s.getPoints().firstValue(scope);
					IShape ls = s.getPoints().lastValue(scope);
					toConnect.add(
							fs.euclidianDistanceTo(pedestrianArea) < ls.euclidianDistanceTo(pedestrianArea) ? fs : ls);
				}
			}
		}

		DEBUG.OUT("Add all pedestrian corridor from pedestrian network");

		for (IShape corridors : pNetwork) {
			corridors.setAttribute(PedestrianRoadSkill.PEDESTRIAN_ROAD_STATUS, PedestrianRoadSkill.COMPLEX_STATUS);
			currentLines.add(corridors);
		}

		IList<IShape> obstacles = GamaListFactory.create();
		for (IContainer<?, ? extends IShape> shp : obst) {
			for (IShape obs : shp.iterable(scope)) { obstacles.add(obs); }
		}

		IShape o = Operators.union(scope, obstacles);

		DEBUG.OUT("Iterate over potential connecting points (corridor that cross the pedestrian area boundaries)");

		IList<IShape> pNodes = pNetwork.stream(scope).flatMap(corridor -> corridor.getPoints().stream())
				.collect(Collectors.toSet()).stream().collect(GamaListFactory.toGamaList());
		for (IShape pt : toConnect) {

			Map<IShape, Double> candidateNodes =
					pNodes.stream(scope).collect(Collectors.toMap(node -> node, node -> pt.euclidianDistanceTo(node)));

			IList<IShape> newLinks = GamaListFactory.create();

			if (newLinks.isEmpty()) {
				IShape cn = Collections.min(candidateNodes.entrySet(), Comparator.comparing(Entry::getValue)).getKey();
				newLinks.add(Creation.link(scope, cn, pt));
			}

			for (IShape connection : newLinks.stream().filter(link -> !link.crosses(o)).toList()) {
				connection.setAttribute(PedestrianRoadSkill.PEDESTRIAN_ROAD_STATUS, PedestrianRoadSkill.COMPLEX_STATUS);
				currentLines.add(connection);
			}

		}

		return currentLines;

	}

}
