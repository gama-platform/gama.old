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
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IFieldGetter;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.*;
import org.eclipse.emf.common.notify.*;

public class TypeFieldExpression implements IExpression {

	IFieldGetter getter;
	protected IType type = null;
	protected String contentType = null;
	protected IType keyType = null;
	IExpression left;
	String name;

	public TypeFieldExpression(final String n, final IType type, final String contentType,
		final IType keyType, final IFieldGetter g) {
		setName(n);
		setType(type);
		setContentType(contentType);
		setKeyType(keyType);
		getter = g;
	}

	public TypeFieldExpression copyWith(final IExpression leftExpression) {
		TypeFieldExpression f = new TypeFieldExpression(name, type, contentType, keyType, getter);
		f.left = leftExpression;
		return f;
	}

	@Override
	public TypeFieldExpression resolveAgainst(final IScope scope) {
		return copyWith(left.resolveAgainst(scope));
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		Object parameter = left.value(scope);
		if ( parameter instanceof IValue ) { return getter.run(scope, (IValue) parameter); }
		return null;
	}

	public void setName(final String s) {
		name = s;
	}

	@Override
	public String getName() {
		return name;
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
	}

	private void setContentType(final String t) {
		contentType = t;
	}

	private void setKeyType(final IType t) {
		keyType = t == null ? type.defaultKeyType() : t;
	}

	@Override
	public String literalValue() {
		return name;
	}

	@Override
	public IType getType() {
		return type == null ? Types.NO_TYPE : type;
	}

	@Override
	public IType getContentType() {
		if ( !getType().hasContents() ) { return Types.NO_TYPE; }
		if ( contentType == null ) { return getType().defaultContentType(); }
		if ( ITypeProvider.SELF_TYPE.equals(contentType) ) {
			return left == null ? Types.NO_TYPE : left.getType();
		} else if ( ITypeProvider.CONTENT_TYPE.equals(contentType) ) {
			return left == null ? Types.NO_TYPE : left.getContentType();
		} else if ( ITypeProvider.INDEX_TYPE.equals(contentType) ) { return left == null
			? Types.NO_TYPE : left.getKeyType(); }
		// FIXME The model (or the types) should be known here
		IDescription d = GAMA.getModelContext();
		if ( d == null ) { return Types.get(contentType); }
		return d.getTypeNamed(contentType);
	}

	@Override
	public IType getKeyType() {
		if ( !getType().hasContents() ) { return Types.NO_TYPE; }
		return keyType == null ? getType().defaultKeyType() : keyType;
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return "Type " + getType() + " from object of type " + left.getType();
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
