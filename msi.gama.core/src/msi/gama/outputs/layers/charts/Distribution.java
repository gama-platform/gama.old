/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.Distribution.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.math.BigDecimal;
import java.util.Arrays;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Maths;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@SuppressWarnings ({ "rawtypes" })
public class Distribution {

	public static IMap computeDistrib2d(final IScope scope, final IList lvaluex, final IList lvaluey,
			final int nbBarresx, final double vminx, final double vmaxx, final int nbBarresy, final double vminy,
			final double vmaxy) {
		int len = lvaluex.length(scope);
		final int leny = lvaluey.length(scope);
		len = Math.min(len, leny);
		final double[] doublelistx = new double[len];
		final double[] doublelisty = new double[len];
		final double[] doublelistorx = new double[len];
		final double[] doublelistory = new double[len];
		final int[][] distribInts = new int[nbBarresx][nbBarresy];
		final int[] distribParamsx = new int[2];
		final int[] distribParamsy = new int[2];
		final String[] distribLegendx = new String[nbBarresx];
		final String[] distribLegendy = new String[nbBarresy];

		double newminIntx = 0;
		double deuxpuissancekx = 0;
		double newminInty = 0;
		double deuxpuissanceky = 0;

		// x

		for (int i = 0; i < len; i++) {
			doublelistx[i] = Cast.asFloat(scope, lvaluex.get(i));
			doublelistorx[i] = Cast.asFloat(scope, lvaluex.get(i));
		}

		deuxpuissancekx = (vmaxx - vminx) / nbBarresx;
		newminIntx = vminx;

		distribParamsx[0] = 0;
		distribParamsx[1] = 0;

		// y

		for (int i = 0; i < len; i++) {
			doublelisty[i] = Cast.asFloat(scope, lvaluey.get(i));
			doublelistory[i] = Cast.asFloat(scope, lvaluey.get(i));
		}

		deuxpuissanceky = (vmaxy - vminy) / nbBarresy;
		newminInty = vminy;

		distribParamsy[0] = 0;
		distribParamsy[1] = 0;

		final double[] thresholdsx = new double[nbBarresx + 1];
		final double[] thresholdsy = new double[nbBarresy + 1];

		double preval = newminIntx;
		double postval = 0;

		for (int i = 0; i < nbBarresx; i++) {
			thresholdsx[i] = preval;
			postval = preval;
			preval = preval + deuxpuissancekx;
			distribLegendx[i] = "[" + postval + ":" + preval + "]";
		}

		preval = newminInty;
		postval = 0;

		for (int i = 0; i < nbBarresy; i++) {
			thresholdsy[i] = preval;
			postval = preval;
			preval = preval + deuxpuissanceky;
			distribLegendy[i] = "[" + postval + ":" + preval + "]";
		}

		for (int i = 0; i < nbBarresx; i++) {
			for (int j = 0; j < nbBarresy; j++) {
				distribInts[i][j] = 0;
			}
		}
		int nx, ny;
		for (int k = 0; k < len; k++) {
			nx = 0;
			ny = 0;
			while (thresholdsx[nx + 1] < doublelistorx[k] && nx + 2 < nbBarresx) {
				nx++;
			}
			while (thresholdsy[ny + 1] < doublelistory[k] && ny + 2 < nbBarresy) {
				ny++;
			}
			distribInts[nx][ny]++;
		}

		final IList[] mytlist = new IList[nbBarresx];
		for (int i = 0; i < nbBarresx; i++) {
			final IList vallists = GamaListFactory.create(scope, Types.INT, distribInts[i]);
			// DEBUG.LOG("add "+distribInts[i]);
			mytlist[i] = vallists;
		}
		// DEBUG.LOG("fin " + mytlist);
		final IList vallist = GamaListFactory.create(scope, Types.LIST, mytlist);

		final IMap<String, Object> result = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
		final IList parlist = GamaListFactory.create(scope, Types.INT, distribParamsx);
		final IList leglist = GamaListFactory.create(scope, Types.STRING, distribLegendx);
		final IList parlisty = GamaListFactory.create(scope, Types.INT, distribParamsy);
		final IList leglisty = GamaListFactory.create(scope, Types.STRING, distribLegendy);
		result.addValueAtIndex(scope, "values", vallist);
		result.addValueAtIndex(scope, "legendx", leglist);
		result.addValueAtIndex(scope, "parlistx", parlist);
		result.addValueAtIndex(scope, "legendy", leglisty);
		result.addValueAtIndex(scope, "parlisty", parlisty);

		return result;

	}

