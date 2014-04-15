/*********************************************************************************************
 * 
 * 
 * 'DeprecatedOperators.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.operators;

import static com.google.common.collect.Iterables.toArray;
import static msi.gama.util.GAML.nullCheck;
import java.util.Iterator;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML.InterleavingIterator;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import com.google.common.collect.Iterators;

/**
 * Class Deprecated.
 * 
 * @author drogoul
 * @since 16 janv. 2014
 * 
 */
public class DeprecatedOperators {

	@operator(value = "as_csv", can_be_const = true, index_type = IType.INT)
	@doc(deprecated = "use csv_file(path, separator) instead", value = "allows to specify the character to use as a separator for a CSV format and returns the file. Yields an error if the file is not a text file", examples = @example("let fileT type: file value: text(\"../includes/Stupid_Cell.csv\") as_csv ';';"))
	@Deprecated
	public static IGamaFile as_csv(final IScope scope, final IGamaFile file, final String s)
		throws GamaRuntimeException {
		return new GamaCSVFile(scope, file.getPath(), s);
		// if ( !(file instanceof GamaTextFile) ) { throw GamaRuntimeException
		// .warning("The 'as_csv' operator can only be applied to text files"); }
		// if ( s == null || s.isEmpty() ) { throw GamaRuntimeException
		// .warning("The 'as_csv' operator expects a non-empty string as its right operand"); }
		// ((GamaTextFile) file).setCsvSeparators(s);
		// return file;
	}

