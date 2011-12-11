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
package msi.gama.interfaces;

import msi.gama.internal.expressions.IExpressionFactory;
import msi.gama.kernel.exceptions.*;

/**
 * Written by drogoul Modified on 9 juin 2010
 * 
 * @todo Description
 * 
 */
public interface IType<Inner> extends Comparable<IType> {

	/** Constant fields to indicate the types of facets */
	public static final String LABEL = "label";
	public static final String ID = "id";
	public static final String TYPE_ID = "type";
	public static final String NEW_VAR_ID = "new_var";
	public static final String NEW_TEMP_ID = "new_temp";
	/** Constant field NO_TYPE. */
	public static final short NONE = 0;
	public static final String NONE_STR = "unknown";
	/** Constant field INT. */
	public final static short INT = 1;
	public final static String INT_STR = "int";
	/** Constant field FLOAT. */
	public final static short FLOAT = 2;
	public final static String FLOAT_STR = "float";
	/** Constant field BOOL. */
	public final static short BOOL = 3;
	public final static String BOOL_STR = "bool";
	/** Constant field STRING. */
	public final static short STRING = 4;
	public final static String STRING_STR = "string";
	/** Constant field LIST. */
	public final static short LIST = 5;
	public final static String LIST_STR = "list";
	/** Constant field COLOR. */
	public final static short COLOR = 6;
	public final static String COLOR_STR = "rgb";
	/** Constant field POINT. */
	public final static short POINT = 7;
	public final static String POINT_STR = "point";
	/** Constant field MATRIX. */
	public final static short MATRIX = 8;
	public final static String MATRIX_STR = "matrix";
	/** Constant field PAIR. */
	public final static short PAIR = 9;
	public final static String PAIR_STR = "pair";
	/** Constant field MAP. */
	public final static short MAP = 10;
	public final static String MAP_STR = "map";
	/** Constant field AGENT. */
	public final static short AGENT = 11;
	public final static String AGENT_STR = "agent";
	/** Constant field SPECIES. */
	public final static short SPECIES = 14;
	public final static String SPECIES_STR = "species";
	/** Constant field FILE. */
	public final static short FILE = 12;
	public final static String FILE_STR = "file";
	/** Constant field GEOMETRY. */
	public static final short GEOMETRY = 13;
	public final static String GEOM_STR = "geometry";

	public static final short GRAPH = 15;
	public static final String GRAPH_STR = "graph";

	public static final short PATH = 17;
	public static final String PATH_STR = "path";

	public static final short TOPOLOGY = 18;
	public static final String TOPOLOGY_STR = "topology";

	public static final short CONTAINER = 16;
	public static final String CONTAINER_STR = "container";

	public final static short AVAILABLE_TYPES = 50;
	public final static short SPECIES_TYPES = 100;

	public Inner cast(Object obj) throws GamaRuntimeException;

	public Inner cast(IScope scope, Object obj) throws GamaRuntimeException;

	public Inner cast(IScope scope, Object obj, Object param) throws GamaRuntimeException;

	public short id();

	public Class toClass();

	public Inner getDefault();

	public IExpression getGetter(String name);

	public boolean isSpeciesType();

	public abstract IType defaultContentType();

	public String getSpeciesName();

	public void initFieldGetters();

	public boolean isSuperTypeOf(IType type);

	public boolean isSubTypeOf(IType type);

	public boolean isAssignableFrom(IType l);

	/**
	 * @throws GamlException
	 * @param expr
	 * @param factory
	 * @return
	 */
	IExpression coerce(IExpression expr, IExpressionFactory factory) throws GamlException;

	/**
	 * returns the distance between two types : 0 if they are equal, 1 if they are directly related,
	 * maxInt if they are not.
	 * @param originalChildType
	 * @return
	 */
	public int distanceTo(IType originalChildType);

}