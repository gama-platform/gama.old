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
import msi.gama.kernel.experiment.IExperimentSpecies;
import msi.gama.outputs.IOutput;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gama.util.file.GAMLFile;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

@symbol(name = "simulate", kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@facets(value = {
	@facet(name = "comodel", type = { IType.FILE }, optional = false),
	@facet(name = "with_experiment", type = { IType.STRING }, optional = true),
	@facet(name = "with_input", type = { IType.MAP }, optional = true),
	@facet(name = "with_output", type = { IType.MAP }, optional = true),
	@facet(name = "reset", type = { IType.BOOL }, optional = true),
	@facet(name = IKeyword.UNTIL, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.REPEAT, type = { IType.INT }, optional = true) }, omissible = "comodel")
@inside(kinds = { ISymbolKind.EXPERIMENT, ISymbolKind.SPECIES,
		ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = IKeyword.CHART)
@doc(value = "Allows an agent, the sender agent (that can be the [Sections161#global world agent]), to ask another (or other) agent(s) to perform a set of statements. "
	+ "It obeys the following syntax, where the target attribute denotes the receiver agent(s):", examples = {
	@example("ask receiver_agent(s) {"), @example("     // [statements]"), @example("}") })
public class SimulateStatement extends AbstractStatementSequence {

	private AbstractStatementSequence sequence = null;
	private final IExpression comodel;
	private IExperimentSpecies exp;
	private IExpression with_exp;
	// private IModel mm = null;
	private IExpression param_input = null;
	private IExpression param_output = null;
	private IExpression reset = null;
	private final IOutput exp_output = null;
	private IExpression repeat = null;
	private IExpression stopCondition = null;
	private final GamaMap in = new GamaMap();
	private final GamaMap out = new GamaMap();

	public SimulateStatement(final IDescription desc) {
		super(desc);
		comodel = getFacet("comodel");
		if ( comodel == null ) { return; }
		setName("simulate " + comodel.toGaml());

		with_exp = getFacet("with_experiment");

		exp = null;

		param_input = getFacet("with_input");

		param_output = getFacet("with_output");

		reset = getFacet("reset");

		repeat = getFacet(IKeyword.REPEAT);
		stopCondition = getFacet(IKeyword.UNTIL);


	}

	@Override
	public void setChildren(final List<? extends ISymbol> com) {
		sequence = new AbstractStatementSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) {
		Object modelfile = comodel.value(scope);
		if (modelfile instanceof GAMLFile) {
			((GAMLFile) modelfile).execute(scope, with_exp, param_input,
					param_output, in, out, reset, repeat, stopCondition);
		}


		// exp.getCurrentSimulation().halt(exp.getAgent().getScope());
		return null;
	}

}