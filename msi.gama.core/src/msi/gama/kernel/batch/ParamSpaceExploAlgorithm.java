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
package msi.gama.kernel.batch;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.*;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import org.uncommons.maths.number.NumberGenerator;

/**
 * The Class ParamSpaceExploAlgorithm.
 */
@inside(kinds = { ISymbolKind.EXPERIMENT })
public abstract class ParamSpaceExploAlgorithm extends Symbol implements IExploration {

	public final static String[] COMBINATIONS = new String[] { "maximum", "minimum", "average" };
	public static final Class[] CLASSES = { GeneticAlgorithm.class, SimulatedAnnealing.class,
		HillClimbing.class, TabuSearch.class, TabuSearchReactive.class, ExhaustiveSearch.class };

	private NumberGenerator<Double> randUniform;
	protected Map<ParametersSet, Double> testedSolutions;
	protected IExpression fitnessExpression;
	protected boolean isMaximize;
	protected BatchExperiment currentExperiment;
	protected ParametersSet bestSolution;
	protected Double bestFitness;
	protected short combination;

	protected abstract ParametersSet findBestSolution() throws GamaRuntimeException;

	@Override
	public void initializeFor(final BatchExperiment f) throws GamaRuntimeException {
		currentExperiment = f;
	}

	protected NumberGenerator<Double> getRandUniform() {
		if ( randUniform == null ) {
			randUniform = GAMA.getRandom().createUniform(0., 1.);
		}
		return randUniform;
	}

	public ParamSpaceExploAlgorithm(final IDescription desc) {
		super(desc);
		testedSolutions = new HashMap<ParametersSet, Double>();
		fitnessExpression = getFacet(IKeyword.MAXIMIZE, getFacet(IKeyword.MINIMIZE));
		isMaximize = hasFacet(IKeyword.MAXIMIZE);
		String ag = getLiteral(IKeyword.AGGREGATION);
		combination = IKeyword.MAX.equals(ag) ? C_MAX : IKeyword.MIN.equals(ag) ? C_MIN : C_MEAN;

	}

	@Override
	public String getCombinationName() {
		return COMBINATIONS[combination];
	}

	@Override
	public void run() {
		try {
			findBestSolution();
		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
		}
		GAMA.getExperiment().stop();
	}

	@Override
	public void start() {
		new Thread(this, getName() + " thread").start();
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) throws GamlException {}

	protected boolean isMaximize() {
		return isMaximize;
	}

	@Override
	public void addParametersTo(final BatchExperiment exp) {
		exp.addMethodParameter(new ParameterAdapter("Exploration method",
			IExperiment.BATCH_CATEGORY_NAME, IType.STRING) {

			@Override
			public Object value() {
				List<Class> classes = new GamaList(CLASSES);
				return IKeyword.METHODS[classes.indexOf(ParamSpaceExploAlgorithm.this.getClass())];
			}

		});
	}

	@Override
	public Double getBestFitness() {
		return bestFitness;
	}

	@Override
	public IExpression getFitnessExpression() {
		return fitnessExpression;
	}

	@Override
	public ParametersSet getBestSolution() {
		return bestSolution;
	}

	@Override
	public short getCombination() {
		return combination;
	}
}
