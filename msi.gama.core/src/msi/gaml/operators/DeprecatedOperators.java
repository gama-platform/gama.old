/*******************************************************************************************************
 *
 * msi.gaml.operators.DeprecatedOperators.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.operators;

import java.util.Arrays;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.precompiler.Reason;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaGridFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.draw.MeshDrawingAttributes;

/**
 * Class Deprecated.
 *
 * @author drogoul
 * @since 16 janv. 2014
 *
 */
@SuppressWarnings ({ "rawtypes" })
public class DeprecatedOperators {
	//
	// @operator (
	// value = { "link" },
	// category = { IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE },
	// concept = { IConcept.SHAPE, IConcept.SPATIAL_COMPUTATION, IConcept.GEOMETRY })
	// @doc (
	// value = "A link between the 2 elements of the pair.",
	// deprecated = "Use link(g1, g2) instead",
	// usages = { @usage ("if the operand is nil, link returns a point {0,0}"),
	// @usage ("if one of the elements of the pair is a list of geometries or a species, link will consider the union of
	// the geometries or of the geometry of each agent of the species") },
	// comment = "The geometry of the link is a line between the locations of the two elements of the pair, which is
	// built and maintained dynamically ",
	// examples = { @example (
	// value = "link (geom1::geom2)",
	// equals = "a link geometry between geom1 and geom2.",
	// isExecutable = false) },
	// see = { "around", "circle", "cone", "line", "norm", "point", "polygon", "polyline", "rectangle", "square",
	// "triangle" })
	// @no_test
	// public static IShape link(final IScope scope, final GamaPair points) throws GamaRuntimeException {
	// if (points == null
	// || points.first() == null && points.last() == null) { return new GamaShape(new GamaPoint(0, 0)); }
	// return GamaGeometryType.pairToGeometry(scope, points);
	// }

	// @Deprecated
	// @operator (
	// value = "neighbours_of",
	// content_type = IType.AGENT,
	// category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES })
	// @doc (
	// deprecated = "use neighbors_of instead",
	// value = "a list, containing all the agents of the same species than the argument (if it is an agent) located at a
	// distance inferior or equal to 1 to the right-hand operand agent considering the left-hand operand topology.",
	// masterDoc = true,
	// examples = { @example (
	// value = "topology(self) neighbours_of self",
	// equals = "returns all the agents located at a distance lower or equal to 1 to the agent applying the operator
	// considering its topology.",
	// test = false) },
	// see = { "neighbors_of" })
	// @no_test
	// public static IList neighbours_of_deprecated(final IScope scope, final ITopology t, final IAgent agent) {
	// return Spatial.Queries.neighbors_of(scope, t, agent);
	// }
	//
	// @Deprecated
	// @operator (
	// value = "neighbours_of",
	// content_type = IType.AGENT,
	// category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES })
	// /* TODO, expected_content_type = { IType.FLOAT, IType.INT } */
	// @doc (
	// deprecated = "Use 'neighbors_of(topology, agent, distance)' instead",
	// usages = @usage (
	// value = "a list, containing all the agents of the same species than the key of the pair argument (if it is an
	// agent) located at a distance inferior or equal to the right member (float) of the pair (right-hand operand) to
	// the left member (agent, geometry or point) considering the left-hand operand topology.",
	// examples = { @example (
	// value = "topology(self) neighbours_of self::10",
	// equals = "all the agents located at a distance lower or equal to 10 to the agent applying the operator
	// considering its topology.",
	// test = false) }))
	// @no_test
	// public static IList neighbours_of_deprecated(final IScope scope, final ITopology t, final GamaPair pair) {
	// return neighbours_of(scope, t, pair);
	// // TODO We could compute a filter based on the population if it is an
	// // agent
	// }

	// @Deprecated
	// @operator (
	// value = "neighbours_of",
	// content_type = IType.AGENT,
	// category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES })
	// /* TODO, expected_content_type = { IType.FLOAT, IType.INT } */
	// @doc (
	// deprecated = "use neighbors_of instead",
	// usages = @usage (
	// value = "a list, containing all the agents of the same species than the left argument (if it is an agent) located
	// at a distance inferior or equal to the third argument to the second argument (agent, geometry or point)
	// considering the first operand topology.",
	// examples = { @example (
	// value = "neighbours_of (topology(self), self,10)",
	// equals = "all the agents located at a distance lower or equal to 10 to the agent applying the operator
	// considering its topology.",
	// test = false) }))
	// @no_test
	// public static IList neighbours_of_deprecated(final IScope scope, final ITopology t, final IShape agent,
	// final Double distance) {
	// return Spatial.Queries.neighbors_of(scope, t, agent, distance);
	// }
	//
	// @Deprecated
	// @operator (
	// value = "neighbours_at",
	// content_type = ITypeProvider.TYPE_AT_INDEX + 1,
	// category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES })
	// @doc (
	// deprecated = "use neighbors_at instead",
	// value = "a list, containing all the agents of the same species than the left argument (if it is an agent) located
	// at a distance inferior or equal to the right-hand operand to the left-hand operand (geometry, agent, point).",
	// comment = "The topology used to compute the neighborhood is the one of the left-operand if this one is an agent;
	// otherwise the one of the agent applying the operator.",
	// examples = { @example (
	// value = "(self neighbours_at (10))",
	// equals = "all the agents located at a distance lower or equal to 10 to the agent applying the operator.",
	// test = false) },
	// see = { "neighbors_at" })
	// @no_test
	// public static IList neighbours_at_deprecated(final IScope scope, final IShape agent, final Double distance) {
	// return Spatial.Queries.neighbors_at(scope, agent, distance);
	// }

	// @Deprecated
	// @operator (
	// value = "toChar",
	// can_be_const = true,
	// category = { IOperatorCategory.STRING })
	// @doc (
	// deprecated = "Use 'char' instead",
	// special_cases = { "convert ACSII integer value to character" },
	// examples = { @example (
	// value = "toChar (34)",
	// equals = "'\"'") })
	// static public String toChar(final Integer s) {
	// return Strings.asChar(s);
	// }
	//
	// @operator (
	// value = "as_csv",
	// can_be_const = true,
	// index_type = IType.INT)
	// @doc (
	// deprecated = "use csv_file(path, separator) instead",
	// value = "allows to specify the character to use as a separator for a CSV format and returns the file. Yields an
	// error if the file is not a text file",
	// examples = @example ("let fileT type: file value: text(\"../includes/Stupid_Cell.csv\") as_csv ';';"))
	// @Deprecated
	// public static IGamaFile as_csv(final IScope scope, final IGamaFile file, final String s)
	// throws GamaRuntimeException {
	// return new GamaCSVFile(scope, file.getPath(scope), s);
	// }

