package msi.gama.kernel.experiment;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.AbstractExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.test.TestStatement;
import msi.gaml.types.IType;

@experiment (IKeyword.TEST)
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class TestAgent extends BatchAgent {

	public TestAgent(final IPopulation p) throws GamaRuntimeException {
		super(p);
	}

	@Override
	protected IExpression defaultStopCondition() {
		return new AbstractExpression() {

			@Override
			public String serialize(final boolean includingBuiltIn) {
				return "cycle = 1";
			}

			@Override
			public Boolean value(final IScope scope) throws GamaRuntimeException {
				return scope.getClock().getCycle() == 1;
			}

		};
	}

	@Override
	public boolean init(final IScope scope) {
		super.init(scope);
		getAllTests().forEach(t -> t.reset());
		scope.getGui().displayTests(scope);
		return true;
	}

	@Override
	public boolean step(final IScope scope) {
		super.step(scope);
		scope.getGui().displayTests(scope);
		return true;
	}

	@Override
	protected String endStatus() {
		return "Tests over.";
	}

	@Override
	public void addSpecificParameters(final List<IParameter.Batch> params) {
		params.add(new ParameterAdapter("Stop condition", IExperimentPlan.TEST_CATEGORY_NAME, IType.STRING) {

			@Override
			public String value() {
				return stopCondition != null ? stopCondition.serialize(false) : "none";
			}

		});

		params.add(new ParameterAdapter("Parameter space", IExperimentPlan.TEST_CATEGORY_NAME, "", IType.STRING) {

			@Override
			public String value() {
				final Map<String, IParameter.Batch> params = getSpecies().getExplorableParameters();
				if (params.isEmpty()) { return "1"; }
				String result = "";
				int dim = 1;
				for (final Map.Entry<String, IParameter.Batch> entry : params.entrySet()) {
					result += entry.getKey() + " (";
					final int entryDim = getExplorationDimension(entry.getValue());
					dim = dim * entryDim;
					result += String.valueOf(entryDim) + ") * ";
				}
				if (!result.isEmpty())
					result = result.substring(0, result.length() - 2);
				result += " = " + dim;
				return result;
			}

			int getExplorationDimension(final IParameter.Batch p) {
				if (p.getAmongValue(getScope()) != null) { return p.getAmongValue(getScope()).size(); }
				return (int) ((p.getMaxValue(getScope()).doubleValue() - p.getMinValue(getScope()).doubleValue())
						/ p.getStepValue(getScope()).doubleValue()) + 1;
			}

		});

	}

	public List<TestStatement> getAllTests() {
		final List<TestStatement> tests = getModel().getAllTests();
		final Consumer<IStatement> filter = t -> {
			if (t instanceof TestStatement)
				tests.add((TestStatement) t);
		};
		getSpecies().getBehaviors().forEach(filter);
		return tests;
	}

}
