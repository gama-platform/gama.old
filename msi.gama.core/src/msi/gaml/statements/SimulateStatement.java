/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.statements;

import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentSpecies;
import msi.gama.kernel.experiment.IExperimentSpecies;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.IOutput;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gama.util.file.GAMLFile;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol(name = "simulate", kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@facets(value = {
		@facet(name = "comodel", type = { IType.FILE }, optional = false),
		@facet(name = "with_experiment", type = { IType.STRING }, optional = true),
	@facet(name = "with_input", type = { IType.MAP }, optional = true),
	@facet(name = "with_output", type = { IType.MAP }, optional = true),
	@facet(name = IKeyword.UNTIL, type = IType.BOOL, optional = true),
		@facet(name = IKeyword.REPEAT, type = { IType.INT }, optional = true) }, omissible = "comodel")
@inside(kinds = { ISymbolKind.EXPERIMENT, ISymbolKind.SPECIES,
		ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = IKeyword.CHART)
@doc(value = "Allows an agent, the sender agent (that can be the [Sections151#global world agent]), to ask another (or other) agent(s) to perform a set of statements. "
	+ "It obeys the following syntax, where the target attribute denotes the receiver agent(s):", examples = {
	"ask receiver_agent(s) {", "     [statements]", "}" })
public class SimulateStatement extends AbstractStatementSequence {

	private AbstractStatementSequence sequence = null;
	private final IExpression comodel;
	private IExperimentSpecies exp;
	private IExpression with_exp;
	// private IModel mm = null;
	private IExpression param_input = null;
	private IExpression param_output = null;
	private final IOutput exp_output = null;

	private GamaMap in = new GamaMap();
	private GamaMap out = new GamaMap();

	public SimulateStatement(final IDescription desc) {
		super(desc);
		comodel = getFacet("comodel");
		if ( comodel == null ) { return; }
		setName("simulate " + comodel.toGaml());

		with_exp = getFacet("with_experiment");

		exp = null;

		param_input = getFacet("with_input");

		param_output = getFacet("with_output");


	}

	@Override
	public void setChildren(final List<? extends ISymbol> com) {
		sequence = new AbstractStatementSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) {

		if ( with_exp != null ) {
			// exp =
			// ((GAMLFile)comodel.value(scope)).getModel().getExperiment("Experiment "
			// + with_exp.getName());
			exp = ((GAMLFile) comodel.value(scope)).getExperiment(
					with_exp.getName());
		}


		if (param_input != null) {
			in = (GamaMap) param_input.value(scope);
			for (int i = 0; i < in.getKeys().size(); i++) {
				exp.getModel().getVar(in.getKeys().get(i).toString())
						.setValue(in.getValues().get(i));
			}
		}


		exp.getSimulationOutputs().removeAllOutput();

//		GAMA.controller.getScheduler()
//				.removeStepable(exp.getAgent().toString());
//
//		 if (!GAMA.controller.getScheduler().hasSchedule(exp.getAgent())) {
//			for (IPopulation pop : exp.getAgent().getMicroPopulations()) {
//				GAMA.controller.getScheduler().removeStepable(pop.toString());
//			}
//			GAMA.controller.getScheduler().schedule(exp.getAgent(),
//					exp.getAgent().getScope());
//		}
		SimulationAgent sim = (SimulationAgent) exp.getAgent().getSimulation();
		IScope simScope = null;
		if (sim == null) {
			sim = exp.getAgent().createSimulation(
					new ParametersSet(), false);

		}
		simScope = sim.getScope();
		sim._init_(simScope);



		IExpression repeat = getFacet(IKeyword.REPEAT);
		IExpression stopCondition = getFacet(IKeyword.UNTIL);
		int n = 1;
		int i = 0;
		if ( repeat != null ) {
			n = (Integer) repeat.value(scope);
		}
		boolean mustStop = false;
		if (stopCondition == null) {
			mustStop = true;
		}
		while (!mustStop || i < n) {
			exp.getAgent().getSimulation().step(simScope);
			if ( param_output != null ) {
				out = (GamaMap) param_output.value(scope);
				for ( int j = 0; j < out.getKeys().size(); j++ ) {
					scope.setAgentVarValue(
							out.getValues().get(j).toString(),
							((ExperimentSpecies) exp)
						.getExperimentScope().getGlobalVarValue(out.getKeys().get(j).toString()));

				}
			}
			if ( stopCondition != null ) {
				mustStop = Cast.asBool(scope, scope.evaluate(stopCondition, scope.getAgentScope()));
			}
			i++;
		}

		// exp.getCurrentSimulation().halt(exp.getAgent().getScope());
		return null;
	}

}