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
package msi.gama.precompiler;

/**
 * Written by drogoul Modified on 1 aožt 2010
 * 
 * @todo Description
 * 
 */
public interface ISymbolKind {

	public static final int	SPECIES				= 0;

	public static final int	MODEL				= 1;

	public static final int	SINGLE_COMMAND		= 2;

	public static final int	SEQUENCE_COMMAND	= 11;

	public static final int	BEHAVIOR			= 3;

	public static final int	ACTION				= 11;

	public static final int	VARIABLE			= 4;

	public static final int	OUTPUT				= 5;

	public static final int	LAYER				= 6;

	public static final int	BATCH_SECTION		= 8;

	public static final int	BATCH_METHOD		= 9;

	public static final int	ENVIRONMENT			= 10;

	public static final int	EXPERIMENT			= 13;

	public static final int	GAML_LANGUAGE		= -1;

	public static final int	GAML_PARSING		= -2;

}
