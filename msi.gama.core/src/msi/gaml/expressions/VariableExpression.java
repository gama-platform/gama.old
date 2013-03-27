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

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.*;

public abstract class VariableExpression extends AbstractExpression implements IVarExpression {

	protected final Boolean isNotModifiable;
	private final IDescription definitionDescription;

	protected VariableExpression(final String n, final IType type, final IType contentType,
		IType keyType, final boolean notModifiable, final IDescription definitionDescription) {
		setName(n);
		setType(type);
		setContentType(contentType);
		setKeyType(keyType);
		isNotModifiable = notModifiable;
		this.definitionDescription = definitionDescription;
	}

	@Override
	public abstract Object value(final IScope scope) throws GamaRuntimeException;

	@Override
	public String toGaml() {
		return getName();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean isNotModifiable() {
		return isNotModifiable;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create)
		throws GamaRuntimeException {

	}

	public IDescription getDefinitionDescription() {
		return definitionDescription;
	}

	@Override
	public void setType(final IType type) {
		this.type = type;
	}

	@Override
	public void setContentType(final IType t) {
		contentType = t == null || t == Types.NO_TYPE ? type.defaultContentType() : t;

	}

	@Override
	public void setKeyType(final IType t) {
		keyType = t == null || t == Types.NO_TYPE ? type.defaultKeyType() : t;

	}

	@Override
	public String getTitle() {
		return isNotModifiable ? "Constant" : "Variable " + getName();
	}

}
