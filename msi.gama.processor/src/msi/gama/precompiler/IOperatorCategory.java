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
	
	public static final String TYPE				= "Types-related operators";
	
	public static final String POINT			= "Points-related operators";
	
	public static final String WATER			= "Water level operators";
	
	public static final String FIPA				= "FIPA-related operators";

	public static final String MAP_COMPARAISON 	= "Map comparaison operators";
	
	public static final String USER_CONTROL 	= "User control operators";
	
	public static final String TIME				= "Time-related operators";
	
	public static final String DEPRECATED 		= "DeprecatedOperators";	
}
