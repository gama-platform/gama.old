/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
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
