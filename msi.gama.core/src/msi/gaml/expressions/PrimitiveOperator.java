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
import msi.gaml.commands.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;

/**
 * PrimitiveOperator. An operator that wraps a primitive and defines its own scope for running it.
 * 
 * @author drogoul 4 sept. 07
 */

public class PrimitiveOperator extends AbstractBinaryOperator {

	ICommand.WithArgs command;
	IDescription sd;

	public PrimitiveOperator(final String op) {
		setName(op);
	}

	PrimitiveOperator(final String op, final IDescription species) {
		setName(op);
		sd = species;
	}

	@Override
	public PrimitiveOperator init(final String op, final IExpression left, final IExpression right,
		final IDescription context) {
		this.left = left;
		this.right = right;
		Facets facets = new Facets();
		facets.putAsLabel(IKeyword.ACTION, name); // TODO A vérifier
		IDescription cd =
			DescriptionFactory.createDescription(IKeyword.DO, sd, IKeyword.ACTION, name);
		command = new DoCommand(cd);
		type = command.getReturnType();
		contentType = command.getReturnContentType();
		command.setFormalArgs(createArgs());
		return this;
	}

	public void setTargetSpecies(final IDescription species) {
		sd = species;
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		IAgent target = Cast.asAgent(scope, left.value(scope));
		if ( target == null ) { return null; }
		if ( scope == null ) { return null; }
		Object result = scope.execute(command, target);
		return result;
	}

	private Arguments createArgs() {
		Arguments result = new Arguments();
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
		return new PrimitiveOperator(name, sd);
	}

	@Override
	public boolean isConst() {
		return false;
	}

}
