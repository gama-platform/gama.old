/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.compilation;

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
