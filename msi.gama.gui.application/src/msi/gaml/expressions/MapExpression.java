/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
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
package msi.gaml.expressions;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;

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

	MapExpression(final GamaMap pairs) {
		keys = new IExpression[pairs.size()];
		vals = new IExpression[pairs.size()];
		int i = 0;
		for ( Map.Entry entry : pairs.entrySet() ) {
			keys[i] = (IExpression) entry.getKey();
			vals[i] = (IExpression) entry.getValue();
			i++;
		}
		values = new GamaMap(keys.length);
		setName(pairs.toString());
		type = Types.get(IType.MAP);
		determineContentType();
	}

	private void determineContentType() {
		IType previousType = Types.get(IType.NONE);
		contentType = previousType;
		for ( IExpression e : vals ) {
			IType type = e.type();
			if ( previousType == Types.get(IType.NONE) || type == previousType ) {
				contentType = type;
				previousType = type;
			} else if ( type != previousType ) {
				if ( type == Types.get(IType.INT) && contentType == Types.get(IType.FLOAT) ||
					type == Types.get(IType.FLOAT) && contentType == Types.get(IType.INT) ) {
					contentType = Types.get(IType.FLOAT);
					previousType = type;
				} else {
					contentType = Types.get(IType.NONE);
					previousType = type;
				}

			}
		}

	}

	@Override
	public GamaMap value(final IScope scope) throws GamaRuntimeException {
		if ( isConst && computed ) { return (GamaMap) values.clone(); }
		for ( int i = 0; i < keys.length; i++ ) {
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
			if ( !keys[i].isConst() || !vals[i].isConst() ) { return false; }
		}
		isConst = true;
		return true;
	}

	@Override
	public String toGaml() {
		final GamaList<GamaPair> list = getElements().listValue(null);
		return list.toGaml();
	}

	public IExpression[] keysArray() {
		return keys;
	}

	public IExpression[] valuesArray() {
		return vals;
	}

	public GamaMap getElements() {
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
			result.put(pair.left(), pair.right());
		}
		return result;
	}

}
