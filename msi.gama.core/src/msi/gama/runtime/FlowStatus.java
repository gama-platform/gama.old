/*******************************************************************************************************
 *
 * FlowStatus.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime;

/**
 * The Enum LoopStatus.
 */
public enum FlowStatus {

	/** The break. */
	BREAK,
	/** The return. */
	RETURN,
	/** The continue. */
	CONTINUE,
	/** The die status: when the agent running in the scope is dead. */
	DIE,
	/** The close. When the simulations/experiments are closing */
	DISPOSE,
	/** The normal. */
	NORMAL;
}