	// @operator(value = "gamlfile", can_be_const = true, index_type =
	// IType.INT)
	// @doc(deprecated = "use gaml_file instead",
	// value = "opens a file that a is a kind of model file.",
	// comment = "The file should have a shapefile extension, cf. file type
	// definition for supported file extensions.",
	// special_cases = "If the specified string does not refer to an existing
	// shapefile file, an exception is risen.",
	// examples = { @example("let fileT type: file value:
	// shapefile(\"../includes/testProperties.shp\");"),
	// @example(" // fileT represents the shapefile file
	// \"../includes/testProperties.shp\"") },
	// see = { "file", "properties", "image", "text" })
	// @Deprecated
	// public static IGamaFile gamlFile(final IScope scope, final String s)
	// throws GamaRuntimeException {
	// return new GAMLFile(scope, s);
	// }

	// @operator(value = "gridfile", can_be_const = true, index_type =
	// IType.INT)
	// @doc(deprecated = "use grid_file instead",
	// value = "opens a file that a is a kind of shapefile.",
	// comment = "The file should have a gridfile extension, cf. file type
	// definition for supported file extensions.",
	// special_cases = "If the specified string does not refer to an existing
	// gridfile file, an exception is risen.",
	// examples = { @example("file fileT <-
	// gridfile(\"../includes/testProperties.asc\");"),
	// @example(" // fileT represents the gridfile file
	// \"../includes/testProperties.asc\"") },
	// see = { "file", "properties", "image", "text", "shapefile" })
	// @Deprecated
	// public static IGamaFile gridFile(final IScope scope, final String s)
	// throws GamaRuntimeException {
	// return new GamaGridFile(scope, s);
	// }

	// @operator(value = "gridfile", can_be_const = true, index_type =
	// IType.INT)
	// @doc(deprecated = "use grid_file instead",
	// value = "opens a file that a is a kind of gridfile. The integer parameter
	// allows to specify a coordinate reference system (CRS). If equal to zero,
	// it forces reading the data as alreay
	// projected",
	// comment = "The file should have a gridfile extension, cf. file type
	// definition for supported file extensions.",
	// special_cases = "If the specified string does not refer to an existing
	// gridfile file, an exception is risen.",
	// examples = { @example("file fileT <-
	// gridfile(\"../includes/testProperties.asc\");"),
	// @example(" // fileT represents the gridfile file
	// \"../includes/testProperties.asc\"") },
	// see = { "file", "properties", "image", "text", "shapefile" })
	// @Deprecated
	// public static IGamaFile gridFile(final IScope scope, final String s,
	// final Integer code)
	// throws GamaRuntimeException {
	// return new GamaGridFile(scope, s, code);
	// }

	// @operator(value = "osmfile", can_be_const = true, index_type = IType.INT)
	// @doc(deprecated = "use osm_file instead",
	// value = "opens a file that a is a kind of osmfile.",
	// comment = "The file should have a osmfile extension, cf. file type
	// definition for supported file extensions.",
	// special_cases = "If the specified string does not refer to an existing
	// osmfile file, an exception is risen.",
	// examples = { @example("file fileT <-
	// osmfile(\"../includes/testProperties.osm\");"),
	// @example(" // fileT represents the osm file
	// \"../includes/testProperties.osm\"") },
	// see = { "file", "properties", "image", "text", "shapefile" })
	// @Deprecated
	// public static IGamaFile osmFile(final IScope scope, final String s)
	// throws GamaRuntimeException {
	// return new GamaOsmFile(scope, s);
	// }

	// @operator(value = "osmfile", can_be_const = true, index_type = IType.INT)
	// @doc(deprecated = "use osm_file instead",
	// value = "opens a file that a is a kind of osmfile, specifying an optional
	// CRS EPSG code",
	// comment = "The file should have an osmfile extension, cf. file type
	// definition for supported file extensions.",
	// special_cases = "If the specified string does not refer to an existing
	// osmfile file, an exception is risen.",
	// examples = { @example("file fileT <-
	// osmfile(\"../includes/testProperties.osm\", 4326);"),
	// @example(" // fileT represents the osm file
	// \"../includes/testProperties.osm\"") },
	// see = { "file", "properties", "image", "text", "shapefile" })
	// @Deprecated
	// public static IGamaFile osmFile(final IScope scope, final String s, final
	// Integer i) throws GamaRuntimeException {
	// return new GamaOsmFile(scope, s, i);
	// }
	//
	// @operator (
	// value = "image",
	// can_be_const = true,
	// index_type = IType.POINT)
	// @doc (
	// deprecated = "use image_file instead",
	// value = "opens a file that is a kind of image.",
	// comment = "The file should have an image extension, cf. file type definition for supported file extensions.",
	// special_cases = "If the specified string does not refer to an existing image file, an exception is risen.",
	// examples = {
	// @example ("let fileT type: file value: image(\"../includes/testImage.png\"); // fileT represents the file
	// \"../includes/testShape.png\"") },
	// see = { "file", "shapefile", "properties", "text" })
	// @Deprecated
	// public static IGamaFile imageFile(final IScope scope, final String s) throws GamaRuntimeException {
	// return new GamaImageFile(scope, s);
	// }
	//
	// @operator (
	// value = "read",
	// type = ITypeProvider.TYPE_AT_INDEX + 1,
	// content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
	// index_type = ITypeProvider.KEY_TYPE_AT_INDEX + 1)
	// @doc (
	// deprecated = "use the operator \"writable\" instead",
	// value = "marks the file so that only read operations are allowed.",
	// comment = "A file is created by default in read-only mode. The operator write can change the mode.",
	// examples = {
	// @example ("read(shapefile(\"../images/point_eau.shp\")) --: returns a file in read-only mode representing
	// \"../images/point_eau.shp\"") },
	// see = { "file", "writable" })
	// @Deprecated
	// public static IGamaFile opRead(final IScope scope, final IGamaFile s) {
	// s.setWritable(scope, false);
	// return s;
	// }
	//
	// @operator (
	// value = IKeyword.WRITE,
	// type = ITypeProvider.TYPE_AT_INDEX + 1,
	// content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
	// index_type = ITypeProvider.KEY_TYPE_AT_INDEX + 1)
	// @doc (
	// deprecated = "use the operator \"writable\" instead",
	// value = "marks the file so that read and write operations are allowed.",
	// comment = "A file is created by default in read-only mode.",
	// examples = {
	// @example ("write(shapefile(\"../images/point_eau.shp\")) --: returns a file in read-write mode representing
	// \"../images/point_eau.shp\"") },
	// see = { "file", "writable" })
	// @Deprecated
	// public static IGamaFile opWrite(final IScope scope, final IGamaFile s) {
	// s.setWritable(scope, true);
	// return s;
	// }
	//
	// @operator (
	// value = "text",
	// can_be_const = true,
	// index_type = IType.INT)
	// @doc (
	// deprecated = "use text_file instead",
	// value = "opens a file that a is a kind of text.",
	// comment = "The file should have a text extension, cf. file type definition for supported file extensions.",
	// special_cases = "If the specified string does not refer to an existing text file, an exception is risen.",
	// examples = { @example ("let fileT type: file value: text(\"../includes/Stupid_Cell.Data\");"),
	// @example (" // fileT represents the text file \"../includes/Stupid_Cell.Data\"") },
	// see = { "file", "properties", "image", "shapefile" })
	// @Deprecated
	// public static IGamaFile textFile(final IScope scope, final String s) throws GamaRuntimeException {
	// return new GamaTextFile(scope, s);
	// }

