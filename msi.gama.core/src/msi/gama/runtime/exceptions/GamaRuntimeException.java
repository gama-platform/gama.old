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

import java.util.*;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.runtime.GAMA;
import msi.gaml.statements.IStatement;
import org.eclipse.emf.ecore.EObject;

/**
 * Written by drogoul Modified on 7 janv. 2011
 * 
 * A kind of exception thrown when an abnormal situation happens while running a model.
 * 
 */

public class GamaRuntimeException extends RuntimeException {

	private final long cycle;
	private String agent = null;
	private boolean isWarning;
	protected final List<String> context = new ArrayList();
	protected EObject editorContext;
	protected int lineNumber;
	protected int times = 0;

	public static GamaRuntimeException create(Throwable ex) {
		return new GamaRuntimeException(ex);
	}

	public static GamaRuntimeException error(String s) {
		return new GamaRuntimeException(s, false);
	}

	public static GamaRuntimeException warning(String s) {
		return new GamaRuntimeException(s, true);
	}

	private GamaRuntimeException(final Throwable ex) {
		super(ex.toString(), ex);
		addContext(ex.toString());
		for ( StackTraceElement element : ex.getStackTrace() ) {
			addContext(element.toString());
		}
		cycle = computeCycle();

	}

	protected GamaRuntimeException(final String s, final boolean warning) {
		super(s);
		cycle = computeCycle();
		isWarning = warning;
	}

	public void addContext(final String c) {
		context.add(c);
	}

	public void addContext(final IStatement s) {
		addContext("in " + s.toGaml());
		final EObject e = s.getDescription().getUnderlyingElement(null);
		if ( e != null ) {
			editorContext = e;
		}
	}

	public EObject getEditorContext() {
		return editorContext;
	}

	public void addAgent(final String agent) {
		times++;
		if ( times == 1 ) {
			this.agent = agent;
			addContext("in agent(s) " + agent);
		} else {
			this.agent = String.valueOf(times) + " agents";
			String oldContext = context.remove(context.size() - 1);
			String newContext = oldContext + ", " + agent;
			addContext(newContext);
		}
	}

	public long getCycle() {
		return cycle;
	}

	public String getAgent() {
		return agent;
	}

	public boolean isWarning() {
		return isWarning && !GAMA.TREAT_WARNINGS_AS_ERRORS;
	}

	public static long computeCycle() {
		SimulationClock clock = GAMA.getClock();
		return clock == null ? 0l : clock.getCycle();
	}

	public List<String> getContextAsList() {
		return context;
	}

	@Override
	public String toString() {
		String s = getClass().getName();
		String message = getLocalizedMessage();
		return message != null ? message : s;
	}

	public boolean equivalentTo(GamaRuntimeException ex) {
		return editorContext == ex.editorContext && getMessage().equals(ex.getMessage()) && getCycle() == ex.getCycle();
	}

}
