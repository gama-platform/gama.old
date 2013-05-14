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
package msi.gaml.expressions;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.operators.Cast;
import msi.gaml.statements.*;

/**
 * PrimitiveOperator. An operator that wraps a primitive or an action.
 * 
 * @author drogoul 4 sept. 07
 */

public class PrimitiveOperator extends AbstractNAryOperator {

	// IStatement.WithArgs statement;
	Arguments parameters;

	public PrimitiveOperator(final String op) {
		setName(op);
	}

	@Override
	public PrimitiveOperator init(final String op, final IDescription context, final IExpression ... args) {
		final StatementDescription action = context.getAction(op);
		return init(op, context, action, args);
	}

	public PrimitiveOperator init(final String op, final IDescription callerContext, final StatementDescription action,
		final IExpression ... args) {
		this.exprs = args;
		type = action.getType();
		contentType = action.getContentType();
		keyType = action.getKeyType();
		parameters = createArgs();
		// final IDescription cd = DescriptionFactory.create(IKeyword.DO, action.getSpeciesContext(), IKeyword.ACTION,
		// op);
		action.verifyArgs(callerContext, parameters);
		// statement = new DoStatement(cd);
		// if ( statement != null ) {
		// statement.setFormalArgs(param);
		// }
		return this;
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		if ( scope == null ) { return null; }
		final IAgent target = Cast.asAgent(scope, arg(0).value(scope));
		if ( target == null ) { return null; }
		// AD 13/05/13 The target should not be pushed so early to the scope, as the arguments will be (incorrectly)
		// evaluated in its context, but how to prevent it ? See Issue 401.
		// One way is (1) to gather the executer
		final IStatement.WithArgs executer = target.getSpecies().getAction(getName());
		// Then, (2) to set the caller to the actual agent on the scope (in the context of which the arguments need to
		// be evaluated
		if ( executer != null ) {
			parameters.setCaller(scope.getAgentScope());
			// And finally, (3) to execute the executer on the target (it will be pushed in the scope)
			return scope.execute(executer, target, parameters);
		}
		return null;
	}

	private Arguments createArgs() {
		final Arguments result = new Arguments();
		final IExpression right = arg(1); // FIXME A bit dangerous !
		if ( !(right instanceof MapExpression) ) { return result; }
		final IExpression[] keys = ((MapExpression) right).keysArray();
		final IExpression[] values = ((MapExpression) right).valuesArray();
		for ( int i = 0; i < keys.length; i++ ) {
			result.put(keys[i].literalValue(), values[i]);
		}
		return result;
	}

	@Override
	public PrimitiveOperator copy() {
		return new PrimitiveOperator(getName()/* , sd */);
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String getTitle() {
		final StringBuilder sb = new StringBuilder(50);
		sb.append("action ").append(getName()).append(" of ").append(arg(0).getType().getSpeciesName())
			.append(" returns ").append(typeToString());
		return sb.toString();

	}

	@Override
	public String getDocumentation() {
		final StringBuilder sb = new StringBuilder(200);
		// TODO insert here a @documentation if possible
		sb.append("Returns a value of type ").append(type.toString()).append("<br>");
		sb.append("Defined in ").append(arg(0).getType().getSpeciesName()).append("<br>");
		return sb.toString();
	}

}
