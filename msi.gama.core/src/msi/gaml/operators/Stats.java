/*********************************************************************************************
 * 
 * 
 * 'Stats.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.operators;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gama.util.graph.IGraph;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;
import org.uncommons.maths.statistics.DataSet;
import rcaller.RCaller;
import rcaller.exception.*;

/**
 * Written by drogoul Modified on 15 janv. 2011
 * 
 * @todo Description
 * 
 */
public class Stats {

	private static final boolean DEBUG = false; // Change DEBUG = false for release version

	private static DataSet from(final IScope scope, final IContainer values) {
		DataSet d = new DataSet(values.length(scope));
		for ( Object o : values.iterable(scope) ) {
			if ( o instanceof Number ) {
				d.addValue(((Number) o).doubleValue());
			}
		}
		return d;
	}

	@operator(value = "max",
		can_be_const = true,
		type = ITypeProvider.FIRST_CONTENT_TYPE,
		expected_content_type = { IType.INT, IType.FLOAT, IType.POINT },
		category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER })
	@doc(value = "the maximum element found in the operand",
		masterDoc = true,
		comment = "the max operator behavior depends on the nature of the operand",
		usages = {
			@usage(value = "if it is a list of int of float, max returns the maximum of all the elements",
				examples = { @example(value = "max ([100, 23.2, 34.5])", equals = "100.0") }),
			@usage(value = "if it is a list of points: max returns the maximum of all points as a point (i.e. the point with the greatest coordinate on the x-axis, in case of equality the point with the greatest coordinate on the y-axis is chosen. If all the points are equal, the first one is returned. )",
				examples = { @example(value = "max([{1.0,3.0},{3.0,5.0},{9.0,1.0},{7.0,8.0}])", equals = "{9.0,1.0}") }),
			@usage("if it is a population of a list of other type: max transforms all elements into integer and returns the maximum of them"),
			@usage("if it is a map, max returns the maximum among the list of all elements value"),
			@usage("if it is a file, max returns the maximum of the content of the file (that is also a container)"),
			@usage("if it is a graph, max returns the maximum of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)"),
			@usage("if it is a matrix of int, float or object, max returns the maximum of all the numerical elements (thus all elements for integer and float matrices)"),
			@usage("if it is a matrix of geometry, max returns the maximum of the list of the geometries"),
			@usage("if it is a matrix of another type, max returns the maximum of the elements transformed into float") },
		see = { "min" })
	public static
		Object max(final IScope scope, final IContainer l) {
		Number maxNum = null;
		ILocation maxPoint = null;
		for ( Object o : l.iterable(scope) ) {
			if ( o instanceof ILocation && maxNum == null ) {
				if ( maxPoint == null || ((ILocation) o).compareTo(maxPoint) > 0 ) {
					maxPoint = (ILocation) o;
				}
			} else if ( o instanceof Number && maxPoint == null &&
				(maxNum == null || ((Number) o).doubleValue() > maxNum.doubleValue()) ) {
				maxNum = (Number) o;
			} else {
				Double d = Cast.asFloat(scope, o);
				if ( maxNum == null || d > maxNum.doubleValue() ) {
					maxNum = d;
				}
			}
		}
		return maxNum == null ? maxPoint : maxNum;
	}

	@operator(value = "min",
		can_be_const = true,
		type = ITypeProvider.FIRST_CONTENT_TYPE,
		expected_content_type = { IType.INT, IType.FLOAT, IType.POINT },
		category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER })
	@doc(value = "the minimum element found in the operand.",
		masterDoc = true,
		comment = "the min operator behavior depends on the nature of the operand",
		usages = {
			@usage(value = "if it is a list of int or float: min returns the minimum of all the elements",
				examples = { @example(value = "min ([100, 23.2, 34.5])", equals = "23.2") }),
			@usage(value = "if it is a list of points: min returns the minimum of all points as a point (i.e. the point with the smallest coordinate on the x-axis, in case of equality the point with the smallest coordinate on the y-axis is chosen. If all the points are equal, the first one is returned. )"),
			@usage(value = "if it is a population of a list of other types: min transforms all elements into integer and returns the minimum of them"),
			@usage(value = "if it is a map, min returns the minimum among the list of all elements value"),
			@usage(value = "if it is a file, min returns the minimum of the content of the file (that is also a container)"),
			@usage(value = "if it is a graph, min returns the minimum of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)"),
			@usage(value = "if it is a matrix of int, float or object, min returns the minimum of all the numerical elements (thus all elements for integer and float matrices)"),
			@usage(value = "if it is a matrix of geometry, min returns the minimum of the list of the geometries"),
			@usage(value = "if it is a matrix of another type, min returns the minimum of the elements transformed into float") },
		see = { "max" })
	public static
		Object min(final IScope scope, final IContainer l) {
		Number minNum = null;
		ILocation minPoint = null;
		for ( Object o : l.iterable(scope) ) {
			if ( o instanceof ILocation && minNum == null ) {
				if ( minPoint == null || ((ILocation) o).compareTo(minPoint) < 0 ) {
					minPoint = (ILocation) o;
				}
			} else if ( o instanceof Number && minPoint == null &&
				(minNum == null || ((Number) o).doubleValue() < minNum.doubleValue()) ) {
				minNum = (Number) o;
			} else {
				Double d = Cast.asFloat(scope, o);
				if ( minNum == null || d < minNum.doubleValue() ) {
					minNum = d;
				}
			}
		}
		return minNum == null ? minPoint : minNum;
	}

	@operator(value = { "mul", "product" },
		can_be_const = true,
		type = ITypeProvider.FIRST_CONTENT_TYPE,
		expected_content_type = { IType.INT, IType.FLOAT, IType.POINT },
		category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER })
	@doc(value = "the product of all the elements of the operand",
		masterDoc = true,
		comment = "the mul operator behavior depends on the nature of the operand",
		usages = {
			@usage(value = "if it is a list of int or float: mul returns the product of all the elements",
				examples = { @example(value = "mul ([100, 23.2, 34.5])", equals = "80040.0") }),
			@usage(value = "if it is a list of points: mul returns the product of all points as a point (each coordinate is the product of the corresponding coordinate of each element)"),
			@usage(value = "if it is a list of other types: mul transforms all elements into integer and multiplies them"),
			@usage(value = "if it is a map, mul returns the product of the value of all elements"),
			@usage(value = "if it is a file, mul returns the product of the content of the file (that is also a container)"),
			@usage(value = "if it is a graph, mul returns the product of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)"),
			@usage(value = "if it is a matrix of int, float or object, mul returns the product of all the numerical elements (thus all elements for integer and float matrices)"),
			@usage(value = "if it is a matrix of geometry, mul returns the product of the list of the geometries"),
			@usage(value = "if it is a matrix of other types: mul transforms all elements into float and multiplies them") },
		see = { "sum" })
	public static
		Object product(final IScope scope, final IContainer l) {
		DataSet x = new DataSet();
		DataSet y = null, z = null;
		for ( Object o : l.iterable(scope) ) {
			if ( o instanceof ILocation ) {
				if ( y == null ) {
					y = new DataSet();
					z = new DataSet();
				}
				ILocation p = (ILocation) o;
				x.addValue(p.getX());
				y.addValue(p.getY());
				z.addValue(p.getZ());
			} else {
				x.addValue(Cast.asFloat(scope, o));
			}
		}
		if ( x.getSize() == 0 ) {
			if ( y == null ) { return 0.0; }
			return new GamaPoint(0, 0, 0);
		}
		if ( y == null ) { return x.getProduct(); }
		return new GamaPoint(x.getProduct(), y.getProduct(), z.getProduct());
	}

	@operator(value = "sum",
		can_be_const = true,
		type = ITypeProvider.FIRST_CONTENT_TYPE,
		expected_content_type = { IType.INT, IType.FLOAT, IType.POINT },
		category = { IOperatorCategory.STATISTICAL, IOperatorCategory.CONTAINER })
	@doc(value = "the sum of all the elements of the operand",
		masterDoc = true,
		comment = "the sum operator behavior depends on the nature of the operand",
		usages = {
			@usage(value = "if it is a list of int or float: sum returns the sum of all the elements",
				examples = { @example(value = "sum ([12,10,3])", returnType = IKeyword.INT, equals = "25") }),
			@usage(value = "if it is a list of points: sum returns the sum of all points as a point (each coordinate is the sum of the corresponding coordinate of each element)",
				examples = { @example(value = "sum([{1.0,3.0},{3.0,5.0},{9.0,1.0},{7.0,8.0}])", equals = "{20.0,17.0}") }),
			@usage(value = "if it is a population or a list of other types: sum transforms all elements into integer and sums them"),
			@usage(value = "if it is a map, sum returns the sum of the value of all elements"),
			@usage(value = "if it is a file, sum returns the sum of the content of the file (that is also a container)"),
			@usage(value = "if it is a graph, sum returns the sum of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)"),
			@usage(value = "if it is a matrix of int, float or object, sum returns the sum of all the numerical elements (i.e. all elements for integer and float matrices)"),
			@usage(value = "if it is a matrix of geometry, sum returns the sum of the list of the geometries"),
			@usage(value = "if it is a matrix of other types: sum transforms all elements into float and sums them") },
		see = { "mul" })
	public static
		Object sum(final IScope scope, final IContainer l) {
		DataSet x = new DataSet();
		DataSet y = null, z = null;
		for ( Object o : l.iterable(scope) ) {
			if ( o instanceof ILocation ) {
				if ( y == null ) {
					y = new DataSet();
					z = new DataSet();
				}
				ILocation p = (ILocation) o;
				x.addValue(p.getX());
				y.addValue(p.getY());
				z.addValue(p.getZ());
			} else {
				x.addValue(Cast.asFloat(scope, o));
			}
		}
		if ( x.getSize() == 0 ) {
			// y should ALWAYS be null in that case...
			if ( y == null ) { return 0.0; }
			return new GamaPoint(0, 0, 0);
		}
		if ( y == null ) { return x.getAggregate(); }
		return new GamaPoint(x.getAggregate(), y.getAggregate(), z.getAggregate());
	}

	@operator(value = "sum", can_be_const = true, type = IType.GRAPH, category = { IOperatorCategory.GRAPH })
	public static double sum(final IScope scope, final IGraph g) {
		if ( g == null ) { return 0.0; }
		return g.computeTotalWeight();
	}

	@operator(value = "mean", can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, expected_content_type = {
		IType.INT, IType.FLOAT, IType.POINT }, category = { IOperatorCategory.STATISTICAL })
	@doc(value = "the mean of all the elements of the operand",
		comment = "the elements of the operand are summed (see sum for more details about the sum of container elements ) and then the sum value is divided by the number of elements.",
		special_cases = { "if the container contains points, the result will be a point" },
		examples = { @example(value = "mean ([4.5, 3.5, 5.5, 7.0])", equals = "5.125 ") },
		see = { "sum" })
	public static
		Object getMean(final IScope scope, final IExpression expr) throws GamaRuntimeException {
		IType type = expr.getType();
		if ( !type.isContainer() ) { throw GamaRuntimeException.error("'mean' can only operate on containers."); }
		IContainer l = (IContainer) Types.get(IType.CONTAINER).cast(scope, expr.value(scope), null);
		if ( l.length(scope) == 0 ) { return type.getContentType().id() == IType.POINT ? new GamaPoint(0, 0, 0)
			: Double.valueOf(0d); }
		return getMean(scope, l);
	}

	public static Object getMean(final IScope scope, final IContainer l) throws GamaRuntimeException {
		// FIXME Problem wiith this line if the container is intended to contain points...
		if ( l.length(scope) == 0 ) { return Double.valueOf(0d); }
		Object s = sum(scope, l);
		if ( s instanceof Number ) { return ((Number) s).doubleValue() / l.length(scope); }
		if ( s instanceof ILocation ) { return Points.divide((GamaPoint) s, l.length(scope)); }
		return Cast.asFloat(scope, s) / l.length(scope);
	}

	// TODO Penser a faire ces calculs sur les points, egalement (et les entiers ?)

	@operator(value = "median",
		can_be_const = true,
		expected_content_type = { IType.INT, IType.FLOAT },
		category = { IOperatorCategory.STATISTICAL })
	@doc(value = "the median of all the elements of the operand.",
		comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.",
		special_cases = { "" },
		examples = { @example(value = "median ([4.5, 3.5, 5.5, 7.0])", equals = "5.0") },
		see = { "mean" })
	public static
		Double opMedian(final IScope scope, final IContainer values) {
		DataSet d = from(scope, values);
		return d.getMedian();
	}

	@operator(value = "standard_deviation",
		can_be_const = true,
		expected_content_type = { IType.INT, IType.FLOAT },
		category = { IOperatorCategory.STATISTICAL })
	@doc(value = "the standard deviation on the elements of the operand. See <A href=\"http://en.wikipedia.org/wiki/Standard_deviation\">Standard_deviation</A> for more details.",
		comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.",
		special_cases = { "" },
		examples = { @example(value = "standard_deviation ([4.5, 3.5, 5.5, 7.0])", equals = "1.2930100540985752") },
		see = { "mean", "mean_deviation" })
	public static
		Double opStDev(final IScope scope, final IContainer values) {
		DataSet d = from(scope, values);
		return d.getStandardDeviation();
	}

	@operator(value = "geometric_mean",
		can_be_const = true,
		expected_content_type = { IType.INT, IType.FLOAT },
		category = { IOperatorCategory.STATISTICAL })
	@doc(value = "the geometric mean of the elements of the operand. See <A href=\"http://en.wikipedia.org/wiki/Geometric_mean\">Geometric_mean</A> for more details.",
		comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.",
		special_cases = { "" },
		examples = { @example(value = "geometric_mean ([4.5, 3.5, 5.5, 7.0])", equals = "4.962326343467649") },
		see = { "mean", "median", "harmonic_mean" })
	public static
		Double opGeomMean(final IScope scope, final IContainer values) {
		DataSet d = from(scope, values);
		return d.getGeometricMean();
	}

	@operator(value = "harmonic_mean",
		can_be_const = true,
		expected_content_type = { IType.INT, IType.FLOAT },
		category = { IOperatorCategory.STATISTICAL })
	@doc(value = "the harmonic mean of the elements of the operand. See <A href=\"http://en.wikipedia.org/wiki/Harmonic_mean\">Harmonic_mean</A> for more details.",
		comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.",
		special_cases = { "" },
		examples = { @example(value = "harmonic_mean ([4.5, 3.5, 5.5, 7.0])", equals = "4.804159445407279") },
		see = { "mean", "median", "geometric_mean" })
	public static
		Double opHarmonicMean(final IScope scope, final IContainer values) {
		DataSet d = from(scope, values);
		return d.getHarmonicMean();
	}

	@operator(value = "variance",
		can_be_const = true,
		expected_content_type = { IType.INT, IType.FLOAT },
		category = { IOperatorCategory.STATISTICAL })
	@doc(value = "the variance of the elements of the operand. See <A href=\"http://en.wikipedia.org/wiki/Variance\">Variance</A> for more details.",
		comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded. ",
		examples = { @example(value = "variance ([4.5, 3.5, 5.5, 7.0])", equals = "1.671875") },
		see = { "mean", "median" })
	public static
		Double opVariance(final IScope scope, final IContainer values) {
		DataSet d = from(scope, values);
		return d.getVariance();
	}

	@operator(value = "mean_deviation",
		can_be_const = true,
		expected_content_type = { IType.INT, IType.FLOAT },
		category = { IOperatorCategory.STATISTICAL })
	@doc(value = "the deviation from the mean of all the elements of the operand. See <A href= \"http://en.wikipedia.org/wiki/Absolute_deviation\" >Mean_deviation</A> for more details.",
		comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.",
		examples = { @example(value = "mean_deviation ([4.5, 3.5, 5.5, 7.0])", equals = "1.125") },
		see = { "mean", "standard_deviation" })
	public static
		Double opMeanDeviation(final IScope scope, final IContainer values) {
		DataSet d = from(scope, values);
		return d.getMeanDeviation();
	}

	@operator(value = { "frequency_of" },
		can_be_const = true,
		iterator = true,
		index_type = ITypeProvider.SECOND_CONTENT_TYPE,
		content_type = IType.INT,
		category = { IOperatorCategory.STATISTICAL })
	@doc(value = "Returns a map with keys equal to the application of the right-hand argument (like collect) and values equal to the frequency of this key (i.e. how many times it has been obtained)",
		comment = "",
		examples = { @example(value = "[ag1, ag2, ag3, ag4] frequency_of each.size",
			equals = "the different sizes as keys and the number of agents of this size as values",
			isExecutable = false) }, see = "as_map")
	public static
		GamaMap frequencyOf(final IScope scope, final IContainer original, final IExpression filter)
			throws GamaRuntimeException {
		if ( original == null ) { return new GamaMap(); }
		final GamaMap<Object, Integer> result = new GamaMap();
		for ( Object each : original.iterable(scope) ) {
			scope.setEach(each);
			Object key = filter.value(scope);
			if ( !result.containsKey(key) ) {
				result.put(key, 1);
			} else {
				result.put(key, result.get(key) + 1);
			}
		}
		return result;
	}

	@operator(value = "corR", can_be_const = false, type = IType.FLOAT, category = { IOperatorCategory.STATISTICAL })
	@doc(value = "returns the Pearson correlation coefficient of two given vectors (right-hand operands) in given variable  (left-hand operand).",
		special_cases = "if the lengths of two vectors in the right-hand aren't equal, returns 0",
		examples = { @example("list X <- [1, 2, 3];"), @example("list Y <- [1, 2, 4];"),
			@example(value = "corR(X, Y)", equals = "0.981980506061966") })
	public static
		Object getCorrelationR(final IScope scope, final IContainer l1, final IContainer l2)
			throws GamaRuntimeException, RCallerParseException, RCallerExecutionException {
		if ( l1.length(scope) == 0 || l2.length(scope) == 0 ) { return Double.valueOf(0d); }

		if ( l1.length(scope) != l2.length(scope) ) { return Double.valueOf(0d); }

		RCaller caller = new RCaller();

		String RPath = ((IGamaFile) GamaPreferences.LIB_R.value(scope)).getPath();
		caller.setRscriptExecutable(RPath);
		// caller.setRscriptExecutable("\"" + RPath + "\"");
		// if ( java.lang.System.getProperty("os.name").startsWith("Mac") ) {
		// caller.setRscriptExecutable(RPath);
		// }

		double[] vectorX = new double[l1.length(scope)];
		double[] vectorY = new double[l2.length(scope)];

		int i = 0;
		for ( Object o : l1.iterable(scope) ) {
			vectorX[i++] = Double.parseDouble(o.toString());
		}

		i = 0;
		for ( Object o : l2.iterable(scope) ) {
			vectorY[i++] = Double.parseDouble(o.toString());
		}

		caller.addDoubleArray("vectorX", vectorX);
		caller.addDoubleArray("vectorY", vectorY);

		caller.addRCode("corCoef<-cor(vectorX, vectorY, method='pearson')");

		caller.runAndReturnResult("corCoef");

		double[] results;
		try {
			results = caller.getParser().getAsDoubleArray("corCoef");
		} catch (Exception ex) {
			return 0.0;
		}

		return results[0];
	}

	@operator(value = "meanR",
		can_be_const = false,
		type = ITypeProvider.FIRST_CONTENT_TYPE,
		category = { IOperatorCategory.STATISTICAL })
	@doc(value = "returns the mean value of given vector (right-hand operand) in given variable  (left-hand operand).",
		examples = { @example("list<int> X <- [2, 3, 1];"),
			@example(value = "meanR(X)", equals = "2", returnType = IKeyword.INT) })
	public static Object getMeanR(final IScope scope, final IContainer l) throws GamaRuntimeException,
		RCallerParseException, RCallerExecutionException {
		if ( l.length(scope) == 0 ) { return Double.valueOf(0d); }

		double[] results;
		RCaller caller = new RCaller();

		String RPath = ((IGamaFile) GamaPreferences.LIB_R.value(scope)).getPath();
		caller.setRscriptExecutable(RPath);
		// caller.setRscriptExecutable("\"" + RPath + "\"");
		// if ( java.lang.System.getProperty("os.name").startsWith("Mac") ) {
		// caller.setRscriptExecutable(RPath);
		// }

		double[] data = new double[l.length(scope)];
		int i = 0;
		for ( Object o : l.iterable(scope) ) {
			data[i++] = Double.parseDouble(o.toString());
		}

		caller.addDoubleArray("data", data);
		caller.addRCode("mean<-mean(data)");
		caller.runAndReturnResult("mean");

		results = caller.getParser().getAsDoubleArray("mean");
		return results[0];
	}

	@operator(value = "R_compute",
		can_be_const = false,
		content_type = IType.LIST,
		index_type = IType.STRING,
		category = { IOperatorCategory.STATISTICAL })
	@doc(deprecated = "Use R_file instead",
		value = "returns the value of the last left-hand operand of given R file (right-hand operand) in given vector  (left-hand operand).",
		examples = {
			@example(value = "file f <- file('Correlation.r');", isTestOnly = true),
			@example(value = "save \"x <- c(1, 2, 3);\" to: f.path;", isTestOnly = true),
			@example(value = "save \"y <- c(1, 2, 4);\" to: f.path;", isTestOnly = true),
			@example(value = "save \"result<- cor(x, y);\" to: f.path;", isTestOnly = true),
			@example(value = "R_compute('Correlation.R')", var = "result", equals = "['result'::['0.981980506061966']]"),
			@example("////// Correlation.R file:"), @example("// x <- c(1, 2, 3);"), @example("// y <- c(1, 2, 4);"),
			@example("// result <- cor(x, y);"), @example("// Output:"), @example("// result::[0.981980506061966]") })
	@Deprecated
	public static
		GamaMap opRFileEvaluate(final IScope scope, final String RFile) throws GamaRuntimeException,
			RCallerParseException, RCallerExecutionException {
		RFile obj = new RFile(scope, RFile);
		return obj.getContents(scope);
	}

	@operator(value = "R_compute_param",
		can_be_const = false,
		type = IType.MAP,
		content_type = IType.LIST,
		index_type = IType.STRING,
		category = { IOperatorCategory.STATISTICAL })
	@doc(deprecated = "Use R_file instead",
		value = "returns the value of the last left-hand operand of given R file (right-hand operand) in given vector  (left-hand operand), R file (first right-hand operand) reads the vector (second right-hand operand) as the parameter vector",
		examples = { @example(value = "file f <- file('AddParam.r');", isTestOnly = true),
			@example(value = "save \"v1 <- vectorParam[1];\" to: f.path;", isTestOnly = true),
			@example(value = "save \"v2<-vectorParam[2];\" to: f.path;", isTestOnly = true),
			@example(value = "save \"v3<-vectorParam[3];\" to: f.path;", isTestOnly = true),
			@example(value = "save \"result<-v1+v2+v3;\" to: f.path;", isTestOnly = true),
			@example("list<int> X <- [2, 3, 1];"),
			@example(value = "R_compute_param('AddParam.R', X)", var = "result", equals = "['result'::['6']]"),
			@example("////// AddParam.R file:"), @example("// v1 <- vectorParam[1];"),
			@example("// v2<-vectorParam[2];"), @example("// v3<-vectorParam[3];"), @example("// result<-v1+v2+v3;"),
			@example("////// Output:"), @example("// 'result'::[6]") })
	@Deprecated
	public static
		GamaMap operateRFileEvaluate(final IScope scope, final String RFile, final IContainer param)
			throws GamaRuntimeException, RCallerParseException, RCallerExecutionException {
		RFile obj = new RFile(scope, RFile, param);
		return obj.getContents(scope);
	}

}
