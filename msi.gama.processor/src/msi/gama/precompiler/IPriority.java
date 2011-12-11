/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
 * Written by drogoul Modified on 8 aožt 2010
 * 
 * @todo Relative priorities of the different operators (used in @operator annotations)
 * 
 */
public interface IPriority {

	static final short MIN_PRIORITY = 100;

	static final short TERNARY = 0;

	static final short BOOLEAN = 1;

	static final short COMPARATOR = 2;

	static final short CAST = 3;

	static final short PRODUCT = 4;

	static final short ADDITION = 5;

	static final short ITERATOR = 6;

	static final short ADDRESSING = MIN_PRIORITY - 1;

	static final short DEFAULT = MIN_PRIORITY - 2;

}
