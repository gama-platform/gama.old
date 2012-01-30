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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.species;

import java.util.*;
import msi.gama.common.interfaces.IValue;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.commands.*;
import msi.gaml.compilation.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 25 avr. 2010
 * 
 * @todo Description
 * 
 */
public interface ISpecies extends IValue, ISymbol {

	// public static final String SPATIAL_LEVEL = "spatial_level";

	public abstract IExpression getFrequency();

	public abstract IExpression getSchedule();

	public abstract boolean extendsSpecies(final ISpecies s);

	public abstract boolean isGrid();

	public abstract boolean isGlobal();

	/**
	 * Indicates that this species is copied from the parent species or not.
	 * 
	 * @return
	 */
	public abstract boolean isCopy();

	/**
	 * Returns all the direct micro-species.
	 * 
	 * @return
	 */
	public abstract IList<ISpecies> getMicroSpecies();

	/**
	 * Returns a micro-species with the specified name or null otherwise.
	 * 
	 * @param microSpeciesName
	 * @return a species or null
	 */
	public abstract ISpecies getMicroSpecies(String microSpeciesName);

	/**
	 * Returns a list of name of all micro-species.
	 * 
	 * @return
	 */
	public abstract IList<String> getMicroSpeciesNames();

	/**
	 * Verifies if this species has micro-species or not.
	 * 
	 * @return
	 *         true if this species has micro-species
	 *         false otherwise
	 */
	public abstract boolean hasMicroSpecies();

	/**
	 * Verifies of the specified species is a micro-species of this species of not.
	 * 
	 * @param species
	 * @return
	 */
	public abstract boolean containMicroSpecies(ISpecies species);

	/**
	 * Returns the parent species.
	 * 
	 * @return
	 */
	public abstract ISpecies getParentSpecies();

	/**
	 * Returns all the species sharing the same direct macro-species with this species.
	 * 
	 * @return
	 */
	public abstract IList<ISpecies> getPeersSpecies();

	/**
	 * Returns a peer species with the specified name.
	 * 
	 * @param peerName name of the peer species.
	 * @return a peer species with the specified name or null.
	 */
	public abstract ISpecies getPeerSpecies(String peerName);

	/**
	 * Returns a list of visible species from this species.
	 * 
	 * A species can see the following species:
	 * 1. Its direct micro-species.
	 * 2. Its peer species.
	 * 3. Its direct&in-direct macro-species and their peers.
	 * 
	 * @return
	 */
	public abstract Collection<ISpecies> getVisibleSpecies();

	/**
	 * Returns a visible species from the view point of this species.
	 * If the visible species list contains a species with the specified name.
	 * 
	 * @param speciesName
	 */
	public abstract ISpecies getVisibleSpecies(String speciesName);

	/**
	 * Returns the level of this species.
	 * "world" species is the top level species having 0 as level.
	 * level of a species is equal to level of its direct macro-species plus 1.
	 * 
	 * @return
	 */
	public abstract int getLevel();

	// TODO THESE METHODS ARE INTENDED TO BE PORTED ON IPopulation instead
	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @return
	 */
	IList listValue(IScope scope) throws GamaRuntimeException;

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @return
	 */
	Map mapValue(IScope scope) throws GamaRuntimeException;

	public abstract void addAction(final ICommand ce);

	public abstract void addAspect(final ICommand ce);

	public abstract void addBehavior(ICommand b);

	public abstract void addChild(final ISymbol s);

	public abstract void addVariable(final IVariable v);

	public abstract ICommand.WithArgs getAction(final String name);

	public abstract IAgentConstructor getAgentConstructor();

	public abstract IType getAgentType();

	public abstract IAspect getAspect(final String n);

	public abstract IList<String> getAspectNames();

	public abstract IList<ICommand> getBehaviors();

	public abstract IArchitecture getArchitecture();

	public abstract String getArchitectureName();

	public abstract ISpecies getMacroSpecies();

	public abstract String getParentName();

	public abstract IVariable getVar(final String n);

	public abstract IList<String> getVarNames();

	public abstract Collection<IVariable> getVars();

	public abstract boolean hasAspect(final String n);

	public abstract boolean hasVar(final String name);

	public abstract void setMacroSpecies(final ISpecies macroSpecies);
}