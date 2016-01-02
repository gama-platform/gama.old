/*********************************************************************************************
 *
 *
 * 'GamaType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.interfaces.IValue;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaBundleLoader;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 25 ao�t 2010
 *
 * The superclass of all types descriptions in GAMA. Provides convenience methods, as well as some
 * basic definitions. Types allow to manipulate any Java class as a type in GAML. To be recognized
 * by GAML, subclasses must be annotated with the @type annotation (see GamlAnnotations).
 *
 * Types are primarily used for conversions between values. They are also intended to support the
 * operators specific to the objects they encompass (but this is not mandatory, as these operators
 * need to be defined as static ones (and thus can be defined anywhere)
 *
 * Primary (simple) types also serve as the basis of parametric types (see ParametricType).
 *
 */

public abstract class GamaType<Support> implements IType<Support> {

	protected int id;
	protected String name;
	protected Class[] supports;
	Map<String, OperatorProto> getters;
	protected IType parent;
	protected boolean parented;
	protected int varKind;
	protected final String plugin;

	public GamaType() {
		plugin = GamaBundleLoader.CURRENT_PLUGIN_NAME;
	}

	@Override
	public String getTitle() {
		return getName();
	}

	@Override
	public String getDefiningPlugin() {
		return plugin;
	}

	@Override
	public String getDocumentation() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		// Nothing
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return name;
	}

	@Override
	public void init(final int varKind, final int id, final String name, final Class ... supports) {
		this.varKind = varKind;
		this.id = id;
		this.name = name;
		this.supports = supports;
	}

	@Override
	public void setSupport(final Class clazz) {
		supports = new Class[] { clazz };
	}

	@Override
	public int getVarKind() {
		return varKind;
	}

	@Override
	public void setParent(final IType p) {
		parented = true;
		parent = p;
	}

	@Override
	public IType getParent() {
		return parent;
	}

	@Override
	public void setFieldGetters(final Map<String, OperatorProto> map) {
		getters = map;
		// AD 20/09/13 Added the initialization of the type containing the fields
		for ( OperatorProto t : map.values() ) {
			t.setSignature(this);
		}
	}

	@Override
	public OperatorProto getGetter(final String field) {
		if ( getters == null ) { return null; }
		return getters.get(field);
	}

	//
	// @Override
	// public Map<String, ? extends IGamlDescription> getFieldDescriptions(final ModelDescription desc) {
	// if ( getters == null ) { return Collections.EMPTY_MAP; }
	// return getters;
	// }

	@Override
	public abstract Support cast(IScope scope, final Object obj, final Object param, boolean copy)
		throws GamaRuntimeException;

	@Override
	public Support cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentType, final boolean copy) throws GamaRuntimeException {
		// by default
		return cast(scope, obj, param, copy);
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(final Object c) {
		if ( c instanceof IType ) { return ((IType) c).id() == id; }
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String asPattern() {
		boolean vowel = StringUtils.startsWithAny(name, vowels);
		return "${" + (vowel ? "an_" : "a_") + name + "}";
	}

	@Override
	public Class toClass() {
		return supports[0];
	}

	@Override
	public abstract Support getDefault();

	@Override
	public boolean isAgentType() {
		return false;
	}

	@Override
	public boolean isSkillType() {
		return false;
	}

	@Override
	public IType getContentType() {
		return Types.NO_TYPE;
	}

	@Override
	public IType getKeyType() {
		return Types.NO_TYPE;
	}

	@Override
	public String getSpeciesName() {
		return null;
	}

	@Override
	public SpeciesDescription getSpecies() {
		return null;
	}

	@Override
	public boolean isParented() {
		return parented;
	}

	protected boolean isSuperTypeOf(final IType type) {
		if ( type == null ) { return false; }
		if ( parented && type.isParented() ) { return type == this || isSuperTypeOf(type.getParent()); }
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

	@Override
	public boolean isParametricType() {
		return false;
	}

	@Override
	public boolean isParametricFormOf(final IType l) {
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
		if ( parented ) {
			if ( isSuperTypeOf(type) ) { return 1 + distanceTo(type.getParent()); }
			return 1 + getParent().distanceTo(type);
		}
		if ( isTranslatableInto(type) ) { return 1; }
		if ( isAssignableFrom(type) ) { return 1; }
		return Integer.MAX_VALUE;
	}

	@Override
	public IType findCommonSupertypeWith(final IType type) {
		if ( type == this ) { return this; }
		if ( type == Types.NO_TYPE ) { return getDefault() == null ? this : type; }
		if ( type.isTranslatableInto(this) ) { return this; }
		if ( this.isTranslatableInto(type) ) { return type; }
		return getParent().findCommonSupertypeWith(type.getParent());
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean isFixedLength() {
		return true;
	}

	public static Object toType(final IScope scope, final Object value, final IType type, final boolean copy) {
		if ( type == null || type.id() == IType.NONE ) { return value; }
		return type.cast(scope, value, null, Types.NO_TYPE, Types.NO_TYPE, copy);
	}

	public IType keyTypeIfCasting(final IExpression exp) {
		return getKeyType();
	}

	public IType contentsTypeIfCasting(final IExpression exp) {
		return getContentType();
	}

	@Override
	public IType getType() {
		return this;
	}

	@Override
	public IType typeIfCasting(final IExpression exp) {
		return from(this, keyTypeIfCasting(exp), contentsTypeIfCasting(exp));
	}

	public static IType from(final TypeDescription species) {
		return from(Types.SPECIES, Types.INT, species.getType());
	}

	public static IContainerType from(final IContainerType t, final IType keyType, final IType contentType) {
		if ( keyType == Types.NO_TYPE && contentType == Types.NO_TYPE ) { return t; }
		IType kt = keyType == Types.NO_TYPE ? t.getType().getKeyType() : keyType;
		IType ct = contentType == Types.NO_TYPE ? t.getType().getContentType() : contentType;
		return new ParametricType(t.getType(), kt, ct);
	}

	public static IType from(final IType t, final IType keyType, final IType contentType) {
		if ( t instanceof IContainerType ) { return from((IContainerType) t, keyType, contentType); }
		return t;
	}

	public static final int TYPE = 0;
	public static final int CONTENT = 1;
	public static final int KEY = 2;

	public static IType findCommonType(final IExpression[] elements, final int kind) {
		IType result = Types.NO_TYPE;
		if ( elements.length == 0 ) { return result; }
		final Set<IType> types = new TLinkedHashSet();
		for ( final IExpression e : elements ) {
			// TODO Indicates a previous error in compiling expressions. Maybe we should cut this
			// part
			if ( e == null ) {
				continue;
			}
			IType eType = e.getType();
			types.add(kind == TYPE ? eType : kind == CONTENT ? eType.getContentType() : eType.getKeyType());
		}
		final IType[] array = types.toArray(new IType[types.size()]);
		return findCommonType(array);
	}

	public static IType findCommonType(final IType ... types) {
		IType result = Types.NO_TYPE;
		if ( types.length == 0 ) { return result; }
		result = types[0];
		if ( types.length == 1 ) { return result; }
		for ( int i = 1; i < types.length; i++ ) {
			IType currentType = types[i];
			if ( currentType == Types.NO_TYPE ) {
				if ( result.getDefault() != null ) {
					result = Types.NO_TYPE;
				}
			} else {
				result = result.findCommonSupertypeWith(currentType);
			}
		}
		return result;
	}

	/**
	 * Return the type of the object passed in parameter
	 * @param obj
	 * @return
	 */
	public static IType of(final Object obj) {
		if ( obj instanceof IValue ) { return ((IValue) obj).getType(); }
		if ( obj instanceof IExpression ) { return ((IExpression) obj).getType(); }
		if ( obj == null ) { return Types.NO_TYPE; }
		return Types.get(obj.getClass());
	}

	/**
	 * @return
	 */
	public static IType findSpecificType(final IType castingType, final IType originalType) {
		return requiresCasting(castingType, originalType) ? castingType : originalType;
	}

	public static boolean requiresCasting(final IType castingType, final IType originalType) {
		if ( castingType == null || castingType == Types.NO_TYPE ||
			castingType.isAssignableFrom(originalType) ) { return false; }
		return true;
	}

}
