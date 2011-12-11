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
package msi.gama.java;

import java.util.*;
import msi.gama.agents.AbstractSpecies;
import msi.gama.interfaces.*;
import msi.gama.interfaces.ICommand.WithArgs;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gaml.control.IControl;

/**
 * Written by drogoul Modified on 29 déc. 2010
 * 
 * @todo Description
 * 
 */
public class JavaSpecies extends AbstractSpecies {

	public JavaSpecies(/* final ISymbol model, */final IDescription desc) {
		super(/* model, */desc);
	}

	@Override
	public boolean extendsSpecies(final ISpecies s) {
		return s.getDescription().getJavaBase().isAssignableFrom(getDescription().getJavaBase());
	}

	@Override
	public IVariable getVar(final String n) {
		// Maybe add some reflection here on JavaBase (later)
		return null;
	}

	@Override
	public Collection<IVariable> getVars() {
		// Maybe add some reflection here on JavaBase (later)
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<String> getAspectNames() {
		// Maybe add some reflection here on JavaBase (later)
		return Collections.EMPTY_LIST;
	}

	@Override
	public WithArgs getAction(final String name) {
		// Maybe add some reflection here on JavaBase (later)
		return null;
	}

	@Override
	public boolean hasAspect(final String s) {
		// Maybe add some reflection here on JavaBase (later)
		return false;
	}

	@Override
	public IAspect getAspect(final String s) {
		// Maybe add some reflection here on JavaBase (later)
		// Or a collection of IAspect passed to the species
		return null;
	}

	@Override
	public List<ICommand> getBehaviors() {
		// Maybe add some reflection here on JavaBase (later)
		return Collections.EMPTY_LIST;
	}

	@Override
	public String getControlName() {
		// ???
		return "java";
	}

	@Override
	public String getParentName() {
		// ???
		return null;
	}

	@Override
	public List<String> getVarNames() {
		// Maybe add some reflection here on JavaBase (later)
		return Collections.EMPTY_LIST;
	}

	@Override
	public void addChild(final ISymbol s) {

	}

	@Override
	public IControl getControl() {
		return null;

		// Pourquoi ne pas renvoyer la classe support ? En appelant init() et step() dessus, par
		// exemple. De cette façon, on n'aurait qu'un seul mode de contrôle.
	}

	@Override
	public boolean hasVar(final String name) {
		return false;
	}

	@Override
	public boolean isGlobal() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.ISpecies#getFrequency()
	 */
	@Override
	public IExpression getFrequency() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.ISpecies#getSchedule()
	 */
	@Override
	public IExpression getSchedule() {
		// TODO Auto-generated method stub
		return null;
	}
}
