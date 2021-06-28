/*******************************************************************************************************
 *
 * msi.gaml.types.IType.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.util.Map;

import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.ITyped;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 9 juin 2010
 *
 * @todo Description
 *
 */
public interface IType<Support> extends IGamlDescription, ITyped {

	String[] vowels = new String[] { "a", "e", "i", "o", "u", "y" };

	/** Constant fields to indicate the types of facets */
	int LABEL = -200;
	int ID = -201;
	int TYPE_ID = -202;
	int NEW_VAR_ID = -203;
	int NEW_TEMP_ID = -204;

	int NONE = 0;
	int INT = 1;
	int FLOAT = 2;
	int BOOL = 3;
	int STRING = 4;
	int LIST = 5;
	int COLOR = 6;
	int POINT = 7;
	int MATRIX = 8;
	int PAIR = 9;
	int MAP = 10;
	int AGENT = 11;
	int FILE = 12;

	int GEOMETRY = 13;
	int SPECIES = 14;
	int GRAPH = 15;
	int CONTAINER = 16;
	int PATH = 17;
	int TOPOLOGY = 18;
	int FONT = 19;
	int IMAGE = 20;
	int REGRESSION = 21;
	int SKILL = 22;
	int DATE = 23;
	int MESSAGE = 24;
	int MATERIAL = 25;
	int ACTION = 26;
	int ATTRIBUTES = 27;

	// Represents the meta-type (type of values type)
	int TYPE = 28;
	int KML = 29;
	int DIRECTORY = 30;
	int FIELD = 31;

	int AVAILABLE_TYPES = 50;
	int SPECIES_TYPES = 100;

	Support cast(IScope scope, Object obj, Object param, boolean copy);

	Support cast(IScope scope, Object obj, Object param, IType<?> keyType, IType<?> contentType, boolean copy);

	int id();

	Class<? extends Support> toClass();

	Support getDefault();

	int getVarKind();

	OperatorProto getGetter(String name);

	Map<String, OperatorProto> getFieldGetters();

	boolean isAgentType();

	boolean isSkillType();

	boolean isParametricType();

	boolean isParametricFormOf(final IType<?> l);

	String getSpeciesName();

	SpeciesDescription getSpecies();

	boolean isAssignableFrom(IType<?> l);

	boolean isTranslatableInto(IType<?> t);

	void setParent(IType<? super Support> p);

	IType<?> getParent();

	IType<?> coerce(IType<?> expr, IDescription context);

	/**
	 * returns the distance between two types
	 *
	 * @param originalChildType
	 * @return
	 */
	int distanceTo(IType<?> originalChildType);

	/**
	 * @param n
	 * @param typeFieldExpression
	 */
	void setFieldGetters(Map<String, OperatorProto> map);

	/**
	 * @param c
	 * @return
	 */
	boolean canBeTypeOf(IScope s, Object c);

	void init(int varKind, final int id, final String name, final Class<Support> clazz);

	/**
	 * Whether or not this type can be considered as having a contents. True for all containers and special types (like
	 * rgb, species, etc.)
	 *
	 * @return
	 */
	// public abstract boolean hasContents();

	boolean isContainer();

	/**
	 * Whether or not this type can be used in add or remove statements
	 *
	 * @return
	 */
	boolean isFixedLength();

	/**
	 * Tries to find a common supertype shared between this and the argument.
	 *
	 * @param iType
	 * @return
	 */
	IType<? super Support> findCommonSupertypeWith(IType<?> iType);

	boolean isParented();

	void setSupport(Class<Support> clazz);

	/**
	 * @param context
	 *            When casting an expression, the type returned is usually that of this type. However, some types will
	 *            compute another type based on the type of the expressoin to cast (for instance, species or agent)
	 * @param exp
	 * @return
	 */
	IType<?> typeIfCasting(final IExpression exp);

	IType<?> getContentType();

	IType<?> getKeyType();

	/**
	 * @return
	 */
	boolean canCastToConst();

	/**
	 * @return
	 */
	String asPattern();

	/**
	 * @param plugin
	 *            name
	 */
	void setDefiningPlugin(String plugin);

	boolean isNumber();

	/**
	 * @return
	 */
	boolean isDrawable();

	default boolean isComparable() {
		return Comparable.class.isAssignableFrom(toClass());
	}

	IType<?> getWrappedType();

	SpeciesDescription getDenotedSpecies();

	/**
	 * Denotes a type that has components which can be exctracted when casting it to a container (for instance, points
	 * have float components). The inner type is returned by getContentType(). Containers are compound types by default
	 *
	 * @return true if the type represents a compound value which components can be extracted
	 */
	boolean isCompoundType();

	/**
	 * Returns the number of type parameters this type can accept (for instance list is 1, int is 0, map is 2, file
	 * depends on the wrapped buffer type, etc.)
	 *
	 * @return
	 */
	int getNumberOfParameters();

}