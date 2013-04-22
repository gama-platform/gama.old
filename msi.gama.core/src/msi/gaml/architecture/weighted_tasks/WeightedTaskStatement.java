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
package msi.gaml.architecture.weighted_tasks;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

/**
 * The Class WeightedTaskCommand. A simple definition of a task (set of commands) with a weight that
 * can be computed dynamically. Depending on the architecture in which the tasks are defined, this
 * weight can be used to choose the active task, or to define the order in which they are executed
 * each step.
 * 
 * @author drogoul
 */

@symbol(name = WeightedTaskStatement.TASK, kind = ISymbolKind.BEHAVIOR, with_sequence = true)
@inside(symbols = WeightedTasksArchitecture.WT, kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT,
	ISymbolKind.MODEL })
@facets(value = { @facet(name = WeightedTaskStatement.WEIGHT, type = IType.FLOAT, optional = false),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false) }, omissible = IKeyword.NAME)
public class WeightedTaskStatement extends AbstractStatementSequence {

	protected static final String WEIGHT = "weight";
	protected static final String TASK = "task";
	protected IExpression weight;

	public WeightedTaskStatement(final IDescription desc) {
		super(desc);
		setName(getLiteral(IKeyword.NAME));
		weight = getFacet(WEIGHT);
	}

	public Double computeWeight(final IScope scope) throws GamaRuntimeException {
		return Cast.asFloat(scope, weight.value(scope));
	}

}
