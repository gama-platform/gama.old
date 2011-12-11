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
package msi.gaml.batch;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.kernel.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.kernel.experiment.*;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.*;
import msi.gama.util.GamaList;
import org.uncommons.maths.number.NumberGenerator;

/**
 * The Class ParamSpaceExploAlgorithm.
 */
@inside(kinds = { ISymbolKind.EXPERIMENT })
public abstract class ParamSpaceExploAlgorithm extends Symbol implements Runnable {

	public final static short		C_MAX			= 0, C_MIN = 1, C_MEAN = 2;
	public final static String[]	COMBINATIONS	= new String[] { "maximum", "minimum",
		"average"									};

	private NumberGenerator<Double>	randUniform;
	protected Map<Solution, Double>	testedSolutions;
	protected IExpression			fitnessExpression;
	protected boolean				isMaximize;
	protected BatchExperiment		currentExperiment;
	protected Solution				bestSolution;
	protected Double				bestFitness;
	protected short					combination;

	public abstract Solution findBestSolution() throws GamaRuntimeException;

	public void initializeFor(final BatchExperiment f) throws GamaRuntimeException {
		currentExperiment = f;
	}

	public NumberGenerator<Double> getRandUniform() {
		if ( randUniform == null ) {
			randUniform = GAMA.getRandom().createUniform(0., 1.);
		}
		return randUniform;
	}

	public ParamSpaceExploAlgorithm(final IDescription desc) {
		super(desc);
		testedSolutions = new HashMap<Solution, Double>();
		fitnessExpression = getFacet(MAXIMIZE, getFacet(MINIMIZE));
		isMaximize = hasFacet(MAXIMIZE);
		String ag = getLiteral(ISymbol.AGGREGATION);
		combination = ISymbol.MAX.equals(ag) ? C_MAX : ISymbol.MIN.equals(ag) ? C_MIN : C_MEAN;

	}

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

	public void start() {
		new Thread(this, getName() + " thread").start();
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) throws GamlException {}

	protected boolean isMaximize() {
		return isMaximize;
	}

	public void addParametersTo(final BatchExperiment exp) {
		exp.addMethodParameter(new ParameterAdapter("Exploration method",
			IExperiment.BATCH_CATEGORY_NAME, IType.STRING) {

			@Override
			public Object value() {
				List<Class> classes = new GamaList(IBatch.CLASSES);
				return IBatch.METHODS[classes.indexOf(ParamSpaceExploAlgorithm.this.getClass())];
			}

		});
	}

	public Double getBestFitness() {
		return bestFitness;
	}

	public IExpression getFitnessExpression() {
		return fitnessExpression;
	}

	public Solution getBestSolution() {
		return bestSolution;
	}

	public short getCombination() {
		return combination;
	}
}
