/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
package msi.gama.internal.expressions;

import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamaRuntimeException;

public class TypeFieldExpression implements IExpression {

	IFieldGetter getter;
	protected IType type = null;
	protected IType contentType = null;
	IExpression left;
	String name;

	public TypeFieldExpression(final String n, final IType type, final IType contentType,
		final IFieldGetter g) {
		setName(n);
		setType(type);
		setContentType(contentType);
		getter = g;
	}

	public TypeFieldExpression copyWith(final IExpression leftExpression) {
		TypeFieldExpression f = new TypeFieldExpression(name, type, contentType, getter);
		f.left = leftExpression;
		return f;
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		Object parameter = left.value(scope);
		if ( parameter instanceof IValue ) { return getter.value((IValue) parameter); }
		return null;
	}

	public void setName(final String s) {
		name = s;
	}

	@Override
	public String toGaml() {
		return left.toGaml() + "." + name;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	private void setType(final IType type) {
		this.type = type;
		if ( type.isSpeciesType() ) {
			setContentType(type);
		}
	}

	private void setContentType(final IType t) {
		contentType =
			t == null || t == Types.NO_TYPE ? type.isSpeciesType() ? type : type
				.defaultContentType() : t;

	}

	@Override
	public String literalValue() {
		return name;
	}

	@Override
	public IType type() {
		return type == null ? Types.NO_TYPE : type;
	}

	@Override
	public IType getContentType() {
		return contentType == null ? Types.NO_TYPE : contentType;
	}

}
