/*******************************************************************************************************
 *
 * TestState.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.test;

import msi.gama.common.interfaces.IColored;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;

/**
 * The Enum TestState.
 */
public enum TestState implements IColored {

	/** The aborted. */
	ABORTED("error"),
	/** The failed. */
	FAILED("failed"),
	/** The warning. */
	WARNING("warning"),
	/** The passed. */
	PASSED("passed"),
	/** The not run. */
	NOT_RUN("not run");

	/** The name. */
	private final String name;

	/**
	 * Instantiates a new test state.
	 *
	 * @param s
	 *            the s
	 */
	TestState(final String s) {
		name = s;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	@Override
	public GamaColor getColor(final IScope scope) {
		return switch (this) {
			case FAILED -> GamaColor.get("gamared");
			case NOT_RUN -> GamaColor.get("gamablue");
			case WARNING -> GamaColor.get("gamaorange");
			case PASSED -> GamaColor.get("gamagreen");
			default -> GamaColor.get(83, 95, 107); // GamaColors.toGamaColor(IGamaColors.NEUTRAL.color());
		};
	}
}