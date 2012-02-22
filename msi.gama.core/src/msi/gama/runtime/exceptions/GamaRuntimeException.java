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
package msi.gama.runtime.exceptions;

import msi.gama.common.interfaces.IGamlable;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gaml.compilation.GamlException;

/**
 * Written by drogoul Modified on 7 janv. 2011
 * 
 * A kind of exception thrown when an abnormal situation happens while running a model.
 * 
 */

public class GamaRuntimeException extends GamlException {

	private static final String ERROR = "Error: \n";
	private static final String WARNING = "Warning: \n";
	private final long cycle;
	private String agent;

	public GamaRuntimeException(final Throwable ex) {
		super(ERROR + ex.toString(), ex);
		cycle = computeCycle();

	}

	public GamaRuntimeException(final String s, final boolean warning) {
		super((warning ? WARNING : ERROR) + s);
		cycle = computeCycle();
		isWarning = warning;
	}

	public GamaRuntimeException(final String s) {
		super(ERROR + s);
		cycle = computeCycle();
	}

	public void addContext(final IGamlable setCommand) {
		addContext("in " + setCommand.toGaml());
	}

	public void addAgent(final String agent) {
		this.agent = agent;
		addContext("in agent " + agent);
	}

	@Override
	public long getCycle() {
		return cycle;
	}

	public String getAgent() {
		return agent;
	}

	@Override
	public boolean isWarning() {
		return isWarning && !SimulationClock.TREAT_WARNINGS_AS_ERRORS;
	}

	public static long computeCycle() {
		return SimulationClock.getCycle();
	}

}
