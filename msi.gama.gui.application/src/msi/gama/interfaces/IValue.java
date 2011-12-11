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
package msi.gama.interfaces;

import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;

/**
 * Written by drogoul Modified on 19 nov. 2008
 * 
 * @todo Description
 * 
 */
public interface IValue {

	public abstract IType type();

	// @operator(value = IType.STRING_STR, can_be_const = true)
	public abstract String stringValue() throws GamaRuntimeException;

	// @operator(value = "to_gaml")
	public abstract String toGaml();

	// @operator(value = "to_java")
	public abstract String toJava();

	@operator(value = "copy", can_be_const = true, type = ITypeProvider.CHILD_TYPE, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public abstract Object copy() throws GamaRuntimeException;

}
