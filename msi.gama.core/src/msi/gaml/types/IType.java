/*******************************************************************************************************
 *
 * IType.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

	/** The vowels. */
	String[] vowels = { "a", "e", "i", "o", "u", "y" };

	/** Constant fields to indicate the types of facets */
	int LABEL = -200;

	/** The id. */
	int ID = -201;

	/** The type id. */
	int TYPE_ID = -202;

	/** The new var id. */
	int NEW_VAR_ID = -203;

	/** The new temp id. */
	int NEW_TEMP_ID = -204;

	/** The none. */
	int NONE = 0;

	/** The int. */
	int INT = 1;

	/** The float. */
	int FLOAT = 2;

	/** The bool. */
	int BOOL = 3;

	/** The string. */
	int STRING = 4;

	/** The list. */
	int LIST = 5;

	/** The color. */
	int COLOR = 6;

	/** The point. */
	int POINT = 7;

	/** The matrix. */
	int MATRIX = 8;

	/** The pair. */
	int PAIR = 9;

	/** The map. */
	int MAP = 10;

	/** The agent. */
	int AGENT = 11;

	/** The file. */
	int FILE = 12;

	/** The geometry. */
	int GEOMETRY = 13;

	/** The species. */
	int SPECIES = 14;

	/** The graph. */
	int GRAPH = 15;

	/** The container. */
	int CONTAINER = 16;

	/** The path. */
	int PATH = 17;

	/** The topology. */
	int TOPOLOGY = 18;

	/** The font. */
	int FONT = 19;

	/** The image. */
	int IMAGE = 20;

	/** The regression. */
	int REGRESSION = 21;

	/** The skill. */
	int SKILL = 22;

	/** The date. */
	int DATE = 23;

	/** The message. */
	int MESSAGE = 24;

	/** The material. */
	int MATERIAL = 25;

	/** The action. */
	int ACTION = 26;

	/** The attributes. */
	int ATTRIBUTES = 27;

	/** The type. */
	// Represents the meta-type (type of values type)
	int TYPE = 28;

	/** The kml. */
	int KML = 29;

	/** The directory. */
	int DIRECTORY = 30;

	/** The field. */
	int FIELD = 31;

	/** The available types. */
	int AVAILABLE_TYPES = 50;

	/** The species types. */
	int SPECIES_TYPES = 100;

	/**
	 * Cast.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @param param
	 *            the param
	 * @param copy
	 *            the copy
	 * @return the support
	 */
	Support cast(IScope scope, Object obj, Object param, boolean copy);

	/**
	 * Cast.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @param param
	 *            the param
	 * @param keyType
	 *            the key type
	 * @param contentType
	 *            the content type
	 * @param copy
	 *            the copy
	 * @return the support
	 */
	Support cast(IScope scope, Object obj, Object param, IType<?> keyType, IType<?> contentType, boolean copy);

	/**
	 * Id.
	 *
	 * @return the int
	 */
	int id();

	/**
	 * To class.
	 *
	 * @return the class<? extends support>
	 */
	Class<? extends Support> toClass();

	/**
	 * Gets the default.
	 *
	 * @return the default
	 */
	Support getDefault();

	/**
	 * Gets the var kind.
	 *
	 * @return the var kind
	 */
	int getVarKind();

	/**
	 * Gets the getter.
	 *
	 * @param name
	 *            the name
	 * @return the getter
	 */
	OperatorProto getGetter(String name);

	/**
	 * Gets the field getters.
	 *
	 * @return the field getters
	 */
	Map<String, OperatorProto> getFieldGetters();

	/**
	 * Checks if is agent type.
	 *
	 * @return true, if is agent type
	 */
	boolean isAgentType();

	/**
	 * Checks if is skill type.
	 *
	 * @return true, if is skill type
	 */
	boolean isSkillType();

	/**
	 * Checks if is parametric type.
	 *
	 * @return true, if is parametric type
	 */
	boolean isParametricType();

	/**
	 * Checks if is parametric form of.
	 *
	 * @param l
	 *            the l
	 * @return true, if is parametric form of
	 */
	boolean isParametricFormOf(final IType<?> l);

	/**
	 * Gets the species name.
	 *
	 * @return the species name
	 */
	String getSpeciesName();

	/**
	 * Gets the species.
	 *
	 * @return the species
	 */
	SpeciesDescription getSpecies();

	/**
	 * Checks if is assignable from.
	 *
	 * @param l
	 *            the l
	 * @return true, if is assignable from
	 */
	boolean isAssignableFrom(IType<?> l);

	/**
	 * Checks if is translatable into.
	 *
	 * @param t
	 *            the t
	 * @return true, if is translatable into
	 */
	boolean isTranslatableInto(IType<?> t);

	/**
	 * Sets the parent.
	 *
	 * @param p
	 *            the new parent
	 */
	void setParent(IType<? super Support> p);

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	IType<?> getParent();

	/**
	 * Coerce.
	 *
	 * @param expr
	 *            the expr
	 * @param context
	 *            the context
	 * @return the i type
	 */
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

	/**
	 * Inits the.
	 *
	 * @param varKind
	 *            the var kind
	 * @param id
	 *            the id
	 * @param name
	 *            the name
	 * @param clazz
	 *            the clazz
	 */
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

	/**
	 * Checks if is parented.
	 *
	 * @return true, if is parented
	 */
	boolean isParented();

	/**
	 * Sets the support.
	 *
	 * @param clazz
	 *            the new support
	 */
	void setSupport(Class<Support> clazz);

	/**
	 * @param context
	 *            When casting an expression, the type returned is usually that of this type. However, some types will
	 *            compute another type based on the type of the expressoin to cast (for instance, species or agent)
	 * @param exp
	 * @return
	 */
	IType<?> typeIfCasting(final IExpression exp);

	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	IType<?> getContentType();

	/**
	 * Gets the key type.
	 *
	 * @return the key type
	 */
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

	/**
	 * Checks if is number.
	 *
	 * @return true, if is number
	 */
	boolean isNumber();

	/**
	 * @return
	 */
	boolean isDrawable();

	/**
	 * Checks if is comparable.
	 *
	 * @return true, if is comparable
	 */
	default boolean isComparable() { return Comparable.class.isAssignableFrom(toClass()); }

	/**
	 * Gets the wrapped type.
	 *
	 * @return the wrapped type
	 */
	IType<?> getWrappedType();

	/**
	 * Gets the denoted species.
	 *
	 * @return the denoted species
	 */
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

	/**
	 * Document fields.
	 *
	 * @param result the result
	 */
	default void documentFields(final Doc result) {}

}