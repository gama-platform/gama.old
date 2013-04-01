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

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.user.UserInputStatement;
import msi.gaml.compilation.ISymbol;
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
@symbol(name = { IKeyword.USER_COMMAND }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, with_args = true)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT }, symbols = IKeyword.USER_PANEL)
@facets(value = { @facet(name = IKeyword.ACTION, type = IType.ID, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.WHEN, type = IType.BOOL_STR, optional = true),
	@facet(name = IKeyword.WITH, type = IType.MAP_STR, optional = true) }, omissible = IKeyword.NAME)
public class UserCommandStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	Arguments args;
	final String actionName;
	final IExpression when;
	List<UserInputStatement> inputs = new ArrayList();

	public UserCommandStatement(final IDescription desc) {
		super(desc);
		setName(desc.getName());
		actionName = getLiteral(IKeyword.ACTION);
		when = getFacet(IKeyword.WHEN);
	}

	public List<UserInputStatement> getInputs() {
		return inputs;
	}

	@Override
	public void setFormalArgs(final Arguments args) {
		this.args = args;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		for ( ISymbol c : children ) {
			if ( c instanceof UserInputStatement ) {
				inputs.add((UserInputStatement) c);
			}
		}
		children.removeAll(inputs);
		super.setChildren(children);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if ( isEnabled(scope) ) {
			if ( actionName == null ) { return super.privateExecuteIn(scope); }
			ISpecies context = scope.getAgentScope().getSpecies();
			IStatement.WithArgs executer = context.getAction(actionName);
			executer.setRuntimeArgs(args);
			Object result = executer.executeOn(scope);
			return result;
		}
		scope.setStatus(ExecutionStatus.skipped);
		return null;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {}

	@Override
	public IType getType() {
		if ( actionName == null ) { return super.getType(); }
		StatementDescription executer = description.getSpeciesContext().getAction(actionName);
		return executer.getType();
	}

	@Override
	public IType getContentType() {
		if ( actionName == null ) { return super.getContentType(); }
		StatementDescription executer = description.getSpeciesContext().getAction(name);
		return executer.getContentType();
	}

	@Override
	public IType getKeyType() {
		if ( actionName == null ) { return Types.NO_TYPE; }
		StatementDescription executer = description.getSpeciesContext().getAction(name);
		return executer.getKeyType();
	}

	public boolean isEnabled(final IScope scope) {
		return when == null || Cast.asBool(scope, when.value(scope));
	}

}
