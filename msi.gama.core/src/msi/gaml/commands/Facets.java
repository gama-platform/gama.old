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
package msi.gaml.commands;

import java.util.*;
import msi.gama.common.util.StringUtils;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 27 aožt 2010
 * 
 * Represents a Map of Facet objects. From there, text, tokens and values of facets can be
 * retrieved.
 * 
 */
public class Facets extends HashMap<String, IExpressionDescription> {

	public Facets() {}

	public Facets(final String ... strings) {
		int index = 0;
		if ( !(strings.length % 2 == 0) ) {
			index = 1;
		}
		for ( ; index < strings.length; index += 2 ) {
			put(strings[index], strings[index + 1]);
		}
	}

	public String getLabel(final String key) {
		IExpressionDescription f = get(key);
		if ( f == null ) { return null; }
		return StringUtils.toJavaString(f.toString());
	}

	public IExpression getExpr(final String key) {
		return getExpr(key, null);
	}

	public IExpression getExpr(final String key, final IExpression ifAbsent) {
		IExpressionDescription f = get(key);
		if ( f == null ) { return ifAbsent; }
		return f.getExpression();
	}

	public IExpressionDescription put(final String key, final String desc) {
		return put(key, new StringBasedExpressionDescription(desc));
	}

	public IExpressionDescription putAsLabel(final String key, final String desc) {
		return put(key, new LabelExpressionDescription(desc));
	}

	public IExpressionDescription put(final String key, final IExpression expr) {
		IExpressionDescription result = get(key);
		if ( result != null ) {
			result.setExpression(expr);
			return result;
		}
		return put(key, new BasicExpressionDescription(expr));
	}

	@Override
	public IExpressionDescription put(final String key, final IExpressionDescription expr) {
		IExpressionDescription previous = get(key);
		super.put(key, expr);
		if ( previous != null ) {
			if ( previous.getAst() != null && expr.getAst() == null ) {
				expr.setAst(previous.getAst());
			}
		}
		return expr;
	}

	public boolean equals(final String key, final String o) {
		IExpressionDescription f = get(key);
		return f == null ? o == null : f.equalsString(o);
	}

	public void dispose() {
		for ( Map.Entry<String, IExpressionDescription> entry : entrySet() ) {
			entry.getValue().dispose();
		}
		clear();
	}

}
