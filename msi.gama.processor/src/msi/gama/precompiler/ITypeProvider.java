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
 * Written by drogoul Modified on 2 ao�t 2010
 * 
 * @todo Description
 * 
 */
public interface ITypeProvider {

	/*
	 * The content type is provided by the content type of the first child of the expression
	 */
	static final int FIRST_CONTENT_TYPE = -20;
	static final int FIRST_KEY_TYPE = -23;
	/*
	 * The content type is provided by the content type of the second child of the expression
	 */
	static final int SECOND_CONTENT_TYPE = -19;
	static final int SECOND_KEY_TYPE = -24;
	/*
	 * The content type is provided by the type of the first child of the expression
	 */
	static final int FIRST_TYPE = -18;
	/*
	 * The content type is provided by the type of the second child of the expression
	 */
	static final int SECOND_TYPE = -17;
	static final int SECOND_CONTENT_TYPE_OR_TYPE = -25;
	static final int FIRST_CONTENT_TYPE_OR_TYPE = -26;
	/*
	 * The content type is provided by the type of the expression itself (i.e. species)
	 */
	static final int TYPE = -14;
	/*
	 * The content type cannot be computed
	 */
	static final int NONE = -13;
	/*
	 * The type or content type are provided by both operands (which must match).
	 */
	static final int BOTH = -21;
	/*
	 * The content type is provided by the content type of the first element of the child (if the
	 * child is a container) -- EXPERIMENTAL RIGHT NOW (and probably limited to the matrix and
	 * as_matrix operators)
	 * e.g. : matrix ([[4, 5, 6],[1, 2, 4]]) should get int as a content type.
	 */
	static final int FIRST_ELEMENT_CONTENT_TYPE = -22;

	/**
	 * The type, content type, key type are provided by the element computed by its index. 0 for the first element, 1
	 * for the second, etc.
	 * For instance, TYPE_AT_INDEX + 2 will represent the third argument
	 */
	static final int INDEXED_TYPES = -100;
	static final int TYPE_AT_INDEX = -200;
	static final int CONTENT_TYPE_AT_INDEX = -300;
	static final int KEY_TYPE_AT_INDEX = -400;

}