	// @operator (
	// value = "properties",
	// can_be_const = true,
	// index_type = IType.STRING)
	// @doc (
	// deprecated = "use property_file instead",
	// value = "opens a file that is a kind of properties.",
	// comment = "The file should have a properties extension, cf. type file definition for supported file extensions.",
	// special_cases = "If the specified string does not refer to an existing propserites file, an exception is risen.",
	// examples = {
	// @example ("let fileT type: file value: properties(\"../includes/testProperties.properties\"); // fileT represents
	// the properties file \"../includes/testProperties.properties\"") },
	// see = { "file", "shapefile", "image", "text" })
	// @Deprecated
	// public static IGamaFile propertyFile(final IScope scope, final String s) throws GamaRuntimeException {
	// return new GamaPropertyFile(scope, s);
	// }
	//
	// @operator (
	// value = "shapefile",
	// can_be_const = true,
	// index_type = IType.INT)
	// @doc (
	// deprecated = "use shape_file instead",
	// value = "opens a file that a is a kind of shapefile.",
	// comment = "The file should have a shapefile extension, cf. file type definition for supported file extensions.",
	// special_cases = "If the specified string does not refer to an existing shapefile file, an exception is risen.",
	// examples = { @example ("let fileT type: file value: shapefile(\"../includes/testProperties.shp\");"),
	// @example (" // fileT represents the shapefile file \"../includes/testProperties.shp\"") },
	// see = { "file", "properties", "image", "text" })
	// @Deprecated
	// public static IGamaFile shapeFile(final IScope scope, final String s) throws GamaRuntimeException {
	// return new GamaShapeFile(scope, s);
	// }
	//
	// @operator (
	// value = "shapefile",
	// can_be_const = true,
	// index_type = IType.INT)
	// @doc (
	// deprecated = "use shape_file instead",
	// value = "opens a file that a is a kind of shapefile, forcing the initial CRS to be the one indicated by the
	// second int parameter (see http://spatialreference.org/ref/epsg/). If this int parameter is equal to 0, the data
	// is considered as already projected",
	// comment = "The file should have a shapefile extension, cf. file type definition for supported file extensions.",
	// special_cases = "If the specified string does not refer to an existing shapefile file, an exception is risen.",
	// examples = { @example ("let fileT type: file value: shapefile(\"../includes/testProperties.shp\");"),
	// @example (" // fileT represents the shapefile file \"../includes/testProperties.shp\"") },
	// see = { "file", "properties", "image", "text" })
	// @Deprecated
	// public static IGamaFile shapeFile(final IScope scope, final String s, final Integer code)
	// throws GamaRuntimeException {
	// return new GamaShapeFile(scope, s, code);
	// }

	// @operator (
	// value = { "add_z" })
	// @doc (
	// deprecated = "use set location instead",
	// value = "add_z",
	// comment = "Return a geometry with a z value" + "The add_z operator set the z value of the whole shape."
	// + "For each point of the cell the same z value is set.",
	// examples = { @example ("set shape <- shape add_z rnd(100);") },
	// see = { "add_z_pt" })
	// @Deprecated
	// public static IShape add_z(final IShape g, final Double z) {
	// final GamaPoint p = new GamaPoint(g.getLocation().getX(), g.getLocation().getY(), z);
	// g.setLocation(p);
	// return g;
	// }

	// @operator(value = "to_java")
	// @doc(value = "represents the java way to write an expression in java,
	// depending on its type",
	// deprecated = "NOT YET IMPLEMENTED",
	// see = { "to_gaml" })
	// @Deprecated
	// public static String toJava(final Object val) throws GamaRuntimeException
	// {
	// throw GamaRuntimeException.error("to_java is not yet implemented");
	// }

	//
	// @operator(value = IKeyword.UNKNOWN, can_be_const = true)
	// @doc(deprecated = "generated automatically now", value = "returns the
	// operand itself")
	// @Deprecated
	// public static Object asObject(final Object obj) {
	// return obj;
	// }
	//
	// @operator (
	// value = { "collate" },
	// content_type = ITypeProvider.FIRST_ELEMENT_CONTENT_TYPE)
	// @doc (
	// deprecated = "Please use 'interleave' instead.",
	// value = "a new list containing the interleaved elements of the containers contained in the operand",
	// comment = "the operand should be a list of lists of elements. The result is a list of elements. ",
	// examples = { @example ("interleave([1,2,4,3,5,7,6,8]) --: [1,2,3,4,5,7,6,8]"),
	// @example ("interleave([['e11','e12','e13'],['e21','e22','e23'],['e31','e32','e33']]) --:
	// [e11,e21,e31,e12,e22,e32,e13,e23,e33]") })
	// @Deprecated
	// public static IList collate(final IScope scope, final IContainer cc) {
	// return Containers.interleave(scope, cc);
	// // final Iterator it = new
	// // Guava.InterleavingIterator(toArray(nullCheck(scope,
	// // cc).iterable(scope), Object.class));
	// // return GamaListFactory.create(Iterators.toArray(it, Object.class),
	// // Types.NO_TYPE);
	// }

