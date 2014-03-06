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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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

/**
 * Abstract class that defines the structure of all expression classes.
 * 
 * @author drogoul
 */

public abstract class AbstractExpression implements IExpression {

	protected IType type = null;
	protected String name = null;

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
	public String literalValue() {
		return getName();
	}

	@Override
	public void dispose() {}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

	protected IType findCommonType(final List<? extends IExpression> elements, final int kind) {
		IType result = Types.NO_TYPE;
		if ( elements.isEmpty() ) { return result; }
		final Set<IType> types = new LinkedHashSet();
		for ( final IExpression e : elements ) {
			// TODO Indicates a previous error in compiling expressions. Maybe we should cut this
			// part
			if ( e == null ) {
				continue;
			}
			IType eType = e.getType();
			types.add(kind == _type ? eType : kind == _content ? eType.getContentType() : eType.getKeyType());
		}
		final IType[] array = types.toArray(new IType[types.size()]);
		if ( array.length == 0 ) { return result; }
		result = array[0];
		if ( array.length == 1 ) { return result; }
		for ( int i = 1; i < array.length; i++ ) {
			IType currentType = array[i];
			if ( currentType == Types.NO_TYPE ) {
				if ( result.getDefault() != null ) {
					result = Types.NO_TYPE;
				}
			} else {
				result = result.findCommonSupertypeWith(array[i]);
			}
		}
		return result;
	}

	protected String parenthesize(final IExpression ... exp) {
		if ( exp.length == 1 && !exp[0].shouldBeParenthesized() ) { return " " + exp[0].toGaml() + " "; }
		return surround('(', ')', exp);
	}

	protected String surround(final char first, final char last, final IExpression ... exp) {
		final StringBuilder sb = new StringBuilder();
		sb.append(' ').append(first);
		for ( int i = 0; i < exp.length; i++ ) {
			if ( i > 0 ) {
				sb.append(',');
			}
			sb.append(exp[i].toGaml());
		}
		sb.append(last).append(' ');
		return sb.toString();
	}

	@Override
	public boolean shouldBeParenthesized() {
		return true;
	}

}
