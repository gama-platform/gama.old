/*********************************************************************************************
 *
 * 'IOperatorCategory.java, in plugin ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.precompiler;

/**
 * Written by gaudou Modified on 1 mars 2014
 * 
 * Description: all the possible categories for operators
 * 
 */
public interface IOperatorCategory {

	public static final String ARITHMETIC 		= "Arithmetic operators";
	public static final String LOGIC			= "Logical operators";
	public static final String RANDOM			= "Random operators";
	public static final String STATISTICAL		= "Statistical operators";
	public static final String COMPARISON		= "Comparison operators";
	public static final String CASTING			= "Casting operators";
	public static final String COLOR			= "Color-related operators";
	public static final String SYSTEM			= "System";
	
	public static final String EDP 				= "EDP-related operators";		
	
	public static final String SPATIAL			= "Spatial operators";
	public static final String SHAPE			= "Shape";	
	public static final String THREED			= "3D";
	public static final String SP_STATISTICAL	= "Spatial statistical operators";
	public static final String SP_QUERIES		= "Spatial queries operators";
	public static final String SP_PROPERTIES	= "Spatial properties operators";
	public static final String SP_RELATIONS		= "Spatial relations operators";
	public static final String SP_TRANSFORMATIONS = "Spatial transformations operators";
	
	public static final String ITERATOR			= "Iterator operators";
	public static final String CONTAINER		= "Containers-related operators";
	public static final String MATRIX			= "Matrix-related operators";
	public static final String STRING			= "Strings-related operators";
	public static final String LIST				= "List-related operators";
	public static final String MAP				= "Map-related operators";
	public static final String GRAPH			= "Graphs-related operators";
	public static final String FILE				= "Files-related operators";
	public static final String SPECIES			= "Species-related operators";
	public static final String GRID				= "Grid-related operators";
	public static final String PATH				= "Path-related operators";
	public static final String DATE				= "Date-related operators";
	
	public static final String TYPE				= "Types-related operators";
	
	public static final String POINT			= "Points-related operators";
	
	public static final String WATER			= "Water level operators";
	
	public static final String FIPA				= "FIPA-related operators";

	public static final String MAP_COMPARAISON 	= "Map comparaison operators";
	
	public static final String USER_CONTROL 	= "User control operators";
	
	public static final String TIME				= "Time-related operators";
	
	public static final String DEPRECATED 		= "DeprecatedOperators";	
	
	public static final String DRIVING 			= "Driving operators";
	
	public static final String GENSTAR 			= "Genstar operators";
}
