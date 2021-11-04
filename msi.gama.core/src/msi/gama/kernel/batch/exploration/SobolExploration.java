package msi.gama.kernel.batch.exploration;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.moeaframework.core.PRNG;
import org.moeaframework.util.sequence.Saltelli;
import org.moeaframework.util.sequence.Sequence;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.IType;


/**
 * 
 * 
 * @author kevinchapuis
 *
 */
@symbol (
		name = IKeyword.SOBOL,
		kind = ISymbolKind.BATCH_METHOD,
		with_sequence = false,
		concept = { IConcept.BATCH, IConcept.ALGORITHM })
@inside ( kinds = { ISymbolKind.EXPERIMENT })
@facets (
		value = { 
			@facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = false,
				internal = true,
				doc = @doc ("The name of the method. For internal use only")
			),
			@facet (
				name = SobolExploration.SAMPLE_SIZE,
				type = IType.ID,
				optional = false,
				doc = @doc ("The size of the sample")
			),
			@facet(
				name = IKeyword.BATCH_OUTPUTS,
				type = IType.LIST,
				of = IType.STRING,
				optional = false,
				doc = @doc ("The list of output variables to analyse through sobol indexes")
			)
		},
		omissible = IKeyword.NAME
		)
@doc (
		value = "This algorithm runs a Sobol exploration",
		usages = { 
			@usage (
				value = "For example: ",
				examples = { @example (
						value = "method sobol facet_1:fv; ",
						isExecutable = false) }
			) 
		}
		)
public class SobolExploration extends AExplorationAlgorithm {

	protected static final String SAMPLE_SIZE = "sample";
	protected int sample_size;
	
	protected List<ParametersSet> currentParametersSet;
	
	public SobolExploration(IDescription desc) { super(desc); }

	// ----------------------------------------------------------------- //
	
	@Override
	public void explore(final IScope scope) throws GamaRuntimeException {
		List<ParametersSet> sets = new ArrayList<>();
		sets.add(new ParametersSet());
		final List<ParametersSet> solutions = buildParameterSets(scope,sets, 0);
		currentExperiment.launchSimulationsWithSolution(solutions);
	}
	
	@Override
	public void setChildren(Iterable<? extends ISymbol> children) { }
	
	// ----------------------------------------------------------------- //
	
	public List<ParametersSet> buildParameterSets(IScope scope, List<ParametersSet> sets, int index) {
		this.sample_size = Cast.asInt(scope, getFacet(SAMPLE_SIZE).value(scope));
		
		final List<IParameter.Batch> parameters = currentExperiment.getParametersToExplore();
		this.sample_size *= (2 * parameters.size() + 2);
		
		Sequence seq = new Saltelli();
		PRNG.setRandom(scope.getRandom().getGenerator());
		
		double[][] samples = seq.generate(sample_size, parameters.size());
		
		for (int i = 0; i < sample_size; i++) {
			
			ParametersSet origi = new ParametersSet();
			origi = addParameterValue(scope,origi,parameters.get(0),samples[i][0]); 

			for (int j = 1; j < parameters.size(); j++) {
				origi = addParameterValue(scope,origi,parameters.get(j),samples[i][j]);
			}

			sets.add(origi);
		
		}
		
		currentParametersSet = sets;
		
		return sets;
	}
	
