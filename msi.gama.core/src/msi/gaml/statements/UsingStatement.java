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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * "using" is a statement that allows to set the topology to use by its sub-statements. They can
 * gather it by asking the scope to provide it.
 * 
 * @author drogoul 19 janv. 13
 */
@symbol(name = IKeyword.USING, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = { @facet(name = IKeyword.TOPOLOGY, type = IType.TOPOLOGY_STR, optional = false) }, omissible = IKeyword.TOPOLOGY)
public class UsingStatement extends AbstractStatementSequence {

	final IExpression topology;
	ITopology previous;

	/**
	 * Constructor.
	 * 
	 * @param desc, the description of the statement.
	 */
	public UsingStatement(final IDescription desc) {
		super(desc);
		topology = getFacet(IKeyword.TOPOLOGY);
		setName("using " + topology.toGaml());
	}

	/**
	 * When entering the scope, the statement pushes the topology (if not null) to it and remembers
	 * the one that was previously pushed.
	 * @see msi.gaml.statements.AbstractStatementSequence#enterScope(msi.gama.runtime.IScope)
	 */
	@Override
	public void enterScope(final IScope scope) {
		super.enterScope(scope);
		ITopology topo = Cast.asTopology(scope, topology.value(scope));
		if ( topo != null ) {
			previous = scope.setTopology(topo);
		}
	}

	/**
	 * When leaving the scope, the statement replaces its topology by the previous one.
	 * @see msi.gaml.statements.AbstractStatementSequence#leaveScope(msi.gama.runtime.IScope)
	 */

	@Override
	public void leaveScope(final IScope scope) {
		scope.setTopology(previous);
		previous = null;
		super.leaveScope(scope);
	}

}