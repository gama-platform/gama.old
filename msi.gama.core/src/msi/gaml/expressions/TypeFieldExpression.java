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

import msi.gama.common.interfaces.IValue;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IFieldGetter;
import msi.gaml.types.*;
import org.eclipse.emf.common.notify.*;

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

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return "Type " + type() + " from object of type " + left.type();
	}

	/**
	 * @see msi.gaml.descriptions.IGamlDescription#dispose()
	 */
	@Override
	public void dispose() {}

	/**
	 * @see msi.gaml.descriptions.IGamlDescription#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Field <b>" + name + "</b>";
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	@Override
	public void notifyChanged(final Notification notification) {}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#getTarget()
	 */
	@Override
	public Notifier getTarget() {
		return null;
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#setTarget(org.eclipse.emf.common.notify.Notifier)
	 */
	@Override
	public void setTarget(final Notifier newTarget) {}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#isAdapterForType(java.lang.Object)
	 */
	@Override
	public boolean isAdapterForType(final Object type) {
		return false;
	}

	@Override
	public void unsetTarget(final Notifier oldTarget) {}

}
