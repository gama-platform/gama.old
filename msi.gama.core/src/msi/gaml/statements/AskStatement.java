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

import static com.google.common.collect.Iterators.*;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

// A group of commands that can be executed on remote agents.

@symbol(name = IKeyword.ASK, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, remote_context = true)
@facets(value = { 
	@facet(name = IKeyword.TARGET, type = { IType.CONTAINER, IType.AGENT }, optional = false, doc = @doc("an expression that evaluates to an agent or a list of agents")),
	@facet(name = IKeyword.AS, type = { IType.SPECIES }, optional = true, doc = @doc("an expression that evaluates to a species")) }, omissible = IKeyword.TARGET)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = IKeyword.CHART)
@doc(value = "Allows an agent, the sender agent (that can be the [Sections161#global world agent]), to ask another (or other) agent(s) to perform a set of statements. If the value of the target facet is nil or empty, the statement is ignored.", usages = {
	@usage(value = "It obeys the following syntax, where the target facet denotes the receiver agent(s):", examples = {@example(value="ask receiver_agent(s) {",isExecutable=false),@example(value="     [statements]",isExecutable=false),@example(value="}",isExecutable=false)}),
	@usage(value = "The species of the receiver agents must be known in advance for this statement to compile. If not, it is possible to cast them using the `as` facet. If the receiver_agent(s) is not instance(s) of the species a_species_expression, the execution will return a class cast exception in the set of statement:", examples = {@example(value="ask receiver_agent(s) as: a_species_expression {",isExecutable=false),@example(value="     [statement_set]",isExecutable=false),@example(value="}",isExecutable=false)}),
	@usage(value = "As alternative form for the castin, if there is only a single receiver agent: ",examples={@example(value="ask species_name (receiver_agent) {",isExecutable=false),@example(value="     [statement_set]",isExecutable=false),@example(value="}",isExecutable=false)}),
	@usage(value = "As alternative form for the castin, if receiver_agent(s) is a list of agents: ",examples={@example(value="ask receiver_agents of_species species_name {",isExecutable=false),@example(value="     [statement_set]",isExecutable=false),@example(value="}",isExecutable=false)}),
	@usage(value = "Any statement can be declared in the block statements. All the statements will be evaluated in the context of the receiver agent(s), as if they were defined in their species, which means that an expression like `self` will represent the receiver agent and not the sender. If the sender needs to refer to itself, some of its own attributes (or temporary variables) within the block statements, it has to use the keyword `myself`.",examples={
		@example(value="species animal {",isExecutable=false),@example(value="    float energy <- rnd (1000) min: 0.0 {",isExecutable=false),@example(value="    reflex when: energy > 500 { // executed when the energy is above the given threshold",isExecutable=false),@example(value="         list<animal> others <- (animal at_distance 5); // find all the neighbouring animals in a radius of 5 meters",isExecutable=false),@example(value="         float shared_energy  <- (energy - 500) / length (others); // compute the amount of energy to share with each of them",isExecutable=false),@example(value="         ask others { // no need to cast, since others has already been filtered to only include animals",isExecutable=false),@example(value="              if (energy < 500) { // refers to the energy of each animal in others",isExecutable=false),@example(value="                   energy <- energy + myself.shared_energy; // increases the energy of each animal",isExecutable=false),@example(value="                   myself.energy <- myself.energy - myself.shared_energy; // decreases the energy of the sender",isExecutable=false),@example(value="              }",isExecutable=false),@example(value="         }",isExecutable=false),@example(value="    }",isExecutable=false),@example(value="}",isExecutable=false)})
})
public class AskStatement extends AbstractStatementSequence {

	private AbstractStatementSequence sequence = null;
	private final IExpression target;

	public AskStatement(final IDescription desc) {
		super(desc);
		target = getFacet(IKeyword.TARGET);
		if ( target != null ) {
			setName("ask " + target.toGaml());
		}
	}

	@Override
	public void setChildren(final List<? extends ISymbol> com) {
		sequence = new AbstractStatementSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com); 
	}

	@Override
	public Object privateExecuteIn(final IScope scope) {
		final Object t = target.value(scope);
		final Iterator<IAgent> runners =
			t instanceof IContainer ? ((IContainer) t).iterable(scope).iterator() : t instanceof IAgent
				? singletonIterator(t) : emptyIterator();
		// runners =
		// t instanceof ISpecies ? ((ISpecies) t).iterator() : t instanceof IContainer ? ((IContainer) t).iterable(
		// scope).iterator() : t instanceof IAgent ? singletonIterator(t) : emptyIterator();
		scope.addVarWithValue(IKeyword.MYSELF, scope.getAgentScope());
		Object[] result = new Object[1];
		while (runners.hasNext() && scope.execute(sequence, runners.next(), null, result)) {}
		return result[0];
	}

}