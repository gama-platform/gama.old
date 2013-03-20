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

import java.util.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.types.*;

/**
 * ListValueExpr.
 * 
 * @author drogoul 23 ao√ªt 07
 */
public class ListExpression extends AbstractExpression {

	final IExpression[] elements;
	private final Object[] values;
	private boolean isConst, computed;

	ListExpression(final List<? extends IExpression> elements) {
		this.elements = elements.toArray(new IExpression[0]);
		int n = this.elements.length;
		values = new Object[n];
		setName(elements.toString());
		type = Types.get(IType.LIST);
		boolean allTheSame = true;
		if ( n != 0 ) {
			IExpression e = elements.get(0);
			if ( e != null ) {
				contentType = e.getType();
			}
			for ( int i = 1; i < n; i++ ) {
				e = elements.get(i);
				if ( e != null ) {
					allTheSame = e.getType() == contentType;
				}
				if ( !allTheSame ) {
					break;
				}
			}
		}
		// TODO Try to find a common super type if possible
		contentType = allTheSame ? contentType : Types.NO_TYPE;
		isConst();
	}

	public IExpression[] getElements() {
		return elements;
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		ListExpression copy = new ListExpression(Arrays.asList(elements));
		for ( int i = 0; i < elements.length; i++ ) {
			IExpression exp = elements[i];
			if ( exp != null ) {
				copy.elements[i] = exp.resolveAgainst(scope);
			}
		}
		return copy;
	}

	@Override
	public GamaList value(final IScope scope) throws GamaRuntimeException {
		if ( isConst && computed ) { return new GamaList(values); }
		for ( int i = 0; i < elements.length; i++ ) {
			values[i] = elements[i].value(scope);
		}
		computed = true;
		// Important NOT to return the reference to values (but a copy of it).
		return new GamaList(values);
	}

	@Override
	public String toString() {
		return Arrays.toString(elements);
	}

	@Override
	public boolean isConst() {
		for ( final IExpression e : elements ) {
			if ( e != null && !e.isConst() ) { return false; }
		}
		isConst = true;
		return true;
	}

	@Override
	public String toGaml() {
		StringBuilder sb = new StringBuilder(elements.length * 5);
		sb.append('[');
		for ( int i = 0; i < elements.length; i++ ) {
			if ( i > 0 ) {
				sb.append(',');
			}
			sb.append(elements[i].toGaml());
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public String getTitle() {
		return "Literal list expression";
	}

	@Override
	public String getDocumentation() {
		return "Constant " + isConst() + "<br>Contains elements of type " + contentType;
	}

}