	public static IMap computeDistrib2d(final IScope scope, final IList lvaluex, final IList lvaluey,
			final int nbBarresx, final int nbBarresy) {
		int len = lvaluex.length(scope);
		final int leny = lvaluey.length(scope);
		len = Math.min(len, leny);
		final double[] doublelistx = new double[len];
		final double[] doublelisty = new double[len];
		final double[] doublelistorx = new double[len];
		final double[] doublelistory = new double[len];
		final int[][] distribInts = new int[nbBarresx][nbBarresy];
		final int[] distribParamsx = new int[2];
		final int[] distribParamsy = new int[2];
		final String[] distribLegendx = new String[nbBarresx];
		final String[] distribLegendy = new String[nbBarresy];

		double newminIntx = 0;
		double deuxpuissancekx = 0;
		double newminInty = 0;
		double deuxpuissanceky = 0;

		// x

		for (int i = 0; i < len; i++) {
			doublelistx[i] = Cast.asFloat(scope, lvaluex.get(i));
			doublelistorx[i] = Cast.asFloat(scope, lvaluex.get(i));
		}
		Arrays.sort(doublelistx);
		double min = doublelistx[0];
		double max = doublelistx[len - 1];
		int twoExponent = 0;
		int startMultiplier = 0;

		if (min == max) {
			twoExponent = 0;
			startMultiplier = (int) min;
			deuxpuissancekx = (float) Math.pow(2, twoExponent);
			newminIntx = (int) min;

		}

		else {

			final double intermin = min;
			final double intermax = max;

			final float minInt = (float) intermin;
			final float maxInt = (float) intermax;
			double N = Math.log10((maxInt - minInt) / (double) (nbBarresx - 1)) / Math.log10(2);
			// DEBUG.LOG("Ncalc: maxmin: "+maxInt+"/"+minInt+" N "+N);
			twoExponent = (int) N;
			deuxpuissancekx = (float) Math.pow(2, twoExponent);
			newminIntx = deuxpuissancekx * (int) (minInt / deuxpuissancekx);
			startMultiplier = (int) (minInt / deuxpuissancekx);
			if (newminIntx > min) {
				newminIntx = deuxpuissancekx * (int) (minInt / deuxpuissancekx - 1);
				startMultiplier = (int) (minInt / deuxpuissancekx - 1);
			}
			if (newminIntx + nbBarresx * deuxpuissancekx <= max) {
				N = N + 1;
				twoExponent = (int) N;
				deuxpuissancekx = (float) Math.pow(2, twoExponent);
				newminIntx = deuxpuissancekx * (int) (minInt / deuxpuissancekx);
				startMultiplier = (int) (minInt / deuxpuissancekx);
				if (newminIntx > min) {
					newminIntx = deuxpuissancekx * (int) (minInt / deuxpuissancekx - 1);
					startMultiplier = (int) (minInt / deuxpuissancekx - 1);
				}
			}

		}

		distribParamsx[0] = twoExponent;
		distribParamsx[1] = startMultiplier;

		// y

		for (int i = 0; i < len; i++) {
			doublelisty[i] = Cast.asFloat(scope, lvaluey.get(i));
			doublelistory[i] = Cast.asFloat(scope, lvaluey.get(i));
		}
		Arrays.sort(doublelisty);
		min = doublelisty[0];
		max = doublelisty[len - 1];
		twoExponent = 0;
		startMultiplier = 0;

		if (min == max) {
			twoExponent = 0;
			startMultiplier = (int) min;
			deuxpuissanceky = (float) Math.pow(2, twoExponent);
			newminInty = (int) min;

		}

		else {

			final double intermin = min;
			final double intermax = max;

			final float minInt = (float) intermin;
			final float maxInt = (float) intermax;
			double N = Math.log10((maxInt - minInt) / (double) (nbBarresy - 1)) / Math.log10(2);
			// DEBUG.LOG("Ncalc: maxmin: " + maxInt + "/" + minInt + "
			// N " + N);
			twoExponent = (int) N;
			deuxpuissanceky = (float) Math.pow(2, twoExponent);
			newminInty = deuxpuissanceky * (int) (minInt / deuxpuissanceky);
			startMultiplier = (int) (minInt / deuxpuissanceky);
			if (newminInty > min) {
				newminInty = deuxpuissanceky * (int) (minInt / deuxpuissanceky - 1);
				startMultiplier = (int) (minInt / deuxpuissanceky - 1);
			}
			if (newminInty + nbBarresy * deuxpuissanceky <= max) {
				N = N + 1;
				twoExponent = (int) N;
				deuxpuissanceky = (float) Math.pow(2, twoExponent);
				newminInty = deuxpuissanceky * (int) (minInt / deuxpuissanceky);
				startMultiplier = (int) (minInt / deuxpuissanceky);
				if (newminInty > min) {
					newminInty = deuxpuissanceky * (int) (minInt / deuxpuissanceky - 1);
					startMultiplier = (int) (minInt / deuxpuissanceky - 1);
				}
			}

		}

		distribParamsy[0] = twoExponent;
		distribParamsy[1] = startMultiplier;

		final double[] thresholdsx = new double[nbBarresx + 1];
		final double[] thresholdsy = new double[nbBarresy + 1];

		double preval = newminIntx;
		double postval = 0;

		for (int i = 0; i < nbBarresx; i++) {
			thresholdsx[i] = preval;
			postval = preval;
			preval = preval + deuxpuissancekx;
			distribLegendx[i] = "[" + postval + ":" + preval + "]";
		}

		preval = newminInty;
		postval = 0;

		for (int i = 0; i < nbBarresy; i++) {
			thresholdsy[i] = preval;
			postval = preval;
			preval = preval + deuxpuissanceky;
			distribLegendy[i] = "[" + postval + ":" + preval + "]";
		}

		for (int i = 0; i < nbBarresx; i++) {
			for (int j = 0; j < nbBarresy; j++) {
				distribInts[i][j] = 0;
			}
		}
		int nx, ny;
		for (int k = 0; k < len; k++) {
			nx = 0;
			ny = 0;
			while (thresholdsx[nx + 1] < doublelistorx[k] && nx + 2 < nbBarresx) {
				nx++;
			}
			while (thresholdsy[ny + 1] < doublelistory[k] && ny + 2 < nbBarresy) {
				ny++;
			}
			distribInts[nx][ny]++;
		}

		final IList[] mytlist = new IList[nbBarresx];
		for (int i = 0; i < nbBarresx; i++) {
			final IList vallists = GamaListFactory.create(scope, Types.INT, distribInts[i]);
			// DEBUG.LOG("add "+distribInts[i]);
			mytlist[i] = vallists;
		}
		// DEBUG.LOG("fin "+mytlist);
		final IList vallist = GamaListFactory.create(scope, Types.LIST, mytlist);

		final IMap<String, Object> result = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
		final IList parlist = GamaListFactory.create(scope, Types.INT, distribParamsx);
		final IList leglist = GamaListFactory.create(scope, Types.STRING, distribLegendx);
		final IList parlisty = GamaListFactory.create(scope, Types.INT, distribParamsy);
		final IList leglisty = GamaListFactory.create(scope, Types.STRING, distribLegendy);
		result.addValueAtIndex(scope, "values", vallist);
		result.addValueAtIndex(scope, "legendx", leglist);
		result.addValueAtIndex(scope, "parlistx", parlist);
		result.addValueAtIndex(scope, "legendy", leglisty);
		result.addValueAtIndex(scope, "parlisty", parlisty);

		return result;

	}

