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
package msi.gama.java;

import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;

/**
 * Written by drogoul Modified on 19 oct. 2010
 * 
 * @todo Description
 * 
 */
public abstract class JavaExpression implements IExpression {

	@Override
	public abstract Object value(IScope scope);

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String toGaml() {
		return "";
	}

	@Override
	public String literalValue() {
		return value(null).toString();
	}

	@Override
	public IType getContentType() {
		return Types.get(IType.NONE);
	}

	@Override
	public IType type() {
		return Types.get(IType.NONE);
	}

}
