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
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.types.*;

/**
 * ListValueExpr.
 * 
 * @author drogoul 23 août 07
 */
public class MapExpression extends AbstractExpression {

	private final IExpression[] keys;
	private final IExpression[] vals;
	private final GamaMap values;
	private boolean isConst, computed;

	MapExpression(final List<? extends IExpression> pairs) {
		this(fromBinaryPairs((List<BinaryOperator>) pairs));
	}

	MapExpression(final GamaMap<IExpression, IExpression> pairs) {
		keys = new IExpression[pairs.size()];
		vals = new IExpression[pairs.size()];
		int i = 0;
		for ( Map.Entry<IExpression, IExpression> entry : pairs.entrySet() ) {
			keys[i] = entry.getKey();
			vals[i] = entry.getValue();
			i++;
		}
		values = new GamaMap(keys.length);
		setName(pairs.toString());
		type =
			GamaType.from(Types.get(IType.MAP), findCommonType(Arrays.asList(keys), _type),
				findCommonType(Arrays.asList(vals), _type));
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		GamaMap result = new GamaMap(keys.length);
		for ( int i = 0; i < keys.length; i++ ) {
			if ( keys[i] == null || vals[i] == null ) {
				continue;
			}
			result.put(keys[i].resolveAgainst(scope), vals[i].resolveAgainst(scope));
		}
		MapExpression copy = new MapExpression(getElements());
		return copy;
	}

	@Override
	public GamaMap value(final IScope scope) throws GamaRuntimeException {
		if ( isConst && computed ) { return (GamaMap) values.clone(); }
		for ( int i = 0; i < keys.length; i++ ) {
			if ( keys[i] == null || vals[i] == null ) {
				computed = false;
				return GamaMap.EMPTY_MAP;
			}
			values.put(keys[i].value(scope), vals[i].value(scope));
		}
		computed = true;
		return (GamaMap) values.clone();
	}

	@Override
	public String toString() {
		return getElements().toString();
	}

	@Override
	public boolean isConst() {
		for ( int i = 0; i < keys.length; i++ ) {
			// indicates an error in the compilation process of a former expression
			if ( keys[i] == null || vals[i] == null ) { return false; }
			if ( !keys[i].isConst() || !vals[i].isConst() ) { return false; }
		}
		isConst = true;
		return true;
	}

	@Override
	public String toGaml() {
		final StringBuilder sb = new StringBuilder();
		sb.append(' ').append('[');
		for ( int i = 0; i < keys.length; i++ ) {
			if ( i > 0 ) {
				sb.append(',');
			}
			if ( keys[i] == null || vals[i] == null ) {
				sb.append("nill::nil");
			} else {
				sb.append(keys[i].toGaml());
				sb.append("::");
				sb.append(vals[i].toGaml());
			}
		}
		sb.append(']').append(' ');
		return sb.toString();
	}

	public IExpression[] keysArray() {
		return keys;
	}

	public IExpression[] valuesArray() {
		return vals;
	}

	public GamaMap<IExpression, IExpression> getElements() {
		GamaMap result = new GamaMap(keys.length);
		for ( int i = 0; i < keys.length; i++ ) {
			result.put(keys[i], vals[i]);
		}
		return result;
	}

	public static GamaMap fromBinaryPairs(final List<BinaryOperator> pairs) {
		final GamaMap result = new GamaMap();
		for ( int i = 0, n = pairs.size(); i < n; i++ ) {
			BinaryOperator pair = pairs.get(i);
			if ( pair != null ) {
				result.put(pair.left(), pair.right());
			}
		}
		return result;
	}

	@Override
	public String getTitle() {
		return "literal map of type " + getType().getTitle();
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */

	@Override
	public String getDocumentation() {
		return "Constant " + isConst() + "<br>Contains elements of type " + type.getContentType().getTitle();
	}

}