	@operator (
			value = { IKeyword.AT, "@" },
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1)
	@doc (
			deprecated = "The use of at on a species is deprecated, please use it one a population instead (list(species_name) instead of species_name)")
	@no_test
	public static IAgent at(final IScope scope, final ISpecies s, final GamaPoint val) throws GamaRuntimeException {
		return scope.getAgent().getPopulationFor(s).getAgent(scope, val);
	}
	//
	// @operator (
	// value = "is_properties")
	// @doc (
	// deprecated = "use 'is_property' instead")
	// @Deprecated
	// @no_test
	// public static Boolean isProperties(final String f) {
	// return GamaFileType.verifyExtension("property", f);
	// }
	//
	// @operator (
	// value = "is_GAML")
	// @doc (
	// deprecated = "use 'is_gaml' instead")
	// @Deprecated
	// @no_test
	// public static Boolean isGAML(final String f) {
	// return GamaFileType.verifyExtension("gaml", f);
	// }
	//
	// @Deprecated
	// @operator (
	// value = "neighbors_of",
	// content_type = IType.AGENT,
	// category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_QUERIES })
	// /* TODO, expected_content_type = { IType.FLOAT, IType.INT } */
	// @doc (
	// deprecated = "Use 'neighbors_of(topology, agent, distance)' instead",
	// usages = @usage (
	// value = "a list, containing all the agents of the same species than the key of the pair argument (if it is an
	// agent) located at a distance inferior or equal to the right member (float) of the pair (right-hand operand) to
	// the left member (agent, geometry or point) considering the left-hand operand topology.",
	// examples = { @example (
	// value = "topology(self) neighbors_of self::10",
	// equals = "all the agents located at a distance lower or equal to 10 to the agent applying the operator
	// considering its topology.",
	// test = false) }))
	// @no_test
	// public static IList neighbours_of(final IScope scope, final ITopology t, final GamaPair pair) {
	// if (pair == null) { return GamaListFactory.create(); }
	// final Object agent = pair.key;
	// return Spatial.Queries._neighbors(scope,
	// agent instanceof IAgent ? In.list(scope, ((IAgent) agent).getPopulation()) : Different.with(), agent,
	// pair.value, t);
	// // TODO We could compute a filter based on the population if it is an
	// // agent
	// }
	//
	// @Deprecated
	// @operator (
	// value = "covered_by",
	// category = { IOperatorCategory.SPATIAL, IOperatorCategory.SP_PROPERTIES })
	// @doc (
	// deprecated = "Use 'g2 covers g1' instead of 'g1 covered_by g2'",
	// value = "A boolean, equal to true if the left-geometry (or agent/point) is covered by the right-geometry (or
	// agent/point).",
	// usages = { @usage ("if one of the operand is null, returns false.") },
	// examples = { @example (
	// value = "square(5) covered_by square(2)",
	// equals = "false") },
	// see = { "disjoint_from", "crosses", "overlaps", "partially_overlaps", "touches" })
	// public static Boolean covered_by(final IShape g1, final IShape g2) {
	// return Spatial.Properties.covers(g2, g1);
	// }

	// @Deprecated
	// @operator (
	// value = { "copy_between" /* , "copy" */ },
	// can_be_const = true,
	// content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
	// category = { IOperatorCategory.LIST })
	// @doc (
	// deprecated = "Deprecated. Use copy_between(list, int, int) instead")
	// @no_test
	// public static IList copy_between(final IScope scope, final IList l1, final GamaPoint p) {
	// return Containers.Range.copy_between(scope, l1, (int) notNull(scope, p).x, (int) p.y);
	// }
	//
	// @Deprecated
	// @operator (
	// value = "add_z",
	// can_be_const = true,
	// category = IOperatorCategory.POINT)
	// @doc (
	// deprecated = "Use the standard construction {x,y,z} instead. ")
	// @no_test
	// public static ILocation add_z(final GamaPoint p, final Double z) {
	// return new GamaPoint(p.x, p.y, z);
	// }

	// @Deprecated
	// @operator (
	// value = "add_z",
	// can_be_const = true,
	// category = IOperatorCategory.POINT)
	// @doc (
	// deprecated = " Use the standard construction {x,y,z} instead. ")
	// @no_test
	// public static ILocation add_z(final GamaPoint p, final Integer z) {
	// return new GamaPoint(p.x, p.y, z);
	// }
	//
	// @Deprecated
	// @operator (
	// value = "binomial",
	// category = { IOperatorCategory.RANDOM })
	// @doc (
	// deprecated = " Use binomial(int, float) instead",
	// value = "A value from a random variable following a binomial distribution. The operand {n,p} represents the
	// number of experiments n and the success probability p.",
	// comment = "The binomial distribution is the discrete probability distribution of the number of successes in a
	// sequence of n independent yes/no experiments, each of which yields success with probability p, cf. Binomial
	// distribution on Wikipedia.",
	// examples = { @example (
	// value = "binomial({15,0.6})",
	// equals = "a random positive integer",
	// test = false) },
	// see = { "poisson", "gauss" })
	// @no_test
	// public static Integer opBinomial(final IScope scope, final GamaPoint point) {
	// final Integer n = (int) point.x;
	// final Double p = point.y;
	// return Random.opBinomial(scope, n, p);
	// }

