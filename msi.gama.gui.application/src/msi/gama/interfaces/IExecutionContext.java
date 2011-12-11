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
package msi.gama.interfaces;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.IAgentConstructor;
import msi.gaml.commands.*;
import msi.gaml.control.IControl;

public interface IExecutionContext extends ISymbol {

	public abstract IControl getControl();

	public abstract String getControlName();

	public abstract IType getAgentType();

	public abstract IAgentConstructor getAgentConstructor();

	public abstract void addVariable(final IVariable v);

	public abstract IVariable getVar(final String n);

	public abstract boolean hasVar(final String name);

	public abstract List<String> getVarNames();

	public abstract Collection<IVariable> getVars();

	public abstract void addAction(final ActionCommand ce);

	public abstract ICommand.WithArgs getAction(final String name);

	public abstract void addAspect(final AspectCommand ce);

	public abstract boolean hasAspect(final String n);

	public abstract IAspect getAspect(final String n);

	public abstract List<String> getAspectNames();

	public abstract void addBehavior(ICommand b);

	public abstract List<ICommand> getBehaviors();

	public abstract void addChild(final ISymbol s);

	@Override
	public abstract IDescription getDescription();

	public abstract String getParentName();

	public abstract ISpecies getMacroSpecies();
	
	public abstract void setMacroSpecies(final ISpecies macroSpecies);
}
