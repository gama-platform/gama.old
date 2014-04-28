/*********************************************************************************************
 * 
 * 
 * 'ParametricType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.types;

import java.util.Map;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;

/**
 * Class ParametrizedType. A class that allows to build composed types with a content type and a key type
 * 
 * @author drogoul
 * @since 19 janv. 2014
 * 
 */
public class ParametricType implements IContainerType {

	private final IContainerType type;
	private final IType contentsType;
	private final IType keyType;

	protected ParametricType(final IContainerType t, final IType kt, final IType ct) {
		type = t;
		contentsType = ct;
		keyType = kt;
	}

	/**
	 * Method getType()
	 * @see msi.gama.common.interfaces.ITyped#getType()
	 */
	@Override
	public IContainerType getType() {
		return type;
	}

	/**
	 * Method cast()
	 * @see msi.gaml.types.IType#cast(msi.gama.runtime.IScope, java.lang.Object, java.lang.Object, msi.gaml.types.IType, msi.gaml.types.IType)
	 */
	@Override
	public IContainer cast(final IScope scope, final Object obj, final Object param, final IType kt, final IType ct)
		throws GamaRuntimeException {
		return type.cast(scope, obj, param, keyType, contentsType);
	}

	/**
	 * Method id()
	 * @see msi.gaml.types.IType#id()
	 */
	@Override
	public int id() {
		return type.id();
		// return type.id() + keyType.id() * 100 + contentsType.id() * 1000;
	}

	/**
	 * Method toClass()
	 * @see msi.gaml.types.IType#toClass()
	 */
	@Override
	public Class toClass() {
		return type.toClass();
	}

	/**
	 * Method getDefault()
	 * @see msi.gaml.types.IType#getDefault()
	 */
	@Override
	public Object getDefault() {
		return type.getDefault();
	}

	/**
	 * Method getVarKind()
	 * @see msi.gaml.types.IType#getVarKind()
	 */
	@Override
	public int getVarKind() {
		return ISymbolKind.Variable.CONTAINER;
	}

	/**
	 * Method getGetter()
	 * @see msi.gaml.types.IType#getGetter(java.lang.String)
	 */
	@Override
	public OperatorProto getGetter(final String name) {
		return type.getGetter(name);
	}

	/**
	 * Method getFieldDescriptions()
	 * @see msi.gaml.types.IType#getFieldDescriptions(msi.gaml.descriptions.ModelDescription)
	 */
	// @Override
	// public Map getFieldDescriptions(final ModelDescription model) {
	// return type.getFieldDescriptions(model);
	// }

	/**
	 * Method isSpeciesType()
	 * @see msi.gaml.types.IType#isSpeciesType()
	 */
	@Override
	public boolean isAgentType() {
		// Verify this
		return type.isAgentType();
	}

	/**
	 * Method isSkillType()
	 * @see msi.gaml.types.IType#isSkillType()
	 */
	@Override
	public boolean isSkillType() {
		return false;
	}

	/**
	 * Method defaultContentType()
	 * @see msi.gaml.types.IType#defaultContentType()
	 */
	@Override
	public IType getContentType() {
		return contentsType;
	}

	/**
	 * Method defaultKeyType()
	 * @see msi.gaml.types.IType#defaultKeyType()
	 */
	@Override
	public IType getKeyType() {
		return keyType;
	}

	/**
	 * Method getSpeciesName()
	 * @see msi.gaml.types.IType#getSpeciesName()
	 */
	@Override
	public String getSpeciesName() {
		return type.getSpeciesName();
	}

	/**
	 * Method getSpecies()
	 * @see msi.gaml.types.IType#getSpecies()
	 */
	@Override
	public SpeciesDescription getSpecies() {
		return type.getSpecies();
	}

	/**
	 * Method isAssignableFrom()
	 * @see msi.gaml.types.IType#isAssignableFrom(msi.gaml.types.IType)
	 */
	@Override
	public boolean isAssignableFrom(final IType l) {
		return type.isAssignableFrom(l.getType()) && contentsType.isAssignableFrom(l.getContentType()) &&
			keyType.isAssignableFrom(l.getKeyType());
	}

	/**
	 * Method isTranslatableInto()
	 * @see msi.gaml.types.IType#isTranslatableInto(msi.gaml.types.IType)
	 */
	@Override
	public boolean isTranslatableInto(final IType l) {
		return type.isTranslatableInto(l.getType()) && contentsType.isTranslatableInto(l.getContentType()) &&
			keyType.isTranslatableInto(l.getKeyType());
	}

	/**
	 * Method setParent()
	 * @see msi.gaml.types.IType#setParent(msi.gaml.types.IType)
	 */
	@Override
	public void setParent(final IType p) {
		// if ( p instanceof GamaContainerType ) {
		// type = (GamaContainerType) p;
		// }
	}