	// @Deprecated
	// @operator (
	// value = "rnd_float")
	// @doc (
	// deprecated = "Use rnd instead with a float argument",
	// examples = { @example (
	// value = "rnd_float(3)",
	// equals = "a random float between 0.0 and 3.0",
	// test = false) },
	// see = { "rnd" })
	// @no_test
	// public static Double opRndFloat(final IScope scope, final Double max) {
	// return Random.opRnd(scope, max);
	// }
	//
	// @Deprecated
	// @operator (
	// value = { "add_z_pt" },
	// category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED })
	// @doc (
	// deprecated = "Use 'set_z' instead",
	// value = "add_z_pt",
	// comment = "Return a geometry with a z value",
	// examples = { @example ("loop i from: 0 to: length(shape.points) - 1{" + "shape <- shape add_z_pt {i,valZ};"
	// + "}") },
	// see = { "add_z" })
	// public static IShape add_z_pt(final IShape geom, final GamaPoint data) {
	// geom.getInnerGeometry().getCoordinates()[(int) data.x].z = data.y;
	// return geom;
	// }
	//
	// @operator (
	// value = "R_compute_param",
	// can_be_const = false,
	// type = IType.MAP,
	// content_type = IType.LIST,
	// index_type = IType.STRING,
	// category = { IOperatorCategory.STATISTICAL })
	// @doc (
	// deprecated = "Use R_file instead",
	// value = "returns the value of the last left-hand operand of given R file (right-hand operand) in given vector
	// (left-hand operand), R file (first right-hand operand) reads the vector (second right-hand operand) as the
	// parameter vector",
	// examples = { @example (
	// value = "file f <- file('AddParam.r');",
	// isTestOnly = true,
	// isExecutable = false),
	// @example (
	// value = "save \"v1 <- vectorParam[1];\" to: f.path;",
	// isTestOnly = true,
	// isExecutable = false),
	// @example (
	// value = "save \"v2<-vectorParam[2];\" to: f.path;",
	// isTestOnly = true,
	// isExecutable = false),
	// @example (
	// value = "save \"v3<-vectorParam[3];\" to: f.path;",
	// isTestOnly = true,
	// isExecutable = false),
	// @example (
	// value = "save \"result<-v1+v2+v3;\" to: f.path;",
	// isTestOnly = true,
	// isExecutable = false),
	// @example (
	// value = "list<int> X <- [2, 3, 1];",
	// isExecutable = false),
	// @example (
	// value = "R_compute_param('AddParam.R', X)",
	// var = "result",
	// equals = "['result'::['6']]",
	// isExecutable = false),
	// @example (
	// value = "////// AddParam.R file:",
	// isExecutable = false),
	// @example (
	// value = "// v1 <- vectorParam[1];",
	// isExecutable = false),
	// @example (
	// value = "// v2<-vectorParam[2];",
	// isExecutable = false),
	// @example (
	// value = "// v3<-vectorParam[3];"),
	// @example (
	// value = "// result<-v1+v2+v3;",
	// isExecutable = false),
	// @example (
	// value = "////// Output:",
	// isExecutable = false),
	// @example (
	// value = "// 'result'::[6]",
	// isExecutable = false) })
	// @Deprecated
	// public static GamaMap operateRFileEvaluate(final IScope scope, final String RFile, final GamaMap param)
	// throws GamaRuntimeException, ParseException, ExecutionException {
	// final RFile obj = new RFile(scope, RFile, param);
	// return obj.getContents(scope);
	// }
	//
	// @Deprecated
	// @operator (
	// value = { "copy_between" /* , "copy" */ },
	// can_be_const = true,
	// category = { IOperatorCategory.STRING })
	// @doc (
	// deprecated = "Deprecated. Use copy_between(string, int, int) instead")
	// @no_test
	// public static String opCopy(final String target, final GamaPoint p) {
	// final int beginIndex = (int) p.x;
	// final int endIndex = (int) p.y;
	// return Strings.opCopy(target, beginIndex, endIndex);
	// }
	//
	// @operator (
	// value = { "neighbours_of" },
	// type = IType.LIST,
	// content_type = ITypeProvider.KEY_TYPE_AT_INDEX + 1,
	// category = { IOperatorCategory.GRAPH })
	// @doc (
	// deprecated = "use neighbors_of instead",
	// value = "returns the list of neighbours of the given vertex (right-hand operand) in the given graph (left-hand
	// operand)",
	// examples = { @example (
	// value = "graphEpidemio neighbours_of (node(3))",
	// equals = "[node0,node2]",
	// isExecutable = false),
	// @example (
	// value = "graphFromMap neighbours_of node({12,45})",
	// equals = "[{1.0,5.0},{34.0,56.0}]",
	// isExecutable = false) },
	// see = { "neighbors_of" })
	// @no_test
	// public static IList neighboursOf_deprecated(final IScope scope, final IGraph graph, final Object vertex) {
	// return Graphs.neighborsOf(scope, graph, vertex);
	// }
	//
	// @operator (
	// value = "R_compute",
	// can_be_const = false,
	// content_type = IType.LIST,
	// index_type = IType.STRING,
	// category = { IOperatorCategory.STATISTICAL })
	// @doc (
	// deprecated = "Use R_file instead",
	// value = "returns the value of the last left-hand operand of given R file (right-hand operand) in "
	// + "given vector (left-hand operand).",
	// examples = { @example (
	// value = "file f <- file('Correlation.r');",
	// isTestOnly = true,
	// isExecutable = false),
	// @example (
	// value = "save \"x <- c(1, 2, 3);\" to: f.path;",
	// isTestOnly = true,
	// isExecutable = false),
	// @example (
	// value = "save \"y <- c(1, 2, 4);\" to: f.path;",
	// isTestOnly = true,
	// isExecutable = false),
	// @example (
	// value = "save \"result<- cor(x, y);\" to: f.path;",
	// isTestOnly = true,
	// isExecutable = false),
	// @example (
	// value = "R_compute('Correlation.R')",
	// var = "result",
	// equals = "['result'::['0.981980506061966']]",
	// isExecutable = false),
	// @example (
	// value = "////// Correlation.R file:",
	// isExecutable = false),
	// @example (
	// value = "// x <- c(1, 2, 3);",
	// isExecutable = false),
	// @example ("// y <- c(1, 2, 4);"), @example (
	// value = "// result <- cor(x, y);",
	// isExecutable = false),
	// @example (
	// value = "// Output:",
	// isExecutable = false),
	// @example (
	// value = "// result::[0.981980506061966]",
	// isExecutable = false) })
	// @Deprecated
	// public static GamaMap opRFileEvaluate(final IScope scope, final String RFile)
	// throws GamaRuntimeException, ParseException, ExecutionException {
	// final RFile obj = new RFile(scope, RFile);
	// return obj.getContents(scope);
	// }
	//
	// @Deprecated
	// @operator (
	// value = "path_between",
	// content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
	// category = { IOperatorCategory.GRAPH, IOperatorCategory.PATH })
	// @doc (
	// value = "The shortest path between a list of two objects in a graph",
	// examples = { @example (
	// value = "my_graph path_between (ag1:: ag2)",
	// equals = "A path between ag1 and ag2",
	// isExecutable = false) },
	// deprecated = "use 'path_between(graph, geometry, geometry)' instead")
	// @no_test
	// public static IPath path_between(final IScope scope, final GamaGraph graph, final GamaPair sourTarg)
	// throws GamaRuntimeException {
	// // DEBUG.OUT("Cast.asTopology(scope, graph) : " +
	// // Cast.asTopology(scope, graph));
	// return Graphs.path_between(scope, graph, (IShape) sourTarg.key, (IShape) sourTarg.value);
	//
	// // return graph.computeShortestPathBetween(sourTarg.key,
	// // sourTarg.value);
	//
	// }

