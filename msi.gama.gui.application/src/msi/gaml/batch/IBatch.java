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
