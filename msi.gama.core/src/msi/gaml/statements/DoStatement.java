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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 7 f√©vr. 2010
 * 
 * @todo Description
 * 
 */
@symbol(name = { IKeyword.DO, IKeyword.REPEAT }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = true, with_scope = false, with_args = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = IKeyword.CHART)
@facets(value = { @facet(name = IKeyword.ACTION, type = IType.ID, optional = false),
	@facet(name = IKeyword.WITH, type = IType.MAP, optional = true),
	@facet(name = IKeyword.INTERNAL_FUNCTION, type = IType.NONE, optional = true),
	@facet(name = IKeyword.RETURNS, type = IType.NEW_TEMP_ID, optional = true) }, omissible = IKeyword.ACTION)
public class DoStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	Arguments args;
	String returnString;
	final IExpression function;

	public DoStatement(final IDescription desc) {
		super(desc);
		returnString = getLiteral(IKeyword.RETURNS);
		function = getFacet(IKeyword.INTERNAL_FUNCTION);
		setName(getLiteral(IKeyword.ACTION));
	}

	@Override
	public void enterScope(final IScope scope) {
		if ( returnString != null ) {
			scope.addVarWithValue(returnString, null);
		}
		super.enterScope(scope);
	}

	@Override
	public void setFormalArgs(final Arguments args) {
		this.args = args;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final ISpecies context = scope.getAgentScope().getSpecies();
		final IStatement.WithArgs executer = context.getAction(name);
		Object result = null;
		if ( executer != null ) {
			executer.setRuntimeArgs(args);
			result = executer.executeOn(scope);
		} else if ( function != null ) {
			result = function.value(scope);
		}
		if ( returnString != null ) {
			scope.setVarValue(returnString, result);
		}
		return result;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {}

	@Override
	public IType getType() {
		final StatementDescription executer = description.getSpeciesContext().getAction(name);
		if ( executer != null ) {
			return executer.getType();
		} else if ( function != null ) { return function.getType(); }
		return Types.NO_TYPE;
	}

	@Override
	public IType getContentType() {
		final StatementDescription executer = description.getSpeciesContext().getAction(name);
		if ( executer != null ) {
			return executer.getContentType();
		} else if ( function != null ) { return function.getContentType(); }
		return Types.NO_TYPE;
	}

	@Override
	public IType getKeyType() {
		final StatementDescription executer = description.getSpeciesContext().getAction(name);
		if ( executer != null ) {
			return executer.getKeyType();
		} else if ( function != null ) { return function.getKeyType(); }
		return Types.NO_TYPE;
	}

	@Override
	public Double computePertinence(final IScope scope) throws GamaRuntimeException {
		final ISpecies context = scope.getAgentScope().getSpecies();
		final IStatement.WithArgs executer = context.getAction(name);
		if ( executer.getPertinence() != null ) { return Cast.asFloat(scope, executer.getPertinence().value(scope)); }
		return 1.0;
	}
}