	// @operator (
	// value = { "read", "get" },
	// category = IOperatorCategory.FILE,
	// concept = { IConcept.ATTRIBUTE, IConcept.FILE })
	// @doc (
	// deprecated = "use the 'attributes` field of the file or database read to obtain the name of the ith attribute.",
	// value = "Reads an attribute of the agent. The attribute index is specified by the operand.",
	// examples = { @example (
	// value = "read (2)",
	// var = "second_variable",
	// equals = "reads the second variable of agent then assigns the returned value to the 'second_variable' variable.
	// ",
	// test = false) })
	// @no_test
	// public static Object opRead(final IScope scope, final Integer index) throws GamaRuntimeException {
	// // First try to read in the temp attributes
	// final Map attributes = scope.peekReadAttributes();
	// if (attributes != null) { return attributes.get(index); }
	// // Try to read in the agent, if it has been created from a GIS/CSV file.
	// // final IAgent g = scope.getAgentScope();
	// // final IList<String> attributes =
	// // g.getPopulation().getSpecies().getAttributeNames(scope);
	// // if (index > 0 && index < attributes.size())
	// // return g.getAttribute(attributes.get(index));
	// return null;
	//
	// }

	// @Deprecated
	// @operator(value = "rewire_p", category = { IOperatorCategory.GRAPH })
	// @doc(value = "Rewires a graph (in the Watts-Strogatz meaning)",
	// deprecated = "Does not work now",
	// examples = { @example(value = "graph graphEpidemio <- graph([]);",
	// isTestOnly = true),
	// @example(value = "graphEpidemio rewire_p 0.2", test = false) },
	// see = "rewire_p")
	// public static IGraph rewireGraph(final IScope scope, final IGraph g,
	// final Double probability) {
	// GraphAlgorithmsHandmade.rewireGraphProbability(scope, g, probability);
	// g.incVersion();
	// return g;
	// }

	//////////////////////////////////////// OLD 3D SHAPE
	//////////////////////////////////////// ////////////////////////////////////////

	//
	//
	// @Deprecated
	// @operator(value = { "rgb_cube" }, category = { IOperatorCategory.SPATIAL,
	// IOperatorCategory.SHAPE })
	// @doc(value = "A cube geometry which side size is equal to the operand.",
	// deprecated = "This operator is deprecated and return a cube instead",
	// usages = { @usage(value = "returns nil if the operand is nil.") },
	// comment = "the center of the cube is by default the location of the
	// current agent in which has been called this operator.",
	// examples = {
	// @example(value = "cube(10)", equals = "a geometry as a square of side
	// size 10.", test = false) },
	// see = { "around", "circle", "cone", "line", "link", "norm", "point",
	// "polygon", "polyline", "rectangle",
	// "triangle" })
	// public static IShape rgbcube(final IScope scope, final Double side_size)
	// {
	// ILocation location;
	// final IAgent a = scope.getAgentScope();
	// location = a != null ? a.getLocation() : new GamaPoint(0, 0);
	// if ( side_size <= 0 ) { return new GamaShape(location); }
	// return GamaGeometryType.buildCube(side_size, location);
	// }

	// @Deprecated
	// @operator(value = "rgb_triangle", category = { IOperatorCategory.SPATIAL,
	// IOperatorCategory.SHAPE })
	// @doc(value = "A triangle geometry which side size is given by the
	// operand.",
	// usages = { @usage("returns nil if the operand is nil.") },
	// comment = "the center of the triangle is by default the location of the
	// current agent in which has been called this operator.",
	// examples = { @example(value = "triangle(5)",
	// equals = "a geometry as a triangle with side_size = 5.",
	// test = false) },
	// see = { "around", "circle", "cone", "line", "link", "norm", "point",
	// "polygon", "polyline", "rectangle",
	// "square" })
	// public static IShape rgbtriangle(final IScope scope, final Double
	// side_size) {
	// ILocation location;
	// final IAgent a = scope.getAgentScope();
	// location = a != null ? a.getLocation() : new GamaPoint(0, 0);
	// if ( side_size <= 0 ) { return new GamaShape(location); }
	// return GamaGeometryType.buildTriangle(side_size, location);
	// }

	//
	// @Deprecated
	// @operator(value = "hemisphere", category = { IOperatorCategory.SPATIAL,
	// IOperatorCategory.SHAPE })
	// @doc(value = "An hemisphere geometry which radius is equal to the
	// operand.",
	// deprecated = "This operator is deprecated and return a sphere instead",
	// special_cases = { "returns a point if the operand is lower or equal to
	// 0." },
	// comment = "the centre of the hemisphere is by default the location of the
	// current agent in which has been called this operator.",
	// examples = { @example(value = "hemisphere(10,0.5)",
	// equals = "a geometry as a circle of radius 10 but displays an
	// hemisphere.",
	// test = false) },
	// see = { "around", "cone", "line", "link", "norm", "point", "polygon",
	// "polyline", "rectangle", "square",
	// "triangle", "hemisphere", "pie3D" })
	// public static IShape hemisphere(final IScope scope, final Double radius,
	// final Double ratio) {
	// ILocation location;
	// final IAgent a = scope.getAgentScope();
	// location = a != null ? a.getLocation() : new GamaPoint(0, 0);
	// if ( radius <= 0 ) { return new GamaShape(location); }
	// return GamaGeometryType.buildSphere(radius, location);
	// }

