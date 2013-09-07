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
package msi.gaml.expressions;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IGamlDescription;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 25 d�c. 2010
 * 
 * @todo Description
 * 
 */
public interface IExpression extends IGamlDescription {

	public abstract Object value(final IScope scope) throws GamaRuntimeException;

	public abstract boolean isConst();

	public abstract String toGaml();

	public abstract String literalValue();

	/*
	 * Returns an expression where all the temp variables belonging to the scope passed in parameter
	 * are replaced by constants representing their values
	 */
	public abstract IExpression resolveAgainst(IScope scope);

	/*
	 * FIXME Highly exploratory !
	 * Tries to gather the key and content types of the elements contained in this expression, if
	 * any. The type of the elements is provided by getContentType(), but these methods try to
	 * return the key and content types of the elements themselves when they are available.
	 */

	public abstract IType getElementsContentType();

	public abstract IType getElementsKeyType();

	public abstract void setElementsContentType(IType t);

	public abstract void setElementsKeyType(IType t);

	public abstract boolean shouldBeParenthesized();

}