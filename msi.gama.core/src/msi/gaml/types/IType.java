/*********************************************************************************************
 *
 *
 * 'IType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
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

	public static String[] vowels = new String[] { "a", "e", "i", "o", "u", "y" };

	/** Constant fields to indicate the types of facets */
	public static final int LABEL = -200;
	public static final int ID = -201;
	public static final int TYPE_ID = -202;
	public static final int NEW_VAR_ID = -203;
	public static final int NEW_TEMP_ID = -204;

	public static final int NONE = 0;
	public final static int INT = 1;
	public final static int FLOAT = 2;
	public final static int BOOL = 3;
	public final static int STRING = 4;
	public final static int LIST = 5;
	public final static int COLOR = 6;
	public final static int POINT = 7;
	public final static int MATRIX = 8;
	public final static int PAIR = 9;
	public final static int MAP = 10;
	public final static int AGENT = 11;
	public final static int FILE = 12;
	public static final int GEOMETRY = 13;
	public final static int SPECIES = 14;
	public static final int GRAPH = 15;
	public static final int CONTAINER = 16;
	public static final int PATH = 17;
	public static final int TOPOLOGY = 18;

	public static final int FONT = 19;
	public static final int IMAGE = 20;
	public final static int REGRESSION = 21;
	public final static int SKILL = 22;
	public final static int DATE = 23;
	public final static int MESSAGE = 24;
	public final static int MATERIAL = 25;
	public final static int AVAILABLE_TYPES = 50;
	public final static int SPECIES_TYPES = 100;

	public Support cast(IScope scope, Object obj, Object param, boolean copy);

	public Support cast(IScope scope, Object obj, Object param, IType<?> keyType, IType<?> contentType, boolean copy);

	public int id();

	public Class<? extends Support> toClass();

	public Support getDefault();

	public int getVarKind();

	public OperatorProto getGetter(String name);

	public Map<String, OperatorProto> getFieldGetters();

	public boolean isAgentType();

	public boolean isSkillType();

	public boolean isParametricType();

	public boolean isParametricFormOf(final IType<?> l);

	public String getSpeciesName();

	public SpeciesDescription getSpecies();

	public boolean isAssignableFrom(IType<?> l);

	public boolean isTranslatableInto(IType<?> t);

	public void setParent(IType<? super Support> p);

	public IType<?> getParent();

	IType<?> coerce(IType<?> expr, IDescription context);

	/**
	 * returns the distance between two types
	 * 
	 * @param originalChildType
	 * @return
	 */
	public int distanceTo(IType<?> originalChildType);

	/**
	 * @param n
	 * @param typeFieldExpression
	 */
	public void setFieldGetters(Map<String, OperatorProto> map);

	/**
	 * @param c
	 * @return
	 */
	boolean canBeTypeOf(IScope s, Object c);

	public void init(int varKind, final int id, final String name, final Class<Support> clazz);

	/**
	 * Whether or not this type can be considered as having a contents. True for
	 * all containers and special types (like rgb, species, etc.)
	 * 
	 * @return
	 */
	// public abstract boolean hasContents();

	public abstract boolean isContainer();

	/**
	 * Whether or not this type can be used in add or remove statements
	 * 
	 * @return
	 */
	public abstract boolean isFixedLength();

	/**
	 * Tries to find a common supertype shared between this and the argument.
	 * 
	 * @param iType
	 * @return
	 */
	public IType<? super Support> findCommonSupertypeWith(IType<?> iType);

	public boolean isParented();

	public void setSupport(Class<Support> clazz);

	/**
	 * @param context
	 *            When casting an expression, the type returned is usually that
	 *            of this type. However, some types will compute another type
	 *            based on the type of the expressoin to cast (for instance,
	 *            species or agent)
	 * @param exp
	 * @return
	 */
	public IType<?> typeIfCasting(final IExpression exp);

	public IType<?> getContentType();

	public IType<?> getKeyType();

	/**
	 * @return
	 */
	public boolean canCastToConst();

	/**
	 * @return
	 */
	public String asPattern();

	/**
	 * @param plugin
	 *            name
	 */
	public void setDefiningPlugin(String plugin);

	public boolean isNumber();

	/**
	 * @return
	 */
	public boolean isDrawable();

	public IType<?> getWrappedType();

	SpeciesDescription getDenotedSpecies();

	/**
	 * Denotes a type that has components which can be exctracted when casting
	 * it to a container (for instance, points have float components). The inner
	 * type is returned by getContentType(). Containers are compound types by
	 * default
	 * 
	 * @return true if the type represents a compound value which components can
	 *         be extracted
	 */
	public boolean isCompoundType();

}