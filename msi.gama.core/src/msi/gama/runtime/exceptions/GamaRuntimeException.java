/*******************************************************************************************************
 *
 * GamaRuntimeException.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.exceptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;

import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.IScope;
import msi.gaml.compilation.ISymbol;
import msi.gaml.operators.Strings;

/**
 * Written by drogoul Modified on 7 janv. 2011
 *
 * A kind of exception thrown when an abnormal situation happens while running a model.
 *
 */

public class GamaRuntimeException extends RuntimeException {

	/** The cycle. */
	private final long cycle;
	
	/** The agents names. */
	protected final List<String> agentsNames = new ArrayList<>();
	
	/** The is warning. */
	private boolean isWarning;
	
	/** The context. */
	protected final List<String> context = new ArrayList<>();
	
	/** The editor context. */
	protected EObject editorContext;
	
	/** The occurrences. */
	protected int occurrences = 0;
	
	/** The reported. */
	protected boolean reported = false;
	
	/** The scope. */
	protected final IScope scope;

	// Factory methods
	/**
	 * This call is deprecated. Use the equivalent method that passes the scope
	 *
	 * @param s
	 * @return
	 */

	public static GamaRuntimeException create(final Throwable ex, final IScope scope) {
		if (ex instanceof GamaRuntimeException) return (GamaRuntimeException) ex;
		if (ex instanceof IOException || ex instanceof FileNotFoundException)
			return new GamaRuntimeFileException(scope, ex);
		return new GamaRuntimeException(scope, ex);
	}

	/**
	 * Error.
	 *
	 * @param s the s
	 * @param scope the scope
	 * @return the gama runtime exception
	 */
	public static GamaRuntimeException error(final String s, final IScope scope) {
		return new GamaRuntimeException(scope, s, false);
	}

	/**
	 * Warning.
	 *
	 * @param s the s
	 * @param scope the scope
	 * @return the gama runtime exception
	 */
	public static GamaRuntimeException warning(final String s, final IScope scope) {
		return new GamaRuntimeException(scope, s, true);
	}

	// Constructors

	/**
	 * The Class GamaRuntimeFileException.
	 */
	public static class GamaRuntimeFileException extends GamaRuntimeException {

		/**
		 * @param scope
		 * @param ex
		 */
		public GamaRuntimeFileException(final IScope scope, final Throwable ex) {
			super(scope, ex);
		}

		/**
		 * Instantiates a new gama runtime file exception.
		 *
		 * @param scope the scope
		 * @param s the s
		 */
		public GamaRuntimeFileException(final IScope scope, final String s) {
			super(scope, s, false);
		}

	}

	/**
	 * Gets the exception name.
	 *
	 * @param ex the ex
	 * @return the exception name
	 */
	protected static String getExceptionName(final Throwable ex) {
		final String s = ex.getClass().getName();
		if (s.contains("geotools") || s.contains("opengis")) return "exception in GeoTools library";
		if (s.contains("jts"))
			return "exception in JTS library";
		else if (s.contains("rcaller"))
			return "exception in RCaller library";
		else if (s.contains("jogamp"))
			return "exception in JOGL library";
		else if (s.contains("weka"))
			return "exception in Weka library";
		else if (s.contains("math3")) return "exception in Math library";
		if (ex instanceof NullPointerException) return "nil value detected";
		if (ex instanceof IndexOutOfBoundsException)
			return "index out of bounds";
		else if (ex instanceof IOException)
			return "I/O error";
		else if (ex instanceof CoreException)
			return "exception in Eclipse";
		else if (ex instanceof ClassCastException)
			return "wrong casting";
		else if (ex instanceof IllegalArgumentException) return "illegal argument";

		return ex.getClass().getSimpleName();
	}

