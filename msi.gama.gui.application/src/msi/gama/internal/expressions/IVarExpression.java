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
package msi.gama.internal.expressions;

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;

/**
 * VariableExpression.
 * 
 * @author drogoul 4 sept. 07
 */
public interface IVarExpression extends IExpression {

	public static final int GLOBAL = 0;
	public static final int AGENT = 1;
	public static final int TEMP = 2;
	public static final int EACH = 3;
	public static final int SELF = 4;
	public static final int WORLD = 5;

	public abstract void setType(IType type);

	public abstract void setContentType(IType speciesType);

	void setVal(IScope scope, Object v, boolean create) throws GamaRuntimeException;

}