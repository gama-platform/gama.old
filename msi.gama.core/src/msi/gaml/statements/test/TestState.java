package msi.gaml.statements.test;

import msi.gama.util.GamaColor;

public enum TestState {
	ABORTED("error"), FAILED("failed"), WARNING("warning"), PASSED("passed"), NOT_RUN("not run");
	private final String name;

	TestState(final String s) {
		name = s;
	}

	@Override
	public String toString() {
		return name;
	}

	public GamaColor getColor() {
		switch (this) {
			case FAILED:
				return GamaColor.getNamed("gamared");
			case NOT_RUN:
				return GamaColor.getNamed("gamablue");
			case WARNING:
				return GamaColor.getNamed("gamaorange");
			case PASSED:
				return GamaColor.getNamed("gamagreen");
			default:
				return new GamaColor(83, 95, 107); // GamaColors.toGamaColor(IGamaColors.NEUTRAL.color());
		}
	}
}