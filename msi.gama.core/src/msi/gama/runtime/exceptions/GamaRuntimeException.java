/*******************************************************************************************************
 *
 * msi.gama.runtime.exceptions.GamaRuntimeException.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

	private final long cycle;
	protected final List<String> agentsNames = new ArrayList<>();
	private boolean isWarning;
	protected final List<String> context = new ArrayList<>();
	protected EObject editorContext;
	protected int occurrences = 0;
	protected boolean reported = false;
	protected final IScope scope;

	// Factory methods
	/**
	 * This call is deprecated. Use the equivalent method that passes the scope
	 *
	 * @param s
	 * @return
	 */

	public static GamaRuntimeException create(final Throwable ex, final IScope scope) {
		if (ex instanceof GamaRuntimeException) { return (GamaRuntimeException) ex; }
		if (ex instanceof IOException || ex instanceof FileNotFoundException) {
			return new GamaRuntimeFileException(scope, ex);
		}
		return new GamaRuntimeException(scope, ex);
	}

	public static GamaRuntimeException error(final String s, final IScope scope) {
		final GamaRuntimeException ex = new GamaRuntimeException(scope, s, false);
		return ex;
	}

	public static GamaRuntimeException warning(final String s, final IScope scope) {
		final GamaRuntimeException ex = new GamaRuntimeException(scope, s, true);
		return ex;
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

	protected static String getExceptionName(final Throwable ex) {
		final String s = ex.getClass().getName();
		if (s.contains("geotools") || s.contains("opengis")) {
			return "exception in GeoTools library";
		} else if (s.contains("jts")) {
			return "exception in JTS library";
		} else if (s.contains("rcaller")) {
			return "exception in RCaller library";
		} else if (s.contains("jogamp")) {
			return "exception in JOGL library";
		} else if (s.contains("weka")) {
			return "exception in Weka library";
		} else if (s.contains("math3")) { return "exception in Math library"; }
		if (ex instanceof NullPointerException) {
			return "nil value detected";
		} else if (ex instanceof IndexOutOfBoundsException) {
			return "index out of bounds";
		} else if (ex instanceof IOException) {
			return "I/O error";
		} else if (ex instanceof CoreException) {
			return "exception in Eclipse";
		} else if (ex instanceof ClassCastException) {
			return "wrong casting";
		} else if (ex instanceof IllegalArgumentException) { return "illegal argument"; }

		return ex.getClass().getSimpleName();
	}

	protected GamaRuntimeException(final IScope scope, final Throwable ex) {
		super(ex == null ? "Unknown error" : "Java error: " + getExceptionName(ex), ex);
		if (scope != null) {
			final ISymbol symbol = scope.getCurrentSymbol();
			if (symbol != null) {
				addContext(symbol);
			}
		}
		if (ex != null) {
			ex.printStackTrace();
			addContext(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			int i = 0;
			for (final StackTraceElement element : ex.getStackTrace()) {
				addContext(element.toString());
				if (i++ > 5) {
					break;
				}
			}
		}
		cycle = computeCycle(scope);
		// AD: 18/01/16 Adding this to address Issue #1411
		this.scope = scope;

	}

	protected GamaRuntimeException(final IScope scope, final String s, final boolean warning) {
		super(s);
		if (scope != null) {
			final ISymbol symbol = scope.getCurrentSymbol();
			if (symbol != null) {
				addContext(symbol);
			}
		}
		cycle = computeCycle(scope);
		isWarning = warning;
		// AD: 18/01/16 Adding this to address Issue #1411
		this.scope = scope;
	}

	public void addContext(final String c) {
		context.add(c);
	}

	public void addContext(final ISymbol s) {
		addContext("in " + s.serialize(false));
		final EObject e = s.getDescription().getUnderlyingElement();
		if (e != null) {
			editorContext = e;
		}
	}

	public EObject getEditorContext() {
		return editorContext;
	}

	public void addAgent(final String agent) {
		occurrences++;
		if (agentsNames.contains(agent)) { return; }
		agentsNames.add(agent);
	}

	public void addAgents(final List<String> agents) {
		for (final String agent : agents) {
			addAgent(agent);
		}
	}

	public long getCycle() {
		return cycle;
	}

	public String getAgentSummary() {
		final int size = agentsNames.size();
		final String agents = size == 0 ? "" : size == 1 ? agentsNames.get(0) : String.valueOf(size) + " agents";
		final String occurence = occurrences == 0 ? ""
				: occurrences == 1 ? "1 occurence in " : String.valueOf(occurrences) + " occurrences in ";
		return occurence + agents;
	}

	public boolean isWarning() {
		return isWarning;
	}

	public long computeCycle(final IScope scope) {
		final SimulationClock clock = scope == null ? null : scope.getClock();
		return clock == null ? 0l : clock.getCycle();
	}

	public List<String> getContextAsList() {
		final List<String> result = new ArrayList<>();
		result.addAll(context);
		final int size = agentsNames.size();
		if (size == 0) { return result; }
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

	public boolean equivalentTo(final GamaRuntimeException ex) {
		return this == ex || editorContext == ex.editorContext && getMessage().equals(ex.getMessage())
				&& getCycle() == ex.getCycle();
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
		if (a != null) {
			sb.append(a).append(" at ");
		}
		sb.append("cycle ").append(getCycle()).append(": ").append(getMessage());
		final List<String> strings = getContextAsList();
		for (final String s : strings) {
			sb.append(Strings.LN).append(s);
		}
		return sb.toString();
	}

	// If the simulation or experiment is dead, no need to report errors
	public boolean isInvalid() {
		return scope == null || scope instanceof ExecutionScope && ((ExecutionScope) scope)._root_interrupted();
	}

}
