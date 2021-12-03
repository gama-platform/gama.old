package msi.gama.kernel.batch.exploration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.moeaframework.util.sequence.Saltelli;
import org.moeaframework.util.sequence.Sequence;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.FileUtils;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.experiment.ParameterAdapter;
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
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;


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
				doc = @doc ("The size of the sample for the sobol sequence")
			),
			@facet(
				name = IKeyword.BATCH_OUTPUTS,
				type = IType.LIST,
				of = IType.STRING,
				optional = false,
				doc = @doc ("The path to the file where the Sobol report will be written")
			),
			@facet(
				name = IKeyword.BATCH_REPORT,
				type = IType.STRING,
				optional = true,
				doc = @doc ("The list of output variables to analyse through sobol indexes")
			)
		},
		omissible = IKeyword.NAME
		)
@doc (
		value = "This algorithm runs a Sobol exploration - it has been built upon the moea framework at https://github.com/MOEAFramework/MOEAFramework",
		usages = { 
			@usage (
				value = "For example: ",
				examples = { @example (
						value = "method sobol sample_size:100; ",
						isExecutable = false) }
			) 
		}
		)
public class SobolExploration extends AExplorationAlgorithm {

	protected static final String SAMPLE_SIZE = "sample";
	protected int sample;
	protected int _sample;
	protected int _resample = 1000; // Bootstraping for confidence interval
	protected List<Batch> parameters;
	
	/* The parameter space defined by the Sobol sequence (Satteli sampling method) */
	protected List<ParametersSet> currentParametersSpace;
	/* All the outputs for each simulation */
	protected IMap<ParametersSet,Map<String,List<Object>>> res_outputs;
	/* Sobol indexes for every output of interest */
	private Map<String,Map<Batch,List<Double>>> sobolNumReport;
	
	public SobolExploration(IDescription desc) { super(desc); }

	// ----------------------------------------------------------------- //
	
	@SuppressWarnings("unchecked")
	@Override
	public void explore(final IScope scope) throws GamaRuntimeException {
		List<ParametersSet> solutions = currentParametersSpace == null ? buildParameterSets(scope, new ArrayList<>(), 0) : currentParametersSpace;
		if (solutions.size()!=_sample) {GamaRuntimeException.error("Saltelli sample should be "+_sample+" but is "+solutions.size(), scope);}
		/* Disable repetitions / repeat argument */
		currentExperiment.setSeeds(new Double[1]);
		if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue()) {
			res_outputs = currentExperiment.launchSimulationsWithSolution(solutions);
		} else {
			res_outputs = GamaMapFactory.create();
			for (ParametersSet sol : solutions) { res_outputs.put(sol,currentExperiment.launchSimulationsWithSolution(sol)); }
		}
		
		computeSobolIndexes(scope);
		
