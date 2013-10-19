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
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.IOutput;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol(name = "simulate", kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@facets(value = { @facet(name = "comodel", type = { IType.STRING }, optional = false),
	@facet(name = "with_experiment", type = { IType.STRING }, optional = true),
	@facet(name = "with_input", type = { IType.MAP }, optional = true),
	@facet(name = "with_output", type = { IType.MAP }, optional = true),
	@facet(name = IKeyword.UNTIL, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.REPEAT, type = { IType.INT }, optional = true) }, omissible = "model")
@inside(kinds = { ISymbolKind.EXPERIMENT, ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = IKeyword.CHART)
@doc(value = "Allows an agent, the sender agent (that can be the [Sections151#global world agent]), to ask another (or other) agent(s) to perform a set of statements. "
	+ "It obeys the following syntax, where the target attribute denotes the receiver agent(s):", examples = {
	"ask receiver_agent(s) {", "     [statements]", "}" })
public class SimulateStatement extends AbstractStatementSequence {

	private AbstractStatementSequence sequence = null;
	private final IExpression comodel;
	private IExperimentSpecies exp;
	private IModel mm = null;
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

		mm = ((GamlExpressionFactory) GAML.getExpressionFactory()).getParser().createModelFromFile(comodel.getName());
		// desc.getModelDescription().constructModelRelativePath(
		// comodel.getName(), true));

		IExpression with_exp = getFacet("with_experiment");
		exp = null;// = mm.getExperiment("Experiment default");

		if ( with_exp != null ) {
			exp = mm.getExperiment("Experiment " + with_exp.getName());
		}
		param_input = getFacet("with_input");

		param_output = getFacet("with_output");

		// exp_output = exp.getSimulationOutputs().getOutput(
		// "msi.gama.application.view.LayeredDisplayViewpredator_display");
		// IOutput tmp = exp_output;
		// tmp.setUserCreated(true);
		// exp.getSimulationOutputs().removeOutput(tmp);

		// exp.getSimulationOutputs().removeOutput(exp_output);

		// recursiveExplore(scope.getModel().getDescription());

	}

	@Override
	public void setChildren(final List<? extends ISymbol> com) {
		sequence = new AbstractStatementSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) {

		if ( param_input != null ) {
			in = (GamaMap) param_input.value(scope);
			for ( int i = 0; i < in.getKeys().size(); i++ ) {
				((ExperimentSpecies) exp).getExperimentScope().setGlobalVarValue(in.getKeys().get(i).toString(),
					in.getValues().get(i));
			}
		}

		// GuiUtils.debug(">>>>>> call model init "
		// + in.getKeys().get(0).toString() + " " + in.getValues().get(0));

		((ExperimentSpecies) exp).createAgentForMultiExp();
		GAMA.controller.getScheduler().removeStepable(exp.getAgent().toString());

		for ( IPopulation pop : exp.getAgent().getMicroPopulations() ) {
			GAMA.controller.getScheduler().removeStepable(pop.toString());
		}
		// Iterator<IPopulation> pops = exp.getAgent().getMicroPopulations().iterator();
		// while (pops.hasNext()) {
		// GAMA.controller.getScheduler().removeStepable(pops.next().toString());
		// }

		GAMA.controller.getScheduler().schedule(exp.getAgent(), exp.getAgent().getScope());

		// GAMA.getExperiment()
		// .getExperimentOutputs()
		// .addOutput(
		// exp.getSimulationOutputs()
		// .getOutput(
		// "msi.gama.application.view.LayeredDisplayViewpredator_display"));

		// System.out.println(param_input.getElements()[0].getName() + " = "
		// + in.get(0));

		IExpression repeat = getFacet(IKeyword.REPEAT);
		IExpression stopCondition = getFacet(IKeyword.UNTIL);
		int n = 1;
		int i = 0;
		if ( repeat != null ) {
			n = (Integer) repeat.value(scope);
		}
		boolean mustStop = false;
		while (i < n && !mustStop) {
			exp.getAgent().getSimulation().step(exp.getExperimentScope());

			if ( param_output != null ) {
				out = (GamaMap) param_output.value(scope);
				for ( int j = 0; j < out.getKeys().size(); j++ ) {
					scope.setGlobalVarValue(out.getValues().get(j).toString(), ((ExperimentSpecies) exp)
						.getExperimentScope().getGlobalVarValue(out.getKeys().get(j).toString()));
				}
			}
			if ( stopCondition != null ) {
				mustStop = Cast.asBool(scope, scope.evaluate(stopCondition, scope.getAgentScope()));
			}
			i++;
		}

		// System.out.println("after simulation "
		// + ((ExperimentSpecies) exp).getExperimentScope()
		// .getGlobalVarValue(out.getKeys().get(0).toString()));

		// GuiUtils.informConsole(""
		// + exp.getAgent().getSimulation().getAttribute("pre_ordre"));
		// final Object t = target.value(scope);
		// final Iterator<IAgent> runners;
		// runners =
		// t instanceof ISpecies ? ((ISpecies) t).iterator() : t instanceof
		// IContainer ? ((IContainer) t).iterable(
		// scope).iterator() : t instanceof IAgent ? singletonIterator(t) :
		// emptyIterator();
		// scope.addVarWithValue(IKeyword.MYSELF, scope.getAgentScope());
		// Object[] result = new Object[1];
		// while (runners.hasNext() && scope.execute(sequence, runners.next(),
		// null, result)) {}
		return null;
	}

	// public void recursiveExplore(String sss, IDescription des) {
	// System.out.println(sss + des);
	// sss += "\t";
	// for (IDescription d : des.getChildren()) {
	// // System.out.println(d);
	// recursiveExplore(sss, d);
	// }
	//
	// // getContents(scope);
	// // spec.open();
	// // ExperimentSpecies e = (ExperimentSpecies) spec;
	// // IPopulation p = e.createExperimentAgent();
	// // IList<? extends IAgent> la = p.createAgents(scope, 1,
	// // Collections.EMPTY_LIST, false);
	// // scope.getModel().getDescription().addChild((IDescription) spec);
	//
	// // GAMA.getExpressionFactory().registerParser(new
	// // GamlExpressionCompiler());
	// // DescriptionFactory.getModelFactory().compile(mycomodel973061166);}
	//
	// // IModel model = (IModel) DescriptionFactory.compile(mm);
	// // GAMA.controller.closeExperiment();
	// // GAMA.controller.newExperiment("Experiment hunt", mm);
	// // ((ExperimentSpecies) newExperiment).open();
	//
	// // StringBuilder sb = new StringBuilder(buffer.length(null) * 200); //
	// // VERIFY NULL SCOPE
	// // for ( String s : buffer ) {
	// // sb.append(s).append("\n"); // TODO Factorize the different calls to
	// // "new line" ...
	// // }
	// // sb.setLength(sb.length() - 1);
	// }
}