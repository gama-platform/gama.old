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

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamlException;
import msi.gaml.expressions.IExpression;

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

	public Inner cast(IScope scope, Object obj, Object param) throws GamaRuntimeException;

	public short id();

	public Class toClass();

	public Inner getDefault();

	public IExpression getGetter(String name);

	public boolean isSpeciesType();

	public abstract IType defaultContentType();

	public String getSpeciesName();

	public boolean isAssignableFrom(IType l);

	/**
	 * @throws GamlException
	 * @param expr
	 * @param factory
	 * @return
	 */
	IType coerce(IType expr) throws GamlException;

	/**
	 * returns the distance between two types : 0 if they are equal, 1 if they are directly related,
	 * maxInt if they are not.
	 * @param originalChildType
	 * @return
	 */
	public int distanceTo(IType originalChildType);

	/**
	 * @param n
	 * @param typeFieldExpression
	 */
	public void addFieldGetter(String n, IExpression typeFieldExpression);

}