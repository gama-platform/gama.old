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

import java.util.*;
import msi.gama.runtime.IScope;
import msi.gaml.types.*;
import org.eclipse.emf.common.notify.*;

/**
 * Abstract class that defines the structure of all expression classes.
 * 
 * @author drogoul
 */

public abstract class AbstractExpression implements IExpression {

	protected IType type = null;
	protected IType contentType = null;
	protected IType keyType = null;
	protected String name = null;
	protected IType elementsContentType = Types.NO_TYPE;
	protected IType elementsKeyType = Types.NO_TYPE;

	protected static final int _type = 0;
	protected static final int _content = 1;
	protected static final int _key = 2;

	@Override
	public String getName() {
		return name;
	}

	public void setName(final String s) {
		name = s;
	}

	@Override
	public IType getType() {
		return type == null ? Types.NO_TYPE : type;
	}

	@Override
	public IType getContentType() {
		if ( !getType().hasContents() ) { return Types.NO_TYPE; }
		return contentType == null ? getType().defaultContentType() : contentType;
	}

	@Override
	public IType getKeyType() {
		if ( !getType().hasContents() ) { return Types.NO_TYPE; }
		return keyType == null ? getType().defaultKeyType() : keyType;
	}

	protected String typeToString() {
		String t = type.toString();
		if ( type.hasContents() ) {
			t += "&lt;" + getKeyType().toString() + ", " + getContentType().toString() + "&gt;";
		}
		return t;
	}

	@Override
	public String literalValue() {
		return getName();
	}

	@Override
	public void dispose() {}

	@Override
	public void notifyChanged(final Notification notification) {}

	@Override
	public Notifier getTarget() {
		return null;
	}

	@Override
	public void setTarget(final Notifier newTarget) {}

	@Override
	public boolean isAdapterForType(final Object type) {
		return false;
	}

	@Override
	public void unsetTarget(final Notifier object) {

	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

	protected IType findCommonType(List<? extends IExpression> elements, int kind) {
		IType result = Types.NO_TYPE;
		if ( elements.isEmpty() ) { return result; }
		Set<IType> types = new LinkedHashSet();
		for ( IExpression e : elements ) {
			// TODO Indicates a previous error in compiling expressions. Maybe we should cut this
			// part
			if ( e == null ) {
				continue;
			}
			types.add(kind == _type ? e.getType() : kind == _content ? e.getContentType() : e
				.getKeyType());
		}
		IType[] array = types.toArray(new IType[types.size()]);
		result = array[0];
		if ( array.length == 1 ) { return result; }
		for ( int i = 1; i < array.length; i++ ) {
			result = result.findCommonSupertypeWith(array[i]);
		}
		return result;
	}

	@Override
	public IType getElementsContentType() {
		return elementsContentType == Types.NO_TYPE ? getContentType().defaultContentType()
			: elementsContentType;
	}

	@Override
	public IType getElementsKeyType() {
		return elementsKeyType == Types.NO_TYPE ? getContentType().defaultKeyType()
			: elementsKeyType;
	}

	@Override
	public void setElementsContentType(IType t) {
		elementsContentType = t;
	}

	@Override
	public void setElementsKeyType(IType t) {
		elementsKeyType = t;
	}

}
