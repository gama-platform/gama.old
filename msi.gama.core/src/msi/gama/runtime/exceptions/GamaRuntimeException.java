/*********************************************************************************************
 *
 *
 * 'GamaRuntimeException.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.runtime.exceptions;

import java.io.*;
import java.util.*;
import org.eclipse.emf.ecore.EObject;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.runtime.*;
import msi.gaml.operators.Strings;
import msi.gaml.statements.IStatement;

/**
 * Written by drogoul Modified on 7 janv. 2011
 *
 * A kind of exception thrown when an abnormal situation happens while running a model.
 *
 */

public class GamaRuntimeException extends RuntimeException {

	private final long cycle;
	protected final List<String> agentsNames = new ArrayList();
	private boolean isWarning;
	protected final List<String> context = new ArrayList();
	protected EObject editorContext;
	protected int lineNumber;
	protected int occurences = 0;
	protected boolean reported = false;

	// Factory methods
	/**
	 * This call is deprecated. Use the equivalent method that passes the scope
	 * @param s
	 * @return
	 */
	// @Deprecated
	// public static GamaRuntimeException create(final Throwable ex) {
	// // Uses the dangerous and error-prone GAMA.getDefaultScope() method, which can return null or the scope of
	// // another simulation
	// return create(ex, GAMA.getRuntimeScope());
	// }

	public static GamaRuntimeException create(final Throwable ex, final IScope scope) {
		if ( ex instanceof GamaRuntimeException ) { return (GamaRuntimeException) ex; }
		if ( ex instanceof IOException ||
			ex instanceof FileNotFoundException ) { return new GamaRuntimeFileException(scope, ex); }
		return new GamaRuntimeException(scope, ex);
	}

	/**
	 * This method is deprecated. Use the equivalent method that passes the scope
	 * @param s
	 * @return
	 */
	@Deprecated
	public static GamaRuntimeException error(final String s) {
		// Uses the dangerous and error-prone GAMA.getRuntimeScope() method, which can return null or the scope of
		// another simulation
		return error(s, GAMA.getRuntimeScope());
	}

	public static GamaRuntimeException error(final String s, final IScope scope) {
		GamaRuntimeException ex = new GamaRuntimeException(scope, s, false);
		if ( scope == null ) { return ex; }
		IStatement statement = scope.getStatement();
		if ( statement != null ) {
			ex.addContext(statement);
		}
		return ex;
	}

	public static GamaRuntimeException warning(final String s, final IScope scope) {
		GamaRuntimeException ex = new GamaRuntimeException(scope, s, true);
		return ex;
	}

	/**
	 * This call is deprecated. Use the equivalent method that passes the scope
	 * @param s
	 * @return
	 */
	@Deprecated
	public static GamaRuntimeException warning(final String s) {
		// Uses the dangerous and error-prone GAMA.getDefaultScope() method, which can return null or the scope of
		// another simulation
		return warning(s, GAMA.getRuntimeScope());
	}

	// Constructors

	public static class GamaRuntimeFileException extends GamaRuntimeException {

		/**
		 * @param scope
		 * @param ex
		 */
		public GamaRuntimeFileException(final IScope scope, final Throwable ex) {
			super(scope, ex);
		}

		public GamaRuntimeFileException(final IScope scope, final String s) {
			super(scope, s, false);
		}

	}

	public GamaRuntimeException(final IScope scope, final Throwable ex) {
		super(ex == null ? "Unknown error" : ex.toString(), ex);
		if ( scope != null ) {
			IStatement statement = scope.getStatement();
			if ( statement != null ) {
				addContext(statement);
			}
		}
		if ( ex != null ) {
			addContext(ex.toString());
			for ( StackTraceElement element : ex.getStackTrace() ) {
				addContext(element.toString());
			}
		}
		cycle = computeCycle(scope);

	}

	protected GamaRuntimeException(final IScope scope, final String s, final boolean warning) {
		super(s);
		if ( scope != null ) {
			IStatement statement = scope.getStatement();
			if ( statement != null ) {
				addContext(statement);
			}
		}
		cycle = computeCycle(scope);
		isWarning = warning;
	}

	public void addContext(final String c) {
		context.add(c);
	}

	public void addContext(final IStatement s) {
		addContext("in " + s.serialize(false));
		final EObject e = s.getDescription().getUnderlyingElement(null);
		if ( e != null ) {
			editorContext = e;
		}
	}

	public EObject getEditorContext() {
		return editorContext;
	}

	public void addAgent(final String agent) {
		occurences++;
		if ( agentsNames.contains(agent) ) { return; }
		agentsNames.add(agent);
	}

	public void addAgents(final List<String> agents) {
		for ( String agent : agents ) {
			addAgent(agent);
		}
	}

	public long getCycle() {
		return cycle;
	}

	public String getAgentSummary() {
		int size = agentsNames.size();
		String agents = size == 0 ? "" : size == 1 ? agentsNames.get(0) : String.valueOf(size) + " agents";
		String occurence =
			occurences == 0 ? "" : occurences == 1 ? "1 occurence in " : String.valueOf(occurences) + " occurences in ";
		return occurence + agents;
	}

	public boolean isWarning() {
		return isWarning;
	}

	public long computeCycle(final IScope scope) {
		SimulationClock clock = scope == null ? null : scope.getClock();
		return clock == null ? 0l : clock.getCycle();
	}

	public List<String> getContextAsList() {
		List<String> result = new ArrayList();
		result.addAll(context);
		int size = agentsNames.size();
		if ( size == 0 ) { return result; }
		if ( size == 1 ) {
			result.add("in agent " + agentsNames.get(0));
		} else {
			String s = "in agents " + agentsNames.get(0);
			for ( int i = 1; i < agentsNames.size(); i++ ) {
				s += ", " + agentsNames.get(i);
			}
			result.add(s);
		}
		return result;
	}

	@Override
	public String toString() {
		String s = getClass().getName();
		String message = getLocalizedMessage();
		return message != null ? message : s;
	}

	public boolean equivalentTo(final GamaRuntimeException ex) {
		return editorContext == ex.editorContext && getMessage().equals(ex.getMessage()) && getCycle() == ex.getCycle();
	}

	public void setReported() {
		reported = true;
	}

	public boolean isReported() {
		return reported;
	}

	/**
	 * @return
	 */
	public List<String> getAgentsNames() {
		return agentsNames;
	}

	/**
	 * @return
	 */
	public String getAllText() {
		final StringBuilder sb = new StringBuilder(300);
		final String a = getAgentSummary();
		if ( a != null ) {
			sb.append(a).append(" at ");
		}
		sb.append("cycle ").append(getCycle()).append(": ").append(getMessage());
		List<String> strings = getContextAsList();
		for ( String s : strings ) {
			sb.append(Strings.LN).append(s);
		}
		return sb.toString();
	}

}