	// @Deprecated
	// @operator(value = "antislice", category = { IOperatorCategory.SPATIAL,
	// IOperatorCategory.SHAPE })
	// @doc(value = "A sphere geometry which radius is equal to the operand made
	// of 2 hemispheres.",
	// special_cases = { "returns a point if the operand is lower or equal to
	// 0." },
	// deprecated = "This operator is deprecated and return a sphere instead",
	// comment = "the centre of the sphere is by default the location of the
	// current agent that is calling this operator.",
	// examples = { @example(value = "antislice(10,0.3)",
	// equals = "a circle geometry of radius 10, displayed as an antislice.",
	// test = false) },
	// see = { "around", "cone", "line", "link", "norm", "point", "polygon",
	// "polyline", "rectangle", "square",
	// "triangle", "hemisphere", "pie3D" })
	// public static IShape hemispherePac(final IScope scope, final Double
	// radius, final Double ratio) {
	// ILocation location;
	// final IAgent a = scope.getAgentScope();
	// location = a != null ? a.getLocation() : new GamaPoint(0, 0);
	// if ( radius <= 0 ) { return new GamaShape(location); }
	// return GamaGeometryType.buildSphere(radius, location);
	// }
	//
	// @Deprecated
	// @operator(value = "slice", category = { IOperatorCategory.SPATIAL,
	// IOperatorCategory.SHAPE })
	// @doc(value = "An sphere geometry which radius is equal to the operand
	// made of 2 hemisphere.",
	// deprecated = "This operator is deprecated and return a sphere instead",
	// special_cases = { "returns a point if the operand is lower or equal to
	// 0." },
	// comment = "the centre of the sphere is by default the location of the
	// current agent in which has been called this operator.",
	// examples = { @example(value = "slice(10,0.3)",
	// equals = "a circle geometry of radius 10, displayed as a slice.",
	// test = false) },
	// see = { "around", "cone", "line", "link", "norm", "point", "polygon",
	// "polyline", "rectangle", "square",
	// "triangle", "hemisphere", "pie3D" })
	// public static IShape hemisphereMan(final IScope scope, final Double
	// radius, final Double ratio) {
	// ILocation location;
	// final IAgent a = scope.getAgentScope();
	// location = a != null ? a.getLocation() : new GamaPoint(0, 0);
	// if ( radius <= 0 ) { return new GamaShape(location); }
	// return GamaGeometryType.buildSphere(radius, location);
	// }

	// @Deprecated
	// @operator(value = "spherical_pie", category = {
	// IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
	// @doc(value = "An sphere geometry which radius is equal to the operand
	// made of n pie.",
	// deprecated = "This operator is deprecated and return a sphere instead",
	// special_cases = { "returns a point if the operand is lower or equal to
	// 0." },
	// comment = "the centre of the sphere is by default the location of the
	// current agent in which has been called this operator.",
	// examples = { @example(value = "spherical_pie(10,[1.0,1.0,1.0])",
	// equals = "a circle geometry of radius 10, displayed as a sphere with 4
	// slices.",
	// test = false) },
	// see = { "around", "cone", "line", "link", "norm", "point", "polygon",
	// "polyline", "rectangle", "square",
	// "triangle", "hemisphere", "pie3D" })
	// public static IShape pieSphere(final IScope scope, final Double radius,
	// final IList<Double> ratio) {
	// ILocation location;
	// final IAgent a = scope.getAgentScope();
	// location = a != null ? a.getLocation() : new GamaPoint(0, 0);
	// if ( radius <= 0 ) { return new GamaShape(location); }
	//
	// Double sum = 0.0;
	// for ( Object curR : ratio ) {
	// sum = sum + Cast.asFloat(scope, curR);
	// }
	// for ( int i = 0; i < ratio.size(); i++ ) {
	// ratio.set(i, Cast.asFloat(scope, ratio.get(i)) / sum);
	// }
	//
	// return GamaGeometryType.buildSphere(radius, location);
	// }

	// @Deprecated
	// @operator(value = "spherical_pie", category = {
	// IOperatorCategory.SPATIAL, IOperatorCategory.SHAPE })
	// @doc(value = "An sphere geometry which radius is equal to the operand
	// made of n pie.",
	// deprecated = "This operator is deprecated and return a sphere instead",
	// special_cases = { "returns a point if the operand is lower or equal to
	// 0." },
	// comment = "the centre of the sphere is by default the location of the
	// current agent in which has been called this operator.",
	// examples = { @example(value =
	// "spherical_pie(10/2,[0.1,0.9],[#red,#green])",
	// equals = "a circle geometry of radius 10, displayed as a sphere with 2
	// slices.",
	// test = false) },
	// see = { "around", "cone", "line", "link", "norm", "point", "polygon",
	// "polyline", "rectangle", "square",
	// "triangle", "hemisphere", "pie3D" })
	// public static IShape pieSphere(final IScope scope, final Double radius,
	// final IList<Double> ratio,
	// final IList<GamaColor> colors) {
	// ILocation location;
	// final IAgent a = scope.getAgentScope();
	// location = a != null ? a.getLocation() : new GamaPoint(0, 0);
	// if ( radius <= 0 ) { return new GamaShape(location); }
	//
	// Double sum = 0.0;
	// for ( Object curR : ratio ) {
	// sum = sum + Cast.asFloat(scope, curR);
	// }
	// for ( int i = 0; i < ratio.size(); i++ ) {
	// ratio.set(i, Cast.asFloat(scope, ratio.get(i)) / sum);
	// }
	// if ( ratio.size() > colors.size() ) {
	//
	// throw GamaRuntimeException
	// .warning("The number of value is greater of the number of color whereas
	// it should be equal.", scope); }
	//
	// if ( ratio.size() < colors.size() ) { throw GamaRuntimeException
	// .warning("The number of color is greater of the number of value whereas
	// it should be equal.", scope); }
	//
	// return GamaGeometryType.buildSphere(radius, location);
	// }

