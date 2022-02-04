/*******************************************************************************************************
 *
 * AExplorationAlgorithm.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.batch.exploration;

import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.batch.IExploration;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.experiment.ParameterAdapter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.IType;

/**
 * The Class AExplorationAlgorithm.
 */
@inside (
		kinds = { ISymbolKind.EXPERIMENT })
public abstract class AExplorationAlgorithm extends Symbol implements IExploration {

	/** The current experiment. */
	protected BatchAgent currentExperiment;
	
	/** The outputs expression. */
	protected IExpression outputsExpression;
	
	/** The output variables. */
	protected List<String> outputVariables;
	
	/** The automatic output batch file */
	protected IExpression outputFilePath;
	
	@Override
	public void initializeFor(IScope scope, BatchAgent agent) throws GamaRuntimeException {
		this.currentExperiment = agent;
	}
	
	/**
	 * Instantiates a new a exploration algorithm.
	 *
	 * @param desc the desc
	 */
	public AExplorationAlgorithm(final IDescription desc) { 
		super(desc);
		if (hasFacet(IKeyword.BATCH_VAR_OUTPUTS)) {outputsExpression = getFacet(IKeyword.BATCH_VAR_OUTPUTS);}
		if (hasFacet(IKeyword.BATCH_OUTPUT)) { outputFilePath = getFacet(IKeyword.BATCH_OUTPUT); }
	}

	@Override
	public void addParametersTo(List<Batch> exp, BatchAgent agent) {
		exp.add(new ParameterAdapter("Exploration method", IExperimentPlan.BATCH_CATEGORY_NAME, IType.STRING) {

			@Override
			public Object value() {
				@SuppressWarnings ("rawtypes") final List<Class> classes = Arrays.asList(CLASSES);
				final String methodName = IKeyword.METHODS[classes.indexOf(AExplorationAlgorithm.this.getClass())];
				return "Method " + methodName + " | " + (getOutputs()==null ? "No specified outputs" : (" outputs " + getOutputs().literalValue())) ;
			}

		});

	}
	
	@Override
	public void run(final IScope scope) {
		try {
			explore(scope);
		} catch (final GamaRuntimeException e) {
			GAMA.reportError(scope, e, false);
		}
	}

	@Override
	public boolean isFitnessBased() { return false; }
	
	// MAIN ABSTRACTION
	
	/**
	 * Main method that launch the exploration
	 * 
	 * @param scope
	 */
	public abstract void explore(IScope scope);
	
	/**
	 * Gives the list of variables the exploration method is targeting
	 * 
	 * @return {@link IExpression}
	 */
	public IExpression getOutputs() {return outputsExpression;}
	
	/**
	 * 
	 */
	public String getReport() { return "";}
	
	/**
	 * Main method to build the set of points to visit during the exploration of the model
	 * 
	 * @param scope
	 * @param sets
	 * @param index
	 * @return
	 */
	public abstract List<ParametersSet> buildParameterSets(IScope scope, List<ParametersSet> sets, int index);
	
	// UTILITY
	
	/**
	 * Adds the parameter value.
	 *
	 * @param scope the scope
	 * @param set the set
	 * @param var the var
	 * @return the parameters set
	 */
	/*
	 * Add a value to a parameter set
	 */
	public ParametersSet addParameterValue(IScope scope, ParametersSet set, Batch var) {
		switch (var.getType().id()) {
			case IType.INT:
				int intValue = Cast.asInt(scope, var.getMinValue(scope));
				int maxIntValue = Cast.asInt(scope, var.getMaxValue(scope));
				while (intValue <= maxIntValue) {
					set.put(var.getName(), intValue);
					intValue = intValue + Cast.asInt(scope, var.getStepValue(scope));
				}
				return set;
			case IType.FLOAT:
				double floatValue = Cast.asFloat(scope, var.getMinValue(scope));
				double maxFloatValue = Cast.asFloat(scope, var.getMaxValue(scope));
				while (floatValue <= maxFloatValue) {
					set.put(var.getName(), floatValue);
					floatValue = floatValue + Cast.asFloat(scope, var.getStepValue(scope));
				}
				return set;
			case IType.DATE:
				GamaDate dateValue = GamaDateType.staticCast(scope, var.getMinValue(scope), null, false);
				GamaDate maxDateValue = GamaDateType.staticCast(scope, var.getMaxValue(scope), null, false);
				while (dateValue.isSmallerThan(maxDateValue, false)) {
					set.put(var.getName(), dateValue);
					dateValue = dateValue.plus(Cast.asFloat(scope, var.getStepValue(scope)), ChronoUnit.SECONDS);
				}
				return set;
			case IType.POINT:
				GamaPoint pointValue = Cast.asPoint(scope, var.getMinValue(scope));
				GamaPoint maxPointValue = Cast.asPoint(scope, var.getMaxValue(scope));
				while (pointValue.smallerThanOrEqualTo(maxPointValue)) {
					set.put(var.getName(), pointValue);
					pointValue = pointValue.plus(Cast.asPoint(scope, var.getStepValue(scope)));
				}
				return set;
			case IType.BOOL:
				set.put(var.getName(), true);
				set.put(var.getName(), false);
				return set;
			default:
				GamaRuntimeException.error("Trying to add a variable of unknown type "+var.getType().id()+" to a parameter set", scope);
				return set;
		}
	}

}
