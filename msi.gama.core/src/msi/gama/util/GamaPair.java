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
package msi.gama.util;

import java.util.Map;
import msi.gama.common.interfaces.IValue;
import msi.gama.common.util.StringUtils;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;

/**
 * The Class GamaPair.
 */
@vars({ @var(name = GamaPair.KEY, type = ITypeProvider.FIRST_KEY_TYPE),
	@var(name = GamaPair.VALUE, type = ITypeProvider.FIRST_CONTENT_TYPE) })
public class GamaPair<K, V> implements IValue, Map.Entry<K, V> {

	// TODO Makes it inherit from Map.Entry<K,V> in order to tighten the link between it and GamaMap
	// (have the entrySet() of GamaMap built from GamaPairs)
	// FIXME: This has still to be implemented

	public static final String KEY = "key";
	public static final String VALUE = "value";

	public K key;
	public V value;

	public GamaPair(final K k, final V v) {
		key = k;
		value = v;
	}

	public GamaPair(Map.Entry<K, V> entry) {
		this(entry.getKey(), entry.getValue());
	}

	public boolean equals(final GamaPair p) {
		return key.equals(p.key) && value.equals(p.value);
	}

	@Override
	public boolean equals(final Object a) {
		if ( a == null ) { return false; }
		if ( a instanceof GamaPair ) { return equals((GamaPair) a); }
		return false;
	}

	@Override
	@getter(KEY)
	public K getKey() {
		return key;
	}

	// FIXME: To be removed
	public K first() {
		return key;
	}

	@Override
	@getter(VALUE)
	public V getValue() {
		return value;
	}

	// FIXME: To be removed
	public V last() {
		return value;
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return Cast.asString(scope, key) + "::" + Cast.asString(scope, value);
	}

	@Override
	public String toGaml() {
		return "(" + StringUtils.toGaml(key) + ")" + "::" + "(" + StringUtils.toGaml(value) + ")";
	}

	@Override
	public String toString() {
		return (key == null ? "nil" : key.toString()) + "::" +
			(value == null ? "nil" : value.toString());
	}

	@Override
	public GamaPair<K, V> copy(IScope scope) {
		return new GamaPair(key, value);
	}

	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}

}
