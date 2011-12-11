/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.expressions;

import msi.gama.factories.DescriptionFactory;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.internal.expressions.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.util.Cast;
import msi.gaml.commands.DoCommand;

/**
 * PrimitiveOperator. An operator that wraps a primitive and defines its own scope for running it.
 * 
 * @author drogoul 4 sept. 07
 */

public class PrimitiveOperator extends AbstractBinaryOperator {

	ICommand.WithArgs	command;
	IDescription		sd;

	public PrimitiveOperator(final String op) {
		setName(op);
	}

	PrimitiveOperator(final String op, final IDescription species) {
		setName(op);
		sd = species;
	}

	@Override
	public PrimitiveOperator init(final String op, final IExpression left, final IExpression right)
		throws GamlException {
		this.left = left;
		this.right = right;
		Facets facets = new Facets();
		facets.putAsLabel(ISymbol.ACTION, name); // TODO A vérifier
		IDescription cd =
			DescriptionFactory.createDescription(ISymbol.DO, sd, ISymbol.ACTION, name);
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
