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
package msi.gama.internal.compilation;

import msi.gama.interfaces.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 14 aožt 2010
 * 
 * @todo Description
 * 
 */
public abstract class PrimitiveExecuter implements IPrimitiveExecuter {

	IType returnType = Types.NO_TYPE;

	@Override
	public abstract Object execute(ISkill skill, IAgent agent, IScope scope)
		throws GamaRuntimeException;

	@Override
	public IType getReturnType() {
		return returnType;
	}

	@Override
	public void setReturnType(final IType returnType) {
		this.returnType = returnType;
	}

}