	@operator (
			value = { "distribution2d_of" },
			can_be_const = false,
			// index_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			index_type = IType.STRING,
			content_type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.CHART })
	@doc (
			value = "Discretize two lists of values into n bins (computes the bins from a numerical variable into n (default 10) bins. Returns a distribution map with the values (values key), the interval legends (legend key), the distribution parameters (params keys, for cumulative charts).",
			comment = "",
			examples = { @example (
					value = "distribution2d_of([1,1,2,12.5],10)",
					equals = "map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])",
					isExecutable = false) },
			see = "as_map")
	@no_test
	public static IMap Distribution2dOf(final IScope scope, final IContainer valuesx, final IContainer valuesy,
			final Integer nbbarsx, final Integer nbbarsy) throws GamaRuntimeException {

		if (valuesx == null) { return GamaMapFactory.create(Types.STRING, Types.LIST); }
		final IList lvaluex = Cast.asList(scope, valuesx);
		if (lvaluex.length(scope) < 1) { return GamaMapFactory.create(Types.STRING, Types.LIST); }

		int nbBarresx = 10;
		nbBarresx = nbbarsx.intValue();

		final IList lvaluey = Cast.asList(scope, valuesy);
		if (lvaluey.length(scope) < 1) { return GamaMapFactory.create(Types.STRING, Types.LIST); }

		int nbBarresy = 10;
		nbBarresy = nbbarsy.intValue();

		return computeDistrib2d(scope, lvaluex, lvaluey, nbBarresx, nbBarresy);

	}

	@operator (
			value = { "distribution2d_of" },
			can_be_const = false,
			// index_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			index_type = IType.STRING,
			content_type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.CHART })
	@doc (
			value = "Discretize two lists of values into n bins (computes the bins from a numerical variable into n (default 10) bins. Returns a distribution map with the values (values key), the interval legends (legend key), the distribution parameters (params keys, for cumulative charts).",
			comment = "",
			examples = { @example (
					value = "distribution2d_of([1,1,2,12.5],10)",
					equals = "map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])",
					isExecutable = false) },
			see = "as_map")
	@no_test
	public static IMap Distribution2dOf(final IScope scope, final IContainer valuesx, final IContainer valuesy,
			final Integer nbbarsx, final Double startvaluex, final Double endvaluex, final Integer nbbarsy,
			final Double startvaluey, final Double endvaluey) throws GamaRuntimeException {

		if (valuesx == null) { return GamaMapFactory.create(Types.STRING, Types.LIST); }
		final IList lvaluex = Cast.asList(scope, valuesx);
		if (lvaluex.length(scope) < 1) { return GamaMapFactory.create(Types.STRING, Types.LIST); }

		int nbBarresx = 10;
		nbBarresx = nbbarsx.intValue();

		final IList lvaluey = Cast.asList(scope, valuesy);
		if (lvaluey.length(scope) < 1) { return GamaMapFactory.create(Types.STRING, Types.LIST); }

		int nbBarresy = 10;
		nbBarresy = nbbarsy.intValue();
		double vminx = 0.0d;
		vminx = startvaluex.doubleValue();
		double vmaxx = 1.0d;
		vmaxx = endvaluex.doubleValue();
		double vminy = 0.0d;
		vminy = startvaluey.doubleValue();
		double vmaxy = 1.0d;
		vmaxy = endvaluey.doubleValue();

		return computeDistrib2d(scope, lvaluex, lvaluey, nbBarresx, vminx, vmaxx, nbBarresy, vminy, vmaxy);

	}

	@operator (
			value = { "distribution2d_of" },
			can_be_const = false,
			// index_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			index_type = IType.STRING,
			content_type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.CHART })
	@doc (
			value = "Discretize two lists of values into n bins (computes the bins from a numerical variable into n (default 10) bins. Returns a distribution map with the values (values key), the interval legends (legend key), the distribution parameters (params keys, for cumulative charts). Parameters can be (list), (list, nbbins) or (list,nbbins,valmin,valmax)",
			masterDoc = true,
			comment = "",
			examples = { @example (
					value = "distribution2d_of([1,1,2,12.5])",
					equals = "map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])",
					isExecutable = false) },
			see = "as_map")
	@no_test
	public static IMap Distribution2dOf(final IScope scope, final IContainer valuesx, final IContainer valuesy)
			throws GamaRuntimeException {

		if (valuesx == null) { return GamaMapFactory.create(Types.STRING, Types.LIST); }
		final IList lvaluex = Cast.asList(scope, valuesx);
		if (lvaluex.length(scope) < 1) { return GamaMapFactory.create(Types.STRING, Types.LIST); }
		final IList lvaluey = Cast.asList(scope, valuesy);
		if (lvaluey.length(scope) < 1) { return GamaMapFactory.create(Types.STRING, Types.LIST); }

		final int nbBarres = 10;

		return computeDistrib2d(scope, lvaluex, lvaluey, nbBarres, nbBarres);

	}

	public static IMap computeDistrib(final IScope scope, final IList lvalue, final int nbBarres) {
		final int len = lvalue.length(scope);
		final double[] doublelist = new double[lvalue.length(scope)];
		final int[] distribInts = new int[nbBarres];
		final int[] distribParams = new int[2];
		final String[] distribLegend = new String[nbBarres];

		for (int i = 0; i < lvalue.length(scope); i++) {
			doublelist[i] = Cast.asFloat(scope, lvalue.get(i));
		}
		Arrays.sort(doublelist);
		final double min = doublelist[0];
		final double max = doublelist[len - 1];
		int twoExponent = 0;
		int startMultiplier = 0;

		double newminInt = 0;
		double deuxpuissancek = 0;

		if (min == max) {
			twoExponent = 0;
			startMultiplier = (int) min;
			deuxpuissancek = (float) Math.pow(2, twoExponent);
			newminInt = (int) min;

		}

		else {

			final double intermin = min;
			/*
			 * if (min < 0) { intermin = intermin - 1; }
			 */
			// double intermax = max + 1;
			final double intermax = max;

			final float minInt = (float) intermin;
			final float maxInt = (float) intermax;
			double N = Math.log10((maxInt - minInt) / (double) (nbBarres - 1)) / Math.log10(2);
			// DEBUG.LOG("Ncalc: maxmin: " + maxInt + "/" + minInt + " N " + N);
			twoExponent = (int) N;
			deuxpuissancek = (float) Math.pow(2, twoExponent);
			newminInt = deuxpuissancek * (int) (minInt / deuxpuissancek);
			startMultiplier = (int) (minInt / deuxpuissancek);
			// DEBUG.LOG("Min "+min+" newmin "+newminInt+" startmult
			// "+startMultiplier);
			if (newminInt > min) {
				newminInt = deuxpuissancek * (int) (minInt / deuxpuissancek - 1);
				startMultiplier = (int) (minInt / deuxpuissancek - 1);
			}
			if (newminInt + nbBarres * deuxpuissancek <= max) {
				N = N + 1;
				twoExponent = (int) N;
				deuxpuissancek = (float) Math.pow(2, twoExponent);
				newminInt = deuxpuissancek * (int) (minInt / deuxpuissancek);
				startMultiplier = (int) (minInt / deuxpuissancek);
				if (newminInt > min) {
					newminInt = deuxpuissancek * (int) (minInt / deuxpuissancek - 1);
					startMultiplier = (int) (minInt / deuxpuissancek - 1);
				}
			}
			// DEBUG.LOG(" "+maxInt+"/"+minInt+" N "+N+ " twoexp
			// "+twoExponent+" maxv "+(newminInt+nbBarres*deuxpuissancek));

		}

		double preval = newminInt;
		double postval = 0;
		int nba = 0;
		int nbaprec = 0;
		for (int i = 0; i < nbBarres; i++) {
			if (i != 0) {
				preval = preval + deuxpuissancek;
			}
			postval = preval + deuxpuissancek;
			while (nba < len && doublelist[nba] < postval) {
				nba++;
			}

			distribInts[i] = nba - nbaprec;
			nbaprec = nba;
			distribLegend[i] = "[" + preval + ":" + postval + "]";
		}

		distribParams[0] = twoExponent;
		distribParams[1] = startMultiplier;

		final IMap<String, Object> result = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
		final IList vallist = GamaListFactory.create(scope, Types.INT, distribInts);
		final IList parlist = GamaListFactory.create(scope, Types.INT, distribParams);
		final IList leglist = GamaListFactory.create(scope, Types.STRING, distribLegend);
		result.addValueAtIndex(scope, "values", vallist);
		result.addValueAtIndex(scope, "legend", leglist);
		result.addValueAtIndex(scope, "parlist", parlist);

		return result;

	}

	public static IMap computeDistrib(final IScope scope, final IList lvalue, final int nbBarres, final double vmin,
			final double vmax) {
		final int len = lvalue.length(scope);
		final double[] doublelist = new double[lvalue.length(scope)];

		final int[] distribInts = new int[nbBarres];
		final int[] distribParams = new int[2];
		final String[] distribLegend = new String[nbBarres];

		final double deuxpuissancek = (vmax - vmin) / nbBarres;
		final double newminInt = vmin;

		for (int i = 0; i < lvalue.length(scope); i++) {
			doublelist[i] = Cast.asFloat(scope, lvalue.get(i));
		}
		Arrays.sort(doublelist);

		final int scale = BigDecimal.valueOf(deuxpuissancek).scale();

		double preval = newminInt;
		double postval = 0;
		int nba = 0;
		int nbaprec = 0;
		for (int i = 0; i < nbBarres; i++) {
			if (i != 0) {
				preval = preval + deuxpuissancek;
			}
			postval = preval + deuxpuissancek;
			while (nba < len && doublelist[nba] < postval) {
				nba++;
			}

			distribInts[i] = nba - nbaprec;
			nbaprec = nba;
			distribLegend[i] = "[" + Maths.round(preval, scale + 8) + ":" + Maths.round(postval, scale + 8) + "]";

		}

		distribParams[0] = 0;
		distribParams[1] = 0;

		final IMap<String, Object> result = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
		final IList vallist = GamaListFactory.create(scope, Types.INT, distribInts);
		final IList parlist = GamaListFactory.create(scope, Types.INT, distribParams);
		final IList leglist = GamaListFactory.create(scope, Types.STRING, distribLegend);
		result.addValueAtIndex(scope, "values", vallist);
		result.addValueAtIndex(scope, "legend", leglist);
		result.addValueAtIndex(scope, "parlist", parlist);

		return result;

	}

	@operator (
			value = { "distribution_of" },
			can_be_const = false,
			// index_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			index_type = IType.STRING,
			content_type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.CHART })
	@doc (
			value = "Discretize a list of values into n bins (computes the bins from a numerical variable into n (default 10) bins. Returns a distribution map with the values (values key), the interval legends (legend key), the distribution parameters (params keys, for cumulative charts).",
			comment = "",
			examples = { @example (
					value = "distribution_of([1,1,2,12.5],10)",
					equals = "map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])",
					isExecutable = false) },
			see = "as_map")
	@no_test
	public static IMap DistributionOf(final IScope scope, final IContainer values, final Integer nbbars)
			throws GamaRuntimeException {

		if (values == null) { return GamaMapFactory.create(Types.STRING, Types.LIST); }
		final IList lvalue = Cast.asList(scope, values);
		if (lvalue.length(scope) < 1) { return GamaMapFactory.create(Types.STRING, Types.LIST); }

		int nbBarres = 10;
		nbBarres = nbbars.intValue();

		return computeDistrib(scope, lvalue, nbBarres);

	}

	@operator (
			value = { "distribution_of" },
			can_be_const = false,
			// index_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			index_type = IType.STRING,
			content_type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.CHART })
	@doc (
			value = "Discretize a list of values into n bins (computes the bins from a numerical variable into n (default 10) bins. Returns a distribution map with the values (values key), the interval legends (legend key), the distribution parameters (params keys, for cumulative charts). Parameters can be (list), (list, nbbins) or (list,nbbins,valmin,valmax)",
			masterDoc = true,
			comment = "",
			examples = { @example (
					value = "distribution_of([1,1,2,12.5])",
					equals = "map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])",
					isExecutable = false) },
			see = "as_map")
	@no_test
	public static IMap DistributionOf(final IScope scope, final IContainer values) throws GamaRuntimeException {

		if (values == null) { return GamaMapFactory.create(Types.STRING, Types.LIST); }
		final IList lvalue = Cast.asList(scope, values);
		if (lvalue.length(scope) < 1) { return GamaMapFactory.create(Types.STRING, Types.LIST); }

		final int nbBarres = 10;

		return computeDistrib(scope, lvalue, nbBarres);

	}

	@operator (
			value = { "distribution_of" },
			can_be_const = false,
			// index_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			index_type = IType.STRING,
			content_type = IType.LIST,
			category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC, IConcept.CHART })
	@doc (
			value = "Discretize a list of values into n bins (computes the bins from a numerical variable into n (default 10) bins. Returns a distribution map with the values (values key), the interval legends (legend key), the distribution parameters (params keys, for cumulative charts).",
			masterDoc = false,
			comment = "",
			examples = { @example (
					value = "distribution_of([1,1,2,12.5])",
					equals = "map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])",
					isExecutable = false) },
			see = "as_map")
	@no_test
	public static IMap DistributionOf(final IScope scope, final IContainer values, final Integer nbbars,
			final Double startvalue, final Double endvalue) throws GamaRuntimeException {

		if (values == null) { return GamaMapFactory.create(Types.STRING, Types.LIST); }
		final IList lvalue = Cast.asList(scope, values);
		if (lvalue.length(scope) < 1) { return GamaMapFactory.create(Types.STRING, Types.LIST); }

		int nbBarres = 10;
		nbBarres = nbbars.intValue();
		double vmin = 0.0d;
		vmin = startvalue.doubleValue();
		double vmax = 1.0d;
		vmax = endvalue.doubleValue();

		return computeDistrib(scope, lvalue, nbBarres, vmin, vmax);

	}

}
