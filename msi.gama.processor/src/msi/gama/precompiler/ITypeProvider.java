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
package msi.gama.precompiler;

/**
 * Written by drogoul Modified on 2 aožt 2010
 * 
 * @todo Description
 * 
 */
public interface ITypeProvider {

	/*
	 * The content type is provided by the content type of the left child of the binary expression
	 */
	static final short LEFT_CONTENT_TYPE = -20;
	/*
	 * The content type is provided by the content type of the right child of the binary expression
	 */
	static final short RIGHT_CONTENT_TYPE = -19;
	/*
	 * The content type is provided by the type of the left child of the binary expression
	 */
	static final short LEFT_TYPE = -18;
	/*
	 * The content type is provided by the type of the right child of the expression
	 */
	static final short RIGHT_TYPE = -17;
	/*
	 * The content type is provided by the content type of the child of the unary expression
	 */
	static final short CHILD_CONTENT_TYPE = -16;
	/*
	 * The content type is provided by the type of the child of the unary expression
	 */
	static final short CHILD_TYPE = -15;
	/*
	 * The content type is provided by the type of of the expression itself (i.e. species)
	 */
	static final short TYPE = -14;
	/*
	 * The content type cannot be computed
	 */
	static final short NONE = -13;
	/*
	 * The type or content type are provided by both operands (which must match).
	 */
	static final short BOTH = -21;
}