	/**
	 * Apply the Sobol decomposition to various input parameter type
	 * 
	 * TODO : validate that Sobol decomposition can be applied to any discrete variable
	 * @see https://stats.stackexchange.com/questions/190843/sobol-indices-for-discrete-variables
	 *  
	 * @param scope
	 * @param set
	 * @param var
	 * @param sobolDecomposition
	 * @return
	 */
	private ParametersSet addParameterValue(IScope scope, ParametersSet set, Batch var, double sobolDecomposition) {
		switch (var.getType().id()) {
			case IType.INT:
				int intValue = Cast.asInt(scope, var.getMinValue(scope));
				int maxIntValue = Cast.asInt(scope, var.getMaxValue(scope));
				int sobolIValue = Math.round(Math.round(intValue + sobolDecomposition * (maxIntValue - intValue)));
				set.put(var.getName(), sobolIValue);
				return set;
			case IType.FLOAT:
				double floatValue = Cast.asFloat(scope, var.getMinValue(scope));
				double maxFloatValue = Cast.asFloat(scope, var.getMaxValue(scope));
				double sobolFValue = floatValue + sobolDecomposition * (maxFloatValue - floatValue);
				set.put(var.getName(), sobolFValue);
				return set;
			case IType.DATE:
				GamaDate dateValue = GamaDateType.staticCast(scope, var.getMinValue(scope), null, false);
				GamaDate maxDateValue = GamaDateType.staticCast(scope, var.getMaxValue(scope), null, false);
				GamaDate sobolDValue = dateValue.plus( dateValue.getTemporal().until(maxDateValue, ChronoUnit.SECONDS) 
						* sobolDecomposition, ChronoUnit.SECONDS );
				set.put(var.getName(), sobolDValue);
				return set;
			case IType.POINT:
				GamaPoint pointValue = Cast.asPoint(scope, var.getMinValue(scope));
				GamaPoint maxPointValue = Cast.asPoint(scope, var.getMaxValue(scope));
				
				double sobolPXValue = pointValue.getX() + sobolDecomposition * (maxPointValue.getX() - pointValue.getX());
				double sobolPYValue = pointValue.getY() + sobolDecomposition * (maxPointValue.getY() - pointValue.getY());
				double sobolPZValue = pointValue.getZ() + sobolDecomposition * (maxPointValue.getZ() - pointValue.getZ());
				set.put(var.getName(), new GamaPoint(sobolPXValue,sobolPYValue,sobolPZValue));
				return set;
			case IType.BOOL:
				set.put(var.getName(), sobolDecomposition>0.5?true:false);
				return set;
			default:
				GamaRuntimeException.error("Trying to add a variable of unknown type "+var.getType().id()+" to a parameter set", scope);
				return set;
		}
	}

	// ------------------------------------------------------------------- //
	// 					COPY PAST FROM MOEAFRAMEWORK					   //
	
	/**
	 * Output from the original parameters.
	 */
	private double[] A;

	/**
	 * Output from the resampled parameters.
	 */
	private double[] B;

	/**
	 * Output from the original samples where the j-th parameter is replaced by
	 * the corresponding resampled parameter.
	 */
	private double[][] C_A;

	/**
	 * Output from the resampled samples where the j-th parameter is replaced by
	 * the corresponding original parameter.
	 */
	private double[][] C_B;
	
	
	
	/**
	 * Returns the first-order confidence interval of the i-th parameter.  The
	 * arguments to this method mirror the arguments to
	 * {@link #computeFirstOrder}.
	 * 
	 * @param a0 the output from the first independent samples
	 * @param a1 the output from the samples produced by swapping the i-th
	 *        parameter in the first independent samples with the i-th parameter
	 *        from the second independent samples
	 * @param a2 the output from the second independent samples
	 * @param nsample the number of samples
	 * @param nresample the number of resamples used when calculating the
	 *        confidence interval
	 * @return the first-order confidence interval of the i-th parameter
	 */
	private double computeFirstOrderConfidence(double[] a0, double[] a1,
			double[] a2, int nsample, int nresample) {
		double[] b0 = new double[nsample];
		double[] b1 = new double[nsample];
		double[] b2 = new double[nsample];
		double[] s = new double[nresample];

		for (int i = 0; i < nresample; i++) {
			for (int j = 0; j < nsample; j++) {
				int index = PRNG.nextInt(nsample);

				b0[j] = a0[index];
				b1[j] = a1[index];
				b2[j] = a2[index];
			}

			s[i] = computeFirstOrder(b0, b1, b2, nsample);
		}

		double ss = Arrays.stream(s).sum() / nresample;
		double sss = 0.0;
		
		for (int i = 0; i < nresample; i++) {
			sss += Math.pow(s[i] - ss, 2.0);
		}

		return 1.96 * Math.sqrt(sss / (nresample - 1));
	}