		if (hasFacet(IKeyword.BATCH_REPORT)) {
			String path_to = Cast.asString(scope, getFacet(IKeyword.BATCH_REPORT).value(scope));
			FileWriter fw;
			try {
				final File f = new File(FileUtils.constructAbsoluteFilePath(scope, path_to, false));
				final File parent = f.getParentFile();
				if (!parent.exists()) { parent.mkdirs(); }
				if (f.exists()) f.delete();
				f.createNewFile();
				fw = new FileWriter(f, false);
				fw.write(buildSobolReport());
			} catch (IOException e) {
				GamaRuntimeFileException.create(e, scope);
			}
		}
		
	}

	@Override
	public String getReport() {return buildSobolReport();}
	
	@Override
	public void setChildren(Iterable<? extends ISymbol> children) { }
	
	@Override
	public void addParametersTo(List<Batch> exp, BatchAgent agent) {
		super.addParametersTo(exp, agent);
		
		exp.add(new ParameterAdapter("Sample of parameter space:", IExperimentPlan.BATCH_CATEGORY_NAME, IType.STRING) {
				@Override public Object value() { return _sample; }
		});
		
		@SuppressWarnings("unchecked")
		final List<String> outputVals = GamaListFactory.create(agent.getScope(), Types.STRING, 
				Cast.asList(agent.getScope(), getOutputs().value(agent.getScope())));
		for (String var : outputVals) {
			exp.add(new ParameterAdapter("Sobol index for var:", IExperimentPlan.BATCH_CATEGORY_NAME, IType.STRING) {
				@Override public Object value() { return var; }
			});
			exp.add(new ParameterAdapter("First order SI: ", IExperimentPlan.BATCH_CATEGORY_NAME, IType.MAP) {
				@Override public Object value() { return sobolNumReport==null?GamaMapFactory.create():
					sobolNumReport.get(var).entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().get(0))); }
			});
			exp.add(new ParameterAdapter("Total order SI: ", IExperimentPlan.BATCH_CATEGORY_NAME, IType.MAP) {
				@Override public Object value() { return sobolNumReport==null?GamaMapFactory.create():
					sobolNumReport.get(var).entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().get(1))); }
			});
		
		}

	}
	
	// ----------------------------------------------------------------- //
	
	public List<ParametersSet> buildParameterSets(IScope scope, List<ParametersSet> sets, int index) {
		this.sample = Cast.asInt(scope, getFacet(SAMPLE_SIZE).value(scope));
		
		// Do not trust getExplorableParameter of the BatchAgent
		// Needs a step to explore a parameter, also for any sampling methods only min/max is required
		List<Batch> params = currentExperiment.getSpecies().getParameters().values().stream()
				.filter(p -> p.getMinValue(scope)!=null && p.getMaxValue(scope)!=null)
				.map(p -> (Batch) p)
				.collect(Collectors.toList());
		parameters = parameters==null?params:parameters;
		/* times 2 the number of parameters for the bootstraping (Saltelli 2002) and +2 because of sample A & B */
		this._sample = this.sample * (2 * parameters.size() + 2); 
		
		Sequence seq = new Saltelli();
		double[][] samples = seq.generate(_sample, parameters.size());
		
		for (int i = 0; i < _sample; i++) {
			
			ParametersSet origi = new ParametersSet(); 

			for (int j = 0; j < parameters.size(); j++) {
				origi = addParameterValue(scope,origi,parameters.get(j),samples[i][j]);
			}

			sets.add(origi);
		
		}
		
		currentParametersSpace = sets;
		
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
	
	@SuppressWarnings("unchecked")
	/**
	 * Manage simulation outputs from BatchAgent and compute (First & Total order) Sobol indices on requested outputs <\br>
	 * TODO : implement second order and refined indices with grouped outputs
	 * @param scope
	 */
	private void computeSobolIndexes(IScope scope) {
		
		/* Retrieve the output variable names */
		final List<String> outputVals = GamaListFactory.create(scope, Types.STRING, Cast.asList(scope, getOutputs().value(scope)));
		
		/* Build Sobol outputs */
		this.sobolNumReport = GamaMapFactory.create();
		
		List<Map<String,Object>> res_rebuilt = rebuildSimulationResults(scope, res_outputs);
		// TODO : i don't know why it is not raised ????
		if (res_rebuilt.size() != this._sample || sample * (2 + this.parameters.size() * 2) != _sample) {
			GamaRuntimeException.error("Sobol analysis carry out less simulation than expected: "+_sample, scope);
		}
				
		for (String v : outputVals) {
			
			/* INIT MOEA FRAMEWORK SOBOL SEQUENCE */
			A = new double[sample];
			B = new double[sample];
			C_A = new double[sample][this.parameters.size()];
			C_B = new double[sample][this.parameters.size()];
			
			Iterator<Map<String,Object>> outIter = res_rebuilt.iterator();
			
			for (int i = 0; i < sample; i++) {
				// TODO : does it has to be continuous output variables ? How to check for int for example ?
				A[i] = Double.valueOf(outIter.next().get(v).toString());

				for (int j = 0; j < this.parameters.size(); j++) {
					C_A[i][j] = Double.valueOf(outIter.next().get(v).toString()); 
				}

				for (int j = 0; j < this.parameters.size(); j++) {
					C_B[i][j] = Double.valueOf(outIter.next().get(v).toString()); 
				}

				B[i] = Double.valueOf(outIter.next().get(v).toString()); 
			}
			
			/* Create one Sobol report entry for output variable 'v' */
			sobolNumReport.put(v,GamaMapFactory.create());
			
			for (int j = 0; j < this.parameters.size(); j++) {
				
				List<Double> sobolIndexes = GamaListFactory.create();
				double[] a0 = new double[sample];
				double[] a1 = new double[sample];
				double[] a2 = new double[sample];

				for (int i = 0; i < sample; i++) {
					a0[i] = A[i];
					a1[i] = C_A[i][j];
					a2[i] = B[i];
				}
				
				// First order
				sobolIndexes.add(computeFirstOrder(a0, a1, a2, sample));
				sobolIndexes.add(computeFirstOrderConfidence(scope, a0, a1, a2, sample, _resample));
				// Total order
				sobolIndexes.add(computeTotalOrder(a0, a1, a2, sample));
				sobolIndexes.add(computeTotalOrderConfidence(scope, a0, a1, a2, sample, _resample));
				
				sobolNumReport.get(v).put(this.parameters.get(j), sobolIndexes);
			}	
			
			// TODO : add second ordered Sobol index, and may be look for other decomposition of variance (like all A, only A+B)
			
		}
		
		// TODO : copy past from batch, but should be called within IExploration
		// At last, we update the parameters (last fitness and best fitness)
		scope.getGui().showParameterView(scope, currentExperiment.getSpecies());
		
	}
	
	/**
	 * Turn the replication style simulation result given by BatchAgent 
	 * to fit table style simulation results of MoaeFramework Sobol index computation
	 */
	private List<Map<String, Object>> rebuildSimulationResults(IScope scope,
			IMap<ParametersSet, Map<String, List<Object>>> gama_res) {
		
		int expected_final_size = gama_res.stream(scope).mapToInt(e -> e.values().stream().findAny().get().size()).sum();
		if (expected_final_size != _sample) {
			GamaRuntimeException.error("There is a mismatch between simulation output size "+expected_final_size
					+" and requested Saltelli samples "+_sample, scope);
		}
		List<Map<String, Object>> res = GamaListFactory.create();
		
		for (Map<String, List<Object>> e : gama_res.values()) {
			
			Collection<List<Object>> res_values = e.values(); 
			int expected_size = res_values.iterator().next().size();
			if(res_values.stream().skip(1).anyMatch(v -> v.size()!=expected_size)) {
				GamaRuntimeException.error("There is strange simulation output in batch experiment "+currentExperiment, scope);
			}
			
			for (int i = 0; i < expected_size; i++){
				@SuppressWarnings("unchecked")
				Map<String,Object> res_updated = GamaMapFactory.create();
				for (String variable : e.keySet()) {
					res_updated.put(variable, e.get(variable).get(i));
				}
				res.add(res_updated);
			}
			
		}
		
		return res;
	}

	/**
	 * Construct the Sobol report as made in moeaframework
	 */
	private String buildSobolReport() {
		StringBuffer sb = new StringBuffer();
		
		final String eq = " = ";
		final String sep = "----------";
		sb.append("Parameter Sensitivity (Confidence)");
		sb.append(Strings.LN).append(sep);

		for (String var : sobolNumReport.keySet()) {
			sb.append(Strings.LN).append("Oucome of interest: ")
				.append(var).append(Strings.LN);
			Map<Batch,List<Double>> sobRes = sobolNumReport.get(var);
			
			sb.append(Strings.LN).append("1. First-Order Effects");
			for (Batch para : sobRes.keySet()) {
				sb.append(Strings.LN).append(Strings.TAB);
				sb.append(para.toString());
				sb.append(eq).append(sobRes.get(para).get(0))
					.append(" ("+sobRes.get(para).get(1)+")");
			}
			
			sb.append(Strings.LN).append("2. Total-Order Effects");
			for (Batch para : sobolNumReport.get(var).keySet()) {
				sb.append(Strings.LN).append(Strings.TAB);
				sb.append(para.toString());
				sb.append(eq).append(sobRes.get(para).get(2))
					.append(" ("+sobRes.get(para).get(3)+")");
			}
			sb.append(Strings.LN).append(sep);
		}
		return sb.toString();
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
	private double computeFirstOrderConfidence(IScope scope, double[] a0, double[] a1,
			double[] a2, int nsample, int nresample) {
		double[] b0 = new double[nsample];
		double[] b1 = new double[nsample];
		double[] b2 = new double[nsample];
		double[] s = new double[nresample];

		for (int i = 0; i < nresample; i++) {
			for (int j = 0; j < nsample; j++) {
				int index = scope.getRandom().getGenerator().nextInt(nsample);

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
	private double computeTotalOrderConfidence(IScope scope, double[] a0, double[] a1,
			double[] a2, int nsample, int nresample) {
		double[] b0 = new double[nsample];
		double[] b1 = new double[nsample];
		double[] b2 = new double[nsample];
		double[] s = new double[nresample];

		for (int i = 0; i < nresample; i++) {
			for (int j = 0; j < nsample; j++) {
				int index = scope.getRandom().getGenerator().nextInt(nsample);

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
	@SuppressWarnings("unused")
	private double computeSecondOrderConfidence(IScope scope, double[] a0,
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
				int index = scope.getRandom().getGenerator().nextInt(nsample);

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