	@operator(value = "gamlfile", can_be_const = true, index_type = IType.INT)
	@doc(deprecated = "use gaml_file instead", value = "opens a file that a is a kind of model file.", comment = "The file should have a shapefile extension, cf. file type definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing shapefile file, an exception is risen.", examples = {
		@example("let fileT type: file value: shapefile(\"../includes/testProperties.shp\");"),
		@example("            // fileT represents the shapefile file \"../includes/testProperties.shp\"") }, see = {
		"file", "properties", "image", "text" })
	@Deprecated
	public static IGamaFile gamlFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GAMLFile(scope, s);
	}

	@operator(value = "gridfile", can_be_const = true, index_type = IType.INT)
	@doc(deprecated = "use grid_file instead", value = "opens a file that a is a kind of shapefile.", comment = "The file should have a gridfile extension, cf. file type definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing gridfile file, an exception is risen.", examples = {
		@example("file fileT <- gridfile(\"../includes/testProperties.asc\");"),
		@example("            // fileT represents the gridfile file \"../includes/testProperties.asc\"") }, see = {
		"file", "properties", "image", "text", "shapefile" })
	@Deprecated
	public static IGamaFile gridFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaGridFile(scope, s);
	}

	@operator(value = "gridfile", can_be_const = true, index_type = IType.INT)
	@doc(deprecated = "use grid_file instead", value = "opens a file that a is a kind of gridfile. The integer parameter allows to specify a coordinate reference system (CRS). If equal to zero, it forces reading the data as alreay projected", comment = "The file should have a gridfile extension, cf. file type definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing gridfile file, an exception is risen.", examples = {
		@example("file fileT <- gridfile(\"../includes/testProperties.asc\");"),
		@example("            // fileT represents the gridfile file \"../includes/testProperties.asc\"") }, see = {
		"file", "properties", "image", "text", "shapefile" })
	@Deprecated
	public static IGamaFile gridFile(final IScope scope, final String s, final Integer code)
		throws GamaRuntimeException {
		return new GamaGridFile(scope, s, code);
	}

	@operator(value = "osmfile", can_be_const = true, index_type = IType.INT)
	@doc(deprecated = "use osm_file instead", value = "opens a file that a is a kind of osmfile.", comment = "The file should have a osmfile extension, cf. file type definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing osmfile file, an exception is risen.", examples = {
		@example("file fileT <- osmfile(\"../includes/testProperties.osm\");"),
		@example("            // fileT represents the osm file \"../includes/testProperties.osm\"") }, see = { "file",
		"properties", "image", "text", "shapefile" })
	@Deprecated
	public static IGamaFile osmFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaOsmFile(scope, s);
	}

	@operator(value = "osmfile", can_be_const = true, index_type = IType.INT)
	@doc(deprecated = "use osm_file instead", value = "opens a file that a is a kind of osmfile, specifying an optional CRS EPSG code", comment = "The file should have an osmfile extension, cf. file type definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing osmfile file, an exception is risen.", examples = {
		@example("file fileT <- osmfile(\"../includes/testProperties.osm\", 4326);"),
		@example("            // fileT represents the osm file \"../includes/testProperties.osm\"") }, see = { "file",
		"properties", "image", "text", "shapefile" })
	@Deprecated
	public static IGamaFile osmFile(final IScope scope, final String s, final Integer i) throws GamaRuntimeException {
		return new GamaOsmFile(scope, s, i);
	}

	@operator(value = "image", can_be_const = true, index_type = IType.POINT)
	@doc(deprecated = "use image_file instead", value = "opens a file that is a kind of image.", comment = "The file should have an image extension, cf. file type deifnition for supported file extensions.", special_cases = "If the specified string does not refer to an existing image file, an exception is risen.", examples = { @example("let fileT type: file value: image(\"../includes/testImage.png\");  // fileT represents the file \"../includes/testShape.png\"") }, see = {
		"file", "shapefile", "properties", "text" })
	@Deprecated
	public static IGamaFile imageFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaImageFile(scope, s);
	}

	@operator(value = "read", type = ITypeProvider.FIRST_TYPE, content_type = ITypeProvider.FIRST_CONTENT_TYPE, index_type = ITypeProvider.FIRST_KEY_TYPE)
	@doc(deprecated = "use the operator \"writable\" instead", value = "marks the file so that only read operations are allowed.", comment = "A file is created by default in read-only mode. The operator write can change the mode.", examples = { @example("read(shapefile(\"../images/point_eau.shp\"))  --:  returns a file in read-only mode representing \"../images/point_eau.shp\"") }, see = {
		"file", "writable" })
	@Deprecated
	public static IGamaFile opRead(final IScope scope, final IGamaFile s) {
		s.setWritable(false);
		return s;
	}

	@operator(value = IKeyword.WRITE, type = ITypeProvider.FIRST_TYPE, content_type = ITypeProvider.FIRST_CONTENT_TYPE, index_type = ITypeProvider.FIRST_KEY_TYPE)
	@doc(deprecated = "use the operator \"writable\" instead", value = "marks the file so that read and write operations are allowed.", comment = "A file is created by default in read-only mode.", examples = { @example("write(shapefile(\"../images/point_eau.shp\"))   --: returns a file in read-write mode representing \"../images/point_eau.shp\"") }, see = {
		"file", "writable" })
	@Deprecated
	public static IGamaFile opWrite(final IScope scope, final IGamaFile s) {
		s.setWritable(true);
		return s;
	}

	@operator(value = "text", can_be_const = true, index_type = IType.INT)
	@doc(deprecated = "use text_file instead", value = "opens a file that a is a kind of text.", comment = "The file should have a text extension, cf. file type definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing text file, an exception is risen.", examples = {
		@example("let fileT type: file value: text(\"../includes/Stupid_Cell.Data\");"),
		@example("				// fileT represents the text file \"../includes/Stupid_Cell.Data\"") }, see = { "file",
		"properties", "image", "shapefile" })
	@Deprecated
	public static IGamaFile textFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaTextFile(scope, s);
	}

	@operator(value = "properties", can_be_const = true, index_type = IType.STRING)
	@doc(deprecated = "use property_file instead", value = "opens a file that is a kind of properties.", comment = "The file should have a properties extension, cf. type file definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing propserites file, an exception is risen.", examples = { @example("let fileT type: file value: properties(\"../includes/testProperties.properties\");  // fileT represents the properties file \"../includes/testProperties.properties\"") }, see = {
		"file", "shapefile", "image", "text" })
	@Deprecated
	public static IGamaFile propertyFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaPropertyFile(scope, s);
	}

	@operator(value = "shapefile", can_be_const = true, index_type = IType.INT)
	@doc(deprecated = "use shape_file instead", value = "opens a file that a is a kind of shapefile.", comment = "The file should have a shapefile extension, cf. file type definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing shapefile file, an exception is risen.", examples = {
		@example("let fileT type: file value: shapefile(\"../includes/testProperties.shp\");"),
		@example("            // fileT represents the shapefile file \"../includes/testProperties.shp\"") }, see = {
		"file", "properties", "image", "text" })
	@Deprecated
	public static IGamaFile shapeFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaShapeFile(scope, s);
	}

	@operator(value = "shapefile", can_be_const = true, index_type = IType.INT)
	@doc(deprecated = "use shape_file instead", value = "opens a file that a is a kind of shapefile, forcing the initial CRS to be the one indicated by the second int parameter (see http://spatialreference.org/ref/epsg/). If this int parameter is equal to 0, the data is considered as already projected", comment = "The file should have a shapefile extension, cf. file type definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing shapefile file, an exception is risen.", examples = {
		@example("let fileT type: file value: shapefile(\"../includes/testProperties.shp\");"),
		@example("            // fileT represents the shapefile file \"../includes/testProperties.shp\"") }, see = {
		"file", "properties", "image", "text" })
	@Deprecated
	public static IGamaFile shapeFile(final IScope scope, final String s, final Integer code)
		throws GamaRuntimeException {
		return new GamaShapeFile(scope, s, code);
	}

	@operator(value = { "add_z" })
	@doc(deprecated = "use set location instead", value = "add_z", comment = "Return a geometry with a z value"
		+ "The add_z operator set the z value of the whole shape."
		+ "For each point of the cell the same z value is set.", examples = { @example("set shape <- shape add_z rnd(100);") }, see = { "add_z_pt" })
	@Deprecated
	public static IShape add_z(final IShape g, final Double z) {
		GamaPoint p = new GamaPoint(g.getLocation().getX(), g.getLocation().getY(), z);
		g.setLocation(p);
		/*
		 * final Coordinate[] coordinates = g.getInnerGeometry().getCoordinates();
		 * ((GamaPoint) g.getLocation()).z = z;
		 * for ( int i = 0; i < coordinates.length; i++ ) {
		 * coordinates[i].z = z;
		 * }
		 */
		return g;
	}

	@operator(value = "to_java")
	@doc(value = "represents the java way to write an expression in java, depending on its type", deprecated = "NOT YET IMPLEMENTED", see = { "to_gaml" })
	@Deprecated
	public static String toJava(final Object val) throws GamaRuntimeException {
		throw GamaRuntimeException.error("to_java is not yet implemented");
	}

	// @operator(value = IKeyword.AS_SKILL, type = ITypeProvider.FIRST_TYPE)
	// @doc(value =
	// "casting an object (left-operand) to an agent if the left-operand is an agent having skill specified by the right-operand.",
	// special_cases =
	// "if the object can not be viewed as an agent having skill specified by the right-operand, then a GamaRuntimeException is thrown.")
	// public static IAgent asSkill(final IScope scope, final Object val, final String skill) {
	// if ( isSkill(scope, val, skill) ) { return (IAgent) val; }
	// throw GamaRuntimeException.error("Cast exception: " + val + " can not be viewed as a " + skill);
	// }

	@operator(value = IKeyword.UNKNOWN, can_be_const = true)
	@doc(deprecated = "generated automatically now", value = "returns the operand itself")
	@Deprecated
	public static Object asObject(final Object obj) {
		return obj;
	}

	@operator(value = { "collate" }, content_type = ITypeProvider.FIRST_ELEMENT_CONTENT_TYPE)
	@doc(deprecated = "The idiom 'collate' is considered as deprecated. Please use 'interleave' instead.", value = "a new list containing the interleaved elements of the containers contained in the operand", comment = "the operand should be a list of lists of elements. The result is a list of elements. ", examples = {
		@example("interleave([1,2,4,3,5,7,6,8]) 	--: 	[1,2,3,4,5,7,6,8]"),
		@example("interleave([['e11','e12','e13'],['e21','e22','e23'],['e31','e32','e33']])  --:  [e11,e21,e31,e12,e22,e32,e13,e23,e33]") })
	@Deprecated
	public static IList collate(final IScope scope, final IContainer cc) {
		final Iterator it = new InterleavingIterator(toArray(nullCheck(scope, cc).iterable(scope), Object.class));
		return new GamaList(Iterators.toArray(it, Object.class));
	}

	@operator(value = "evaluate_with", can_be_const = false, category = { IOperatorCategory.SYSTEM })
	@doc(deprecated = "This operator has been deprecated and there are no plans to replace it soon.", value = "evaluates the left-hand java expressions with the map of parameters (right-hand operand)", see = { "eval_gaml" })
	public static Object opEvalJava(final IScope scope, final String code, final IExpression parameters) {
		return code;
		// try {
		// GamaMap param;
		// if ( parameters instanceof MapExpression ) {
		// param = ((MapExpression) parameters).getElements();
		// } else {
		// param = new GamaMap();
		// }
		// final String[] parameterNames = new String[param.size() + 1];
		// final Class[] parameterTypes = new Class[param.size() + 1];
		// final Object[] parameterValues = new Object[param.size() + 1];
		// parameterNames[0] = "scope";
		// parameterTypes[0] = IScope.class;
		// parameterValues[0] = scope;
		// int i = 1;
		// for ( final Object e : param.entrySet() ) {
		// final Map.Entry<IExpression, IExpression> entry = (Map.Entry<IExpression, IExpression>) e;
		// parameterNames[i] = entry.getKey().literalValue();
		// parameterTypes[i] = entry.getValue().getType().toClass();
		// parameterValues[i] = entry.getValue().value(scope);
		// i++;
		// }
		// final ScriptEvaluator se = new ScriptEvaluator();
		// se.setReturnType(Object.class);
		// se.setDefaultImports(gamaDefaultImports);
		// se.setParameters(parameterNames, parameterTypes);
		// se.cook(code);
		// // Evaluate script with actual parameter values.
		// return se.evaluate(parameterValues);
		//
		// } catch (final Exception e) {
		// final Throwable ee =
		// e instanceof InvocationTargetException ? ((InvocationTargetException) e).getTargetException() : e;
		// GuiUtils.informConsole("Error in evaluating Java code : '" + code + "' in " + scope.getAgentScope() +
		// java.lang.System.getProperty("line.separator") + "Reason: " + ee.getMessage());
		// return null;
		// }
	}

	@operator(value = { IKeyword.AT, "@" }, type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(deprecated = "The use of at on a species is deprecated, please use it one a population instead (list(species_name) instead of species_name)")
	public static IAgent at(final IScope scope, final ISpecies s, final GamaPoint val) throws GamaRuntimeException {
		return scope.getAgentScope().getPopulationFor(s).getAgent(scope, val);
	}
}