	/**
	 * Returns the first-order sensitivity of the i-th parameter.  Note how
	 * the contents of the array {@code a1} specify the parameter being
	 * analyzed.
	 * 
	 * @param a0 the output from the first independent samples
	 * @param a1 the output from the samples produced by swapping the i-th
	 *        parameter in the first independent samples with the i-th parameter
	 *        from the second independent samples
	 * @param a2 the output from the second independent samples
	 * @param nsample the number of samples
	 * @return the first-order sensitivity of the i-th parameter
	 */
	private double computeFirstOrder(double[] a0, double[] a1,
			double[] a2, int nsample) {
		double c = 0.0;
		for (int i = 0; i < nsample; i++) {
			c += a0[i];
		}
		c /= nsample;

		double tmp1 = 0.0;
		double tmp2 = 0.0;
		double tmp3 = 0.0;
		double EY2 = 0.0;

		for (int i = 0; i < nsample; i++) {
			EY2 += (a0[i] - c) * (a2[i] - c);
			tmp1 += (a2[i] - c) * (a2[i] - c);
			tmp2 += (a2[i] - c);
			tmp3 += (a1[i] - c) * (a2[i] - c);
		}

		EY2 /= nsample;

		double V = (tmp1 / (nsample - 1)) - Math.pow(tmp2 / nsample, 2.0);
		double U = tmp3 / (nsample - 1);

		return (U - EY2) / V;
	}

	/**
	 * Returns the total-order sensitivity of the i-th parameter.  Note how
	 * the contents of the array {@code a1} specify the parameter being
	 * analyzed.
	 * 
	 * @param a0 the output from the first independent samples
	 * @param a1 the output from the samples produced by swapping the i-th
	 *        parameter in the first independent samples with the i-th parameter
	 *        from the second independent samples
	 * @param a2 the output from the second independent samples
	 * @param nsample the number of samples
	 * @return the total-order sensitivity of the i-th parameter
	 */
	private double computeTotalOrder(double[] a0, double[] a1,
			double[] a2, int nsample) {
		double c = 0.0;
		
		for (int i = 0; i < nsample; i++) {
			c += a0[i];
		}
		
		c /= nsample;

		double tmp1 = 0.0;
		double tmp2 = 0.0;
		double tmp3 = 0.0;

		for (int i = 0; i < nsample; i++) {
			tmp1 += (a0[i] - c) * (a0[i] - c);
			tmp2 += (a0[i] - c) * (a1[i] - c);
			tmp3 += (a0[i] - c);
		}

		double EY2 = Math.pow(tmp3 / nsample, 2.0);
		double V = (tmp1 / (nsample - 1)) - EY2;
		double U = tmp2 / (nsample - 1);

		return 1.0 - ((U - EY2) / V);
	}

	/**
	 * Returns the total-order confidence interval of the i-th parameter.  The
	 * arguments to this method mirror the arguments to
	 * {@link #computeTotalOrder}.
	 * 
	 * @param a0 the output from the first independent samples
	 * @param a1 the output from the samples produced by swapping the i-th
	 *        parameter in the first independent samples with the i-th parameter
	 *        from the second independent samples
	 * @param a2 the output from the second independent samples
	 * @param nsample the number of samples
	 * @param nresample the number of resamples used when calculating the
	 *        confidence interval
	 * @return the total-order confidence interval of the i-th parameter
	 */
	private double computeTotalOrderConfidence(double[] a0, double[] a1,
			double[] a2, int nsample, int nresample) {
		double[] b0 = new double[nsample];
		double[] b1 = new double[nsample];
		double[] b2 = new double[nsample];
		double[] s = new double[nresample];

		for (int i = 0; i < nresample; i++) {
			for (int j = 0; j < nsample; j++) {
				int index = PRNG.nextInt(nsample);

				b0[j] = a0[index];
				b1[j] = a1[index];
				b2[j] = a2[index];
			}

			s[i] = computeTotalOrder(b0, b1, b2, nsample);
		}

		double ss = Arrays.stream(s).sum() / nresample;
		double sss = 0.0;
		
		for (int i = 0; i < nresample; i++) {
			sss += Math.pow(s[i] - ss, 2.0);
		}

		return 1.96 * Math.sqrt(sss / (nresample - 1));
	}

