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
package msi.gaml.kernel;

import msi.gama.interfaces.IAgent;
import msi.gama.interfaces.IPopulation;
import msi.gama.interfaces.IScope;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;

/**
 * Written by drogoul Modified on 24 nov. 2010
 * 
 * @todo Description
 * 
 */
public interface IGamlPopulation extends IPopulation {
	
	public abstract void createVariablesFor(IScope scope, IAgent agent) throws GamaRuntimeException;

	public abstract void updateVariablesFor(IScope scope, IAgent agent) throws GamaRuntimeException;

	public abstract boolean hasVar(final String n);

	public abstract void init(IScope scope) throws GamaRuntimeException;

	public abstract void step(IScope scope) throws GamaRuntimeException;
}