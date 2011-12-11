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
package msi.gaml.batch;

/**
 * Written by drogoul Modified on 14 juin 2010
 * 
 * @todo Description
 * 
 */
public interface IBatch {

	public static final String GENETIC = "genetic";

	public static final String HILL_CLIMBING = "hill_climbing";

	public static final String ANNEALING = "annealing";

	public static final String TABU = "tabu";

	public static final String REACTIVE_TABU = "reactive_tabu";

	public static final String EXHAUSTIVE = "exhaustive";

	public static final String[] METHODS = { GENETIC, ANNEALING, HILL_CLIMBING, TABU,
		REACTIVE_TABU, EXHAUSTIVE };

	public static final Class[] CLASSES = { GeneticAlgorithm.class, SimulatedAnnealing.class,
		HillClimbing.class, TabuSearch.class, TabuSearchReactive.class, ExhaustiveSearch.class };
}