	/**
	 * Instantiates a new gama runtime exception.
	 *
	 * @param scope the scope
	 * @param ex the ex
	 */
	protected GamaRuntimeException(final IScope scope, final Throwable ex) {
		super(ex == null ? "Unknown error" : "Java error: " + getExceptionName(ex), ex);
		if (scope != null) {
			final ISymbol symbol = scope.getCurrentSymbol();
			if (symbol != null) { addContext(symbol); }
		}
		if (ex != null) {
			ex.printStackTrace();
			addContext(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			int i = 0;
			for (final StackTraceElement element : ex.getStackTrace()) {
				addContext(element.toString());
				if (i++ > 5) { break; }
			}
		}
		cycle = computeCycle(scope);
		// AD: 18/01/16 Adding this to address Issue #1411
		this.scope = scope;

	}

	/**
	 * Instantiates a new gama runtime exception.
	 *
	 * @param scope the scope
	 * @param s the s
	 * @param warning the warning
	 */
	protected GamaRuntimeException(final IScope scope, final String s, final boolean warning) {
		super(s);
		if (scope != null) {
			final ISymbol symbol = scope.getCurrentSymbol();
			if (symbol != null) { addContext(symbol); }
		}
		cycle = computeCycle(scope);
		isWarning = warning;
		// AD: 18/01/16 Adding this to address Issue #1411
		this.scope = scope;
	}

	/**
	 * Adds the context.
	 *
	 * @param c the c
	 */
	public void addContext(final String c) {
		context.add(c);
	}

	/**
	 * Adds the context.
	 *
	 * @param s the s
	 */
	public void addContext(final ISymbol s) {
		addContext("in " + s.serialize(false));
		final EObject e = s.getDescription().getUnderlyingElement();
		if (e != null) { editorContext = e; }
	}

	/**
	 * Gets the editor context.
	 *
	 * @return the editor context
	 */
	public EObject getEditorContext() { return editorContext; }

	/**
	 * Adds the agent.
	 *
	 * @param agent the agent
	 */
	public void addAgent(final String agent) {
		occurrences++;
		if (agentsNames.contains(agent)) return;
		agentsNames.add(agent);
	}

	/**
	 * Adds the agents.
	 *
	 * @param agents the agents
	 */
	public void addAgents(final List<String> agents) {
		for (final String agent : agents) { addAgent(agent); }
	}

	/**
	 * Gets the cycle.
	 *
	 * @return the cycle
	 */
	public long getCycle() { return cycle; }

	/**
	 * Gets the agent summary.
	 *
	 * @return the agent summary
	 */
	public String getAgentSummary() {
		final int size = agentsNames.size();
		final String agents = size == 0 ? "" : size == 1 ? agentsNames.get(0) : String.valueOf(size) + " agents";
		final String occurence = occurrences == 0 ? "" : occurrences == 1 ? "1 occurence in "
				: String.valueOf(occurrences) + " occurrences in ";
		return occurence + agents;
	}

	/**
	 * Checks if is warning.
	 *
	 * @return true, if is warning
	 */
	public boolean isWarning() { return isWarning; }

	/**
	 * Compute cycle.
	 *
	 * @param scope the scope
	 * @return the long
	 */
	public long computeCycle(final IScope scope) {
		final SimulationClock clock = scope == null ? null : scope.getClock();
		return clock == null ? 0l : clock.getCycle();
	}

	/**
	 * Gets the context as list.
	 *
	 * @return the context as list
	 */
	public List<String> getContextAsList() {
		final List<String> result = new ArrayList<>();
		result.addAll(context);
		final int size = agentsNames.size();
		if (size == 0) return result;
		if (size == 1) {
			result.add("in agent " + agentsNames.get(0));
		} else {
			final StringBuilder sb = new StringBuilder();
			sb.append("in agents ").append(agentsNames.get(0));
			for (int i = 1; i < agentsNames.size(); i++) {
				sb.append(", ").append(agentsNames.get(i));
				if (sb.length() > 100) {
					sb.append("...");
					break;
				}
			}
			result.add(sb.toString());
		}
		return result;
	}

	@Override
	public String toString() {
		final String s = getClass().getName();
		final String message = getLocalizedMessage();
		return message != null ? message : s;
	}

	/**
	 * Equivalent to.
	 *
	 * @param ex the ex
	 * @return true, if successful
	 */
	public boolean equivalentTo(final GamaRuntimeException ex) {
		return this == ex || editorContext == ex.editorContext && getMessage().equals(ex.getMessage())
				&& getCycle() == ex.getCycle();
	}

	/**
	 * Sets the reported.
	 */
	public void setReported() {
		reported = true;
	}

	/**
	 * Checks if is reported.
	 *
	 * @return true, if is reported
	 */
	public boolean isReported() { return reported; }

	/**
	 * @return
	 */
	public List<String> getAgentsNames() { return agentsNames; }

	/**
	 * @return
	 */
	public String getAllText() {
		final StringBuilder sb = new StringBuilder(300);
		final String a = getAgentSummary();
		sb.append(a).append(" at ");
		sb.append("cycle ").append(getCycle()).append(": ").append(getMessage());
		final List<String> strings = getContextAsList();
		for (final String s : strings) { sb.append(Strings.LN).append(s); }
		return sb.toString();
	}

	/**
	 * Checks if is invalid.
	 *
	 * @return true, if is invalid
	 */
	// If the simulation or experiment is dead, no need to report errors
	public boolean isInvalid() {
		return scope == null || scope instanceof ExecutionScope && ((ExecutionScope) scope)._root_interrupted();
	}

}