	/**
	 * Method getParent()
	 * @see msi.gaml.types.IType#getParent()
	 */
	@Override
	public IType getParent() {
		return type;
	}

	/**
	 * Method coerce()
	 * @see msi.gaml.types.IType#coerce(msi.gaml.types.IType, msi.gaml.descriptions.IDescription)
	 */
	@Override
	public IType coerce(final IType expr, final IDescription context) {
		return null;
	}

	/**
	 * Method distanceTo()
	 * @see msi.gaml.types.IType#distanceTo(msi.gaml.types.IType)
	 */
	@Override
	public int distanceTo(final IType t) {
		return t.getType().distanceTo(type) + t.getContentType().distanceTo(contentsType) +
			t.getKeyType().distanceTo(keyType);
	}

	/**
	 * Method setFieldGetters()
	 * @see msi.gaml.types.IType#setFieldGetters(java.util.Map)
	 */
	@Override
	public void setFieldGetters(final Map map) {}

	/**
	 * Method canBeTypeOf()
	 * @see msi.gaml.types.IType#canBeTypeOf(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public boolean canBeTypeOf(final IScope s, final Object c) {
		return type.canBeTypeOf(s, c);
	}

	/**
	 * Method init()
	 * @see msi.gaml.types.IType#init(int, int, java.lang.String, java.lang.Class[])
	 */
	@Override
	public void init(final int varKind, final int id, final String name, final Class ... supports) {}

	/**
	 * Method isContainer()
	 * @see msi.gaml.types.IType#isContainer()
	 */
	@Override
	public boolean isContainer() {
		return true; // ???
	}

	/**
	 * Method isFixedLength()
	 * @see msi.gaml.types.IType#isFixedLength()
	 */
	@Override
	public boolean isFixedLength() {
		return type.isFixedLength();
	}

	/**
	 * Method findCommonSupertypeWith()
	 * @see msi.gaml.types.IType#findCommonSupertypeWith(msi.gaml.types.IType)
	 */
	@Override
	public IType findCommonSupertypeWith(final IType iType) {
		if ( iType instanceof ParametricType ) {
			IType pType = iType;
			IType cType = type.findCommonSupertypeWith(pType.getType());
			if ( cType.isContainer() ) {
				IType kt = keyType.findCommonSupertypeWith(pType.getKeyType());
				IType ct = contentsType.findCommonSupertypeWith(pType.getContentType());
				return GamaType.from(cType, kt, ct);
			} else {
				return cType;
			}
		} else if ( iType.isContainer() ) {
			IType cType = type.findCommonSupertypeWith(iType);
			return cType; // dont we need to use the key and contents type here ?
		} else {
			return type.findCommonSupertypeWith(iType);
		}
	}

	/**
	 * Method isParented()
	 * @see msi.gaml.types.IType#isParented()
	 */
	@Override
	public boolean isParented() {
		return type != null; // ??
	}

	/**
	 * Method setSupport()
	 * @see msi.gaml.types.IType#setSupport(java.lang.Class)
	 */
	@Override
	public void setSupport(final Class clazz) {}

	@Override
	public IContainer cast(final IScope scope, final Object obj, final Object param) throws GamaRuntimeException {
		return type.cast(scope, obj, param, keyType, contentsType);
	}

	@Override
	public String toString() {
		if ( type.id() == IType.LIST || type.id() == IType.MATRIX || type.id() == IType.CONTAINER &&
			keyType == Types.NO_TYPE ) { return type.toString() + "<" + contentsType.toString() + ">"; }
		if ( type.id() == IType.SPECIES ) { return type.toString() + "<subspecies of " + contentsType.toString() + ">"; }
		return type.toString() + "<" + keyType.toString() + ", " + contentsType.toString() + ">";
	}

	@Override
	public IContainerType typeIfCasting(final IExpression exp) {
		if ( contentsType == Types.NO_TYPE || keyType == Types.NO_TYPE ) {
			IType genericCast = type.typeIfCasting(exp);
			IType ct = contentsType == Types.NO_TYPE ? genericCast.getContentType() : contentsType;
			IType kt = keyType == Types.NO_TYPE ? genericCast.getKeyType() : keyType;
			return new ParametricType(type, kt, ct);
		}
		return this;
	}

	/**
	 * Method getTitle()
	 * @see msi.gaml.descriptions.IGamlDescription#getTitle()
	 */
	@Override
	public String getTitle() {
		return toString().replace('<', '[').replace('>', ']');
	}

	/**
	 * Method getDocumentation()
	 * @see msi.gaml.descriptions.IGamlDescription#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		return getTitle();
	}

	/**
	 * Method getName()
	 * @see msi.gaml.descriptions.IGamlDescription#getName()
	 */
	@Override
	public String getName() {
		return toString();
	}

	@Override
	public boolean canCastToConst() {
		return type.canCastToConst() && contentsType.canCastToConst() && keyType.canCastToConst();
	}

}