	// @Deprecated
	// @operator(value = "pacman", category = { IOperatorCategory.SPATIAL,
	// IOperatorCategory.SHAPE })
	// @doc(value = "An pacman geometry which radius is equal to first
	// argument.",
	// deprecated = "This operator is deprecated and return a sphere instead",
	// special_cases = { "returns a point if the operand is lower or equal to
	// 0." },
	// comment = "the centre of the sphere is by default the location of the
	// current agent in which has been called this operator.",
	// examples = { @example(value = "pacman(1)",
	// equals = "a geometry as a circle of radius 10 but displays a sphere.",
	// test = false) },
	// see = { "around", "cone", "line", "link", "norm", "point", "polygon",
	// "polyline", "rectangle", "square",
	// "triangle", "hemisphere", "pie3D" })
	// public static IShape pacMan(final IScope scope, final Double radius) {
	// ILocation location;
	// final IAgent a = scope.getAgentScope();
	// location = a != null ? a.getLocation() : new GamaPoint(0, 0);
	// if ( radius <= 0 ) { return new GamaShape(location); }
	// return GamaGeometryType.buildSphere(radius, location);
	// }
	//
	// @Deprecated
	// @operator(value = "pacman", category = { IOperatorCategory.SPATIAL,
	// IOperatorCategory.SHAPE })
	// @doc(value = "An pacman geometry with a dynamic opening mouth which
	// radius is equal to first argument.",
	// deprecated = "This operator is deprecated and return a sphere instead",
	// special_cases = { "returns a point if the operand is lower or equal to
	// 0." },
	// comment = "the centre of the sphere is by default the location of the
	// current agent in which has been called this operator.",
	// examples = { @example(value = "pacman(1,0.2)",
	// equals = "a geometry as a circle of radius 10 but displays a sphere.",
	// test = false) },
	// see = { "around", "cone", "line", "link", "norm", "point", "polygon",
	// "polyline", "rectangle", "square",
	// "triangle", "hemisphere", "pie3D" })
	// public static IShape pacMan(final IScope scope, final Double radius,
	// final Double ratio) {
	// ILocation location;
	// final IAgent a = scope.getAgentScope();
	// location = a != null ? a.getLocation() : new GamaPoint(0, 0);
	// if ( radius <= 0 ) { return new GamaShape(location); }
	// return GamaGeometryType.buildSphere(radius, location);
	// }

	@operator (
			value = "dem",
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED },
			concept = { IConcept.GEOMETRY, IConcept.SPATIAL_COMPUTATION, IConcept.THREED, IConcept.TEXTURE })
	@doc (
			deprecated = "use the 'field' layer statement instead",
			value = "A polygon that is equivalent to the surface of the texture",
			masterDoc = true,
			comment = "",
			examples = { @example (
					value = "dem(dem)",
					equals = "returns a geometry as a rectangle of width and height equal to the texture.",
					isExecutable = false) },
			see = {})
	@no_test (Reason.IMPOSSIBLE_TO_TEST)
	public static IShape dem(final IScope scope, final GamaFile demFileName) {
		return dem(scope, demFileName, demFileName, 1.0);
	}

	@operator (
			value = "dem",
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED },
			concept = {})
	@doc (
			deprecated = "use the 'field' layer statement instead",
			value = "A polygon that is equivalent to the surface of the texture",
			examples = { @example (
					value = "dem(dem,z_factor)",
					equals = "a geometry as a rectangle of weight and height equal to the texture.",
					isExecutable = false) },
			see = {})
	@no_test (Reason.IMPOSSIBLE_TO_TEST)
	public static IShape dem(final IScope scope, final GamaFile demFileName, final Double z_factor) {
		return dem(scope, demFileName, demFileName, z_factor);
	}

	@operator (
			value = "dem",
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED },
			concept = {})
	@doc (
			deprecated = "use the 'field' layer statement instead",
			value = "A polygon equivalent to the surface of the texture",
			examples = { @example (
					value = "dem(dem,texture)",
					equals = "a geometry as a rectangle of weight and height equal to the texture.",
					isExecutable = false) },
			see = {})
	@no_test (Reason.IMPOSSIBLE_TO_TEST)
	public static IShape dem(final IScope scope, final GamaFile demFile, final GamaFile textureFile) {
		return dem(scope, demFile, textureFile, 1.0);
	}

	@operator (
			value = "dem",
			category = { IOperatorCategory.SPATIAL, IOperatorCategory.THREED },
			concept = {})
	@doc (
			deprecated = "use the 'field' layer statement instead",
			value = "Returns a polygon equivalent to the surface of the textures passed in parameters. This operator only makes sense in the context of the 'draw' command",
			examples = { @example (
					value = "dem(dem,texture,z_factor)",
					equals = "a geometry as a rectangle of width and height equal to the texture.",
					isExecutable = false) },
			see = {})
	@no_test (Reason.IMPOSSIBLE_TO_TEST)
	public static IShape dem(final IScope scope, final GamaFile demFile, final GamaFile textureFile,
			final Double z_factor) {
		// if (!(textureFile instanceof GamaImageFile))
		// throw GamaRuntimeException.error("" + textureFile.getPath(scope) + " is not an image", scope);
		final IGraphics graphics = scope.getGraphics();
		if (graphics == null || graphics.cannotDraw()) return null;
		final MeshDrawingAttributes attributes = new MeshDrawingAttributes(null, null, false);
		attributes.setHeight(z_factor);

		if (!graphics.is2D()) {
			// If we are in the OpenGL world
			GamaPoint p = getDimensionsOf(scope, demFile);
			GamaPoint cs =
					new GamaPoint(scope.getGraphics().getEnvWidth() / p.x, scope.getGraphics().getEnvHeight() / p.y);
			attributes.setCellSize(cs);
			attributes.setGrayscaled(attributes.isGrayscaled() || !(textureFile instanceof GamaImageFile));
			attributes.setTriangulated(true);
			if (!attributes.isGrayscaled()) {
				// TODO AD Understand why we cant use the "normal" way of loading textures in OpenGL.
				// final BufferedImage dem = ((GamaImageFile) textureFile).getImage(scope, true);
				attributes.setTextures(Arrays.asList(textureFile));
			}
			// graphics.drawField(buildDoubleArrayFrom(scope, demFile), attributes);
		} else {
			graphics.drawFile(demFile, attributes);
		}
		return null;
	}

	private static GamaPoint getDimensionsOf(final IScope scope, final GamaFile file) {
		GamaPoint result = new GamaPoint();
		if (file instanceof GamaImageFile) {
			result.x = ((GamaImageFile) file).getCols(scope);
			result.y = ((GamaImageFile) file).getRows(scope);
		} else if (file instanceof GamaGridFile) {
			result.x = ((GamaGridFile) file).getCols(scope);
			result.y = ((GamaGridFile) file).getRows(scope);
		}

		return result;
	}

}
