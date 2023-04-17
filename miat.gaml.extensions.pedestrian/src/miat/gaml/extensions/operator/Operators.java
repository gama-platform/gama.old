/*******************************************************************************************************
 *
 * Operators.java, in miat.gaml.extensions.pedestrian, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package miat.gaml.extensions.operator;

import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;
import msi.gama.util.IList;

/**
 * The Class Operators.
 */
public class Operators {

	/**
	 * Generate network.
	 *
	 * @param scope the scope
	 * @param obst the obst
	 * @param bounds the bounds
	 * @param openArea the open area
	 * @param randomDist the random dist
	 * @param valDistForOpenArea the val dist for open area
	 * @param valDensityOpenArea the val density open area
	 * @param cleanNetwork the clean network
	 * @param toleranceClip the tolerance clip
	 * @param toleranceTriang the tolerance triang
	 * @param minDistPath the min dist path
	 * @param simplificationDist the simplification dist
	 * @param SizeSquares the size squares
	 * @return the i list
	 */
	@operator(value = "generate_pedestrian_network", category = { "Pedestrian" } , concept = { IConcept.NETWORK })
	@doc (
			value = "The method allows to build a network of corridors to be used by pedestrian"
					+ " while traveling around a space made of obstacles and other users. It makes it possible"
					+ " to avoide collision with other agents (e.g. buildings) including other pedestrians and in"
					+ " the same time managing a path to a destination in a complex environment (e.g. a city). The method "
					+ " is highly customizable, with many parameters listed as below: <p>"
					+ " <ol> "
					+ "  <li> obstacles : a list containing the lists of geometries or agents that are obstacles for pedestrians (e.g. walls, cars). </li> "
					+ "  <li> bounds : a list of geometries that represent the spatial boundary of the network (i.e. the enclosing space of the network). </li> "
					+ "  <li> open : a boolean expression that will add nodes in the network within open areas. More precisely, new invisible "
							+ "points are added to improve triangulation in areas with very few obstacles.</li> "
					+ "  <li> randomDist : a boolean expression, related to the previous 'open' parameter, that allows to switch between a random (true)"
							+ " spatial distribution or a distribution (false) that build upon a equidistant repartition of points all around the area. </li>"
					+ "  <li> open area : a float in meters representing the minimum distance for an area to be considered as an open area"
							+ " (i.e. euclidian distance between centroid and farest obstacle) </li>"
					+ "  <li> density point : a float representing the density of points per meter within open areas.</li>"
					+ "  <li> clean network : a boolean expression that allows to enhance the network (true) or living as it is generated (false)."
							+ " Enhancement includes filling very small gaps between edges and nodes. </li>"
					+ "  <li> cliping : tolerance for the cliping in triangulation (float; distance) - see skeletonize operator </li>"
					+ "  <li> tolerance : tolerance for the triangulation (float) </li>"
					+ "  <li> min dist obstacle : minimal distance to obstacles to keep a path (float; if 0.0, no filtering) </li>"
					+ "  <li> simplification : simplification distance for the final geometries</li>"
					+ "  <li> square size : size of squares for decomposition (optimization) </li>"
					+ " </ol> ",
			returns = "A Graph, i.e. the generated pedestrian network",
			examples = { @example (
					value = "generate_pedestrian_network([wall], [world],true,false,3.0,0.1, true,0.1,0.0,0.0,0.0,50.0)",
					equals = "a list of polylines corresponding to the pedestrian paths",
					isExecutable = false) })
	@no_test
	public static IList<IShape> generateNetwork(IScope scope,  IList<IContainer<?, ? extends IShape>> obst, IContainer<?, ? extends IShape> bounds, Boolean openArea,
			boolean randomDist, double valDistForOpenArea, double valDensityOpenArea,
			Boolean cleanNetwork, double toleranceClip, double toleranceTriang, double minDistPath, double simplificationDist, double SizeSquares) {
		return PedestrianNetwork.generateNetwork(scope, obst, bounds, null, openArea, randomDist,valDistForOpenArea,
				valDensityOpenArea, cleanNetwork, toleranceClip, toleranceTriang, minDistPath, simplificationDist, SizeSquares);
	}

