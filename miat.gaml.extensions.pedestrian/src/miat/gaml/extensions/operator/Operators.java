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

public class Operators {

	@operator(value = "generate_pedestrian_network", category = { "Pedestrian" } , concept = { IConcept.NETWORK })
	@doc (
			value = "generateNetwork(obstacles (list of lists of geometries/agents), bounds (list of geometries/agents), add point to open areas (boolean),\n" + 
					" random densification (boolean; if true, use random points to fill open areas; if false, use uniform points), min distance to considered an area as open area (float), density of points in the open areas (float),\n" + 
			" clean network (boolean), tolerance for the cliping in triangulation (float; distance), tolerance for the triangulation (float), minimal distance to obstacles to keep a path (float; if 0.0, no filtering), "
			+ "simplification distance for the final geometries, size of squares for decomposition (optimization)",
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

	@operator(value = "generate_pedestrian_network", category = { "Pedestrian" } , concept = { IConcept.NETWORK })
	@doc (
			usages = { @usage (
					value = "generateNetwork("
							+ "obstacles (list of lists of geometries/agents), "
							+ "bounds (list of geometries/agents), "
							+ "add point to open areas (boolean), \n" 
							+ "random densification (boolean; if true, use random points to fill open areas; if false, use uniform points), "
							+ "min distance to considered an area as open area (float), "
							+ "density of points in the open areas (float), \n" 
							+ "clean network (boolean), "
							+ "tolerance for the cliping in triangulation (float; distance), "
							+ "tolerance for the triangulation (float), "
							+ "minimal distance to obstacles to keep a path (float; if 0.0, no filtering),"
							+ "simplification distance for the final geometries") },
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

	@operator(value = "generate_pedestrian_network", category = { "Pedestrian" } , concept = { IConcept.NETWORK })
	@doc (
			usages = { @usage (
					value = "generateNetwork("
							+ "obstacles (list of lists of geometries/agents), \n"
							+ "bounds (list of geometries/agents), \n"
							+ "the road network (list of line) to have simple pedestrian behavior (1D movement) outside of the bounds \n"
							+ "add point to open areas (boolean), \n"
							+ "random densification (boolean; if true, use random points to fill open areas; if false, use uniform points), \n"
							+ "min distance to considered an area as open area (float), \n"
							+ "density of points in the open areas (float), \n" 
							+ "clean network (boolean), tolerance for the cliping in triangulation (float; distance), tolerance for the triangulation (float), "
							+ "minimal distance to obstacles to keep a path (float; if 0.0, no filtering), "
							+ "simplification distance for the final geometries,"
							+ " size of squares for decomposition (optimization)") },
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
	
	@operator(value = "generate_pedestrian_network", category = { "Pedestrian" } , concept = { IConcept.NETWORK })
	@doc (
			usages = { @usage (
					value = "generateNetwork(obstacles (list of lists of geometries/agents), bounds (list of geometries/agents), \n"
							+ "the road network (list of line) to have simple pedestrian behavior (1D movement) outside of the bounds \n"
							+ "add point to open areas (boolean), random densification (boolean; if true, use random points to fill open areas; if false, use uniform points), "
							+ "min distance to considered an area as open area (float), density of points in the open areas (float),\n" + 
							" clean network (boolean), tolerance for the cliping in triangulation (float; distance), tolerance for the triangulation (float), "
							+ "minimal distance to obstacles to keep a path (float; if 0.0, no filtering), "
							+ "simplification distance for the final geometries") },
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
