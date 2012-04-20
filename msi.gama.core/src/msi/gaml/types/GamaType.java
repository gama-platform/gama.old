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
package msi.gaml.types;

import java.util.*;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 25 aožt 2010
 * 
 * The superclass of all types descriptions in GAMA. Provides convenience methods, as well as some
 * basic definitions. Types allow to manipulate any Java class as a type in GAML. To be recognized
 * by GAML, subclasses must be annotated with the @type annotation (see GamlAnnotations).
 * 
 * Types are primarily used for conversions between values. They are also intended to support the
 * operators specific to the objects they encompass (but this is not mandatory, as these operators
 * need to be defined as static ones (and thus can be defined anywhere)
 * 
 */

public abstract class GamaType<Inner> implements IType<Inner> {

	@Override
	public int compareTo(final IType o) {
		if ( isSuperTypeOf(o) ) { return -1; }
		// if ( isSubTypeOf(o) ) { return 1; }
		return 1;
	}

	protected short id;
	protected String name;
	protected Class[] supports;
	Map<String, IExpression> getters = new HashMap();

	public GamaType() {
		type annotation = getClass().getAnnotation(type.class);
		if ( annotation != null ) {
			name = annotation.value();
			id = annotation.id();
			supports = annotation.wraps();
		}
	}

	@Override
	public void addFieldGetter(final String n, final IExpression typeFieldExpression) {
		getters.put(n, typeFieldExpression);
	}

	@Override
	public IExpression getGetter(final String field) {
		return getters.get(field);
	}

	@Override
	public abstract Inner cast(IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException;

	@Override
	public short id() {
		return id;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Class toClass() {
		return supports[0];
	}

	@Override
	public abstract Inner getDefault();

	@Override
	public boolean isSpeciesType() {
		return false;
	}

	@Override
	public IType defaultContentType() {
		return this;
	}

	@Override
	public String getSpeciesName() {
		return null;
	}

	protected boolean isSuperTypeOf(final IType type) {
		Class remote = type.toClass();
		for ( int i = 0; i < supports.length; i++ ) {
			if ( supports[i].isAssignableFrom(remote) ) { return true; }
		}
		return false;
	}

	@Override
	public boolean isAssignableFrom(final IType t) {
		return t == null ? false : this == t || isSuperTypeOf(t);
	}

	@Override
	public boolean canBeTypeOf(final IScope scope, final Object c) {
		if ( c == null ) { return acceptNullInstances(); }
		for ( int i = 0; i < supports.length; i++ ) {
			if ( supports[i].isAssignableFrom(c.getClass()) ) { return true; }
		}
		return false;
	}

	/**
	 * @return true if this type can have nil as an instance
	 */
	protected boolean acceptNullInstances() {
		return getDefault() == null;
	}

	@Override
	public IType coerce(final IType expr, final IDescription context) {
		// Nothing to do in the general case : we rely on Java polymorphism.
		return null;
	}

	@Override
	public int distanceTo(final IType type) {
		if ( this == type ) { return 0; }
		if ( type.isAssignableFrom(this) || this.isAssignableFrom(type) ) { return 1; }
		return Integer.MAX_VALUE;
	}
}
