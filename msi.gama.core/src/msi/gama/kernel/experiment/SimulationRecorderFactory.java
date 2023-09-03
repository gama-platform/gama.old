/*******************************************************************************************************
 *
 * SimulationRecorderFactory.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

/**
 * A factory for creating ISimulationRecorder objects.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 2 sept. 2023
 */
public class SimulationRecorderFactory {

	/** The recorder class. */
	static Class<? extends ISimulationRecorder> RECORDER_CLASS;

	/**
	 * Sets the recorder class.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param clazz
	 *            the new recorder class
	 * @date 2 sept. 2023
	 */
	public static void setRecorderClass(final Class<? extends ISimulationRecorder> clazz) { RECORDER_CLASS = clazz; }

	/**
	 * Creates the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the i simulation recorder
	 * @date 2 sept. 2023
	 */
	public static ISimulationRecorder create() {
		try {
			return RECORDER_CLASS.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