	/**
	 * Returns the second-order sensitivity of the i-th and j-th parameters.  
	 * Note how the contents of the arrays {@code a1}, {@code a2}, and
	 * {@code a3} specify the two parameters being analyzed.
	 * 
	 * @param a0 the output from the first independent samples
	 * @param a1 the output from the samples produced by swapping the i-th
	 *        parameter in the second independent samples with the i-th
	 *        parameter from the first independent samples
	 * @param a2 the output from the samples produced by swapping the j-th
	 *        parameter in the first independent samples with the j-th parameter
	 *        from the second independent samples
	 * @param a3 the output from the samples produced by swapping the i-th
	 *        parameter in the first independent samples with the i-th parameter
	 *        from the second independent samples
	 * @param a4 the output from the second independent samples
	 * @param nsample the number of samples
	 * @param nresample the number of resamples used when calculating the
	 *        confidence interval
	 * @return the second-order sensitivity of the i-th and j-th parameters
	 */
	private double computeSecondOrder(double[] a0, double[] a1,
			double[] a2, double[] a3, double[] a4, int nsample) {
		double c = 0.0;
		
		for (int i = 0; i < nsample; i++) {
			c += a0[i];
		}
		
		c /= nsample;

		double EY = 0.0;
		double EY2 = 0.0;
		double tmp1 = 0.0;
		double tmp2 = 0.0;
		double tmp3 = 0.0;
		double tmp4 = 0.0;
		double tmp5 = 0.0;

		for (int i = 0; i < nsample; i++) {
			EY += (a0[i] - c) * (a4[i] - c);
			EY2 += (a1[i] - c) * (a3[i] - c);
			tmp1 += (a1[i] - c) * (a1[i] - c);
			tmp2 += (a1[i] - c);
			tmp3 += (a1[i] - c) * (a2[i] - c);
			tmp4 += (a2[i] - c) * (a4[i] - c);
			tmp5 += (a3[i] - c) * (a4[i] - c);
		}

		EY /= nsample;
		EY2 /= nsample;

		double V = (tmp1 / (nsample - 1)) - Math.pow(tmp2 / nsample, 2.0);
		double Vij = (tmp3 / (nsample - 1)) - EY2;
		double Vi = (tmp4 / (nsample - 1)) - EY;
		double Vj = (tmp5 / (nsample - 1)) - EY2;

		return (Vij - Vi - Vj) / V;
	}

	/**
	 * Returns the second-order confidence interval of the i-th and j-th
	 * parameters.  The arguments to this method mirror the arguments to
	 * {@link #computeSecondOrder}.
	 * 
	 * @param a0 the output from the first independent samples
	 * @param a1 the output from the samples produced by swapping the i-th
	 *        parameter in the second independent samples with the i-th
	 *        parameter from the first independent samples
	 * @param a2 the output from the samples produced by swapping the j-th
	 *        parameter in the first independent samples with the j-th parameter
	 *        from the second independent samples
	 * @param a3 the output from the samples produced by swapping the i-th
	 *        parameter in the first independent samples with the i-th parameter
	 *        from the second independent samples
	 * @param a4 the output from the second independent samples
	 * @param nsample the number of samples
	 * @return the second-order confidence interval of the i-th and j-th
	 *         parameters
	 */
	private double computeSecondOrderConfidence(double[] a0,
			double[] a1, double[] a2, double[] a3, double[] a4, int nsample,
			int nresample) {
		double[] b0 = new double[nsample];
		double[] b1 = new double[nsample];
		double[] b2 = new double[nsample];
		double[] b3 = new double[nsample];
		double[] b4 = new double[nsample];
		double[] s = new double[nresample];

		for (int i = 0; i < nresample; i++) {
			for (int j = 0; j < nsample; j++) {
				int index = PRNG.nextInt(nsample);

				b0[j] = a0[index];
				b1[j] = a1[index];
				b2[j] = a2[index];
				b3[j] = a3[index];
				b4[j] = a4[index];
			}

			s[i] = computeSecondOrder(b0, b1, b2, b3, b4, nsample);
		}

		double ss = Arrays.stream(s).sum() / nresample;
		double sss = 0.0;
		
		for (int i = 0; i < nresample; i++) {
			sss += Math.pow(s[i] - ss, 2.0);
		}

		return 1.96 * Math.sqrt(sss / (nresample - 1));
	}
	
}
