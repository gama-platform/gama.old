/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML, RCaller), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Benoit Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 * - Truong Xuan Viet, UMI 209 UMMISCO, IRD/UPMC (RCaller integration), 2012
 * - Huynh Quang Nghi, UMI 209 UMMISCO, IRD/UPMC (RCaller integration), 2012
 */
package msi.gaml.operators;

import java.io.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.graph.IGraph;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;
import org.uncommons.maths.statistics.DataSet;
import rcaller.*;
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

	@operator(value = "max", can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, expected_content_type = {
		IType.INT, IType.FLOAT, IType.POINT })
	@doc(value = "the maximum element found in the operand", comment = "the max operator behavior depends on the nature of the operand", special_cases = {
		"if it is a list of int of float, max returns the maximum of all the elements",
		"if it is a list of points: max returns the maximum of all points as a point (i.e. the point with the greatest coordinate on the x-axis, in case of equality the point with the greatest coordinate on the y-axis is chosen. If all the points are equal, the first one is returned. )",
		"if it is a population of a list of other type: max transforms all elements into integer and returns the maximum of them",
		"if it is a map, max returns the maximum among the list of all elements value",
		"if it is a file, max returns the maximum of the content of the file (that is also a container)",
		"if it is a graph, max returns the maximum of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)",
		"if it is a matrix of int, float or object, max returns the maximum of all the numerical elements (thus all elements for integer and float matrices)",
		"if it is a matrix of geometry, max returns the maximum of the list of the geometries",
		"if it is a matrix of another type, max returns the maximum of the elements transformed into float" }, see = { "min" }, examples = {
		"max ([100, 23.2, 34.5]) 			--: 	100.0", "max([{1.0;3.0},{3.0;5.0},{9.0;1.0},{7.0;8.0}]) 	--:  {9.0;1.0}" })
	public static Object max(final IScope scope, final IContainer l) {
		Number maxNum = null;
		ILocation maxPoint = null;
		for ( Object o : l.iterable(scope) ) {
			if ( o instanceof ILocation && maxNum == null &&
				(maxPoint == null || ((ILocation) o).compareTo(maxPoint) > 0) ) {
				maxPoint = (ILocation) o;
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

	@operator(value = "min", can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, expected_content_type = {
		IType.INT, IType.FLOAT, IType.POINT })
	@doc(value = "the minimum element found in the operand.", comment = "the min operator behavior depends on the nature of the operand", special_cases = {
		"if it is a list of int or float: min returns the minimum of all the elements",
		"if it is a list of points: min returns the minimum of all points as a point (i.e. the point with the smallest coordinate on the x-axis, in case of equality the point with the smallest coordinate on the y-axis is chosen. If all the points are equal, the first one is returned. )",
		"if it is a population of a list of other types: min transforms all elements into integer and returns the minimum of them",
		"if it is a map, min returns the minimum among the list of all elements value",
		"if it is a file, min returns the minimum of the content of the file (that is also a container)",
		"if it is a graph, min returns the minimum of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)",
		"if it is a matrix of int, float or object, min returns the minimum of all the numerical elements (thus all elements for integer and float matrices)",
		"if it is a matrix of geometry, min returns the minimum of the list of the geometries",
		"if it is a matrix of another type, min returns the minimum of the elements transformed into float" }, see = { "max" }, examples = { "min ([100, 23.2, 34.5]) 	--: 	23.2" })
	public static Object min(final IScope scope, final IContainer l) {
		Number minNum = null;
		ILocation minPoint = null;
		for ( Object o : l.iterable(scope) ) {
			if ( o instanceof ILocation && minNum == null &&
				(minPoint == null || ((ILocation) o).compareTo(minPoint) < 0) ) {
				minPoint = (ILocation) o;
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

	@operator(value = { "mul", "product" }, can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, expected_content_type = {
		IType.INT, IType.FLOAT, IType.POINT })
	@doc(value = "the product of all the elements of the operand", comment = "the mul operator behavior depends on the nature of the operand", special_cases = {
		"if it is a list of int or float: mul returns the product of all the elements",
		"if it is a list of points: mul returns the product of all points as a point (each coordinate is the product of the corresponding coordinate of each element)",
		"if it is a list of other types: mul transforms all elements into integer and multiplies them",
		"if it is a map, mul returns the product of the value of all elements",
		"if it is a file, mul returns the product of the content of the file (that is also a container)",
		"if it is a graph, mul returns the product of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)",
		"if it is a matrix of int, float or object, mul returns the product of all the numerical elements (thus all elements for integer and float matrices)",
		"if it is a matrix of geometry, mul returns the product of the list of the geometries",
		"if it is a matrix of other types: mul transforms all elements into float and multiplies them", }, see = { "sum" }, examples = { "mul ([100, 23.2, 34.5]) 	--:		80040.0" })
	public static Object product(final IScope scope, final IContainer l) {
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

	@operator(value = "sum", can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, expected_content_type = {
		IType.INT, IType.FLOAT, IType.POINT })
	@doc(value = "the sum of all the elements of the operand", comment = "the sum operator behavior depends on the nature of the operand", special_cases = {
		"if it is a list of int or float: sum returns the sum of all the elements",
		"if it is a list of points: sum returns the sum of all points as a point (each coordinate is the sum of the corresponding coordinate of each element)",
		"if it is a population or a list of other types: sum transforms all elements into integer and sums them",
		"if it is a map, sum returns the sum of the value of all elements",
		"if it is a file, sum returns the sum of the content of the file (that is also a container)",
		"if it is a graph, sum returns the sum of the list of the elements of the graph (that can be the list of edges or vertexes depending on the graph)",
		"if it is a matrix of int, float or object, sum returns the sum of all the numerical elements (i.e. all elements for integer and float matrices)",
		"if it is a matrix of geometry, sum returns the sum of the list of the geometries",
		"if it is a matrix of other types: sum transforms all elements into float and sums them", }, see = { "mul" }, examples = {
		"sum ([12,10, 3]) 	--: 	25.0", "sum([{1.0;3.0},{3.0;5.0},{9.0;1.0},{7.0;8.0}])		--: {20.0;17.0} " })
	public static Object sum(final IScope scope, final IContainer l) {
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

	@operator(value = "sum", can_be_const = true, type = IType.GRAPH)
	public static double sum(final IScope scope, final IGraph g) {
		if ( g == null ) { return 0.0; }
		return g.computeTotalWeight();
	}

	@operator(value = "mean", can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE, expected_content_type = {
		IType.INT, IType.FLOAT, IType.POINT })
	@doc(value = "the mean of all the elements of the operand", comment = "the elements of the operand are summed (see sum for more details about the sum of container elements ) and then the sum value is divided by the number of elements.", special_cases = { "if the container contains points, the result will be a point" }, examples = { "mean ([4.5, 3.5, 5.5, 7.0]) --: 5.125 " }, see = { "sum" })
	public static Object getMean(final IScope scope, final IExpression expr) throws GamaRuntimeException {
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

	@operator(value = "median", expected_content_type = { IType.INT, IType.FLOAT })
	@doc(value = "the median of all the elements of the operand.", comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.", special_cases = { "" }, examples = { "median ([4.5, 3.5, 5.5, 7.0]) --: 5.0" }, see = { "mean" })
	public static Double opMedian(final IScope scope, final IContainer values) {
		DataSet d = from(scope, values);
		return d.getMedian();
	}

	@operator(value = "standard_deviation", expected_content_type = { IType.INT, IType.FLOAT })
	@doc(value = "the standard deviation on the elements of the operand. See <A href=\"http://en.wikipedia.org/wiki/Standard_deviation\">Standard_deviation</A> for more details.", comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.", special_cases = { "" }, examples = { "standard_deviation ([4.5, 3.5, 5.5, 7.0]) --: 1.2930100540985752" }, see = {
		"mean", "mean_deviation" })
	public static Double opStDev(final IScope scope, final IContainer values) {
		DataSet d = from(scope, values);
		return d.getStandardDeviation();
	}

	@operator(value = "geometric_mean", expected_content_type = { IType.INT, IType.FLOAT })
	@doc(value = "the geometric mean of the elements of the operand. See <A href=\"http://en.wikipedia.org/wiki/Geometric_mean\">Geometric_mean</A> for more details.", comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.", special_cases = { "" }, examples = { "geometric_mean ([4.5, 3.5, 5.5, 7.0]) --: 4.962326343467649" }, see = {
		"mean", "median", "harmonic_mean" })
	public static Double opGeomMean(final IScope scope, final IContainer values) {
		DataSet d = from(scope, values);
		return d.getGeometricMean();
	}

	@operator(value = "harmonic_mean", expected_content_type = { IType.INT, IType.FLOAT })
	@doc(value = "the harmonic mean of the elements of the operand. See <A href=\"http://en.wikipedia.org/wiki/Harmonic_mean\">Harmonic_mean</A> for more details.", comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.", special_cases = { "" }, examples = { "	harmonic_mean ([4.5, 3.5, 5.5, 7.0]) --: 4.804159445407279" }, see = {
		"mean", "median", "geometric_mean" })
	public static Double opHarmonicMean(final IScope scope, final IContainer values) {
		DataSet d = from(scope, values);
		return d.getHarmonicMean();
	}

	@operator(value = "variance", expected_content_type = { IType.INT, IType.FLOAT })
	@doc(value = "the variance of the elements of the operand. See <A href=\"http://en.wikipedia.org/wiki/Variance\">Variance</A> for more details.", comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded. ", examples = { "variance ([4.5, 3.5, 5.5, 7.0]) --: 1.671875	" }, see = {
		"mean", "median" })
	public static Double opVariance(final IScope scope, final IContainer values) {
		DataSet d = from(scope, values);
		return d.getVariance();
	}

	@operator(value = "mean_deviation", expected_content_type = { IType.INT, IType.FLOAT })
	@doc(value = "the deviation from the mean of all the elements of the operand. See <A href= \"http://en.wikipedia.org/wiki/Absolute_deviation\" >Mean_deviation</A> for more details.", comment = "The operator casts all the numerical element of the list into float. The elements that are not numerical are discarded.", examples = { "mean_deviation ([4.5, 3.5, 5.5, 7.0]) --: 1.125" }, see = {
		"mean", "standard_deviation" })
	public static Double opMeanDeviation(final IScope scope, final IContainer values) {
		DataSet d = from(scope, values);
		return d.getMeanDeviation();
	}

	@operator(value = { "frequency_of" }, iterator = true, index_type = ITypeProvider.SECOND_CONTENT_TYPE, content_type = IType.INT)
	@doc(value = "Returns a map with keys equal to the application of the right-hand argument (like collect) and values equal to the frequency of this key (i.e. how many times it has been obtained)", comment = "", examples = { "[ag1, ag2, ag3, ag4] frequency_of each.size 	--:   will return the different sizes as keys and the number of agents of this size as values" }, see = "as_map")
	public static GamaMap frequencyOf(final IScope scope, final IContainer original, final IExpression filter)
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

	@operator(value = "corR", can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(value = "returns the Pearson correlation coefficient of two given vectors (right-hand operands) in given variable  (left-hand operand).", special_cases = "if the lengths of two vectors in the right-hand aren't equal, returns 0", examples = {
		"list X <- [2, 3, 1];", "list Y <- [2, 12, 4];", "float corResult <- 0.0;", "corResult <- corR(X, Y);",
		"write corResult; // -> 0.755928946018454" })
	public static Object getCorrelationR(final IScope scope, final IContainer l1, final IContainer l2)
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

	@operator(value = "meanR", can_be_const = true, type = ITypeProvider.FIRST_CONTENT_TYPE)
	@doc(value = "returns the mean value of given vector (right-hand operand) in given variable  (left-hand operand).", examples = {
		"list X <- [2, 3, 1];", "list Y <- [2, 12, 4];", "float meanResult <- 0.0;", "meanResult <- meanR(X);",
		"write meanResult; // -> 2.0" })
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

	@operator(value = "R_compute", can_be_const = true, content_type = IType.LIST, index_type = IType.STRING)
	@doc(value = "returns the value of the last left-hand operand of given R file (right-hand operand) in given vector  (left-hand operand).", examples = {
		"list result;", "result <- R_compute('C:/YourPath/Correlation.R');", "Correlation.R file:", "x <- c(1, 2, 3)",
		"y <- c(1, 2, 4)", "result <- cor(x, y)", "Output:", "result::[0.981980506061966]" })
	public static GamaMap opRFileEvaluate(final IScope scope, final String RFile) throws GamaRuntimeException,
		RCallerParseException, RCallerExecutionException {
		try {
			// Call R
			RCaller caller = new RCaller();

			String RPath = ((IGamaFile) GamaPreferences.LIB_R.value(scope)).getPath();
			caller.setRscriptExecutable(RPath);
			// caller.setRscriptExecutable("\"" + RPath + "\"");
			// if(java.lang.System.getProperty("os.name").startsWith("Mac"))
			// {
			// caller.setRscriptExecutable(RPath);
			// }

			RCode c = new RCode();
			GamaList R_statements = new GamaList<String>();

			// tmthai.begin----------------------------------------------------------------------------
			String fullPath = scope.getSimulationScope().getModel().getRelativeFilePath(RFile, true);
			if ( DEBUG ) {
				GuiUtils.debug("Stats.R_compute.RScript:" + RPath);
				GuiUtils.debug("Stats.R_compute.RFile:" + RFile);
				GuiUtils.debug("Stats.R_compute.fullPath:" + fullPath);
			}

			// FileReader fr = new FileReader(RFile);
			FileReader fr = new FileReader(fullPath);
			// tmthai.end----------------------------------------------------------------------------

			BufferedReader br = new BufferedReader(fr);
			String statement;
			while ((statement = br.readLine()) != null) {
				c.addRCode(statement);
				R_statements.add(statement);
				// java.lang.System.out.println(statement);
				if ( DEBUG ) {
					GuiUtils.debug("Stats.R_compute.statement:" + statement);
				}

			}

			fr.close();

			caller.setRCode(c);

			GamaMap<String, IList> result = new GamaMap();

			String var = computeVariable(R_statements.get(R_statements.length(scope) - 1).toString());
			caller.runAndReturnResult(var);
			for ( String name : caller.getParser().getNames() ) {
				Object[] results = null;
				results = caller.getParser().getAsStringArray(name);
				// for (int i = 0; i < results.length; i++) {
				// java.lang.System.out.println(results[i]);
				// }
				if ( DEBUG ) {
					GuiUtils.debug("Stats.R_compute_param.caller.Name: '" + name + "' length: " + results.length +
						" - Value: " + results.toString());
				}
				result.put(name, new GamaList(results));
			}
			if ( DEBUG ) {
				GuiUtils.debug("Stats.R_compute.return:" + result.toGaml());
			}
			return result;

		} catch (Exception ex) {

			throw GamaRuntimeException.error("RCallerExecutionException " + ex.getMessage());
		}
	}

	@operator(value = "R_compute_param", can_be_const = true, content_type = IType.LIST, index_type = IType.STRING)
	@doc(value = "returns the value of the last left-hand operand of given R file (right-hand operand) in given vector  (left-hand operand), R file (first right-hand operand) reads the vector (second right-hand operand) as the parameter vector", examples = {
		"list X <- [2, 3, 1];", "list result;", "result <- R_compute_param('C:/YourPath/AddParam.R', X);",
		"write result at 0;", "AddParam.R file:", "v1 <- vectorParam[1]", "v2<-vectorParam[2]", "v3<-vectorParam[3]",
		"result<-v1+v2+v3", "Output:", "result::[10]" })
	public static GamaMap operateRFileEvaluate(final IScope scope, final String RFile, final IContainer param)
		throws GamaRuntimeException, RCallerParseException, RCallerExecutionException {
		if ( param.length(scope) == 0 ) { throw GamaRuntimeException.error("Missing Parameter Exception"); }
		try {
			// Call R
			RCaller caller = new RCaller();

			String RPath = ((IGamaFile) GamaPreferences.LIB_R.value(scope)).getPath();
			caller.setRscriptExecutable(RPath);
			// caller.setRscriptExecutable("\"" + RPath + "\"");
			// if(java.lang.System.getProperty("os.name").startsWith("Mac"))
			// {
			// caller.setRscriptExecutable(RPath);
			// }

			double[] vectorParam = new double[param.length(scope)];

			int k = 0;
			for ( Object o : param.iterable(scope) ) {
				vectorParam[k++] = Double.parseDouble(o.toString());
			}

			RCode c = new RCode();
			// Adding the parameters
			c.addDoubleArray("vectorParam", vectorParam);

			// Adding the codes in file
			GamaList R_statements = new GamaList<String>();

			// tmthai.begin----------------------------------------------------------------------------
			String fullPath = scope.getSimulationScope().getModel().getRelativeFilePath(RFile, true);
			if ( DEBUG ) {
				GuiUtils.debug("Stats.R_compute_param.RScript:" + RPath);
				GuiUtils.debug("Stats.R_compute_param.Param:" + vectorParam.toString());
				GuiUtils.debug("Stats.R_compute_param.RFile:" + RFile);
				GuiUtils.debug("Stats.R_compute_param.fullPath:" + fullPath);
			}

			// FileReader fr = new FileReader(RFile);
			FileReader fr = new FileReader(fullPath);
			// tmthai.end----------------------------------------------------------------------------

			BufferedReader br = new BufferedReader(fr);
			String statement;

			while ((statement = br.readLine()) != null) {
				c.addRCode(statement);
				R_statements.add(statement);
				// java.lang.System.out.println(statement);
			}

			fr.close();
			caller.setRCode(c);

			GamaMap<String, IList> result = new GamaMap();

			String var = computeVariable(R_statements.get(R_statements.length(scope) - 1).toString());
			caller.runAndReturnResult(var);

			// DEBUG:
			// java.lang.System.out.println("Name: '" + R_statements.length(scope) + "'");
			if ( DEBUG ) {
				GuiUtils.debug("Stats.R_compute_param.R_statements.length: '" + R_statements.length(scope) + "'");
			}

			for ( String name : caller.getParser().getNames() ) {
				Object[] results = null;
				results = caller.getParser().getAsStringArray(name);
				// java.lang.System.out.println("Name: '" + name + "'");
				if ( DEBUG ) {
					GuiUtils.debug("Stats.R_compute_param.caller.Name: '" + name + "' length: " + results.length +
						" - Value: " + results.toString());
				}

				// for (int i = 0; i < results.length; i++) {
				// //java.lang.System.out.println(results[i]);
				// if (DEBUG) GuiUtils.debug(results[i].toString());
				// //java.lang.System.out.println("Name: '" + name + "'");
				// }
				result.put(name, new GamaList(results));
			}

			if ( DEBUG ) {
				GuiUtils.debug("Stats.R_compute_param.return:" + result.toGaml());
			}

			return result;

		} catch (Exception ex) {

			throw GamaRuntimeException.error("RCallerExecutionException " + ex.getMessage());
		}
	}

	private static String computeVariable(final String string) {
		String[] tokens = string.split("<-");
		return tokens[0];
	}

}
