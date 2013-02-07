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
package msi.gaml.expressions;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.statements.*;

/**
 * PrimitiveOperator. An operator that wraps a primitive and defines its own scope for running it.
 * 
 * @author drogoul 4 sept. 07
 */

public class PrimitiveOperator extends AbstractNAryOperator {

	IStatement.WithArgs statement;

	public PrimitiveOperator(final String op) {
		setName(op);
	}

	PrimitiveOperator(final String op, final IDescription species) {
		setName(op);
		// sd = species;
	}

	@Override
	public PrimitiveOperator init(final String op, final IDescription context,
		final IExpression ... args) {
		this.exprs = args;
		Facets facets = new Facets();
		facets.putAsLabel(IKeyword.ACTION, name); // TODO A vérifier
		IDescription sd = context.getSpeciesDescription(arg(0).getType().getSpeciesName());
		IDescription cd = DescriptionFactory.create(IKeyword.DO, sd, IKeyword.ACTION, name);
		statement = new DoStatement(cd);
		type = statement.getReturnType();
		contentType = statement.getReturnContentType();
		statement.setFormalArgs(createArgs());
		return this;
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		if ( scope == null ) { return null; }
		IAgent target = Cast.asAgent(scope, arg(0).value(scope));
		if ( target == null ) { return null; }
		Object result = scope.execute(statement, target);
		return result;
	}

	private Arguments createArgs() {
		Arguments result = new Arguments();
		IExpression right = arg(1); // FIXME A bit dangerous !
		if ( !(right instanceof MapExpression) ) { return result; }
		IExpression[] keys = ((MapExpression) right).keysArray();
		IExpression[] values = ((MapExpression) right).valuesArray();
		for ( int i = 0; i < keys.length; i++ ) {
			result.put(keys[i].literalValue(), values[i]);
		}
		return result;
	}

	@Override
	public PrimitiveOperator copy() {
		return new PrimitiveOperator(name/* , sd */);
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String getTitle() {
		StringBuilder sb = new StringBuilder(50);
		sb.append("Action <b>").append(getName()).append("</b><br>");
		return sb.toString();

	}

	@Override
	public String getDocumentation() {
		StringBuilder sb = new StringBuilder(200);
		// TODO insert here a @documentation if possible
		sb.append("Returns a value of type ").append(type.toString()).append("<br>");
		sb.append("Defined in ").append(statement.getDescription().getSpeciesContext().getTitle())
			.append("<br>");
		return sb.toString();
	}

}
