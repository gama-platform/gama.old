/*******************************************************************************************************
 *
 * IOperatorCategory.java, in ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler;

/**
 * Written by gaudou Modified on 1 mars 2014
 * 
 * Description: all the possible categories for operators
 * 
 */
public interface IOperatorCategory {

	/** The Constant ARITHMETIC. */
	public static final String ARITHMETIC 		= "Arithmetic operators";
	
	/** The Constant LOGIC. */
	public static final String LOGIC			= "Logical operators";
	
	/** The Constant RANDOM. */
	public static final String RANDOM			= "Random operators";
	
	/** The Constant STATISTICAL. */
	public static final String STATISTICAL		= "Statistical operators";
	
	/** The Constant COMPARISON. */
	public static final String COMPARISON		= "Comparison operators";
	
	/** The Constant CASTING. */
	public static final String CASTING			= "Casting operators";
	
	/** The Constant COLOR. */
	public static final String COLOR			= "Color-related operators";
	
	/** The Constant SYSTEM. */
	public static final String SYSTEM			= "System";
	
	/** The Constant EDP. */
	public static final String EDP 				= "EDP-related operators";		
	
	/** The Constant SPATIAL. */
	public static final String SPATIAL			= "Spatial operators";
	
	/** The Constant SHAPE. */
	public static final String SHAPE			= "Shape";	
	
	/** The Constant THREED. */
	public static final String THREED			= "3D";
	
	/** The Constant SP_STATISTICAL. */
	public static final String SP_STATISTICAL	= "Spatial statistical operators";
	
	/** The Constant SP_QUERIES. */
	public static final String SP_QUERIES		= "Spatial queries operators";
	
	/** The Constant SP_PROPERTIES. */
	public static final String SP_PROPERTIES	= "Spatial properties operators";
	
	/** The Constant SP_RELATIONS. */
	public static final String SP_RELATIONS		= "Spatial relations operators";
	
	/** The Constant SP_TRANSFORMATIONS. */
	public static final String SP_TRANSFORMATIONS = "Spatial transformations operators";
	
	/** The Constant ITERATOR. */
	public static final String ITERATOR			= "Iterator operators";
	
	/** The Constant CONTAINER. */
	public static final String CONTAINER		= "Containers-related operators";
	
	/** The Constant MATRIX. */
	public static final String MATRIX			= "Matrix-related operators";
	
	/** The Constant STRING. */
	public static final String STRING			= "Strings-related operators";
	
	/** The Constant LIST. */
	public static final String LIST				= "List-related operators";
	
	/** The Constant MAP. */
	public static final String MAP				= "Map-related operators";
	
	/** The Constant GRAPH. */
	public static final String GRAPH			= "Graphs-related operators";
	
	/** The Constant FILE. */
	public static final String FILE				= "Files-related operators";
	
	/** The Constant SPECIES. */
	public static final String SPECIES			= "Species-related operators";
	
	/** The Constant GRID. */
	public static final String GRID				= "Grid-related operators";
	
	/** The Constant PATH. */
	public static final String PATH				= "Path-related operators";
	
	/** The Constant DATE. */
	public static final String DATE				= "Date-related operators";
	
	/** The Constant TYPE. */
	public static final String TYPE				= "Types-related operators";
	
	/** The Constant POINT. */
	public static final String POINT			= "Points-related operators";
	
	/** The Constant WATER. */
	public static final String WATER			= "Water level operators";
	
	/** The Constant FIPA. */
	public static final String FIPA				= "FIPA-related operators";

	/** The Constant MAP_COMPARAISON. */
	public static final String MAP_COMPARAISON 	= "Map comparaison operators";
	
	/** The Constant USER_CONTROL. */
	public static final String USER_CONTROL 	= "User control operators";
	
	/** The Constant TIME. */
	public static final String TIME				= "Time-related operators";
	
	/** The Constant DEPRECATED. */
	public static final String DEPRECATED 		= "DeprecatedOperators";	
	
	/** The Constant DRIVING. */
	public static final String DRIVING 			= "Driving operators";
	
	/** The Constant GENSTAR. */
	public static final String GENSTAR 			= "Genstar operators";
}
