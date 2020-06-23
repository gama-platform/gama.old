/*********************************************************************************************
 *
 * 'IConstantCategory.java, in plugin ummisco.gama.annotations, is part of the source code of the
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
public interface IConstantCategory {

	// Units
	public static final String LENGTH		= "Length units";
	public static final String TIME			= "Time units";
	public static final String VOLUME		= "Volume units";	
	public static final String WEIGHT		= "Weight units";
	public static final String SURFACE		= "Surface units";
	public static final String GRAPHIC		= "Graphics units";	
	
	// Constants
	public static final String CONSTANT		= "Constants";
	public static final String COLOR_CSS 	= "Colors";
}
