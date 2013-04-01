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
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;

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

public abstract class GamaType<Support> implements IType<Support> {

	protected short id;
	protected String name;
	protected Class[] supports;
	Map<String, TypeFieldExpression> getters = new HashMap();
	protected IType parent;
	protected Map<String, IType> children = new HashMap();
	protected boolean inited;
	protected int varKind;

	@Override
	public void init(final int varKind, final short id, final String name, final Class ... supports) {
		this.varKind = varKind;
		this.id = id;
		this.name = name;
		this.supports = supports;
	}

	@Override
	public int getVarKind() {
		return varKind;
	}

	@Override
	public void setParent(final IType p) {
		inited = true;
		parent = p;
		if ( p != null ) {
			p.addSubType(this);
		}
	}

	@Override
	public void addSubType(final IType p) {
		children.put(p.toString(), p);
	}

	@Override
	public Collection<IType> getSubTypes() {
		return children.values();
	}

	@Override
	public void clearSubTypes() {
		// parent = null;
		for ( IType t : children.values() ) {
			t.clearSubTypes();
		}
		children.clear();
	}

	@Override
	public IType getParent() {
		return parent;
	}

	@Override
	public void setFieldGetters(final Map<String, TypeFieldExpression> map) {
		getters = map;
	}

	@Override
	public IExpression getGetter(final String field) {
		return getters.get(field);
	}

	@Override
	public Map<String, ? extends IGamlDescription> getFieldDescriptions(final ModelDescription desc) {
		return getters;
	}

	@Override
	public abstract Support cast(IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException;

	@Override
	public short id() {
		return id;
	}

	@Override
	public int hashCode() {
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
	public abstract Support getDefault();

	@Override
	public boolean isSpeciesType() {
		return false;
	}

	@Override
	public boolean isSkillType() {
		return false;
	}

	@Override
	public IType defaultContentType() {
		return Types.NO_TYPE;
	}

	@Override
	public IType defaultKeyType() {
		return Types.NO_TYPE;
	}

	@Override
	public String getSpeciesName() {
		return null;
	}

	protected boolean isSuperTypeOf(final IType type) {
		if ( type == null ) { return false; }
		if ( inited ) { return type == this || isSuperTypeOf(type.getParent()); }
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
	public boolean isTranslatableInto(final IType t) {
		return t.isAssignableFrom(this);
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
		if ( type == this ) { return 0; }
		if ( type == null ) { return Integer.MAX_VALUE; }
		if ( inited ) {
			if ( isSuperTypeOf(type) ) { return 1 + distanceTo(type.getParent()); }
			return 1 + getParent().distanceTo(type);
		}
		if ( isTranslatableInto(type) ) { return 1; }
		if ( isAssignableFrom(type) ) { return 1; }
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean hasContents() {
		return false;
	}

	@Override
	public boolean isFixedLength() {
		return true;
	}
}
