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
package msi.gaml.expressions;

import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;

/**
 * Abstract class that defines the structure of all expression classes.
 * 
 * @author drogoul
 */

public abstract class AbstractExpression implements IExpression {

	protected IType type = null;
	protected IType contentType = null;
	String name = null;

	public String getName() {
		return name;
	}

	public void setName(final String s) {
		name = s;
	}

	@Override
	public IType type() {
		return type == null ? Types.NO_TYPE : type;
	}

	@Override
	public IType getContentType() {
		return contentType == null ? Types.NO_TYPE : contentType;
	}

	@Override
	public String literalValue() {
		return name;
	}

}
