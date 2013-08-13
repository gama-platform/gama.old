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
package msi.gaml.types;

import java.util.Map;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;

/**
 * Written by drogoul Modified on 9 juin 2010
 * 
 * @todo Description
 * 
 */
public interface IType<Support> {

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
	public final static int SPECIES = 14;
	public final static int FILE = 12;
	public static final int GEOMETRY = 13;
	public static final int GRAPH = 15;
	public static final int PATH = 17;
	public static final int TOPOLOGY = 18;
	public static final int CONTAINER = 16;
	public final static int AVAILABLE_TYPES = 50;
	public final static int SPECIES_TYPES = 100;

	public Support cast(IScope scope, Object obj, Object param) throws GamaRuntimeException;

	public int id();

	public Class<Support> toClass();

	public Support getDefault();

	public int getVarKind();

	public IExpression getGetter(String name);

	public Map<String, ? extends IGamlDescription> getFieldDescriptions(ModelDescription model);

	public boolean isSpeciesType();

	public boolean isSkillType();

	public abstract IType defaultContentType();

	public abstract IType defaultKeyType();

	public String getSpeciesName();

	public SpeciesDescription getSpecies();

	public boolean isAssignableFrom(IType l);

	public boolean isTranslatableInto(IType t);

	public void setParent(IType p);

	public IType getParent();

	IType coerce(IType expr, IDescription context);

	/**
	 * returns the distance between two types
	 * @param originalChildType
	 * @return
	 */
	public int distanceTo(IType originalChildType);

	/**
	 * @param n
	 * @param typeFieldExpression
	 */
	public void setFieldGetters(Map<String, TypeFieldExpression> map);

	/**
	 * @param c
	 * @return
	 */
	boolean canBeTypeOf(IScope s, Object c);

	public void init(int varKind, final int id, final String name, final Class ... supports);

	/**
	 * Whether or not this type can be considered as having a contents. True for all containers and
	 * special types (like rgb, species, etc.)
	 * @return
	 */
	public abstract boolean hasContents();

	/**
	 * Whether or not this type can be used in add or remove statements
	 * @return
	 */
	public abstract boolean isFixedLength();

	/**
	 * Tries to find a common supertype shared between this and the argument.
	 * @param iType
	 * @return
	 */
	public IType findCommonSupertypeWith(IType iType);

	public boolean isParented();

	public void setSupport(Class clazz);

}