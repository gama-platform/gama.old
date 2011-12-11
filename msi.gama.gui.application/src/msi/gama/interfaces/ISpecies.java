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
import msi.gama.internal.descriptions.SpeciesDescription;
import msi.gama.util.*;

/**
 * Written by drogoul Modified on 25 avr. 2010
 * 
 * @todo Description
 * 
 */
public interface ISpecies extends IValue, IExecutionContext {

	public static final String PARENT = "parent";
	public static final String SKILLS = "skills";
	public static final String CONTROL = "control";
	public static final String EMF = "emf";
	public static final String FSM = "fsm";
	public static final String BASE = "base";
	public static final String FREQUENCY = "frequency";
	public static final String SCHEDULES = "schedules";
	public static final String TOPOLOGY = "topology";

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
	public abstract List<ISpecies> getMicroSpecies();

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
	public abstract List<String> getMicroSpeciesNames();

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

	@Override
	public abstract SpeciesDescription getDescription();

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
	public abstract List<ISpecies> getPeersSpecies();

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
	 * @param scope
	 * @return
	 */
	GamaList listValue(IScope scope);

	/**
	 * @param scope
	 * @return
	 */
	GamaMap mapValue(IScope scope);
}