	/**
	 * Generate network.
	 *
	 * @param scope the scope
	 * @param obst the obst
	 * @param bounds the bounds
	 * @param openArea the open area
	 * @param randomDist the random dist
	 * @param valDistForOpenArea the val dist for open area
	 * @param valDensityOpenArea the val density open area
	 * @param cleanNetwork the clean network
	 * @param toleranceClip the tolerance clip
	 * @param toleranceTriang the tolerance triang
	 * @param minDistPath the min dist path
	 * @param simplificationDist the simplification dist
	 * @return the i list
	 */
	@operator(value = "generate_pedestrian_network", category = { "Pedestrian" } , concept = { IConcept.NETWORK })
	@doc (
			usages = { @usage (
					value = "The method allows to build a network of corridors to be used by pedestrian"
							+ " while traveling around a space made of obstacles and other users. It makes it possible"
							+ " to avoide collision with other agents (e.g. buildings) including other pedestrians and in"
							+ " the same time managing a path to a destination in a complex environment (e.g. a city). The method "
							+ " is highly customizable, with many parameters listed as below: <p>"
							+ " <ol> "
							+ "  <li> obstacles : a list containing the lists of geometries or agents that are obstacles for pedestrians (e.g. walls, cars). </li> "
							+ "  <li> bounds : a list of geometries that represent the spatial boundary of the network (i.e. the enclosing space of the network). </li> "
							+ "  <li> open : a boolean expression that will add nodes in the network within open areas. More precisely, new invisible "
									+ "points are added to improve triangulation in areas with very few obstacles.</li> "
							+ "  <li> randomDist : a boolean expression, related to the previous 'open' parameter, that allows to switch between a random (true)"
									+ " spatial distribution or a distribution (false) that build upon a equidistant repartition of points all around the area. </li>"
							+ "  <li> open area : a float in meters representing the minimum distance for an area to be considered as an open area"
									+ " (i.e. euclidian distance between centroid and farest obstacle) </li>"
							+ "  <li> density point : a float representing the density of points per meter within open areas.</li>"
							+ "  <li> clean network : a boolean expression that allows to enhance the network (true) or living as it is generated (false)."
									+ " Enhancement includes filling very small gaps between edges and nodes. </li>"
							+ "  <li> cliping : tolerance for the cliping in triangulation (float; distance) - see skeletonize operator </li>"
							+ "  <li> tolerance : tolerance for the triangulation (float) </li>"
							+ "  <li> min dist obstacle : minimal distance to obstacles to keep a path (float; if 0.0, no filtering) </li>"
							+ "  <li> simplification : simplification distance for the final geometries</li>"
							+ " </ol> " ) },
			examples = { @example (
					value = "generate_pedestrian_network([wall], [world],true,false,3.0,0.1, true,0.1,0.0,0.0,0.0,0.0)",
					equals = "a list of polylines corresponding to the pedestrian paths",
					isExecutable = false) })
	@no_test
	public static IList<IShape> generateNetwork(IScope scope,  IList<IContainer<?, ? extends IShape>> obst, IContainer<?, ? extends IShape> bounds, Boolean openArea,
			boolean randomDist, double valDistForOpenArea, double valDensityOpenArea,
			Boolean cleanNetwork, double toleranceClip, double toleranceTriang, double minDistPath, double simplificationDist) {
		return PedestrianNetwork.generateNetwork(scope, obst, bounds, null, openArea, randomDist,valDistForOpenArea,
				valDensityOpenArea, cleanNetwork, toleranceClip, toleranceTriang, minDistPath, simplificationDist, 0);
	}

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
	 * @param simplificationDist the simplification dist
	 * @param sizeSquareOpti the size square opti
	 * @return the i list
	 */
	@operator(value = "generate_pedestrian_network", category = { "Pedestrian" } , concept = { IConcept.NETWORK })
	@doc (
			usages = { @usage (
					value = "The method allows to build a network of corridors to be used by pedestrian"
							+ " while traveling around a space made of obstacles and other users. It makes it possible"
							+ " to avoide collision with other agents (e.g. buildings) including other pedestrians and in"
							+ " the same time managing a path to a destination in a complex environment (e.g. a city). The method "
							+ " is highly customizable, with many parameters listed as below: <p>"
							+ " <ol> "
							+ "  <li> obstacles : a list containing the lists of geometries or agents that are obstacles for pedestrians (e.g. walls, cars). </li> "
							+ "  <li> bounds : a list of geometries that represent the spatial boundary of the network (i.e. the enclosing space of the network). </li> "
							+ "  <li> regular network : allows to combine the generated network with a simplified car user oriented network. More specifically, the "
									+ "network generated will combine enhance pedestrian oriented generated network with the given network: The property of the latter"
									+ " does not allows pedestrian to avoid collision (1D) when using its edges (while moving in 2D space and avoiding collision in the former).</li>"
							+ "  <li> open : a boolean expression that will add nodes in the network within open areas. More precisely, new invisible "
									+ "points are added to improve triangulation in areas with very few obstacles.</li> "
							+ "  <li> randomDist : a boolean expression, related to the previous 'open' parameter, that allows to switch between a random (true)"
									+ " spatial distribution or a distribution (false) that build upon a equidistant repartition of points all around the area. </li>"
							+ "  <li> open area : a float in meters representing the minimum distance for an area to be considered as an open area"
									+ " (i.e. euclidian distance between centroid and farest obstacle) </li>"
							+ "  <li> density point : a float representing the density of points per meter within open areas.</li>"
							+ "  <li> clean network : a boolean expression that allows to enhance the network (true) or living as it is generated (false)."
									+ " Enhancement includes filling very small gaps between edges and nodes. </li>"
							+ "  <li> cliping : tolerance for the cliping in triangulation (float; distance) - see skeletonize operator </li>"
							+ "  <li> tolerance : tolerance for the triangulation (float) </li>"
							+ "  <li> min dist obstacle : minimal distance to obstacles to keep a path (float; if 0.0, no filtering) </li>"
							+ "  <li> simplification : simplification distance for the final geometries</li>"
							+ " </ol> " ) },
			examples = { @example (
					value = "generate_pedestrian_network([wall], [world], [road], true,false,3.0,0.1, true,0.1,0.0,0.0,0.0,50.0)",
					equals = "a list of polylines corresponding to the pedestrian paths",
					isExecutable = false) })
	@no_test
	public static IList<IShape> generateNetwork(IScope scope,  IList<IContainer<?, ? extends IShape>> obst, IContainer<?, ? extends IShape> bounds, 
			IContainer<?, ? extends IShape> regular_network, Boolean openArea,
			boolean randomDist, double valDistForOpenArea, double valDensityOpenArea,
			Boolean cleanNetwork, double toleranceClip, double toleranceTriang, double minDistPath, double simplificationDist,double sizeSquareOpti) {
		return PedestrianNetwork.generateNetwork(scope, obst, bounds, regular_network, openArea, randomDist,valDistForOpenArea,
				valDensityOpenArea, cleanNetwork, toleranceClip, toleranceTriang, minDistPath, simplificationDist, sizeSquareOpti);
	}
	
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
	 * @param simplificationDist the simplification dist
	 * @param minDistPath the min dist path
	 * @return the i list
	 */
	@operator(value = "generate_pedestrian_network", category = { "Pedestrian" } , concept = { IConcept.NETWORK })
	@doc (
			usages = { @usage (
					value = "The method allows to build a network of corridors to be used by pedestrian"
							+ " while traveling around a space made of obstacles and other users. It makes it possible"
							+ " to avoide collision with other agents (e.g. buildings) including other pedestrians and in"
							+ " the same time managing a path to a destination in a complex environment (e.g. a city). The method "
							+ " is highly customizable, with many parameters listed as below: <p>"
							+ " <ol> "
							+ "  <li> obstacles : a list containing the lists of geometries or agents that are obstacles for pedestrians (e.g. walls, cars). </li> "
							+ "  <li> bounds : a list of geometries that represent the spatial boundary of the network (i.e. the enclosing space of the network). </li> "
							+ "  <li> regular network : allows to combine the generated network with a simplified car user oriented network. More specifically, the "
									+ "network generated will combine enhance pedestrian oriented generated network with the given network: The property of the latter"
									+ " does not allows pedestrian to avoid collision (1D) when using its edges (while moving in 2D space and avoiding collision in the former).</li>"
							+ "  <li> open : a boolean expression that will add nodes in the network within open areas. More precisely, new invisible "
									+ "points are added to improve triangulation in areas with very few obstacles.</li> "
							+ "  <li> randomDist : a boolean expression, related to the previous 'open' parameter, that allows to switch between a random (true)"
									+ " spatial distribution or a distribution (false) that build upon a equidistant repartition of points all around the area. </li>"
							+ "  <li> open area : a float in meters representing the minimum distance for an area to be considered as an open area"
									+ " (i.e. euclidian distance between centroid and farest obstacle) </li>"
							+ "  <li> density point : a float representing the density of points per meter within open areas.</li>"
							+ "  <li> clean network : a boolean expression that allows to enhance the network (true) or living as it is generated (false)."
									+ " Enhancement includes filling very small gaps between edges and nodes. </li>"
							+ "  <li> cliping : tolerance for the cliping in triangulation (float; distance) - see skeletonize operator </li>"
							+ "  <li> tolerance : tolerance for the triangulation (float) </li>"
							+ "  <li> min dist obstacle : minimal distance to obstacles to keep a path (float; if 0.0, no filtering) </li>"
							+ " </ol> " ) },
			examples = { @example (
					value = "generate_pedestrian_network([wall], [world], [road], true,false,3.0,0.1, true,0.1,0.0,0.0,0.0)",
					equals = "a list of polylines corresponding to the pedestrian paths",
					isExecutable = false) })
	@no_test
	public static IList<IShape> generateNetwork(IScope scope,  IList<IContainer<?, ? extends IShape>> obst, IContainer<?, ? extends IShape> bounds, 
			IContainer<?, ? extends IShape> regular_network, Boolean openArea,
			boolean randomDist, double valDistForOpenArea, double valDensityOpenArea,
			Boolean cleanNetwork, double toleranceClip, double toleranceTriang, double simplificationDist,double minDistPath) {
		return PedestrianNetwork.generateNetwork(scope, obst, bounds, regular_network, openArea, randomDist,valDistForOpenArea,
				valDensityOpenArea, cleanNetwork, toleranceClip, toleranceTriang, minDistPath, simplificationDist,0);
	}

	
	
}
