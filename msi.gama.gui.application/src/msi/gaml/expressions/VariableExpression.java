/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.expressions;

import msi.gama.interfaces.*;
import msi.gama.internal.expressions.IVarExpression;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamaRuntimeException;

public abstract class VariableExpression extends AbstractExpression implements IVarExpression {

	protected final Boolean isNotModifiable;

	protected VariableExpression(final String n, final IType type, final IType contentType,
		final boolean notModifiable) {
		setName(n);
		setType(type);
		setContentType(contentType);
		isNotModifiable = notModifiable;
	}

	@Override
	public abstract Object value(final IScope scope) throws GamaRuntimeException;

	@Override
	public String toGaml() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create)
		throws GamaRuntimeException {

	}

	@Override
	public void setType(final IType type) {
		this.type = type;
		if ( type.isSpeciesType() ) {
			setContentType(type);
		}
	}

	@Override
	public void setContentType(final IType t) {
		contentType =
			t == null || t == Types.NO_TYPE ? type.isSpeciesType() ? type : type
				.defaultContentType() : t;

	}

}
